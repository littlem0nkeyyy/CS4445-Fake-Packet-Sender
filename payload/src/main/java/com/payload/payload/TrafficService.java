package com.payload.payload;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalTime;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class TrafficService {

    // --- CẤU HÌNH SERVER & THỜI GIAN CHẠY ---
    private static final String TARGET_URL = "http://localhost:8080/api/packet"; // THAY ĐỔI ĐỊA CHỈ NÀY!
    private static final long DURATION_DAYS = 2; // Thời gian chạy là 2 ngày
    
    // --- GIỚI HẠN TỐI ĐA CHO CƯỜNG ĐỘ GÓI ---
    private static final int MAX_CPU_INTENSITY = 10; // Tối đa mức 10
    private static final int MAX_RAM_INTENSITY_MB = 100; // Tối đa 100MB
    private static final int MAX_PROCESSING_TIME_MS = 300; // Tối đa 300ms
    private static final int KB = 1024;
    private static final int MIN_PAYLOAD_SIZE = 10 * KB; 
    private static final int MAX_PAYLOAD_SIZE = 50 * KB; 
    
    // --- CẤU HÌNH ĐA LUỒNG & TẢI TRỌNG ---
    private static final int VIRTUAL_CLIENTS = 20; // Số lượng client ảo gửi đồng thời
    private static final int NUM_REQUESTS = Integer.MAX_VALUE; 

    private final WebClient webClient;
    private final Random random = new Random();
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private int requestCounter = 0;
    
    // Sử dụng pool luồng bằng kích thước client ảo
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(VIRTUAL_CLIENTS);

    public TrafficService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl(TARGET_URL)
                .build();
    }

    // --- HÀM HỖ TRỢ NGẪU NHIÊN ---
    
    /**
     * Trả về một giá trị ngẫu nhiên từ 1 đến max (bao gồm max).
     */
    private int getRandomIntensity(int max) {
        return random.nextInt(max) + 1; 
    }
    
    /**
     * Tạo ra một chuỗi payload ngẫu nhiên.
     */
    private String generateRandomPayload(int minSize, int maxSize) {
        int length = random.nextInt(maxSize - minSize + 1) + minSize;
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
    
    /**
     * Tính toán độ trễ (period) dựa trên giờ hiện tại (Mô hình Peak Hour).
     */
    private long calculatePeriodMs() {
        LocalTime now = LocalTime.now();
        int hour = now.getHour();
        int minute = now.getMinute();

        // 1. Giờ Đột biến (Spike/Flash Sale): 1ms
        if ((hour == 12 || hour == 21) && minute < 15) {
            return 1; 
        } 
        // 2. Cao điểm tối: 5ms
        else if (hour >= 18 && hour < 23) {
            return 5; 
        } 
        // 3. Cao điểm nhẹ (Giờ hành chính): 10ms
        else if (hour >= 9 && hour < 18) {
            return 10; 
        } 
        // 4. Bình thường (Base Load): 50ms
        else {
            return 50; 
        }
    }


    /**
     * Gửi một yêu cầu POST chứa FakePacketRequest với cường độ NGẪU NHIÊN.
     */
    public void sendRandomRequest(long currentPeriodMs) {
        if (requestCounter >= NUM_REQUESTS) {
            return;
        }

        requestCounter++;
        
        // 1. Tạo các giá trị cường độ NGẪU NHIÊN
        Integer randomCpu = getRandomIntensity(MAX_CPU_INTENSITY);
        Integer randomRam = getRandomIntensity(MAX_RAM_INTENSITY_MB);
        Integer randomTime = getRandomIntensity(MAX_PROCESSING_TIME_MS);
        
        // 2. Tạo đối tượng FakePacketRequest
        PayloadRequest requestPayload = new PayloadRequest(
                UUID.randomUUID().toString(), 
                randomCpu, randomRam, randomTime,         
                generateRandomPayload(MIN_PAYLOAD_SIZE, MAX_PAYLOAD_SIZE)
        );
        
        // 3. Gửi yêu cầu POST (sử dụng WebClient không chặn)
        webClient.post()
            .uri(uriBuilder -> uriBuilder.path("").build())
            .body(Mono.just(requestPayload), PayloadRequest.class)
            .retrieve()
            .toBodilessEntity()
            .subscribe(
                response -> System.out.printf("[%04d] POST (Tốc độ: %dms) -> CPU:%d, RAM:%dMB, Time:%dms -> Status: %s%n", 
                                             requestCounter, 
                                             currentPeriodMs,
                                             requestPayload.getCpuIntensity(), 
                                             requestPayload.getRamIntensity(), 
                                             requestPayload.getProcessingTimeMs(), 
                                             response.getStatusCode()),
                error -> System.err.printf("[%04d] LỖI POST (Tốc độ: %dms): %s%n", requestCounter, currentPeriodMs, error.getMessage())
            );
    }
    
    /**
     * Hàm chính để quản lý việc gửi yêu cầu theo chu kỳ sử dụng Thread Pool.
     */
    public void startTrafficSimulation() {
        long totalDurationMs = DURATION_DAYS * 24 * 60 * 60 * 1000;
        long startTime = System.currentTimeMillis();

        // Tác vụ lặp lại (chạy trên nhiều luồng)
        Runnable trafficTask = new Runnable() {
            @Override
            public void run() {
                long elapsedTime = System.currentTimeMillis() - startTime;
                if (elapsedTime >= totalDurationMs) {
                    return; // Task đã kết thúc, không lập lịch lại
                }
                
                // 1. Tính toán độ trễ cho gói tiếp theo (Peak Hour Logic)
                long period = calculatePeriodMs();
                
                // 2. Gửi gói hiện tại (với cường độ ngẫu nhiên)
                sendRandomRequest(period);

                // 3. Lên lịch gói tiếp theo cho luồng này sau thời gian 'period'
                scheduler.schedule(this, period, TimeUnit.MILLISECONDS);
            }
        };

        System.out.println("🚀 Bắt đầu mô phỏng lưu lượng truy cập tới: " + TARGET_URL);
        System.out.printf("   Sử dụng %d client ảo (luồng). Chương trình sẽ chạy trong %d ngày.%n", VIRTUAL_CLIENTS, DURATION_DAYS);
        System.out.println("-------------------------------------------");

        // KHỞI TẠO: Khởi chạy số lượng tác vụ bằng số lượng VIRTUAL_CLIENTS
        for (int i = 0; i < VIRTUAL_CLIENTS; i++) {
            scheduler.schedule(trafficTask, 2, TimeUnit.SECONDS); 
        }
        
        // Lên lịch để shutdown pool sau 2 ngày
        scheduler.schedule(() -> {
            if (!scheduler.isShutdown()) {
                 System.out.println("-------------------------------------------");
                 System.out.println("✅ Đã đạt giới hạn 2 ngày. Đang tắt các luồng.");
                 scheduler.shutdown();
            }
        }, totalDurationMs + 5000, TimeUnit.MILLISECONDS);
    }
}