package com.example.gpsutil;

import com.example.gpsutil.model.Location;
import com.example.gpsutil.model.VisitedLocation;
import com.example.gpsutil.service.GpsUtilService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertEquals;

@SpringBootTest
public class GpsUtilServiceTest {

    @Autowired
    GpsUtilService gpsUtilService;

    @Test
    public void getUserLocation() {
        Locale.setDefault(Locale.US);
        double longitude = ThreadLocalRandom.current().nextDouble(-180.0D, 180.0D);
        longitude = Double.parseDouble(String.format("%.6f", longitude));
        double latitude = ThreadLocalRandom.current().nextDouble(-85.05112878D, 85.05112878D);
        latitude = Double.parseDouble(String.format("%.6f", latitude));
        UUID            userId          = UUID.randomUUID();
        VisitedLocation visitedLocation = new VisitedLocation(userId, new Location(latitude, longitude), new Date());
        Assertions.assertEquals(gpsUtilService.getUserLocation(userId).userId, visitedLocation.userId);
    }
}
