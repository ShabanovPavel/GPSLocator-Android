package com.shp.gps_locator;

import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class JSONQueueDatabase {
    private static long lastId = 0;

    public static class ResultSet
    {
        private  String tag;
        private long lo;
        private long hi;
        private JSONArray objects;

        private ResultSet(String tag, long lo, long hi, JSONArray objects) {
            this.tag = tag;
            this.lo = lo;
            this.hi = hi;
            this.objects = objects;
        }

        public JSONArray get() {
            return objects;
        }

        public long length() {
            return objects.length();
        }
    }

    public static void init(Context context) {
        Configuration.Builder configurationBuilder = new Configuration.Builder(context);
        configurationBuilder.addModelClasses(JSONQueueDatabaseItem.class);
        ActiveAndroid.initialize(configurationBuilder.create());
        JSONQueueDatabaseItem lastItem = new Select()
                .from(JSONQueueDatabaseItem.class)
                .orderBy("_id DESC")
                .executeSingle();
        if (lastItem != null)
            lastId = lastItem.id;
    }

    public static void put(final String tag, final JSONObject data) {
        JSONQueueDatabaseItem item = new JSONQueueDatabaseItem();
        item.id = lastId + 1;
        item.tag = tag;
        item.data = data.toString();
        item.save();
        lastId = item.id;
    }

    public static ResultSet get(String tag, int limit) throws JSONException {
        List<JSONQueueDatabaseItem> items = new Select()
                .from(JSONQueueDatabaseItem.class)
                .where("tag = ?", tag)
                .orderBy("_id ASC")
                .limit(limit)
                .execute();
        long lo = Long.MAX_VALUE;
        long hi = Long.MIN_VALUE;
        JSONArray objects = new JSONArray();
        if (items != null && items.size() > 0) {
            for (JSONQueueDatabaseItem item : items) {
                lo = Math.min(lo, item.id);
                hi = Math.max(hi, item.id);
                objects.put(new JSONObject(item.data));
            }
        }
        return new ResultSet(tag, lo, hi, objects);
    }

    public static void delete(ResultSet r) {
        if (r.length() == 0)
            return;
        new Delete().from(JSONQueueDatabaseItem.class)
                        .where("tag = ? and _id >= ? and _id <= ?", r.tag, r.lo, r.hi)
                        .execute();
    }

    public static void deleteAll(String tag) {
        new Delete().from(JSONQueueDatabaseItem.class)
                        .where("tag = ?", tag)
                        .execute();
    }
}

