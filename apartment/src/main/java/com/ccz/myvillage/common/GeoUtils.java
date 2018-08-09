package com.ccz.myvillage.common;

public class GeoUtils {
    public static double toDMS(double wgs84) {
        int d = (int)wgs84;
        int m = (int) ((wgs84 - (double)d)* 60f);
        double s =  ((wgs84 - (double)d) * 60f - (double)m) * 60f;
        String dms = String.format("%d.%02d%02d", d, m, (int)(s * 10000f));
        return Double.parseDouble(dms);
    }

    public static double toDegree(double dms) {
        String svalue = dms +"";
        int idx = svalue.indexOf(".");

        int degree = Integer.parseInt(svalue.substring(0, idx));
        idx += 1;	//point
        int minute = Integer.parseInt(svalue.substring(idx, idx+2));
        idx += 2;
        int second = Integer.parseInt(svalue.substring(idx, idx+2));
        idx += 2;

        String remain = svalue.substring(idx, svalue.length());
        double allSecond = Double.parseDouble(String.format("%d.%s", second, remain));

        double belowDigit = ((minute * 60f) + allSecond) / 3600;
        return degree + belowDigit;
    }
}
