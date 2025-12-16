package com.payload.payload;

// Lưu ý: Nếu bạn sử dụng thư viện Lombok, bạn có thể thay thế tất cả Getters/Setters/Constructors
// bằng @Data, @Builder, @NoArgsConstructor, @AllArgsConstructor.
public class PayloadRequest {
    private String packetId;
    private Integer cpuIntensity; // 1-10 scale for CPU load (Ngẫu nhiên)
    private Integer ramIntensity; // 1-100 scale for RAM load (MB) (Ngẫu nhiên)
    private Integer processingTimeMs; // Minimum processing time in milliseconds (Ngẫu nhiên)
    private String payload; // Dữ liệu ngẫu nhiên

    // Constructor mặc định (cần thiết cho Deserialization JSON)
    public PayloadRequest() {
    }

    // Constructor đầy đủ
    public PayloadRequest(String packetId, Integer cpuIntensity, Integer ramIntensity, Integer processingTimeMs, String payload) {
        this.packetId = packetId;
        this.cpuIntensity = cpuIntensity;
        this.ramIntensity = ramIntensity;
        this.processingTimeMs = processingTimeMs;
        this.payload = payload;
    }

    // --- Getters và Setters ---
    public String getPayloadId() {
        return packetId;
    }
    public void setPacketId(String packetId) {
        this.packetId = packetId;
    }
    public Integer getCpuIntensity() {
        return cpuIntensity;
    }
    public void setCpuIntensity(Integer cpuIntensity) {
        this.cpuIntensity = cpuIntensity;
    }
    public Integer getRamIntensity() {
        return ramIntensity;
    }
    public void setRamIntensity(Integer ramIntensity) {
        this.ramIntensity = ramIntensity;
    }
    public Integer getProcessingTimeMs() {
        return processingTimeMs;
    }
    public void setProcessingTimeMs(Integer processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }
    public String getPayload() {
        return payload;
    }
    public void setPayload(String payload) {
        this.payload = payload;
    }
}