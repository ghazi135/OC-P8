package com.example.trippricier;

import com.example.trippricier.service.TripPricerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TripPricerServiceTest {


    @Autowired
    TripPricerService tripPricerService;

    @Autowired
    private MockMvc mockMvc;



    @Test
    public void getProviderName() {
        List<String> providerName = new ArrayList<>();
        providerName.add("Holiday Travels");
        providerName.add("Enterprize Ventures Limited");
        providerName.add("Sunny Days");
        providerName.add("FlyAway Trips");
        providerName.add("United Partners Vacations");
        providerName.add("Dream Trips");
        providerName.add("Live Free");
        providerName.add("Dancing Waves Cruselines and Partners");
        providerName.add("AdventureCo");
        providerName.add( "Cure-Your-Blues");
        assertTrue( providerName.contains(tripPricerService.getProviderName("apiKey", 1)));

    }



    @Test
    public void getPrice() throws Exception {
        this.mockMvc.perform(get("/getPrice/apiKey/8c92ecd7-bfb7-4e65-b387-f75265bac1eb/2/2/2/260")).andDo(print()).andExpect(status().isOk());

    }
}
