package com.example.carlos.assignment_one;

/**
 * Created by RZ on 11/3/17.
 */

public class HistoryInfo {
    private String name;
    private double lng,lat;
    private String imageId;
    private boolean petted;

    public HistoryInfo(String name, double lng, double lat, String imageId, boolean petted) {
        this.name = name;
        this.imageId = imageId;
        this.lng = lng;
        this.lat = lat;
        this.petted = petted;
    }

    public String getName() {
        return name;
    }

    public String getImageId() {
        return imageId;
    }

    public double getLng() {
        return lng;
    }

    public double getLat() {
        return lat;
    }

    public boolean getPetted(){
        return petted;
    }

}
