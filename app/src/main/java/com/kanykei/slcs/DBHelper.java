package com.kanykei.slcs;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "SLCS.db";
    public static final String INFO_TABLE_NAME = "info";
    public static final String INFO_COLUMN_ID = "id"; // id of info
    public static final String INFO_COLUMN_TITLE = "title"; // title of info
    public static final String INFO_COLUMN_DETAILS = "details"; // details of info
    public static final String INFO_COLUMN_LANGUAGE = "language"; // details of info
    public static final String GROUP_TABLE_NAME = "group";
    public static final String GROUP_COLUMN_ID = "id"; // id of rooms group
    public static final String GROUP_COLUMN_NAME = "name"; // name of group
    public static final String GROUP_COLUMN_ROOM_ID = "room_id"; // id of room in the group
    public static final String ROOMS_TABLE_NAME = "rooms";
    public static final String ROOMS_COLUMN_ID = "id"; // id of room
    public static final String ROOMS_COLUMN_NAME = "name"; // name of room
    public static final String ROOMS_COLUMN_STATE = "state"; // state of room (turned on/turned off)
    public static final String ROOMS_COLUMN_WAKE_UP_TIME = "wake_up_time"; // time to turn on room's lights
    public static final String ROOMS_COLUMN_GO_SLEEP_TIME = "go_sleep_time"; // time to turn off room's lights
    public static final String ROOMS_COLUMN_RELAY_PIN = "relay_pin"; // id of relay's pin (in0, in1, in2, in3 or in4)
    public static final String ROOMS_COLUMN_DELAY_WAKE = "delay_wake"; // delay time when to turn on state of room
    public static final String ROOMS_COLUMN_DELAY_SLEEP = "delay_sleep"; // delay time when to turn off state of room
    private static DBHelper mInstance = null;
    public Context context;
    SQLiteDatabase db = this.getWritableDatabase();

    private DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
        this.context = context;
        Log.i("My Tag", "DBHelper(Context context)\n insertInfo();");
    }

    public static DBHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance == null) {
            mInstance = new DBHelper(context.getApplicationContext());
            Log.i("My Tag", "if (mInstance == null)");

        }
        Log.i("My Tag", "DBHelper getInstance(Context context)");

        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("My Tag", "DBHelper onCreate");
        db.execSQL("create table " + ROOMS_TABLE_NAME + " (" +
                ROOMS_COLUMN_ID + " integer primary key, " +
                ROOMS_COLUMN_NAME + " text, " +
                ROOMS_COLUMN_STATE + " integer, " +
                ROOMS_COLUMN_WAKE_UP_TIME + " time(0), " +
                ROOMS_COLUMN_GO_SLEEP_TIME + " time(0), " +
                ROOMS_COLUMN_RELAY_PIN + " integer, " +
                ROOMS_COLUMN_DELAY_WAKE + " integer, " +
                ROOMS_COLUMN_DELAY_SLEEP + " integer)");
        db.execSQL("create table " + INFO_TABLE_NAME + " (" +
                INFO_COLUMN_ID + " integer primary key, " +
                INFO_COLUMN_TITLE + " text, " +
                INFO_COLUMN_DETAILS + " text, " +
                INFO_COLUMN_LANGUAGE + " text)");
        db.execSQL("create table " + GROUP_TABLE_NAME + " (" +
                GROUP_COLUMN_ID + " integer primary key, " +
                GROUP_COLUMN_NAME + " text, " +
                GROUP_COLUMN_ROOM_ID + " text) " );
        HashMap<String, String> infoMapEn = new HashMap<String, String>();
        infoMapEn.put("Create a new room","Go to Home tab and click ‘+’ add button in bottom right corner. In input text, write the name of room. Then choose corresponding relay\'s pin. Hit save. Now you can see the newly created room in the list of all rooms.");
        infoMapEn.put("Delete room (-s)","Go to Home tab. Select room (-s) to delete. Click trash box in the top right corner.");
        infoMapEn.put("Set time to turn the light on (off) in room","Go to Routines tab. To set time of turning the light on in room click wake up. The list of all the rooms with corresponding wake up time to the right will appear. Click the room and set the time in the dialog appeared. To set time of turning the light off in room click go to sleep. The list of all the rooms with corresponding go sleep time to the right will appear. Click the room and set the time in the dialog appeared.");
        infoMapEn.put("Change language settings","Go to Settings tab.Choose the language from list.");
        infoMapEn.put("Manage voice control","If you want to voice control on Russian language (there is no Kyrgyz language voice library) first do the following steps.\n" +
                "\nTo install a voice library from Google go to Menu in mobile phone. Choose Settings ⇒ My device ⇒ Language and input ⇒ Voice search ⇒ Speech recognition offline ⇒ tab All ⇒ download Russian language. Now you can use voice control feature on Russian language.\n" +
                "Go to Settings tab.  Turn on voice control. Go to Home tab. Voice control button appeared. Click voice control button and command. The commands are following: “Turn on room_name” or “Turn off room_name”, “Room_name turn on” or “Room_name turn off”; “Включить room_name”, “Отключить room_name” or “Выключить room_name”, “Room_name включить”, “Room_name отключить” or “Room_name выключить”.");
        HashMap<String, String> infoMapRu = new HashMap<String, String>();
        infoMapRu.put("Создать новое помещение","Пройдите во вкладку Главная и нажмите кнопку ‘+’ в правом нижнем углу. Введите название помещения ( комнаты ). Затем выберите соответствующий pin релейного модуля. Нажмите кнопку “Сохранить”. Теперь можете проверить новое помещение в списке помещений во вкладке Главная.");
        infoMapRu.put("Удалить комнату(-ы)","Пройдите во вкладку Главная. Выберите комнату(-ы), которую (-ые) вы хотели бы удалить. Нажмите кнопку “Удалить” (мусорное ведро) в правом верхнем углу.");
        infoMapRu.put("Установить время когда включать(выключать) лампочки","Пройдите во вкладку Время. " +
                "Чтобы установить время включения света в комнате, нажмите “Проснуться”. " +
                "Появится список всех комнат с соответсвтующим временем включения справа." +
                "Нажмите на название комнаты, в появившемся окне установите время. " +
                "\nЧтобы установить время выключения света в комнате, нажмите “Уснуть”. Появится список всех комнат с соответсвтующим временем вsключения справа. Нажмите на название комнаты, в появившемся окне установите время.");
        infoMapRu.put("Настройки языка","Пройдите во вкладку Настройки. Выберите язык из списка.");
        infoMapRu.put("Управление голосом","Если вы хотите управлять на русском языке " +
                "(голосовая библиотека кыргызского языка пока не имплементирована) выполните следующие шаги: " +
                "\nДля установки голосовой библиотеки Google пройдите в Меню на своем смартфоне. " +
                "Выберите Настройки ⇒ Мое устройство ⇒ Язык и ввод ⇒ Голосовое управление ⇒ Распознавание речи офлайн ⇒ " +
                "вкладка ВСЕ ⇒ скачайте Русский язык. Теперь вы можете управлять голосом на русском языке.\n " +
                "Пройдите во вкладку Настройки.  Включите голосовое управление.\n " +
                "Перейдите во вклдаку Home. Появилась кнопка для управления голосом. Нажмите на кнопку и говорите, командуйте. " +
                "Команды могут быть следующие: \n“Turn on название_комнаты” or “Turn off название_комнаты”, " +
                "“Название_комнаты turn on” or “Название_комнаты turn off”; “Включить название_комнаты”, " +
                "“Отключить название_комнаты” or “Выключить название_комнаты”, “Название_комнаты включить”, " +
                "“Название_комнаты отключить” or “Название_комнаты выключить”.");
        HashMap<String, String> infoMapKg = new HashMap<String, String>();
        infoMapKg.put("Жаңы бөлмө кошуу","Башкы бетке барып, ‘+’ кошуу баскычын басыңыз. Текст киргизүү кутучасына бөлмөнүн атын жазыңыз." +
                " Реленин тиешелүү PINин тандаңыз. Сактоо баскычын басыңыз. Эми бүт бөлмөлөрдүн тизмесинде жаңы кошулган бөлмөнү көрө аласыз.");
        infoMapKg.put("Бөлмөнү(-лөрдү) жок кылуу","Башкы бетке барыңыз. Жок кыла турган бөлмөнү(-лөрдү) тандаңыз. Таштанды кутучасын басыңыз.");
        infoMapKg.put("Бөлмөдө жарыкты күйгүзүп өчүрүү убактысын белгилөө","“Убакыт“ кошумча баракчасына барыңыз." +
                " Бөлмөдө жарыкты күйгүзүү убактысын белгилөө үчүн “Ойгонуу” баскычын басыңыз." +
                " Бардык бөлмөлөрдүн тизмеси жана аларга тиешелүү жарыкты күйгүзүү убактысы пайда болот." +
                " Бөлмөнү басыңыз жана пайда болгон диалогдо убакыт тандаңыз." +
                " \\nБөлмөдө жарыкты өчүрүү убактысын белгилөө үчүн “Уктоo” баскычын басыңыз." +
                " Бардык бөлмөлөрдүн тизмеси жана аларга тиешелүү жарыкты өчүрүү убактысы пайда болот." +
                " Бөлмөнү басыңыз жана пайда болгон диалогдо убакыт тандаңыз.");
        infoMapKg.put("Тил орнотуулар","“Орнотуулар” кошумча баракчасына барыңыз. Тизменен тилди тандаңыз.");
        infoMapKg.put("Yн башкаруу","Орус тилинде үн менен башкаргыңыз келсе (кыргыз тилинин үн китепканасы жок)" +
                " биринчи төмөнкү кадамдарды жасаңыз. Google үн китепканасын орнотуу үчүн мобилдик аппаратта Менюну басыңыз." +
                " Настройки ⇒ Мое устройство ⇒ Язык и ввод ⇒ Голосовое управление ⇒ Распознавание речи офлайн ⇒" +
                " вкладка ВСЕ ⇒ Орус тилин жүктөп алыңыз. Эми сиз орус тилинде үн башкаруу мүмкүнчүлүгүн колдоно аласыз. \\n" +
                " Орнотуулар кошумча баракчасына барыңыз. Үн башкарууну күйгүзүңүз. Башкы бетке барыңыз." +
                " Үн менен башкаруу баскычы көрүндү. Үн менен башкаруу баскычын басып жана буйрукту айтыңыз." +
                " Буйрук болуп төмөнкүлөр саналат: “Turn on room_name” or “Turn off room_name”, “Room_name turn on” or “Room_name turn off”;" +
                " “Включить room_name”, “Отключить room_name” or “Выключить room_name”, “Room_name включить”," +
                " “Room_name отключить” or “Room_name выключить”.");

        Iterator hashMapIteratorKg = infoMapKg.keySet().iterator();
        while(hashMapIteratorKg.hasNext()) {
            String language = "kg";
            String title=(String)hashMapIteratorKg.next();
            String details=(String)infoMapKg.get(title);
            db.execSQL("insert into " + INFO_TABLE_NAME + " ("
                    + INFO_COLUMN_TITLE + ","
                    + INFO_COLUMN_DETAILS + ","
                    + INFO_COLUMN_LANGUAGE + ") values(\"" + title + "\", \"" + details + "\", \"" + language + "\")");

            Log.i("My Tag", "inserted: " + title + " " + details);
        }

        Iterator hashMapIteratorEn = infoMapEn.keySet().iterator();
        while(hashMapIteratorEn.hasNext()) {
            String language = "en";
            String title=(String)hashMapIteratorEn.next();
            String details=(String)infoMapEn.get(title);
            db.execSQL("insert into " + INFO_TABLE_NAME + " ("
                    + INFO_COLUMN_TITLE + ","
                    + INFO_COLUMN_DETAILS + ","
                    + INFO_COLUMN_LANGUAGE + ") values(\"" + title + "\", \"" + details + "\", \"" + language + "\")");

            Log.i("My Tag", "inserted: " + title + " " + details);
        }
        Iterator hashMapIteratorRu = infoMapRu.keySet().iterator();
        while(hashMapIteratorRu.hasNext()) {
            String language = "ru";
            String title=(String)hashMapIteratorRu.next();
            String details=(String)infoMapRu.get(title);
            db.execSQL("insert into " + INFO_TABLE_NAME + " ("
                    + INFO_COLUMN_TITLE + ","
                    + INFO_COLUMN_DETAILS + ","
                    + INFO_COLUMN_LANGUAGE + ") values(\"" + title + "\", \"" + details + "\", \"" + language + "\")");

            Log.i("My Tag", "inserted: " + title + " " + details);
        }


        Log.i("My Tag","on create dbhelper");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ROOMS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + INFO_TABLE_NAME);
        onCreate(db);
    }



    public HashMap<String,String> getAllInfo() {
        HashMap<String, String> infoMap = new HashMap<String, String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " +INFO_TABLE_NAME, null );

        res.moveToFirst();

        while(!res.isAfterLast()){
            infoMap.put(res.getString(res.getColumnIndex(INFO_COLUMN_TITLE)),res.getString(res.getColumnIndex(INFO_COLUMN_DETAILS)));
            res.moveToNext();
        }
        if (!res.isClosed())  {
            res.close();
        }
        return infoMap;
    }

    public ArrayList<String> getInfoDetails(int id, String language) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + INFO_TABLE_NAME + " where " + INFO_COLUMN_ID + " = " + id + " and " +
                INFO_COLUMN_LANGUAGE + " = \"" + language + "\"", null );
        Log.i("My tag","select * from " + INFO_TABLE_NAME + " where " + INFO_COLUMN_ID + " = " + id + " and " +
                INFO_COLUMN_LANGUAGE + " = \"" + language + "\"");
        res.moveToFirst();
        ArrayList<String> details = new ArrayList<>();
        while(!res.isAfterLast()){
            details.add(res.getString(res.getColumnIndex(INFO_COLUMN_TITLE)));
            details.add(res.getString(res.getColumnIndex(INFO_COLUMN_DETAILS)));
            res.moveToNext();
        }
        if (!res.isClosed())  {
            res.close();
        }
        return details;
    }

    public ArrayList<String> getInfoTitles(String language) {
        ArrayList<String> titles = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " +INFO_TABLE_NAME + " where " + INFO_COLUMN_LANGUAGE + " = \"" + language + "\"", null );
        res.moveToFirst();
        while(!res.isAfterLast()){
            titles.add(res.getString(res.getColumnIndex(INFO_COLUMN_TITLE)));
            res.moveToNext();
        }
        if (!res.isClosed())  {
            res.close();
        }
        return titles;
    }
    public boolean insertRoom (String name, int relay_pin) {

        Cursor res =  db.rawQuery( "select * from " + ROOMS_TABLE_NAME + " where " + ROOMS_COLUMN_NAME + "=\"" + name + "\" or " + ROOMS_COLUMN_RELAY_PIN + "=" + relay_pin + "", null );
        res.moveToFirst();
        if(res.getCount() == 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(ROOMS_COLUMN_NAME, name);
            contentValues.put(ROOMS_COLUMN_STATE, 0);
            contentValues.put(ROOMS_COLUMN_DELAY_WAKE, 0);
            contentValues.put(ROOMS_COLUMN_DELAY_SLEEP, 0);
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

    public boolean insertGroup (String name, int room_id) {
        Cursor res =  db.rawQuery( "select * from " + GROUP_TABLE_NAME + " where "
                + GROUP_COLUMN_NAME + "=\"" + name + "\" or " + GROUP_COLUMN_ROOM_ID + "=" + room_id + "", null );
        res.moveToFirst();
        if(res.getCount() == 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(GROUP_COLUMN_NAME, name);
            contentValues.put(GROUP_COLUMN_ROOM_ID, room_id);
            db.insert(GROUP_TABLE_NAME, null, contentValues);
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
        ContentValues contentValues = new ContentValues();
        contentValues.put(ROOMS_COLUMN_STATE, state);
        db.update(ROOMS_TABLE_NAME, contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public boolean updateDelayWake (Integer id, long delay) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ROOMS_COLUMN_DELAY_WAKE, delay);
        db.update(ROOMS_TABLE_NAME, contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }
    public boolean updateDelaySleep (Integer id, long delay) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ROOMS_COLUMN_DELAY_SLEEP, delay);
        db.update(ROOMS_TABLE_NAME, contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }
    public boolean updateStateOfRoomByRelayPin (Integer relay_pin, Integer state) {
        Cursor res =  db.rawQuery( "select * from " + ROOMS_TABLE_NAME + " where " + ROOMS_COLUMN_RELAY_PIN + "=" + relay_pin + "", null );
        res.moveToFirst();
        if(res.getCount() != 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(ROOMS_COLUMN_STATE, state);
            Log.i("My tag", "update state by relay pin");
            db.update(ROOMS_TABLE_NAME, contentValues, ROOMS_COLUMN_RELAY_PIN + " = ? ", new String[]{Integer.toString(relay_pin)});
            if (!res.isClosed())  {
                res.close();
            }
            return true;
        }
        if (!res.isClosed())  {
            res.close();
        }
        return false;
    }
    public boolean updateWakeUpTimer (Integer id, String wake_timer) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(ROOMS_COLUMN_WAKE_UP_TIME, wake_timer);
        db.update(ROOMS_TABLE_NAME, contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }
    public boolean updateGoSleepTimer (Integer id, String sleep_timer) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(ROOMS_COLUMN_GO_SLEEP_TIME, sleep_timer);
        db.update(ROOMS_TABLE_NAME, contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public Integer deleteRoom (Integer id) {

        return db.delete(ROOMS_TABLE_NAME, "id = ? ", new String[] { Integer.toString(id) });
    }

//    public boolean updateStateOfRoomEveryMinute () {
//
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
            array_list.add(new Room(
                    res.getInt(res.getColumnIndex(ROOMS_COLUMN_ID)),
                    res.getString(res.getColumnIndex(ROOMS_COLUMN_NAME)),
                    res.getInt(res.getColumnIndex(ROOMS_COLUMN_STATE)),
                    res.getString(res.getColumnIndex(ROOMS_COLUMN_WAKE_UP_TIME)),
                    res.getString(res.getColumnIndex(ROOMS_COLUMN_GO_SLEEP_TIME)),
                    res.getInt(res.getColumnIndex(ROOMS_COLUMN_RELAY_PIN)),
                    res.getLong(res.getColumnIndex(ROOMS_COLUMN_DELAY_WAKE)),
                    res.getLong(res.getColumnIndex(ROOMS_COLUMN_DELAY_SLEEP))
            ));
            res.moveToNext();
        }
        if (!res.isClosed())  {
            res.close();
        }
        return array_list;
    }
}
