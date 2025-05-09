package com.openclassrooms.tourguide.service;

import com.openclassrooms.tourguide.dto.AttractionDTO;
import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.model.UserReward;
import com.openclassrooms.tourguide.tracker.Tracker;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;

import tripPricer.Provider;
import tripPricer.TripPricer;

@Service
public class TourGuideService {
	private Logger logger = LoggerFactory.getLogger(TourGuideService.class);
	private final GpsUtil gpsUtil;
	private final RewardsService rewardsService;
	private final TripPricer tripPricer = new TripPricer();
	public final Tracker tracker;
	boolean testMode = true;

	public TourGuideService(GpsUtil gpsUtil, RewardsService rewardsService) {
		this.gpsUtil = gpsUtil;
		this.rewardsService = rewardsService;
		
		Locale.setDefault(Locale.US);

		if (testMode) {
			logger.info("TestMode enabled");
			logger.debug("Initializing users");
			initializeInternalUsers();
			logger.debug("Finished initializing users");
		}
		tracker = new Tracker(this);
		addShutDownHook();
	}

	public List<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
	}

	public VisitedLocation getUserLocation(User user) {
		return (!user.getVisitedLocations().isEmpty()) ? user.getLastVisitedLocation()
				: trackUserLocation(user, true);
	}

	public User getUser(String userName) {
		return internalUserMap.get(userName);
	}

	public List<User> getAllUsers() {
		return internalUserMap.values().stream().toList();
	}

	public void addUser(User user) {
		if (!internalUserMap.containsKey(user.getUserName())) {
			internalUserMap.put(user.getUserName(), user);
		}
	}

	public List<Provider> getTripDeals(User user) {
		int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
		List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, user.getUserId(),
				user.getUserPreferences().getNumberOfAdults(), user.getUserPreferences().getNumberOfChildren(),
				user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);
		user.setTripDeals(providers);
		return providers;
	}

	public VisitedLocation trackUserLocation(User user, boolean calculateRewards) {
		VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
		user.addToVisitedLocations(visitedLocation);
		if(calculateRewards)
			rewardsService.calculateRewards(user);
		return visitedLocation;
	}

	public List<VisitedLocation> trackUsersLocations(List<User> users, boolean calculateRewards) {
		ExecutorService executorService = Executors.newFixedThreadPool(
				Math.min(users.size(), 200)
		);

		List<CompletableFuture<VisitedLocation>> tasks = users.stream().map(user -> {
			return CompletableFuture.supplyAsync(() -> this.trackUserLocation(user, calculateRewards), executorService);
		}).toList();

		List<VisitedLocation> outVisitedLocations = tasks.stream()
				.map(CompletableFuture::join)
				.toList();

		return outVisitedLocations;
	}

	public List<AttractionDTO> getNearByAttractions(VisitedLocation visitedLocation, User user) {
		List<AttractionDTO> nearbyAttractions = new ArrayList<>();
		List<Attraction> attractions = gpsUtil.getAttractions();

		// Sort attractions by distance based on visitedLocation position, and keep only 5 closest attractions
		attractions = attractions.stream()
				.sorted(Comparator.comparingDouble(attraction -> rewardsService.getDistance(visitedLocation.location, attraction)))
				.limit(5)
				.toList();

		for (Attraction attraction : attractions) {
			nearbyAttractions.add( this.createAttractionDTO(attraction, visitedLocation, user) );
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

	private AttractionDTO createAttractionDTO(
			Attraction attraction,
			VisitedLocation visitedLocation,
			User user
	) {
		return new AttractionDTO(
				attraction.attractionName,
				attraction.longitude,
				attraction.latitude,
				visitedLocation.location.longitude,
				visitedLocation.location.latitude,
				rewardsService.getDistance(visitedLocation.location, attraction),
				rewardsService.getRewardPoints(attraction, user)
		);
	}

	/**********************************************************************************
	 * 
	 * Methods Below: For Internal Testing
	 * 
	 **********************************************************************************/
	private static final String tripPricerApiKey = "test-server-api-key";
	// Database connection will be used for external users, but for testing purposes
	// internal users are provided and stored in memory
	private final Map<String, User> internalUserMap = new HashMap<>();

	private void initializeInternalUsers() {
		IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
			String userName = "internalUser" + i;
			String phone = "000";
			String email = userName + "@tourGuide.com";
			User user = new User(UUID.randomUUID(), userName, phone, email);
			generateUserLocationHistory(user);

			internalUserMap.put(userName, user);
		});
		logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
	}

	private void generateUserLocationHistory(User user) {
		IntStream.range(0, 3).forEach(i -> {
			user.addToVisitedLocations(new VisitedLocation(user.getUserId(),
					new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
		});
	}

	private double generateRandomLongitude() {
		double leftLimit = -180;
		double rightLimit = 180;
		return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}

	private double generateRandomLatitude() {
		double leftLimit = -85.05112878;
		double rightLimit = 85.05112878;
		return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}

	private Date getRandomTime() {
		LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
		return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}

}
