package com.payload.payload;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class TrafficServiceTest {

    @Test
    void sendRandomRequestDoesNotThrow() {
        WebClient.Builder builder = WebClient.builder();
        TrafficService trafficService = new TrafficService(builder);

        // Chỉ kiểm tra rằng việc gửi 1 request ngẫu nhiên không ném exception đồng bộ
        assertDoesNotThrow(() -> trafficService.sendRandomRequest(10L));
    }
}


