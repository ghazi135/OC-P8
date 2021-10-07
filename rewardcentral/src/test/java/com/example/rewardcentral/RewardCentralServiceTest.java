package com.example.rewardcentral;


import com.example.rewardcentral.service.RewardCentralService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.Assert.assertTrue;

@SpringBootTest
public class RewardCentralServiceTest {

    @Autowired
    RewardCentralService rewardCentralService;

    @Test
    public void getAttractionRewardPoints() {

        assertTrue(rewardCentralService.getAttractionRewardPoints(UUID.randomUUID(), UUID.randomUUID()) <= 1000);
        assertTrue(rewardCentralService.getAttractionRewardPoints(UUID.randomUUID(), UUID.randomUUID()) >= 1);

    }

}
