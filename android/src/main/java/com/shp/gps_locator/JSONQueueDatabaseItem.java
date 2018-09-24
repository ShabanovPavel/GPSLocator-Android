package com.shp.gps_locator;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "JSONQueueDatabaseItem")
public class JSONQueueDatabaseItem  extends Model {

    @Column(name = "_id")
    long id;

    @Column(name = "tag")
    String tag;

    @Column(name = "data")
    String data;
}

