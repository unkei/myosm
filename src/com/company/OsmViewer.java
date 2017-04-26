package com.company;

import com.company.data.GeoCoordinate;
import com.company.data.Osm;
import com.company.data.OsmNode;
import com.company.data.OsmWay;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.Animator;

import java.util.HashMap;

import static com.jogamp.opengl.GL2.*;

/**
 * Created by unkei on 2017/04/25.
 */
public class OsmViewer implements GLEventListener {

    Osm osm;
    int w = 300;
    int h = 300;
    double scale = 1;
    GeoCoordinate center;

    public OsmViewer() {
        this(null);
    }

    public OsmViewer(Osm osm) {
        center = new GeoCoordinate(0, 0);
        setOsm(osm);

        GLCapabilities caps = new GLCapabilities(GLProfile.get(GLProfile.GL2));
        GLWindow glWindow = GLWindow.create(caps);
        glWindow.setTitle("First demo (Newt)");
        glWindow.setSize(w, h);
        glWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowDestroyed(WindowEvent windowEvent) {
                super.windowDestroyed(windowEvent);
                System.exit(0);
            }
        });
        glWindow.addGLEventListener(this);
        Animator animator = new Animator();
        animator.add(glWindow);
        animator.start();
        glWindow.setVisible(true);
    }

    public void setOsm(Osm osm) {
        this.osm = osm;
//        if (osm.nodes.size() > 0) {
//            double maxLat, maxLon, minLat, minLon;
//            OsmNode firstNode = osm.nodes.get(0);
//            maxLat = minLat = firstNode.getGeoCoordinate().latitude;
//            maxLon = minLon = firstNode.getGeoCoordinate().longitude;
//            for (OsmNode node : osm.nodes) {
//                double lat = node.getGeoCoordinate().latitude;
//                double lon = node.getGeoCoordinate().longitude;
//                maxLat = Math.max(maxLat, lat);
//                maxLon = Math.max(maxLon, lon);
//                minLat = Math.min(minLat, lat);
//                minLon = Math.min(minLon, lon);
//            }
//            scale = 2f / (((maxLat - minLat) > (maxLon - minLon))? (maxLat - minLat): (maxLon - minLon));
//            center = new GeoCoordinate((maxLat + minLat) / 2, (maxLon + minLon) / 2);
//        }
        double maxLat, maxLon, minLat, minLon;
        minLat = osm.bounds.minLat;
        minLon = osm.bounds.minLon;
        maxLat = osm.bounds.maxLat;
        maxLon = osm.bounds.maxLon;
        scale = 2f / (((maxLat - minLat) > (maxLon - minLon))? (maxLat - minLat): (maxLon - minLon));
        center = new GeoCoordinate((maxLat + minLat) / 2, (maxLon + minLon) / 2);
    }

    GeoCoordinate convToLocal(GeoCoordinate global) {
        return new GeoCoordinate((global.latitude - center.latitude) * scale, (global.longitude - center.longitude) * scale);
    }

    @Override
    public void init(GLAutoDrawable glAutoDrawable) {
        System.out.printf("init\n");
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {
        System.out.printf("dispose\n");
    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();
        gl.glClearColor(0, 0, 0, 1);
        gl.glClear(GL_COLOR_BUFFER_BIT);

        for (OsmWay way: osm.ways) {

            HashMap<String, String> tags = way.getTags();

            String building = tags.get("building");
            String landuse = tags.get("landuse");
            String route = tags.get("route");
            if (building != null && !building.isEmpty()) {
                gl.glColor4f(0f, 1f, 1f, 1f);
                gl.glLineWidth(1f);
                gl.glLineStipple(1, (short)0xFFFF);
                gl.glBegin(GL_POLYGON);
            } else if ("forest".equals(landuse) || "grass".equals(landuse)) {
                gl.glColor4f(0f, 1f, 0f, 1f);
                gl.glLineWidth(1f);
                gl.glLineStipple(1, (short)0xFFFF);
//                gl.glBegin(GL_POLYGON);
                gl.glBegin(GL_LINE_STRIP);
            } else if ("road".equals(route)) {
                gl.glColor4f(0f, 0f, 1f, 1f);
                gl.glLineWidth(5f);
                gl.glLineStipple(1, (short)0xFFFF);
                gl.glBegin(GL_LINE_STRIP);
            } else if ("train".equals(route)) {
                gl.glColor4f(1f, 1f, 1f, 1f);
                gl.glLineWidth(5f);
                gl.glLineStipple(1, (short)0xF0F0);
                gl.glBegin(GL_LINE_STRIP);
            } else {
                gl.glColor4f(0.5f, 0, 0.5f, 1f);
                gl.glLineWidth(1f);
                gl.glLineStipple(1, (short)0xFFFF);
                gl.glBegin(GL_LINE_STRIP);
            }
            for (OsmNode node : way.getOsmNodes()) {
                GeoCoordinate local = convToLocal(node.getGeoCoordinate());
                gl.glVertex2f((float) local.longitude, (float) local.latitude);
            }
            gl.glEnd();
        }

        gl.glBegin(GL_POINTS);
        for (OsmNode node: osm.nodes) {
            HashMap<String, String> tags = node.getTags();

            if (tags.size() == 0) continue;

            String v = tags.get("highway");
            if ("bus_stop".equals(v)) {
                gl.glColor4f(1f, 1f, 1f, 1f);
            } else if ("traffic_signals".equals(v)) {
                gl.glColor4f(1f, 1f, 0f, 1f);
            } else {
                gl.glColor4f(1f, 0, 0, 1f);
            }
            GeoCoordinate local = convToLocal(node.getGeoCoordinate());
            gl.glVertex2f((float)local.longitude, (float)local.latitude);
        }
        gl.glEnd();

//        gl.glColor4f(1f, 1f, 1f, 1f);
//        gl.glBegin(GL_LINE_LOOP);
//        float rw = ((float)w - 30) / w;
//        float rh = ((float)h - 30) / h;
//        gl.glVertex2f(-rw, -rh);
//        gl.glVertex2f( rw, -rh);
//        gl.glVertex2f( rw,  rh);
//        gl.glVertex2f(-rw,  rh);
//        gl.glEnd();
    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int w, int h) {
        System.out.printf("reshape(%d, %d, %d, %d)\n", x, y, w, h);
        this.w = w;
        this.h = h;
    }
}
