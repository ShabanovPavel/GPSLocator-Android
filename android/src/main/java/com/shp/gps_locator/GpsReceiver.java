package com.shp.gps_locator;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.concurrent.TimeUnit;

public class GpsReceiver implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    public static int intervalGPS=10000;
    public static int fastestIntervalGPS=5000;
    public static int smallestDisplacementGPS=1;

    private GoogleApiClient mGoogleApiClient;
    private LocationListener mLocationListener;
    private long mStartTime;
    private long mStartTimeNano;
    private Context mContext;
    private boolean mConnected;

    GpsReceiver(Context context) {
        mContext = context;
    }

    public void connect() {
        if (mConnected)
            return;
        mStartTime = System.currentTimeMillis();
        mStartTimeNano = SystemClock.elapsedRealtimeNanos();
        int res = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext);
        if (res == ConnectionResult.SUCCESS) {
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            mGoogleApiClient.connect();
        } else {
            //requestStdLocationUpdates();
        }
        mConnected = true;
    }

    public void disconnect() {
        if (!mConnected)
            return;
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        if (mLocationListener != null) {
            LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(mContext,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            locationManager.removeUpdates(mLocationListener);
        }

        // only stop if it's connected, otherwise we crash
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        mConnected = false;
    }

    private void requestStdLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                postLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, mLocationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 0, mLocationListener);
        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastLocation != null) {
            postLocation(lastLocation);
        }
    }

    private long getTimestamp(final Location l) {
        long locTime = l.getElapsedRealtimeNanos();
        return mStartTime + TimeUnit.NANOSECONDS.toMillis(locTime - mStartTimeNano);
    }

    private void postLocation(final Location l) {
        try {
            NetworkService.postLocation(
                    getTimestamp(l),
                    l.getLongitude(),
                    l.getLatitude(),
                    l.getAltitude(),
                    l.getAccuracy(),
                    l.getSpeed());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setSmallestDisplacement(smallestDisplacementGPS)
                .setInterval(intervalGPS)
                .setFastestInterval(fastestIntervalGPS);
//мб выпилить
        if (ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        requestStdLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        postLocation(location);
    }
}