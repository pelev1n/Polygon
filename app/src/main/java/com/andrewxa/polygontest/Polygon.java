package com.andrewxa.polygontest;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

public class Polygon {
    private Double minLng;
    private Double minLat;
    private Double maxLng;
    private Double maxLat;

    private List<LatLng> polygonPoints = new ArrayList<>();
    private PolygonOptions options;

    Polygon(List<LatLng> points) {
        polygonPoints.addAll(points);
        options = new PolygonOptions().strokeColor(0xFF25090B).strokeWidth(5);
        options.addAll(polygonPoints);
    }

    public PolygonOptions getOptions() {
        return this.options;
    }

    public List<LatLng> getPolygonPoints() {
        return this.polygonPoints;
    }


    // Checks if a clicked point lays inside a polygon
    // https://stackoverflow.com/questions/217578/how-can-i-determine-whether-a-2d-point-is-within-a-polygon
    public boolean isPointInsidePolygon(LatLng p) {

        if (polygonPoints.size() < 3) {
            return false;
        }

        if (minLat == null) {

            minLng = polygonPoints.get(0).longitude;
            maxLng = polygonPoints.get(0).longitude;
            minLat = polygonPoints.get(0).latitude;
            maxLat = polygonPoints.get(0).latitude;

            for (int i = 1; i < polygonPoints.size(); i++) {
                LatLng point = polygonPoints.get(i);

                minLng = Math.min(point.longitude, minLng);
                maxLng = Math.max(point.longitude, maxLng);
                minLat = Math.min(point.latitude, minLat);
                maxLat = Math.max(point.latitude, maxLat);
            }
        }

        if (p.longitude < minLng || p.longitude > maxLng || p.latitude < minLat || p.latitude > maxLat) {
            return false;
        }
        boolean isInside = false;
        for (int i = 0, j = polygonPoints.size() - 1; i < polygonPoints.size(); j = i++) {
            if ((polygonPoints.get(i).latitude > p.latitude) != (polygonPoints.get(j).latitude > p.latitude) &&
                    p.longitude < (polygonPoints.get(j).longitude - polygonPoints.get(i).longitude) * (p.latitude - polygonPoints.get(i).latitude) / (polygonPoints.get(j).latitude - polygonPoints.get(i).latitude) + polygonPoints.get(i).longitude) {

                isInside = !isInside;
            }
        }
        return isInside;
    }

    // Finds the nearest point on polygon from clicked-given point(clickedPoint)
    // http://stackoverflow.com/a/36105498/6336750
    public LatLng findNearestPoint(LatLng clickedPoint) {
        double distance = -1;

        LatLng minimumDistancePoint = clickedPoint;

        if (clickedPoint == null || polygonPoints == null) {
            return minimumDistancePoint;
        }

        for (int i = 0; i < polygonPoints.size(); i++) {
            LatLng point = polygonPoints.get(i);

            int segmentPoint = i + 1;
            if (segmentPoint >= polygonPoints.size()) {
                segmentPoint = 0;
            }

            double currentDistance = PolyUtil.distanceToLine(clickedPoint, point, polygonPoints.get(segmentPoint));
            if (distance == -1 || currentDistance < distance) {
                distance = currentDistance;

                minimumDistancePoint = Util.findNearestPoint(clickedPoint, point, polygonPoints.get(segmentPoint));
            }

        }
        return minimumDistancePoint;
    }
}
