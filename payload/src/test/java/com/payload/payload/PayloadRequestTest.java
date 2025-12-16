package com.payload.payload;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PayloadRequestTest {

    @Test
    void constructorAndGettersSettersWorkCorrectly() {
        String packetId = "packet-123";
        Integer cpu = 5;
        Integer ram = 64;
        Integer timeMs = 150;
        String payload = "dummy-payload";

        PayloadRequest request = new PayloadRequest(packetId, cpu, ram, timeMs, payload);

        // Lưu ý: getter cho packetId hiện đang là getPayloadId()
        assertEquals(packetId, request.getPayloadId());
        assertEquals(cpu, request.getCpuIntensity());
        assertEquals(ram, request.getRamIntensity());
        assertEquals(timeMs, request.getProcessingTimeMs());
        assertEquals(payload, request.getPayload());

        // Kiểm tra setters thay đổi giá trị
        request.setPacketId("packet-456");
        request.setCpuIntensity(7);
        request.setRamIntensity(32);
        request.setProcessingTimeMs(200);
        request.setPayload("new-payload");

        assertEquals("packet-456", request.getPayloadId());
        assertEquals(7, request.getCpuIntensity());
        assertEquals(32, request.getRamIntensity());
        assertEquals(200, request.getProcessingTimeMs());
        assertEquals("new-payload", request.getPayload());
    }
}


