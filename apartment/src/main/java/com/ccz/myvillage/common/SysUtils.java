package com.ccz.myvillage.common;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Size;

import java.util.List;
import java.util.UUID;

public class SysUtils {
    static public final int REQUEST_LOCATION = 1;

    public static String getUuid() {
        return "apt:" + UUID.randomUUID().toString();
    }

    public static boolean isAboveKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static Size getScreenPixels(Activity act, float rate) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = (int)((float)displayMetrics.heightPixels * rate);
        int width = (int)((float)displayMetrics.widthPixels * rate);
        return new Size(width, height);
    }

    public static Size getScreenDp(Context ctx, float rate) {
        Configuration configuration = ctx.getResources().getConfiguration();
        int screenWidthDp = (int)((float)configuration.screenWidthDp * rate);
        int screenHeightDp = (int)((float)configuration.screenHeightDp * rate);
        int smallestScreenWidthDp = configuration.smallestScreenWidthDp;
        return new Size(screenWidthDp, screenHeightDp);
    }

    public void setGpsListener(Activity act) { //for example
        LocationManager locationManager = (LocationManager) act.getSystemService(Context.LOCATION_SERVICE);

        // GPS 프로바이더 사용가능여부
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 네트워크 프로바이더 사용가능여부
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        System.out.println("isGpsEnabled: " + isGPSEnabled + ", isNetworkEnabled: " + isNetworkEnabled);
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();

                System.out.println("latitude: " + lat + ", longitude: " + lng);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                System.out.println("onStatusChanged");
            }

            public void onProviderEnabled(String provider) {
                System.out.println("onProviderEnabled");
            }

            public void onProviderDisabled(String provider) {
                System.out.println("onProviderDisabled");
            }
        };

        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(act, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(act, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        // 수동으로 위치 구하기
        String locationProvider = LocationManager.GPS_PROVIDER;
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        if (lastKnownLocation != null) {
            double lng = lastKnownLocation.getLongitude();
            double lat = lastKnownLocation.getLatitude();
            System.out.println("longtitude=" + lng + ", latitude=" + lat);
        }
    }



}
