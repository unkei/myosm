package com.company.data;

import java.util.HashMap;
import java.util.List;

/**
 * Created by unkei on 2017/04/26.
 */
public class OsmElement {
    String id;
    List<? extends OsmElement> children;
    HashMap<String, String> tags;

    public OsmElement(String id, List<? extends OsmElement> children, HashMap<String, String> tags) {
        this.id = id;
        this.children = children;
        this.tags = tags;
    }

    public String getId() {
        return this.id;
    }

    public List<? extends OsmElement> getChildren() {
        return this.children;
    }

    public HashMap<String, String> getTags() {
        return this.tags;
    }

    public static OsmElement getOsmElementById(List<? extends OsmElement> osmElemnts, String id) {
        if (id != null && !id.isEmpty()) {
            for (OsmElement osmElement : osmElemnts) {
                if (id.equals(osmElement.getId())) {
                    return osmElement;
                }
            }
        }
        return null;
    }
}
