package com.odauday.utils;

import com.odauday.data.remote.property.model.GeoLocation;
import java.util.Random;

/**
 * Created by infamouSs on 4/11/18.
 */

public class NumberUtils {
    
    
    public static double distanceBetween2Location(GeoLocation a, GeoLocation b) {
        android.location.Location pointA = a.toAndroidLocation();
        android.location.Location pointB = b.toAndroidLocation();
        
        return pointA.distanceTo(pointB) * 0.001;
    }
    
    public static int random(int max, int min) {
        return new Random().nextInt(max) + min;
    }
    
    public static String randomCodeProperty(String propertyId) {
        return new StringBuilder()
            .append(propertyId.substring(1, 5))
            .append("#")
            .append(random(1000, 9999))
            .toString();
    }
    
    public static String meterToKilometer(double meter) {
        double kilometer = meter * 0.001;
        return String.valueOf(round(kilometer, 1)) + " km";
    }
    
    private static double round(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }
    
    public static int getHeightWithAspectRatio(int viewWidth, float ratio, float scaleFactor) {
        return (int) ((((float) viewWidth) / ratio) * scaleFactor);
    }
}
