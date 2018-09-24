package com.shp.gps_locator;
import android.content.Intent;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.IllegalViewOperationException;


public class GPSLocator extends ReactContextBaseJavaModule {

    private Intent currentIntent;
    private static final String E_LAYOUT_ERROR = "E_LAYOUT_ERROR";

    public GPSLocator(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "GPSLocator";
    }

    @ReactMethod
    public void runGPS(String ID, String URl, Promise promise){
        try {
            NetworkService.ID_USER=ID;
            NetworkService.puthURL=URl;
            currentIntent = new Intent(getReactApplicationContext(),NetworkService.class);
            getReactApplicationContext().startService(currentIntent);
            WritableMap map = Arguments.createMap();
            map.putString("response","run");
            promise.resolve(map);
        }catch(IllegalViewOperationException e){
            promise.reject(E_LAYOUT_ERROR, e);
        }
    }


    @ReactMethod
    public void stopGPS(Promise promise){
        try {
            getReactApplicationContext().stopService(currentIntent);
            WritableMap map = Arguments.createMap();
            map.putString("response","stop");
            promise.resolve(map);
        }catch(IllegalViewOperationException e){
            promise.reject(E_LAYOUT_ERROR, e);
        }
    }

    @ReactMethod
    public void setInterval(int interval,Promise promise){
        try {
            GpsReceiver.intervalGPS=interval;
            WritableMap map = Arguments.createMap();
            map.putString("response","true");
            promise.resolve(map);
        }catch(IllegalViewOperationException e){
            promise.reject(E_LAYOUT_ERROR, e);
        }
    }

    @ReactMethod
    public void setFastestInterval(int fastestInterval,Promise promise){
        try {
            GpsReceiver.fastestIntervalGPS=fastestInterval;
            WritableMap map = Arguments.createMap();
            map.putString("response","true");
            promise.resolve(map);
        }catch(IllegalViewOperationException e){
            promise.reject(E_LAYOUT_ERROR, e);
        }
    }

    @ReactMethod
    public void setSmallestDisplacement(int smallestDisplacement,Promise promise){
        try {
            GpsReceiver.smallestDisplacementGPS=smallestDisplacement;
            WritableMap map = Arguments.createMap();
            map.putString("response","true");
            promise.resolve(map);
        }catch(IllegalViewOperationException e){
            promise.reject(E_LAYOUT_ERROR, e);
        }
    }

}
