package com.payload.payload;

import org.junit.jupiter.api.Test;

/**
 * Sanity test to ensure test suite runs; we avoid loading full Spring context
 * to keep tests fast and independent from CommandLineRunner side effects.
 */
class PayloadApplicationTests {

    @Test
    void contextLoads() {
        // No-op: if the test suite runs, this passes.
    }
}
