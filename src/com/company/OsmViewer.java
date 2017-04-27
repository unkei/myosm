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
    double scaleLat = 1;
    double scaleLon = 1;
    GeoCoordinate center;
    GeoCoordinate min;
    GeoCoordinate max;

    public OsmViewer() {
        this(null);
    }

    public OsmViewer(Osm osm) {
        center = new GeoCoordinate(0, 0);
        min = new GeoCoordinate(0, 0);
        max = new GeoCoordinate(0, 0);
        setOsm(osm);

        GLCapabilities caps = new GLCapabilities(GLProfile.get(GLProfile.GL2));
        GLWindow glWindow = GLWindow.create(caps);
        glWindow.setTitle("First demo (Newt)");
        glWindow.setSize(300, 300);
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
        double maxLat, maxLon, minLat, minLon;
        minLat = osm.bounds.minLat;
        minLon = osm.bounds.minLon;
        maxLat = osm.bounds.maxLat;
        maxLon = osm.bounds.maxLon;
        scaleLat = 2f / (maxLat - minLat);
        scaleLon = 2f / (maxLon - minLon);
        center = new GeoCoordinate((maxLat + minLat) / 2, (maxLon + minLon) / 2);
    }

    GeoCoordinate convToLocal(GeoCoordinate global) {
        return new GeoCoordinate((global.latitude - center.latitude) * scaleLat, (global.longitude - center.longitude) * scaleLon);
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
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

//        gl.glEnable(GL_DEPTH_TEST);
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();
        // TODO: Enable anti-aliasing

        // rotating animation
        float tick = ((float) (System.currentTimeMillis() % 64000) / 64000); // 64000ms cycle
        float angle = 360f * tick;
        float rad = 2f * (float) Math.PI * tick;
        gl.glRotatef(angle, 0f, 0f, 1f);
        gl.glTranslatef(0.5f * (float) Math.cos(rad), 0.5f * (float) Math.sin(rad), 0f);

        // render elements
        for (OsmWay way : osm.ways) {

            HashMap<String, String> tags = way.getTags();

            String building = tags.get("building");
            String building_part = tags.get("building:part");
            String landuse = tags.get("landuse");
            String natural = tags.get("natural");
            String route = tags.get("route");
            String highway = tags.get("highway");

            if (building != null || building_part != null) {
                gl.glColor4f(0f, 1f, 1f, 1f);
                gl.glLineWidth(1f);
                gl.glLineStipple(1, (short) 0xFFFF);
//                gl.glBegin(GL_POLYGON);
                gl.glBegin(GL_LINE_STRIP);
            } else if ("forest".equals(landuse) || "grass".equals(landuse) || "wood".equals(natural)) {
                gl.glColor4f(0f, 1f, 0f, 1f);
                gl.glLineWidth(1f);
                gl.glLineStipple(1, (short) 0xFFFF);
//                gl.glBegin(GL_POLYGON);
                gl.glBegin(GL_LINE_STRIP);
            } else if ("water".equals(natural)) {
                gl.glColor4f(0f, 0f, 1f, 1f);
                gl.glLineWidth(1f);
                gl.glLineStipple(1, (short) 0xFFFF);
//                gl.glBegin(GL_POLYGON);
                gl.glBegin(GL_LINE_STRIP);
            } else if ("pedestrian".equals(highway)) {
                gl.glColor4f(0f, 0.5f, 0f, 1f);
                gl.glLineWidth(2f);
                gl.glLineStipple(1, (short) 0xFFFF);
                gl.glBegin(GL_LINE_STRIP);
            } else if ("motorway".equals(highway)) {
                gl.glColor4f(1f, 0.5f, 0f, 1f);
                gl.glLineWidth(5f);
                gl.glLineStipple(1, (short) 0xFFFF);
                gl.glBegin(GL_LINE_STRIP);
            } else if (highway != null) {
                gl.glColor4f(1f, 1f, 1f, 1f);
                gl.glLineWidth(3f);
                gl.glLineStipple(1, (short) 0xFFFF);
                gl.glBegin(GL_LINE_STRIP);
            } else if ("road".equals(route)) {
                gl.glColor4f(1f, 1f, 1f, 1f);
                gl.glLineWidth(1f);
                gl.glLineStipple(1, (short) 0xFFFF);
                gl.glBegin(GL_LINE_STRIP);
            } else if ("train".equals(route)) {
                gl.glColor4f(1f, 1f, 1f, 1f);
                gl.glLineWidth(5f);
                gl.glLineStipple(1, (short) 0xF0F0);
                gl.glBegin(GL_LINE_STRIP);
            } else {
                gl.glColor4f(0.5f, 0, 0.5f, 1f);
                gl.glLineWidth(1f);
                gl.glLineStipple(1, (short) 0xFFFF);
                gl.glBegin(GL_LINE_STRIP);
            }
            for (OsmNode node : way.getOsmNodes()) {
                GeoCoordinate local = convToLocal(node.getGeoCoordinate());
                gl.glVertex2f((float) local.longitude, (float) local.latitude);
            }
            gl.glEnd();
        }

        for (OsmNode node : osm.nodes) {
            HashMap<String, String> tags = node.getTags();

            if (tags.size() == 0) continue;

            String highway = tags.get("highway");
            String natural = tags.get("natural");
            if ("bus_stop".equals(highway)) {
                gl.glPointSize(3f);
                gl.glBegin(GL_POINTS);
                gl.glColor4f(1f, 1f, 1f, 0.7f);
            } else if ("traffic_signals".equals(highway)) {
                gl.glPointSize(3f);
                gl.glBegin(GL_POINTS);
                gl.glColor4f(1f, 1f, 0f, 0.7f);
            } else if ("tree".equals(natural)) {
                gl.glPointSize(3f);
                gl.glBegin(GL_POINTS);
                gl.glColor4f(0f, 1f, 0f, 0.7f);
            } else {
                gl.glPointSize(3f);
                gl.glBegin(GL_POINTS);
                gl.glColor4f(1f, 0, 0, 0.7f);
            }
            GeoCoordinate local = convToLocal(node.getGeoCoordinate());
            gl.glVertex2f((float) local.longitude, (float) local.latitude);
            gl.glEnd();
        }

//        gl.glDisable(GL_DEPTH_TEST);
        gl.glDisable(GL_BLEND);
    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int w, int h) {
        System.out.printf("reshape(%d, %d, %d, %d)\n", x, y, w, h);

        GL2 gl = glAutoDrawable.getGL().getGL2();
        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glViewport(x, y, w, h);
//        gl.glOrtho((float)-w/300, (float)w/300, (float)-h/300, (float)h/300, -1f, 1f);
//        gl.glOrtho(-1f, 1f, (float)-h/w, (float)h/w, -1f, 1f);
        gl.glOrtho((float) -w / h, (float) w / h, -1f, 1f, -1f, 1f);
    }
}
