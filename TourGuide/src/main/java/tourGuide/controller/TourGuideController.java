package tourGuide.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.jsoniter.output.JsonStream;


import tourGuide.model.*;
import tourGuide.service.TourGuideService;

@RestController
public class TourGuideController {

    @Autowired
    TourGuideService tourGuideService;

    @RequestMapping("/")
    public String index() {

        return "Greetings from TourGuide!";
    }

    @RequestMapping("/getLocation")
    public String getLocation(@RequestParam String userName) {

        VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
        return JsonStream.serialize(visitedLocation.getLocation());
    }



    @GetMapping("/getNearFiveAttractions")
    public List<NearAttractions> getNearFiveAttractions(@RequestParam String userName) {
        //  DONE: Change this method to no longer return a List of Attractions.
        //  Instead: Get the closest five tourist attractions to the user - no matter how far away they are.
        //  Return a new JSON object that contains:
        // Name of Tourist attraction,
        // Tourist attractions lat/long,
        // The user's location lat/long,
        // The distance in miles between the user's location and each of the attractions.
        // The reward points for visiting each Attraction.
        //    Note: Attraction reward points can be gathered from RewardsCentral
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
        return tourGuideService.getNearFiveAttractions(visitedLocation, this.getUser(userName));

    }

    @RequestMapping("/getNearbyAttractions")
    public String getNearbyAttractions(@RequestParam String userName) {

        VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
        return JsonStream.serialize(tourGuideService.getNearByAttractions(visitedLocation));
    }

    @RequestMapping("/getRewards")
    public String getRewards(@RequestParam String userName) {

        return JsonStream.serialize(tourGuideService.getUserRewards(getUser(userName)));
    }

    @RequestMapping("/getAllCurrentLocations")
    public List<AllUsersCurrentLocations> getAllCurrentLocations() {
        // DONE: Get a list of every user's most recent location as JSON
        //- Note: does not use gpsUtil to query for their current location,
        //        but rather gathers the user's current location from their stored location history.
        //
        // Return object should be the just a JSON mapping of userId to Locations similar to:
        //     {
        //        "019b04a9-067a-4c76-8817-ee75088c3822": {"longitude":-48.188821,"latitude":74.84371}
        //        ...
        //     }
        return tourGuideService.getAllCurrentLocations();

    }

    @GetMapping("/getTripDeals")
    @ResponseBody
    public List<Provider> getTripDeals(@RequestParam(value = "userName") String userName, @RequestParam(value = "tripDuration") int tripDuration, @RequestParam(value = "numberOfAdults") int numberOfAdults, @RequestParam(value = "numberOfChildren") int numberOfChildren) {

        return tourGuideService.getTripDeals(getUser(userName), tripDuration, numberOfAdults, numberOfChildren);
    }

    private User getUser(String userName) {

        return tourGuideService.getUser(userName);
    }


}
