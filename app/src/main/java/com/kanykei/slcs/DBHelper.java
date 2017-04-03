package com.kanykei.slcs;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "SLCS.db";
    public static final String ROOMS_TABLE_NAME = "rooms";
    public static final String ROOMS_COLUMN_ID = "id";
    public static final String ROOMS_COLUMN_NAME = "name";
    public static final String ROOMS_COLUMN_STATE = "state";
    public static final String ROOMS_COLUMN_WAKE_UP_TIME = "wake_up_time";
    public static final String ROOMS_COLUMN_GO_SLEEP_TIME = "go_sleep_time";
    private static DBHelper mInstance = null;

    private DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    public static DBHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance == null) {
            mInstance = new DBHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table rooms (id integer primary key, name text, state integer, wake_up_time time(0), go_sleep_time time(0), set_relay integer)");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS rooms");
        onCreate(db);
    }

    public boolean insertRoom (String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor res =  db.rawQuery( "select * from rooms where name=\"" + name + "\"", null );
        res.moveToFirst();
        if(res.getCount() == 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", name);
            contentValues.put("state", 0);
            db.insert("rooms", null, contentValues);
            if (!res.isClosed())  {
                res.close();
            }
            return true;
        }else{
            if (!res.isClosed())  {
                res.close();
            }
            return false;
        }
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from rooms where id=" + id + "", null );
        res.moveToFirst();
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, ROOMS_TABLE_NAME);
        return numRows;
    }

    public boolean updateRoom (Integer id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res =  db.rawQuery( "select * from rooms where name=\"" + name + "\"", null );
        res.moveToFirst();
        if(res.getCount() == 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", name);
            db.update("rooms", contentValues, "id = ? ", new String[]{Integer.toString(id)});
            if (!res.isClosed())  {
                res.close();
            }
            return true;
        }else{
            if (!res.isClosed())  {
                res.close();
            }
            return false;
        }
    }

    public boolean updateStateOfRoom (Integer id, Integer state) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("state", state);
        db.update("rooms", contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }
    public boolean updateWakeUpTimer (Integer id, String wake_timer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("wake_up_time", wake_timer);
        db.update("rooms", contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }
    public boolean updateGoSleepTimer (Integer id, String sleep_timer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("go_sleep_time", sleep_timer);
        db.update("rooms", contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public Integer deleteRoom (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("rooms", "id = ? ", new String[] { Integer.toString(id) });
    }


    public ArrayList<Room> getAllRooms() {
        ArrayList<Room> array_list = new ArrayList<Room>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from rooms", null );

        res.moveToFirst();

        while(!res.isAfterLast()){
            array_list.add(new Room(res.getInt(res.getColumnIndex("id")),res.getString(res.getColumnIndex("name")),res.getInt(res.getColumnIndex("state")), res.getString(res.getColumnIndex("wake_up_time")), res.getString(res.getColumnIndex("go_sleep_time"))));
            res.moveToNext();
        }
        if (!res.isClosed())  {
            res.close();
        }
        return array_list;
    }
}
