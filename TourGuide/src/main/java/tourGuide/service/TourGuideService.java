package tourGuide.service;


import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import tourGuide.helper.InternalTestHelper;
import tourGuide.model.*;
import tourGuide.proxy.GpsUtilProxy;
import tourGuide.proxy.RewardCentralProxy;
import tourGuide.proxy.TripPricerProxy;
import tourGuide.tracker.Tracker;


@Service
public class TourGuideService {

    /**********************************************************************************
     *
     * Methods Below: For Internal Testing
     *
     **********************************************************************************/
    ExecutorService executorService = Executors.newFixedThreadPool(100);

    private static final String            tripPricerApiKey = "test-server-api-key";
    public final  Tracker        tracker;
    private final RewardsService rewardsService;
    @Autowired
    GpsUtilProxy gpsUtilProxy;
    @Autowired
    RewardCentralProxy rewardCentralProxy;
    @Autowired
    TripPricerProxy tripPricerProxy;
    // Database connection will be used for external users, but for testing purposes internal users are provided and stored in memory
    private final        Map<String, User> internalUserMap  = new HashMap<>();
    boolean testMode = true;
    private       Logger         logger     = LoggerFactory.getLogger(TourGuideService.class);

    public TourGuideService(GpsUtilProxy gpsUtilProxy, RewardsService rewardsService) {

        this.gpsUtilProxy        = gpsUtilProxy;
        this.rewardsService = rewardsService;

        if (testMode) {
            logger.info("TestMode activé");
            logger.debug("initialiser les utilisateurs");
            initializeInternalUsers();
            logger.debug("utilisateurs initialisé");
        }
        tracker = new Tracker(this);
        addShutDownHook();
    }

    public List<UserReward> getUserRewards(User user) {

        return user.getUserRewards();
    }

    public VisitedLocation getUserLocation(User user) {

        return (user.getVisitedLocations().size() > 0) ?
                user.getLastVisitedLocation() :
                trackUserLocation(user);
    }
    public User getUser(String userName) {

        return internalUserMap.get(userName);
    }

    public List<User> getAllUsers() {

        return internalUserMap.values().stream().collect(Collectors.toList());
    }

    public void trackUserLocationWithThread(User user) {
        executorService.execute(new Runnable() {
            public void run() {
                trackUserLocation(user);
            }
        });
    }

    public void shutdown() throws InterruptedException {
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

    }


    public void addUser(User user) {

        if (!internalUserMap.containsKey(user.getUserName())) {
            internalUserMap.put(user.getUserName(), user);
        }
    }

    public List<Provider> getTripDeals(User user, int tripDuration, int numberOfAdults, int numberOfChildren) {
        int rewardCumul = user.getUserRewards().stream().mapToInt(UserReward::getRewardPoints).sum();
        UserPreferences preferences = new UserPreferences();
        preferences.setTripDuration(tripDuration);
        preferences.setNumberOfAdults(numberOfAdults);
        preferences.setNumberOfChildren(numberOfChildren);
        user.setUserPreferences(preferences);
        List<Provider> providers = tripPricerProxy.getPrice(tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
                user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), rewardCumul);
        user.setTripDeals(providers);
        return providers;
    }

    public VisitedLocation trackUserLocation(User user) {

        VisitedLocation visitedLocation = gpsUtilProxy.getUserLocation(user.getUserId());
        user.addToVisitedLocations(visitedLocation);
        rewardsService.calculateRewards(user);
        return visitedLocation;
    }

    public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation) {

        List<Attraction> nearbyAttractions = new ArrayList<>();
        for (Attraction attraction : gpsUtilProxy.getAttractions()) {
            if (rewardsService.isWithinAttractionProximity(attraction, visitedLocation.getLocation())) {
                nearbyAttractions.add(attraction);
            }
        }

        return nearbyAttractions;
    }

    private void addShutDownHook() {

        Runtime.getRuntime().addShutdownHook(new Thread() {

            public void run() {

                tracker.stopTracking();
            }
        });
    }

    private void initializeInternalUsers() {

        IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
            String userName = "internalUser" + i;
            String phone    = "000";
            String email    = userName + "@tourGuide.com";
            User   user     = new User(UUID.randomUUID(), userName, phone, email);
            generateUserLocationHistory(user);

            internalUserMap.put(userName, user);
        });
        logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
    }

    private void generateUserLocationHistory(User user) {

        IntStream.range(0, 3).forEach(i -> {
            user.addToVisitedLocations(new VisitedLocation(user.getUserId(), new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
        });
    }

    private double generateRandomLongitude() {

        double leftLimit  = -180;
        double rightLimit = 180;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    private double generateRandomLatitude() {

        double leftLimit  = -85.05112878;
        double rightLimit = 85.05112878;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    private Date getRandomTime() {

        LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
        return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
    }

    public List<AllUsersCurrentLocations> getAllCurrentLocations() {
        List<User> users = this.getAllUsers();
        List<AllUsersCurrentLocations> currentLocations = new ArrayList<>();
        for (User user : users) {
            this.generateUserLocationHistory(user);
            VisitedLocation lastLocation = user.getLastVisitedLocation();
            AllUsersCurrentLocations currentLocation = new AllUsersCurrentLocations(lastLocation.getUserId(), lastLocation.getLocation().getLatitude(), lastLocation.getLocation().getLongitude());
            currentLocations.add(currentLocation);
        }
        return currentLocations;

    }

    public List<NearAttractions> getNeardistance(VisitedLocation visitedLocation, User user) {
        List<NearAttractions> nearAttractions = new ArrayList<>();
        for (Attraction attraction : gpsUtilProxy.getAttractions()) {
            double distance = rewardsService.getDistance(new Location(attraction.getLatitude(), attraction.getLongitude()), new Location(visitedLocation.getLocation().getLatitude(), visitedLocation.getLocation().getLongitude()));
            NearAttractions nearAttraction = new NearAttractions(attraction.getAttractionName(), distance, attraction.getLatitude(), attraction.getLongitude(), visitedLocation.getLocation(), rewardCentralProxy.getAttractionRewardPoints(attraction.getAttractionId(), user.getUserId()));
            nearAttractions.add(nearAttraction);
            nearAttractions.sort(Comparator.comparingDouble(NearAttractions::getDistance));
        }
        return nearAttractions;
    }

    public List<NearAttractions> getNearFiveAttractions(VisitedLocation visitedLocation, User user) {
        List<NearAttractions> nearAttractions = this.getNeardistance(visitedLocation, user);
        List<NearAttractions> fiveNearAttractions = new ArrayList<>();

        for (NearAttractions nearAttraction : nearAttractions) {

            while (fiveNearAttractions.size() < 5) {
                fiveNearAttractions.add(nearAttraction);
            }
        }
        return fiveNearAttractions;

    }


}
