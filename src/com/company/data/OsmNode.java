package com.company.data;

import java.util.HashMap;
import java.util.List;

/**
 * Created by unkei on 2017/04/25.
 */
public class OsmNode extends OsmElement {
    GeoCoordinate geoCoordinate;

    public OsmNode(String id, GeoCoordinate geoCoordinate, HashMap<String, String> tags) {
        super(id, null, tags);
        this.geoCoordinate = geoCoordinate;
    }

    public GeoCoordinate getGeoCoordinate() {
        return this.geoCoordinate;
    }

    public static OsmNode getOsmNodeById(List<OsmNode> nodes, String id) {
        return (OsmNode)getOsmElementById(nodes, id);
    }
}
