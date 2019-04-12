package ru.willdes.nginxplus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import static ru.willdes.nginxplus.nginxplus.COLUMN_ACTIVE;
import static ru.willdes.nginxplus.nginxplus.COLUMN_ADDRESS;
import static ru.willdes.nginxplus.nginxplus.COLUMN_ID;
import static ru.willdes.nginxplus.nginxplus.COLUMN_IDCONN;
import static ru.willdes.nginxplus.nginxplus.COLUMN_IDSRV;
import static ru.willdes.nginxplus.nginxplus.COLUMN_IDUPSTR;
import static ru.willdes.nginxplus.nginxplus.COLUMN_NAME;
import static ru.willdes.nginxplus.nginxplus.COLUMN_PASSWD;
import static ru.willdes.nginxplus.nginxplus.COLUMN_PORT;
import static ru.willdes.nginxplus.nginxplus.COLUMN_REQPSEC;
import static ru.willdes.nginxplus.nginxplus.COLUMN_REQUESTS;
import static ru.willdes.nginxplus.nginxplus.COLUMN_SERVER;
import static ru.willdes.nginxplus.nginxplus.COLUMN_STATE;
import static ru.willdes.nginxplus.nginxplus.COLUMN_USER;
import static ru.willdes.nginxplus.nginxplus.TABLE_NAME;
import static ru.willdes.nginxplus.nginxplus.TABLE_SERVERS;
import static ru.willdes.nginxplus.nginxplus.TABLE_UPSTREAMS;

public class db {

    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    private Context mCtx;

    public db(Context ctx) {
        mCtx = ctx;
    }

    private class DBHelper extends SQLiteOpenHelper {

        DBHelper(Context context, String name, CursorFactory factory,
                 int version) {
            super(context, name, factory, version);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            // создаем таблицу с полями
            db.execSQL("create table " + TABLE_NAME + " (id integer primary key autoincrement, "
                    + COLUMN_NAME + " text, "
                    + COLUMN_ADDRESS + " text, "
                    + COLUMN_PORT + " text, "
                    + COLUMN_USER + " text, "
                    + COLUMN_PASSWD + " text);");
            db.execSQL("create table " + TABLE_UPSTREAMS + " (" + COLUMN_ID + " integer primary key autoincrement, "
                    + COLUMN_IDCONN + " integer, "
                    + COLUMN_NAME + " text);");
            db.execSQL("create table " + TABLE_SERVERS + " (" + COLUMN_ID + " integer primary key autoincrement, "
                    + COLUMN_IDUPSTR + " integer, "
                    + COLUMN_IDSRV + " integer, "
                    + COLUMN_SERVER + " text, "
                    + COLUMN_STATE + " text, "
                    + COLUMN_ACTIVE + " integer, "
                    + COLUMN_REQUESTS + " integer, "
                    + COLUMN_REQPSEC + " integer);");
            Log.d("DB:", "Created DB");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE " + TABLE_UPSTREAMS);
            db.execSQL("DROP TABLE " + TABLE_SERVERS);
            db.execSQL("DROP TABLE " + TABLE_UPSTREAMS);
            // Создаём новую таблицу
            onCreate(db);
        }
    }

    // открыть подключение
    public void open() {
        mDBHelper = new DBHelper(mCtx, "nginxDB", null, 1);
        mDB = mDBHelper.getWritableDatabase();
        Log.d("DB:", "Connection opend");
    }

    // закрыть подключение
    public void close() {
        if (mDBHelper!=null) mDBHelper.close();
        Log.d("DB:", "Connection closed");
    }

    // получить все данные из таблицы Connections
    public Cursor getAllDataFromConnections() {
        return mDB.query(TABLE_NAME, null, null, null, null, null, null);
    }

    // получить все данные из таблицы Servers
    public Cursor getAllDataFromServersByName(int idupstr, String servername ) {
        return mDB.query(TABLE_SERVERS, null, COLUMN_IDUPSTR + " = '" + idupstr + "' AND " + COLUMN_SERVER +" = '" + servername + "'", null, null, null, null);
    }

    public Cursor getAllDataFromServersByIdupstr(int idupstr ) {
        return mDB.query(TABLE_SERVERS, null, COLUMN_IDUPSTR + " = '" + idupstr + "'", null, null, null, null);
    }


    public Cursor getAllDataFromUpstreamsById(int id, String name) {
        return mDB.query(TABLE_UPSTREAMS, null, COLUMN_IDCONN + " = '" + id + "' AND " + COLUMN_NAME +" = '" + name + "'", null, null, null, null);
    }

    public Cursor getAllDataFromUpstreamsByServerId(int id) {
        return mDB.query(TABLE_UPSTREAMS, new String[] {COLUMN_ID, COLUMN_IDCONN, COLUMN_NAME}, COLUMN_IDCONN + " = " + id, null, null, null, null);
    }

    // добавить запись в Upstreams
    public void addRecToUpstreams(int idconn, String name) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_IDCONN, idconn);
        cv.put(COLUMN_NAME, name);
        mDB.insert(TABLE_UPSTREAMS, null, cv);
    }

    // добавить запись в Servers
    public void addRecToServers(int idsrv, int idupstream, String server, String state, int active, int requests, int rps) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_IDUPSTR, idupstream);
        cv.put(COLUMN_IDSRV, idsrv);
        cv.put(COLUMN_SERVER, server);
        cv.put(COLUMN_STATE, state);
        cv.put(COLUMN_ACTIVE, active);
        cv.put(COLUMN_REQUESTS, requests);
        cv.put(COLUMN_REQPSEC, rps);
        mDB.insert(TABLE_SERVERS, null, cv);
    }

    public void changeRecToServers(int idrow, int idsrv, int idupstream, String server, String state, int active, int requests, int rps) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_IDSRV, idsrv);
        cv.put(COLUMN_IDUPSTR, idupstream);
        cv.put(COLUMN_SERVER, server);
        cv.put(COLUMN_STATE, state);
        cv.put(COLUMN_ACTIVE, active);
        cv.put(COLUMN_REQUESTS, requests);
        cv.put(COLUMN_REQPSEC, rps);
        mDB.update(TABLE_SERVERS,  cv, COLUMN_ID + " = ?",
                new String[] {String.valueOf(idrow)});
    }

    public void addNewServer(String sDisplayName, String sAddress, String sPort, String sUser, String sPasswd) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, sDisplayName);
        cv.put(COLUMN_ADDRESS, sAddress);
        cv.put(COLUMN_PORT,sPort);
        cv.put(COLUMN_USER, sUser);
        cv.put(COLUMN_PASSWD, sPasswd);
        mDB.insert(TABLE_NAME, null, cv);
    }


    // удалить запись из connections
    public void delRec(int id) {

        //нужно получить айди всех апстримов, по ним удалить сервера и удалить сами апстримы
        Cursor cursor = mDB.query(TABLE_UPSTREAMS, new String[] {COLUMN_ID}, COLUMN_IDCONN + " = " + id, null, null, null, null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                int idupstr = cursor.getInt(cursor.getColumnIndex("id"));
                mDB.delete(TABLE_SERVERS, COLUMN_IDUPSTR + " = " + idupstr, null);
            } while (cursor.moveToNext());
        }
        mDB.delete(TABLE_UPSTREAMS, COLUMN_IDCONN + " = " + id, null);
        mDB.delete(TABLE_NAME, "id = " + id, null);
    }

    public Cursor getConnWhereId(int id) {
        return mDB.query(TABLE_NAME, null, "id = '" + id + "'", null, null, null, null);
    }

    public void changeRec(int idrow, String name, String address, String port, String user, String password) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_ADDRESS, address);
        cv.put(COLUMN_PORT, port);
        cv.put(COLUMN_USER, user);
        cv.put(COLUMN_PASSWD, password);
        mDB.update(TABLE_NAME,  cv, "id = ?",
                new String[] {String.valueOf(idrow)});
    }

    public void delUpstreamWhereName(String name) {
        mDB.delete(TABLE_UPSTREAMS, COLUMN_NAME + " = " + name, null);
    }

    public void delServerWhereId(String idupstream, String server) {
        mDB.delete(TABLE_UPSTREAMS, COLUMN_IDUPSTR + " = " + idupstream + " AND " + COLUMN_SERVER + " = " + server, null);
    }

}