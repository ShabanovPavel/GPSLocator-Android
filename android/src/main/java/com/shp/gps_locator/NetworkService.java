package com.shp.gps_locator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.activeandroid.Configuration;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkService extends Service {

    public static String ID_USER;//ID User
    public static String puthURL; //Адрес
    private GpsReceiver mGpsReceiver;
    private static NetworkService mInstance;


    public NetworkService() {
    }

    public static DefaultHttpClient getNewHttpClient() {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpParams params = client.getParams();
        HttpConnectionParams.setConnectionTimeout(params, 5000);
        return client;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mGpsReceiver = new GpsReceiver(this);
        JSONQueueDatabase.init(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGpsReceiver != null)
            mGpsReceiver.disconnect();
        stopSelf();
        mInstance = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mGpsReceiver.connect();
        return START_STICKY;
    }

    public static void postLocation(
            long timestamp,
            double longitude,
            double latitude,
            double altitude,
            double accuracy,
            double speed)
    {
        try {
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("timestamp", Long.toString(timestamp));
            jsonParam.put("longitude", Double.toString(longitude));
            jsonParam.put("latitude", Double.toString(latitude));
            jsonParam.put("altitude", Double.toString(altitude));
            jsonParam.put("accuracy", Double.toString(accuracy));
            jsonParam.put("speed", Double.toString(speed));

            JSONQueueDatabase.put("gps", jsonParam);
        }catch (JSONException e){
            Log.w("error",e.toString());
        }

        sendPost();
    }


    public static void sendPost() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(puthURL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    JSONQueueDatabase.ResultSet res= JSONQueueDatabase.get("gps", 100);
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(res.get().toString());
                    os.flush();
                    os.close();
                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , conn.getResponseMessage());
                    JSONQueueDatabase.delete(res);
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
}