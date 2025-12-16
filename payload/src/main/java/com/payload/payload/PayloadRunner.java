package com.payload.payload;    

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class PayloadRunner implements CommandLineRunner {

    private final TrafficService trafficService;

    public PayloadRunner(TrafficService trafficService) {
        this.trafficService = trafficService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Starting traffic simulation...");
        trafficService.startTrafficSimulation();
    }
}