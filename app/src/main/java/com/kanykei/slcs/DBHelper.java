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
    public static final String ROOMS_COLUMN_ID = "id"; // id of room
    public static final String ROOMS_COLUMN_NAME = "name"; // name of room
    public static final String ROOMS_COLUMN_STATE = "state"; // state of room (turned on/turned off)
    public static final String ROOMS_COLUMN_WAKE_UP_TIME = "wake_up_time"; // time to turn on room's lights
    public static final String ROOMS_COLUMN_GO_SLEEP_TIME = "go_sleep_time"; // time to turn off room's lights
    public static final String ROOMS_COLUMN_RELAY_PIN = "relay_pin"; // id of relay's pin (in0, in1, in2, in3 or in4)
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
        db.execSQL("create table " + ROOMS_TABLE_NAME + " (" +
                ROOMS_COLUMN_ID + " integer primary key, " +
                ROOMS_COLUMN_NAME + " text, " +
                ROOMS_COLUMN_STATE + " integer, " +
                ROOMS_COLUMN_WAKE_UP_TIME + " time(0), " +
                ROOMS_COLUMN_GO_SLEEP_TIME + " time(0), " +
                ROOMS_COLUMN_RELAY_PIN + " integer)");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ROOMS_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertRoom (String name, int relay_pin) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor res =  db.rawQuery( "select * from " + ROOMS_TABLE_NAME + " where " + ROOMS_COLUMN_NAME + "=\"" + name + "\" or " + ROOMS_COLUMN_RELAY_PIN + "=" + relay_pin + "", null );
        res.moveToFirst();
        if(res.getCount() == 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(ROOMS_COLUMN_NAME, name);
            contentValues.put(ROOMS_COLUMN_STATE, 0);
            contentValues.put(ROOMS_COLUMN_RELAY_PIN, relay_pin);
            db.insert(ROOMS_TABLE_NAME, null, contentValues);
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
        Cursor res =  db.rawQuery( "select * from " + ROOMS_TABLE_NAME + " where " + ROOMS_COLUMN_ID + " = " + id + "", null );
        res.moveToFirst();
        return res;
    }

    public Cursor getDataByRelayPin(int id, int relay_pin) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + ROOMS_TABLE_NAME + " where " + ROOMS_COLUMN_RELAY_PIN + " = " + relay_pin + " and " + ROOMS_COLUMN_ID + " <> " + id + "", null );
        res.moveToFirst();
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, ROOMS_TABLE_NAME);
        return numRows;
    }

    public boolean updateRoom (Integer id, String name, int relay_pin) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res =  db.rawQuery( "select * from " + ROOMS_TABLE_NAME +
                " where (" + ROOMS_COLUMN_NAME + "=\"" + name + "\" or " + ROOMS_COLUMN_RELAY_PIN + "=" + relay_pin + ") and " +
                ROOMS_COLUMN_ID + " <> " + id + "", null );
        res.moveToFirst();
        if(res.getCount() == 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(ROOMS_COLUMN_NAME, name);
            contentValues.put(ROOMS_COLUMN_RELAY_PIN, relay_pin);
            db.update(ROOMS_TABLE_NAME, contentValues, "id = ? ", new String[]{Integer.toString(id)});
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
        contentValues.put(ROOMS_COLUMN_STATE, state);
        db.update(ROOMS_TABLE_NAME, contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }
    public boolean updateWakeUpTimer (Integer id, String wake_timer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ROOMS_COLUMN_WAKE_UP_TIME, wake_timer);
        db.update(ROOMS_TABLE_NAME, contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }
    public boolean updateGoSleepTimer (Integer id, String sleep_timer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ROOMS_COLUMN_GO_SLEEP_TIME, sleep_timer);
        db.update(ROOMS_TABLE_NAME, contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public Integer deleteRoom (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(ROOMS_TABLE_NAME, "id = ? ", new String[] { Integer.toString(id) });
    }

//    public boolean updateStateOfRoomEveryMinute () {
//        SQLiteDatabase db = this.getWritableDatabase();
//        String MY_QUERY =
//                " CREATE EVENT reset " +
//                " ON SCHEDULE " +
//                " EVERY 1 MINUTE " +
//                " DO " +
//                " update rooms " +
//                " set state = 1 " +
//                " where wake_up_time = ?";
//
//        db.rawQuery(MY_QUERY, new String[]{"CURTIME()"});
//        return true;
//    }


    public ArrayList<Room> getAllRooms() {
        ArrayList<Room> array_list = new ArrayList<Room>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + ROOMS_TABLE_NAME, null );

        res.moveToFirst();

        while(!res.isAfterLast()){
            array_list.add(new Room(res.getInt(res.getColumnIndex(ROOMS_COLUMN_ID)),res.getString(res.getColumnIndex(ROOMS_COLUMN_NAME)),res.getInt(res.getColumnIndex(ROOMS_COLUMN_STATE)), res.getString(res.getColumnIndex(ROOMS_COLUMN_WAKE_UP_TIME)), res.getString(res.getColumnIndex(ROOMS_COLUMN_GO_SLEEP_TIME)), res.getInt(res.getColumnIndex(ROOMS_COLUMN_RELAY_PIN))));
            res.moveToNext();
        }
        if (!res.isClosed())  {
            res.close();
        }
        return array_list;
    }
}
