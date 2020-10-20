package ru.willdes.nginxplus;

import android.provider.BaseColumns;

public final class nginxplus implements BaseColumns {
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_IDSRV = "idsrv";
    public final static String TABLE_NAME = "connections";
    public final static String TABLE_UPSTREAMS = "upstreams";
    public final static String TABLE_SERVERS = "servers";
    public final static String COLUMN_NAME = "name";
    public final static String COLUMN_ADDRESS = "address";
    public final static String COLUMN_USER = "user";
    public final static String COLUMN_PASSWD = "password";
    public final static String COLUMN_PORT = "port";
    public final static String COLUMN_IDCONN = "id_conn";
    public final static String COLUMN_SERVER = "server";
    public final static String COLUMN_STATE = "state";
    public final static String COLUMN_ACTIVE = "active";
    public final static String COLUMN_REQUESTS = "requests";
    public final static String COLUMN_REQPSEC = "rps";
    public final static String COLUMN_IDUPSTR = "id_upstream";
}
