package com.example.gpsutil.controller;


import com.example.gpsutil.model.Attraction;
import com.example.gpsutil.model.VisitedLocation;
import com.example.gpsutil.service.GpsUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class GpsUtilController {

    @Autowired
    GpsUtilService gpsUtilService;

    @RequestMapping("/")
    public String index() {

        return "Home GpsUtil - Please request /getLocation or /getAttractions";
    }

    @RequestMapping("/getLocation/{userID}")
    @ResponseBody
    public VisitedLocation getUserLocation(@PathVariable UUID userID) {

        return gpsUtilService.getUserLocation(userID);
    }

    @RequestMapping("/getAttractions")
    public List<Attraction> getAttractions() {

        return gpsUtilService.getAttractions();
    }
}
