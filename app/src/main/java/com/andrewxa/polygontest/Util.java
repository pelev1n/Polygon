package com.andrewxa.polygontest;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

public class Util {

    // Computes the nearest point on line segment(start to end) from a given point(p).
    //http://stackoverflow.com/a/36105498/6336750
    public static LatLng findNearestPoint(final LatLng p, final LatLng start, final LatLng end) {
        if (start.equals(end)) {
            return start;
        }

        final double s0lat = Math.toRadians(p.latitude);
        final double s0lng = Math.toRadians(p.longitude);
        final double s1lat = Math.toRadians(start.latitude);
        final double s1lng = Math.toRadians(start.longitude);
        final double s2lat = Math.toRadians(end.latitude);
        final double s2lng = Math.toRadians(end.longitude);

        double s2s1lat = s2lat - s1lat;
        double s2s1lng = s2lng - s1lng;

        final double u = ((s0lat - s1lat) * s2s1lat + (s0lng - s1lng) * s2s1lng)
                / (s2s1lat * s2s1lat + s2s1lng * s2s1lng);
        if (u <= 0) {
            return start;
        }
        if (u >= 1) {
            return end;
        }
        return new LatLng(start.latitude + (u * (end.latitude - start.latitude)),
                start.longitude + (u * (end.longitude - start.longitude)));
    }

    //Calculates distance between points
    public static String getDistance(LatLng startPoint, LatLng endPoint) {
        double distance = SphericalUtil.computeDistanceBetween(startPoint, endPoint);
        return formatNumber(distance);
    }

    // Rounds double to 2 numbers after dot
    public static String formatNumber(double distance) {
        String unit = "m";
        if(distance > 1000) {
            distance /= 1000;
            unit = "km";
        } else if (distance < 1) {
            distance *= 1000;
            unit = "mm";
        }
        return String.format("%.2f%s", distance, unit);
    }
}
