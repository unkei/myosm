package com.company.data;

/**
 * Created by unkei on 2017/04/25.
 */
public class OsmNode {
    String id;
    GeoCoordinate geoCoordinate;

    public OsmNode(String id, GeoCoordinate geoCoordinate) {
        this.id = id;
        this.geoCoordinate = geoCoordinate;
    }

    public String getId() {
        return this.id;
    }

    public GeoCoordinate getGeoCoordinate() {
        return this.geoCoordinate;
    }
}
