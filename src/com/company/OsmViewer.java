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

        gl.glColor4f(0, 0, 1f, 1f);
        for (OsmWay way: osm.ways) {
            gl.glBegin(GL_LINE_STRIP);
            for (OsmNode node : way.getOsmNodes()) {
                GeoCoordinate local = convToLocal(node.getGeoCoordinate());
                gl.glVertex2f((float) local.longitude, (float) local.latitude);
            }
            gl.glEnd();
        }

        gl.glColor4f(1f, 0, 0, 1f);
        gl.glBegin(GL_POINTS);
        for (OsmNode node: osm.nodes) {
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
