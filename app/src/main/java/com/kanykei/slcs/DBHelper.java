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
    public static final String GROUP_TABLE_NAME = "group_of_rooms";
    public static final String GROUP_COLUMN_ID = "id"; // id of rooms group
    public static final String GROUP_COLUMN_NAME = "name"; // name of group
    public static final String GROUP_COLUMN_ROOM_ID = "room_id"; // id of room in the group
    public static final String GROUP_COLUMN_STATE = "state"; // state of room in the group (turned on/turned off)
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
                GROUP_COLUMN_ROOM_ID + " integer, " +
                GROUP_COLUMN_STATE + " integer) " );
        ArrayList<String> title = new ArrayList<>();
        ArrayList<String> details = new ArrayList<>();
        title.add("Create a new room");
        title.add("Create a new group of rooms");
        title.add("Delete room (-s)");
        title.add("Set time to turn the light on (off) in room");
        title.add("Change language settings");
        title.add("Manage voice control");
        details.add("Go to Home tab and click ‘+’ add button in bottom right corner. In input text, write the name of room. Then choose corresponding relay\'s pin. Hit save. Now you can see the newly created room in the list of all rooms.");
        details.add("Go to Home tab. Select room (-s) to add to group. Click group icon in the top right corner. Enter name of a new group. Click “Save” button.");
        details.add("Go to Home tab. Select room (-s) to delete. Click trash box in the top right corner.");
        details.add("Go to Routines tab. To set time of turning the light on in room click wake up. The list of all the rooms with corresponding wake up time to the right will appear. Click the room and set the time in the dialog appeared. To set time of turning the light off in room click go to sleep. The list of all the rooms with corresponding go sleep time to the right will appear. Click the room and set the time in the dialog appeared.");
        details.add("Go to Settings tab.Choose the language from list.");
        details.add("If you want to voice control on Russian language (there is no Kyrgyz language voice library) first do the following steps.\n" +
                "\nTo install a voice library from Google go to Menu in mobile phone. Choose Settings ⇒ My device ⇒ Language and input ⇒ Voice search ⇒ Speech recognition offline ⇒ tab All ⇒ download Russian language. Now you can use voice control feature on Russian language.\n" +
                "Go to Settings tab.  Turn on voice control. Go to Home tab. Voice control button appeared. Click voice control button and command. The commands are following: “Turn on room_name” or “Turn off room_name”, “Room_name turn on” or “Room_name turn off”; “Включить room_name”, “Отключить room_name” or “Выключить room_name”, “Room_name включить”, “Room_name отключить” or “Room_name выключить”.");
        title.add("Создать новое помещение");
        title.add("Создать новую группу");
        title.add("Удалить комнату(-ы)");
        title.add("Установить время когда включать(выключать) лампочки");
        title.add("Настройки языка");
        title.add("Управление голосом");
        details.add("Пройдите во вкладку Главная и нажмите кнопку ‘+’ в правом нижнем углу. Введите название помещения ( комнаты ). Затем выберите соответствующий pin релейного модуля. Нажмите кнопку “Сохранить”. Теперь можете проверить новое помещение в списке помещений во вкладке Главная.");
        details.add("Пройдите во вкладку Главная. Выберите комнату(-ы), которую (-ые) вы хотели бы добавить в группу. Нажмите кнопку “Группа” (три лампочки) в правом верхнем углу. Введите название группы. Нажмите “Сохранить”.");
        details.add("Пройдите во вкладку Главная. Выберите комнату(-ы), которую (-ые) вы хотели бы удалить. Нажмите кнопку “Удалить” (мусорное ведро) в правом верхнем углу.");
        details.add("Пройдите во вкладку Время. " +
                "Чтобы установить время включения света в комнате, нажмите “Проснуться”. " +
                "Появится список всех комнат с соответсвтующим временем включения справа." +
                "Нажмите на название комнаты, в появившемся окне установите время. " +
                "\nЧтобы установить время выключения света в комнате, нажмите “Уснуть”. Появится список всех комнат с соответсвтующим временем вsключения справа. Нажмите на название комнаты, в появившемся окне установите время.");
        details.add("Пройдите во вкладку Настройки. Выберите язык из списка.");
        details.add("Если вы хотите управлять на русском языке " +
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
        title.add("Жаңы бөлмө кошуу");
        title.add("Жаңы топ кошуу");
        title.add("Бөлмөнү(-лөрдү) жок кылуу");
        title.add("Бөлмөдө жарыкты күйгүзүп өчүрүү убактысын белгилөө");
        title.add("Тил орнотуулар");
        title.add("Yн башкаруу");
        details.add("Башкы бетке барып, ‘+’ кошуу баскычын басыңыз. Текст киргизүү кутучасына бөлмөнүн атын жазыңыз." +
                " Реленин тиешелүү PINин тандаңыз. Сактоо баскычын басыңыз. Эми бүт бөлмөлөрдүн тизмесинде жаңы кошулган бөлмөнү көрө аласыз.");
        details.add("Башкы бетке барыңыз. Топко кошо турган бөлмөнү(-лөрдү) тандаңыз. Топтун сүрөтүн басыңыз. Текст киргизүү кутучасына топтун атын жазыңыз.\" +\n" +
                "Сактоо баскычын басыңыз.");
        details.add("Башкы бетке барыңыз. Жок кыла турган бөлмөнү(-лөрдү) тандаңыз. Таштанды кутучасын басыңыз.");
        details.add("“Убакыт“ кошумча баракчасына барыңыз." +
                " Бөлмөдө жарыкты күйгүзүү убактысын белгилөө үчүн “Ойгонуу” баскычын басыңыз." +
                " Бардык бөлмөлөрдүн тизмеси жана аларга тиешелүү жарыкты күйгүзүү убактысы пайда болот." +
                " Бөлмөнү басыңыз жана пайда болгон диалогдо убакыт тандаңыз." +
                " \\nБөлмөдө жарыкты өчүрүү убактысын белгилөө үчүн “Уктоo” баскычын басыңыз." +
                " Бардык бөлмөлөрдүн тизмеси жана аларга тиешелүү жарыкты өчүрүү убактысы пайда болот." +
                " Бөлмөнү басыңыз жана пайда болгон диалогдо убакыт тандаңыз.");
        details.add("“Орнотуулар” кошумча баракчасына барыңыз. Тизменен тилди тандаңыз.");
        details.add("Орус тилинде үн менен башкаргыңыз келсе (кыргыз тилинин үн китепканасы жок)" +
                " биринчи төмөнкү кадамдарды жасаңыз. Google үн китепканасын орнотуу үчүн мобилдик аппаратта Менюну басыңыз." +
                " Настройки ⇒ Мое устройство ⇒ Язык и ввод ⇒ Голосовое управление ⇒ Распознавание речи офлайн ⇒" +
                " вкладка ВСЕ ⇒ Орус тилин жүктөп алыңыз. Эми сиз орус тилинде үн башкаруу мүмкүнчүлүгүн колдоно аласыз. \\n" +
                " Орнотуулар кошумча баракчасына барыңыз. Үн башкарууну күйгүзүңүз. Башкы бетке барыңыз." +
                " Үн менен башкаруу баскычы көрүндү. Үн менен башкаруу баскычын басып жана буйрукту айтыңыз." +
                " Буйрук болуп төмөнкүлөр саналат: “Turn on room_name” or “Turn off room_name”, “Room_name turn on” or “Room_name turn off”;" +
                " “Включить room_name”, “Отключить room_name” or “Выключить room_name”, “Room_name включить”," +
                " “Room_name отключить” or “Room_name выключить”.");
        String language = "en";
        for(int i = 0; i < title.size(); i++){
            if(i < 6) {
                language = "en";
            }else if (i < 12){
                language = "ru";
            }else if (i < 18){
                language = "kg";
            }
            String titles = title.get(i);
            String detail = details.get(i);
            db.execSQL("insert into " + INFO_TABLE_NAME + " ("
                    + INFO_COLUMN_TITLE + ","
                    + INFO_COLUMN_DETAILS + ","
                    + INFO_COLUMN_LANGUAGE + ") values(\"" + titles + "\", \"" + detail + "\", \"" + language + "\")");

            Log.i("My Tag", "inserted: " + title + " " + details + " language " + language);
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ROOMS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + INFO_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GROUP_TABLE_NAME);
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

    public boolean insertGroup (String name, ArrayList<Integer> room_ids) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < room_ids.size(); i++) {
            sb.append(room_ids.get(i));
            if (i != room_ids.size() - 1) {
                sb.append(", ");
            }
        }
        String rooms_ids = sb.toString();
        ContentValues contentValues = new ContentValues();
        contentValues.put(GROUP_COLUMN_NAME, name);
        contentValues.put(GROUP_COLUMN_ROOM_ID, rooms_ids);
        contentValues.put(GROUP_COLUMN_STATE, 0);
        db.insert(GROUP_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean isDuplicateGroup (String name) {
        Cursor res =  db.rawQuery( "select * from " + GROUP_TABLE_NAME + " where "
                + GROUP_COLUMN_NAME + "=\"" + name + "\"", null );
        res.moveToFirst();
        if(res.getCount() == 0) {
            if (!res.isClosed())  {
                res.close();
            }
            return false;
        }else{
            if (!res.isClosed())  {
                res.close();
            }
            return true;
        }
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + ROOMS_TABLE_NAME + " where " + ROOMS_COLUMN_ID + " = " + id + "", null );
            res.moveToFirst();
            return res;

    }
    public Cursor getGroupData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + GROUP_TABLE_NAME + " where " + GROUP_COLUMN_ID + " = " + id + "", null );
            res.moveToFirst();
            return res;


    }

    public Cursor getDataByRelayPin(int id, int relay_pin) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res  =  db.rawQuery( "select * from " + ROOMS_TABLE_NAME + " where " + ROOMS_COLUMN_RELAY_PIN + " = " + relay_pin + " and " + ROOMS_COLUMN_ID + " <> " + id + "", null );
            res.moveToFirst();
            return res;


    }
    public int getRelayPinById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res =  db.rawQuery( "select * from " + ROOMS_TABLE_NAME + " where " + ROOMS_COLUMN_ID + " = " + id + "", null );
            res.moveToFirst();
            return res.getInt(res.getColumnIndex(ROOMS_COLUMN_RELAY_PIN));

        } finally {
            if(res != null) { res.close(); }
        }

    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, ROOMS_TABLE_NAME);
        return numRows;
    }

    public boolean updateRoom (Integer id, String name, int relay_pin) {

        Cursor res = null;
        try {
            res =  db.rawQuery( "select * from " + ROOMS_TABLE_NAME +
                    " where (" + ROOMS_COLUMN_NAME + "=\"" + name + "\" or " + ROOMS_COLUMN_RELAY_PIN + "=" + relay_pin + ") and " +
                    ROOMS_COLUMN_ID + " <> " + id + "", null );
            res.moveToFirst();
            if(res.getCount() == 0) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(ROOMS_COLUMN_NAME, name);
                contentValues.put(ROOMS_COLUMN_RELAY_PIN, relay_pin);
                db.update(ROOMS_TABLE_NAME, contentValues, "id = ? ", new String[]{Integer.toString(id)});
                return true;
            }else{
                return false;
            }
        } finally {
            if(res != null) { res.close(); }
        }

    }

    public boolean updateGroup (Integer id, String name, int room_id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(GROUP_COLUMN_NAME, name);
        contentValues.put(GROUP_COLUMN_ROOM_ID, room_id);
        db.update(GROUP_TABLE_NAME, contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public boolean updateStateOfRoom (Integer id, Integer state) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ROOMS_COLUMN_STATE, state);
        db.update(ROOMS_TABLE_NAME, contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }
    public boolean updateStateOfGroup (Integer id, Integer state) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(GROUP_COLUMN_STATE, state);
        db.update(GROUP_TABLE_NAME, contentValues, "id = ? ", new String[]{Integer.toString(id)});
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
    public Integer deleteGroup (Integer id) {

        return db.delete(GROUP_TABLE_NAME, "id = ? ", new String[] { Integer.toString(id) });
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


    public ArrayList<Room> getRoomsByIds(ArrayList<Integer> room_ids) {
        ArrayList<Room> array_list = new ArrayList<Room>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < room_ids.size(); i++) {
            sb.append(room_ids.get(i));
            if (i != room_ids.size() - 1) {
                sb.append(", ");
            }
        }
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + ROOMS_TABLE_NAME + " where id in (" + sb + ")", null );

        res.moveToFirst();

        while(!res.isAfterLast()){
            array_list.add(new Room(
                    res.getInt(res.getColumnIndex(ROOMS_COLUMN_ID)),
                    res.getString(res.getColumnIndex(ROOMS_COLUMN_NAME))
            ));
            res.moveToNext();
        }
        if (!res.isClosed())  {
            res.close();
        }
        return array_list;
    }

    public ArrayList<Room> getRoomsByGroupId(int group_id) {
        ArrayList<Room> array_list = new ArrayList<Room>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor group =  db.rawQuery( "select * from " + GROUP_TABLE_NAME + " where id = " + group_id, null );
        group.moveToFirst();
        String[] room_id = group.getString(group.getColumnIndex(GROUP_COLUMN_ROOM_ID)).split(", ");
        while(!group.isAfterLast()) {
            for(int i = 0; i < room_id.length; i++) {
                Cursor rooms = db.rawQuery("select * from " + ROOMS_TABLE_NAME + " where id = " + room_id[i], null);
                Log.i("My tag", "select * from " + ROOMS_TABLE_NAME + " where id = " + room_id[i]);
                rooms.moveToFirst();
                while (!rooms.isAfterLast()) {
                    array_list.add(new Room(
                            rooms.getInt(rooms.getColumnIndex(ROOMS_COLUMN_ID)),
                            rooms.getString(rooms.getColumnIndex(ROOMS_COLUMN_NAME)),
                            rooms.getInt(rooms.getColumnIndex(ROOMS_COLUMN_STATE)),
                            rooms.getString(rooms.getColumnIndex(ROOMS_COLUMN_WAKE_UP_TIME)),
                            rooms.getString(rooms.getColumnIndex(ROOMS_COLUMN_GO_SLEEP_TIME)),
                            rooms.getInt(rooms.getColumnIndex(ROOMS_COLUMN_RELAY_PIN)),
                            rooms.getLong(rooms.getColumnIndex(ROOMS_COLUMN_DELAY_WAKE)),
                            rooms.getLong(rooms.getColumnIndex(ROOMS_COLUMN_DELAY_SLEEP))
                    ));
                    rooms.moveToNext();
                }

                if (!rooms.isClosed()) {
                    rooms.close();
                }
            }
            group.moveToNext();
        }
        if (!group.isClosed()) {
            group.close();
        }
        return array_list;
    }


    public ArrayList<Group> getAllGroups() {
        ArrayList<Group> array_list = new ArrayList<Group>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + GROUP_TABLE_NAME, null );

        res.moveToFirst();

        while(!res.isAfterLast()){
            array_list.add(new Group(
                    res.getInt(res.getColumnIndex(GROUP_COLUMN_ID)),
                    res.getString(res.getColumnIndex(GROUP_COLUMN_NAME)),
                    res.getInt(res.getColumnIndex(GROUP_COLUMN_ROOM_ID)),
                    res.getInt(res.getColumnIndex(GROUP_COLUMN_STATE))
            ));
            res.moveToNext();
        }
        if (!res.isClosed())  {
            res.close();
        }
        return array_list;
    }
}
