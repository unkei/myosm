package com.company.data;

/**
 * Created by unkei on 2017/04/26.
 */
public class OsmBounds {

    public double minLat, minLon, maxLat, maxLon;

    public OsmBounds(double minLat, double minLon, double maxLat, double maxLon) {
        this.minLat = minLat;
        this.minLon = minLon;
        this.maxLat = maxLat;
        this.maxLon = maxLon;
    }
}
