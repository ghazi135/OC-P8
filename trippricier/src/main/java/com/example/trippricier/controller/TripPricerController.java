package com.example.trippricier.controller;

import com.example.trippricier.model.Provider;
import com.example.trippricier.service.TripPricerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class TripPricerController {

    @Autowired
    TripPricerService tripPricer;

    @RequestMapping("/")
    public String index() {

        return "Greetings from TripPricerService!";
    }

    @RequestMapping("/getPrice/{apiKey}/{attractionId}/{adults}/{children}/{nightsStay}/{rewardsPoints}")
    public List<Provider> getPrice(@PathVariable String apiKey, @PathVariable UUID attractionId, @PathVariable int adults, @PathVariable int children, @PathVariable int nightsStay, @PathVariable int rewardsPoints) {

        return tripPricer.getPrice(apiKey, attractionId, adults, children, nightsStay, rewardsPoints);
    }

    @RequestMapping("/getProviderName/{apiKey}/{adults}")
    public String getProviderName(@PathVariable String apiKey, @PathVariable int adults) {

        return tripPricer.getProviderName(apiKey, adults);
    }
}
