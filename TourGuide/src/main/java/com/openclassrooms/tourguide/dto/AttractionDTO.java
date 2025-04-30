package com.openclassrooms.tourguide.dto;

public class AttractionDTO {
    private String attractionName;

    private double attractionLatitude;
    private double attractionLongitude;

    private double userLatitude;

    private double userLongitude;

    private double attractionDistance;

    private int rewardPoints;

    public AttractionDTO() {

    }

    public AttractionDTO(
            String attractionName,
            double attractionLatitude,
            double attractionLongitude,
            double userLatitude,
            double userLongitude,
            double attractionDistance,
            int rewardPoints
    ) {
        this.attractionName = attractionName;
        this.attractionLatitude = attractionLatitude;
        this.attractionLongitude = attractionLongitude;
        this.userLatitude = userLatitude;
        this.userLongitude = userLongitude;
        this.attractionDistance = attractionDistance;
        this.rewardPoints = rewardPoints;
    }

    public String getAttractionName() {
        return attractionName;
    }

    public void setAttractionName(String attractionName) {
        this.attractionName = attractionName;
    }

    public double getAttractionLatitude() {
        return attractionLatitude;
    }

    public void setAttractionLatitude(double attractionLatitude) {
        this.attractionLatitude = attractionLatitude;
    }

    public double getAttractionLongitude() {
        return attractionLongitude;
    }

    public void setAttractionLongitude(double attractionLongitude) {
        this.attractionLongitude = attractionLongitude;
    }

    public double getUserLatitude() {
        return userLatitude;
    }

    public void setUserLatitude(double userLatitude) {
        this.userLatitude = userLatitude;
    }

    public double getUserLongitude() {
        return userLongitude;
    }

    public void setUserLongitude(double userLongitude) {
        this.userLongitude = userLongitude;
    }

    public double getAttractionDistance() {
        return attractionDistance;
    }

    public void setAttractionDistance(double attractionDistance) {
        this.attractionDistance = attractionDistance;
    }

    public int getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(int rewardPoints) {
        this.rewardPoints = rewardPoints;
    }
}
