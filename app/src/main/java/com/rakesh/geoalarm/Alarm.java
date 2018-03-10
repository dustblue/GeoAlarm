package com.rakesh.geoalarm;

/**
 * Created by Rakesh on 10-03-2018.
 */

public class Alarm {

    private int id;
    private String name;
    private Double lat;
    private Double lng;
    private Boolean enabled;
    private String Date;

    int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    Double getLat() {
        return lat;
    }

    void setLat(Double lat) {
        this.lat = lat;
    }

    Double getLng() {
        return lng;
    }

    void setLng(Double lng) {
        this.lng = lng;
    }

    Boolean getEnabled() {
        return enabled;
    }

    void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    String getDate() {
        return Date;
    }

    void setDate(String date) {
        Date = date;
    }}
