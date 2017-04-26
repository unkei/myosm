package com.company.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by unkei on 2017/04/25.
 */
public class OsmWay {
    String id;
    List<OsmNode> osmNodes;
    HashMap<String, String> tags;

    public OsmWay(String id, List<OsmNode> osmNodes) {
        this(id, osmNodes, new HashMap<>());
    }

    public OsmWay(String id, List<OsmNode> osmNodes, HashMap<String, String> tags) {
        this.id = id;
        this.osmNodes = osmNodes;
        this.tags = tags;
    }

    public String getId() {
        return this.id;
    }

    public List<OsmNode> getOsmNodes() {
        return this.osmNodes;
    }

    public HashMap<String, String> getTags() {
        return this.tags;
    }
}
