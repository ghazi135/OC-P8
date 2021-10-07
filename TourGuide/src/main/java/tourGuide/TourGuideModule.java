package tourGuide;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tourGuide.proxy.GpsUtilProxy;
import tourGuide.proxy.RewardCentralProxy;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;

@Configuration
public class TourGuideModule {

    GpsUtilProxy       gpsUtilProxy;

    RewardCentralProxy rewardCentralProxy;


    @Bean
    public RewardsService getRewardsService() { return new RewardsService(gpsUtilProxy, rewardCentralProxy); }

    @Bean
    public TourGuideService getTourGuideService() {
        return new TourGuideService(gpsUtilProxy, getRewardsService());
    }


}
