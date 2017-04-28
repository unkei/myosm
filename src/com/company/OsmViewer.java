package com.company;

import com.company.data.GeoCoordinate;
import com.company.data.Osm;
import com.company.data.OsmNode;
import com.company.data.OsmWay;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUtessellator;
import com.jogamp.opengl.glu.GLUtessellatorCallback;
import com.jogamp.opengl.util.Animator;

import java.util.HashMap;

import static com.jogamp.opengl.GL2.*;

/**
 * Created by unkei on 2017/04/25.
 */
public class OsmViewer implements GLEventListener, KeyListener {

    Osm osm;
    double scaleLat = 1;
    double scaleLon = 1;
    GeoCoordinate center;
    GeoCoordinate min;
    GeoCoordinate max;

    boolean wireframe = false;

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
        glWindow.addKeyListener(this);
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
        GLU glu = new GLU();
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

            boolean isPolygon = false;
            boolean isClosed = way.isClosed();
            float r, g, b, a;
            float lw;
            short ls;

            if (building != null || building_part != null) {
                r=0f; g=1f; b=1f; a=1f;
                lw=1f;
                ls=(short)0xFFFF;
                isPolygon = !wireframe && isClosed;
            } else if ("forest".equals(landuse) || "grass".equals(landuse) || "wood".equals(natural)) {
                r=0f; g=1f; b=0f; a=1f;
                lw=1f;
                ls=(short)0xFFFF;
                isPolygon = !wireframe && isClosed;
            } else if ("water".equals(natural)) {
                r=0f; g=0f; b=1f; a=1f;
                lw=1f;
                ls=(short)0xFFFF;
                isPolygon = !wireframe && isClosed;
            } else if ("pedestrian".equals(highway)) {
                r=0f; g=0.5f; b=0f; a=1f;
                lw=2f;
                ls=(short)0xFFFF;
            } else if ("motorway".equals(highway)) {
                r=1f; g=0.5f; b=0f; a=1f;
                lw=5f;
                ls=(short)0xFFFF;
            } else if (highway != null) {
                r=1f; g=1f; b=1f; a=1f;
                lw=3f;
                ls=(short)0xFFFF;
            } else if ("road".equals(route)) {
                r=1f; g=1f; b=1f; a=1f;
                lw=1f;
                ls=(short)0xFFFF;
            } else if ("train".equals(route)) {
                r=1f; g=1f; b=1f; a=1f;
                lw=5f;
                ls=(short)0xF0F0;
            } else {
                r=0.5f; g=0f; b=0.5f; a=1f;
                lw=1f;
                ls=(short)0xFFFF;
            }
            if (isPolygon) {
                r *= 0.5f;
                g *= 0.5f;
                b *= 0.5f;
                gl.glColor4f(r, g, b, a);
                gl.glLineWidth(lw);
                gl.glLineStipple(1, ls);
                GLUtessellator tobj = glu.gluNewTess();
                tessellCallBack tessCallback = new tessellCallBack(gl, glu);
                glu.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, tessCallback);
                glu.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, tessCallback);
                glu.gluTessCallback(tobj, GLU.GLU_TESS_END, tessCallback);
                glu.gluTessCallback(tobj, GLU.GLU_TESS_ERROR, tessCallback);
                glu.gluTessCallback(tobj, GLU.GLU_TESS_COMBINE, tessCallback);
                glu.gluTessBeginPolygon(tobj, null);
                glu.gluTessBeginContour(tobj);
                for (OsmNode node : way.getOsmNodes()) {
                    GeoCoordinate local = convToLocal(node.getGeoCoordinate());
//                    gl.glVertex2f((float) local.longitude, (float) local.latitude);
                    double coord[] = {local.longitude, local.latitude, 0, r, g, b, a};
                    glu.gluTessVertex(tobj, coord, 0, coord);
                }
                glu.gluTessEndContour(tobj);
                glu.gluTessEndPolygon(tobj);
                glu.gluDeleteTess(tobj);
            } else {
                gl.glColor4f(r, g, b, a);
                gl.glLineWidth(lw);
                gl.glLineStipple(1, ls);
                gl.glBegin(GL_LINE_STRIP);
                for (OsmNode node : way.getOsmNodes()) {
                    GeoCoordinate local = convToLocal(node.getGeoCoordinate());
                    gl.glVertex2f((float) local.longitude, (float) local.latitude);
                }
                gl.glEnd();
            }
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

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;
            case KeyEvent.VK_SPACE:
                wireframe = !wireframe;
                break;
            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {

    }

    class tessellCallBack implements GLUtessellatorCallback {
        private GL2 gl;
        private GLU glu;

        public tessellCallBack(GL2 gl, GLU glu) {
            this.gl = gl;
            this.glu = glu;
        }

        public void begin(int type) {
            gl.glBegin(type);
        }

        public void end() {
            gl.glEnd();
        }

        public void vertex(Object vertexData) {
            double[] pointer;
            if (vertexData instanceof double[]) {
                pointer = (double[]) vertexData;
                if (pointer.length == 6) gl.glColor3dv(pointer, 3);
                gl.glVertex3dv(pointer, 0);
            }

        }

        public void vertexData(Object vertexData, Object polygonData) {
        }

        /*
         * combineCallback is used to create a new vertex when edges intersect.
         * coordinate location is trivial to calculate, but weight[4] may be used to
         * average color, normal, or texture coordinate data. In this program, color
         * is weighted.
         */
        public void combine(double[] coords, Object[] data, //
                            float[] weight, Object[] outData) {
            double[] vertex = new double[6];
            int i;

            vertex[0] = coords[0];
            vertex[1] = coords[1];
            vertex[2] = coords[2];
            for (i = 3; i < 6/* 7OutOfBounds from C! */; i++) {
//                vertex[i] = weight[0] //
//                        * ((double[]) data[0])[i] + weight[1]
//                        * ((double[]) data[1])[i] + weight[2]
//                        * ((double[]) data[2])[i] + weight[3]
//                        * ((double[]) data[3])[i];
                vertex[i] = 0;
                for (int j=0; j<data.length; j++) {
                    double[] d = (double[])data[j];
                    if (d != null) {
                        vertex[i] += weight[j] * d[i];
                    }
                }
            }
            outData[0] = vertex;
        }

        public void combineData(double[] coords, Object[] data, //
                                float[] weight, Object[] outData, Object polygonData) {
        }

        public void error(int errnum) {
            String estring;

            estring = glu.gluErrorString(errnum);
            System.err.println("Tessellation Error: " + estring);
            System.exit(0);
        }

        public void beginData(int type, Object polygonData) {
        }

        public void endData(Object polygonData) {
        }

        public void edgeFlag(boolean boundaryEdge) {
        }

        public void edgeFlagData(boolean boundaryEdge, Object polygonData) {
        }

        public void errorData(int errnum, Object polygonData) {
        }
    }
}
