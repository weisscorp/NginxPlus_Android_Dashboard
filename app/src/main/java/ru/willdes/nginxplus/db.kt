package ru.willdes.nginxplus

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class db(private val mCtx: Context) {
    private var mDBHelper: DBHelper? = null
    private var mDB: SQLiteDatabase? = null

    private inner class DBHelper internal constructor(context: Context?, name: String?, factory: CursorFactory?,
                                                      version: Int) : SQLiteOpenHelper(context, name, factory, version) {
        override fun onCreate(db: SQLiteDatabase) {
            // создаем таблицу с полями
            db.execSQL("create table " + nginxplus.TABLE_NAME + " (id integer primary key autoincrement, "
                    + nginxplus.COLUMN_NAME + " text, "
                    + nginxplus.COLUMN_ADDRESS + " text, "
                    + nginxplus.COLUMN_PORT + " text, "
                    + nginxplus.COLUMN_USER + " text, "
                    + nginxplus.COLUMN_PASSWD + " text);")
            db.execSQL("create table " + nginxplus.TABLE_UPSTREAMS + " (" + nginxplus.COLUMN_ID + " integer primary key autoincrement, "
                    + nginxplus.COLUMN_IDCONN + " integer, "
                    + nginxplus.COLUMN_NAME + " text);")
            db.execSQL("create table " + nginxplus.TABLE_SERVERS + " (" + nginxplus.COLUMN_ID + " integer primary key autoincrement, "
                    + nginxplus.COLUMN_IDUPSTR + " integer, "
                    + nginxplus.COLUMN_IDSRV + " integer, "
                    + nginxplus.COLUMN_SERVER + " text, "
                    + nginxplus.COLUMN_STATE + " text, "
                    + nginxplus.COLUMN_ACTIVE + " integer, "
                    + nginxplus.COLUMN_REQUESTS + " integer, "
                    + nginxplus.COLUMN_REQPSEC + " integer);")
            Log.d("DB:", "Created DB")
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE " + nginxplus.TABLE_UPSTREAMS)
            db.execSQL("DROP TABLE " + nginxplus.TABLE_SERVERS)
            db.execSQL("DROP TABLE " + nginxplus.TABLE_UPSTREAMS)
            // Создаём новую таблицу
            onCreate(db)
        }
    }

    // открыть подключение
    fun open() {
        mDBHelper = DBHelper(mCtx, "nginxDB", null, 1)
        mDB = mDBHelper!!.writableDatabase
        //Log.d("DB:", "Connection opend");
    }

    // закрыть подключение
    fun close() {
        if (mDBHelper != null) mDBHelper!!.close()
        //Log.d("DB:", "Connection closed");
    }

    // получить все данные из таблицы Connections
    val allDataFromConnections: Cursor
        get() = mDB!!.query(nginxplus.TABLE_NAME, null, null, null, null, null, null)

    // получить все данные из таблицы Servers
    fun getAllDataFromServersByName(idupstr: Int?, servername: String): Cursor {
        return mDB!!.query(nginxplus.TABLE_SERVERS, null, nginxplus.COLUMN_IDUPSTR + " = '" + idupstr + "' AND " + nginxplus.COLUMN_SERVER + " = '" + servername + "'", null, null, null, null)
    }

    fun getAllDataFromServersByIdupstr(idupstr: Int?): Cursor {
        return mDB!!.query(nginxplus.TABLE_SERVERS, null, nginxplus.COLUMN_IDUPSTR + " = '" + idupstr + "'", null, null, null, null)
    }

    fun getAllDataFromUpstreamsById(id: Int, name: String): Cursor {
        return mDB!!.query(nginxplus.TABLE_UPSTREAMS, null, nginxplus.COLUMN_IDCONN + " = '" + id + "' AND " + nginxplus.COLUMN_NAME + " = '" + name + "'", null, null, null, null)
    }

    fun getAllDataFromUpstreamsByServerId(id: Int): Cursor {
        return mDB!!.query(nginxplus.TABLE_UPSTREAMS, arrayOf(nginxplus.COLUMN_ID, nginxplus.COLUMN_IDCONN, nginxplus.COLUMN_NAME), nginxplus.COLUMN_IDCONN + " = " + id, null, null, null, null)
    }

    // добавить запись в Upstreams
    fun addRecToUpstreams(idconn: Int, name: String?) {
        val cv = ContentValues()
        cv.put(nginxplus.COLUMN_IDCONN, idconn)
        cv.put(nginxplus.COLUMN_NAME, name)
        mDB!!.insert(nginxplus.TABLE_UPSTREAMS, null, cv)
    }

    // добавить запись в Servers
    fun addRecToServers(idsrv: Int, idupstream: Int?, server: String?, state: String?, active: Int, requests: Int, rps: Int) {
        val cv = ContentValues()
        cv.put(nginxplus.COLUMN_IDUPSTR, idupstream)
        cv.put(nginxplus.COLUMN_IDSRV, idsrv)
        cv.put(nginxplus.COLUMN_SERVER, server)
        cv.put(nginxplus.COLUMN_STATE, state)
        cv.put(nginxplus.COLUMN_ACTIVE, active)
        cv.put(nginxplus.COLUMN_REQUESTS, requests)
        cv.put(nginxplus.COLUMN_REQPSEC, rps)
        mDB!!.insert(nginxplus.TABLE_SERVERS, null, cv)
    }

    fun changeRecToServers(idrow: Int, idsrv: Int, idupstream: Int?, server: String?, state: String?, active: Int, requests: Int, rps: Int) {
        val cv = ContentValues()
        cv.put(nginxplus.COLUMN_IDSRV, idsrv)
        cv.put(nginxplus.COLUMN_IDUPSTR, idupstream)
        cv.put(nginxplus.COLUMN_SERVER, server)
        cv.put(nginxplus.COLUMN_STATE, state)
        cv.put(nginxplus.COLUMN_ACTIVE, active)
        cv.put(nginxplus.COLUMN_REQUESTS, requests)
        cv.put(nginxplus.COLUMN_REQPSEC, rps)
        mDB!!.update(nginxplus.TABLE_SERVERS, cv, nginxplus.COLUMN_ID + " = ?", arrayOf(idrow.toString()))
    }

    fun addNewServer(sDisplayName: String?, sAddress: String?, sPort: String?, sUser: String?, sPasswd: String?) {
        val cv = ContentValues()
        cv.put(nginxplus.COLUMN_NAME, sDisplayName)
        cv.put(nginxplus.COLUMN_ADDRESS, sAddress)
        cv.put(nginxplus.COLUMN_PORT, sPort)
        cv.put(nginxplus.COLUMN_USER, sUser)
        cv.put(nginxplus.COLUMN_PASSWD, sPasswd)
        mDB!!.insert(nginxplus.TABLE_NAME, null, cv)
    }

    // удалить запись из connections
    fun delRec(id: Int) {

        //нужно получить айди всех апстримов, по ним удалить сервера и удалить сами апстримы
        mDB!!.delete(nginxplus.TABLE_UPSTREAMS, nginxplus.COLUMN_IDCONN + " = " + id, null)
        mDB!!.delete(nginxplus.TABLE_NAME, "id = $id", null)
    }

    fun delRecUpstream(id: Int) {
        mDB!!.delete(nginxplus.TABLE_UPSTREAMS, nginxplus.COLUMN_IDCONN + " = " + id, null)
    }

    fun delRecServers(idupstr: Int) {
        mDB!!.delete(nginxplus.TABLE_SERVERS, nginxplus.COLUMN_IDUPSTR + " = " + idupstr, null)
    }

    fun getConnWhereId(id: Int): Cursor {
        return mDB!!.query(nginxplus.TABLE_NAME, null, "id = '$id'", null, null, null, null)
    }

    fun changeRec(idrow: Int, name: String?, address: String?, port: String?, user: String?, password: String?) {
        val cv = ContentValues()
        cv.put(nginxplus.COLUMN_NAME, name)
        cv.put(nginxplus.COLUMN_ADDRESS, address)
        cv.put(nginxplus.COLUMN_PORT, port)
        cv.put(nginxplus.COLUMN_USER, user)
        cv.put(nginxplus.COLUMN_PASSWD, password)
        mDB!!.update(nginxplus.TABLE_NAME, cv, "id = ?", arrayOf(idrow.toString()))
    }

    fun delUpstreamWhereName(name: String) {
        mDB!!.delete(nginxplus.TABLE_UPSTREAMS, nginxplus.COLUMN_NAME + " = " + name, null)
    }

    fun delServerWhereId(idupstream: String, server: String) {
        mDB!!.delete(nginxplus.TABLE_UPSTREAMS, nginxplus.COLUMN_IDUPSTR + " = " + idupstream + " AND " + nginxplus.COLUMN_SERVER + " = " + server, null)
    }

}