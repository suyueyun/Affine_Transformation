/**
 * Created by suyueyun on 2017-10-25.
 */

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.awt.*;
import javax.vecmath.*;

public class Shape {
    ArrayList<Point2d> points;
    // shape's transform
    // quick hack, get and set would be better
    float scale = 1.0f;
    int rotate = 0;
    // some optimization to cache points for drawing
    Boolean pointsChanged = false; // dirty bit
    int[] xpoints, ypoints;
    int npoints = 0;
    Color colour = Color.BLACK;
    float thickness = 2.0f;

    Color hightlightClour = Color.yellow;
    float hightlightThinkness = 6.0f;

    boolean isHightlighted = false;

    int transx = 0;
    int transy = 0;

    int offsetx = 0;
    int offsety = 0;

    public void setTrans(int x, int y){
        transx = x;
        transy = y;
    }

    public void clearPoints() {
        points = new ArrayList<Point2d>();
        pointsChanged = true;
    }

    public void setOffset(){
        offsetx += transx;
        offsety += transy;
        transx = 0;
        transy = 0;
    }

    public void addPoint(Point2d p){
        if (points == null) clearPoints();
        points.add(p);
        pointsChanged = true;
    }

    public void addPoint(double x, double y) {
        addPoint(new Point2d(x, y));
    }

    public boolean getisHightlighted(){
        return isHightlighted;
    }

    public void setIsHightlight(boolean val){
        isHightlighted = val;
    }

    void cachePointsArray() {
        xpoints = new int[points.size()];
        ypoints = new int[points.size()];
        for (int i=0; i < points.size(); i++) {
            xpoints[i] = (int)points.get(i).x;
            ypoints[i] = (int)points.get(i).y;

        }
        npoints = points.size();
        calculateCenter(xpoints,ypoints,npoints); /// calculate the center of shape
        pointsChanged = false;
    }

    public void setScale(float n){
        scale = n;
    }

    public void setRotate(int r){
        rotate = r;
    }

    int[] center = new int[2];


    public void draw(Graphics2D g2) {
        if (points == null) return;
        if (pointsChanged) cachePointsArray();
        // save the current g2 transform matrix

        AffineTransform M = g2.getTransform();
        g2.translate(offsetx + transx,offsety + transy);
        g2.translate(center[0],center[1]);
        g2.scale(scale,scale);
        g2.rotate(Math.toRadians(rotate));
        g2.translate(-center[0],-center[1]);
        if(isHightlighted){
            g2.setStroke(new BasicStroke(hightlightThinkness/scale));
            g2.setColor(hightlightClour);
            g2.drawPolyline(xpoints, ypoints, npoints);
        }
        g2.setStroke(new BasicStroke(thickness/scale));
        // call drawing functions
        g2.setColor(colour);
        g2.drawPolyline(xpoints, ypoints, npoints);
        g2.setTransform(M);
    }

    public void calculateCenter(int xps[], int yps[], int npoints) {
        int boundsMinX = Integer.MAX_VALUE;
        int boundsMinY = Integer.MAX_VALUE;
        int boundsMaxX = Integer.MIN_VALUE;
        int boundsMaxY = Integer.MIN_VALUE;
        for (int i = 0; i < npoints; i++) {
            int x = xps[i];
            boundsMinX = Math.min(boundsMinX, x);
            boundsMaxX = Math.max(boundsMaxX, x);
            int y = yps[i];
            boundsMinY = Math.min(boundsMinY, y);
            boundsMaxY = Math.max(boundsMaxY, y);
        }
        center[0] = boundsMinX + (boundsMaxX - boundsMinX)/2; // x for center point;
        center[1] = boundsMinY + (boundsMaxY - boundsMinY)/2; // y for center point;
    }

    //reference: closestPoint() copied from CS349 demo code java/6-graphics/ClosestPoint.java
    static Point2d closestPoint(Point2d M, Point2d P0, Point2d P1) {
        Vector2d v = new Vector2d();
        v.sub(P1,P0); // v = P2 - P1

        // early out if line is less than 1 pixel long
        if (v.lengthSquared() < 0.5)
            return P0;

        Vector2d u = new Vector2d();
        u.sub(M,P0); // u = M - P1
        // scalar of vector projection ...
        double s = u.dot(v) / v.dot(v);

        // find point for constrained line segment
        if (s < 0)
            return P0;
        else if (s > 1)
            return P1;
        else {
            Point2d I = P0;
            Vector2d w = new Vector2d();
            w.scale(s, v); // w = s * v
            I.add(w); // I = P1 + w
            return I;
        }
    }

    private Point2d getTransformed(int x, int y){
        int actualx = x - center[0];
        int actualy = y - center[1];
        actualx = (int) (actualx * scale);
        actualy = (int) (actualy * scale);
        int tempx = actualx;
        int tempy = actualy;
        double r = Math.toRadians(rotate);
        actualx = (int)(Math.cos(r) * tempx - Math.sin(r) * tempy);
        actualy = (int)(Math.sin(r) * tempx + Math.cos(r) * tempy);
        actualx = actualx + center[0] + offsetx;
        actualy = actualy + center[1] + offsety;
        return new Point2d(actualx,actualy);
    }

    public boolean hittest(double x, double y)
    {
        if (points != null) {
            for(int i = 0; i < npoints - 1; i++){
                Point2d p1 = getTransformed(xpoints[i],ypoints[i]);
                Point2d p2 = getTransformed(xpoints[i+1],ypoints[i+1]);
                Point2d test = new Point2d(x,y);
                Point2d closest = closestPoint(test,p1,p2);
                double test_distance = closest.distance(test);
                if(test_distance <= (double)5){
                    return true;
                }
            }
        }
        return false;
    }

}
