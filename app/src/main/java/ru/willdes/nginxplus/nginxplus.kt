package ru.willdes.nginxplus

import android.provider.BaseColumns

object nginxplus : BaseColumns {
    const val COLUMN_ID = "_id"
    const val COLUMN_IDSRV = "idsrv"
    const val TABLE_NAME = "connections"
    const val TABLE_UPSTREAMS = "upstreams"
    const val TABLE_SERVERS = "servers"
    const val COLUMN_NAME = "name"
    const val COLUMN_ADDRESS = "address"
    const val COLUMN_USER = "user"
    const val COLUMN_PASSWD = "password"
    const val COLUMN_PORT = "port"
    const val COLUMN_IDCONN = "id_conn"
    const val COLUMN_SERVER = "server"
    const val COLUMN_STATE = "state"
    const val COLUMN_ACTIVE = "active"
    const val COLUMN_REQUESTS = "requests"
    const val COLUMN_REQPSEC = "rps"
    const val COLUMN_IDUPSTR = "id_upstream"
}