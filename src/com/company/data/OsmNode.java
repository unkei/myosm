package com.company.data;

import java.util.HashMap;

/**
 * Created by unkei on 2017/04/25.
 */
public class OsmNode {
    String id;
    GeoCoordinate geoCoordinate;
    HashMap<String, String> tags;

    public OsmNode(String id) {
        this(id, new GeoCoordinate(), new HashMap<>());
    }

    public OsmNode(String id, GeoCoordinate geoCoordinate) {
        this(id, geoCoordinate, new HashMap<>());
    }

    public OsmNode(String id, GeoCoordinate geoCoordinate, HashMap<String, String> tags) {
        this.id = id;
        this.geoCoordinate = geoCoordinate;
        this.tags = tags;
    }

    public String getId() {
        return this.id;
    }

    public GeoCoordinate getGeoCoordinate() {
        return this.geoCoordinate;
    }

    public HashMap<String, String> getTags() {
        return this.tags;
    }
}
