package com.company.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by unkei on 2017/04/25.
 */
public class OsmWay {
    String id;
    List<OsmNode> osmNodes;

    public OsmWay(String id, List<OsmNode> osmNodes) {
        this.id = id;
        this.osmNodes = osmNodes;
    }

    public String getId() {
        return this.id;
    }

    public List<OsmNode> getOsmNodes() {
        return this.osmNodes;
    }
}
