package ru.willdes.nginxplus

//Singletone class
class ServerConnection {
    var idconn = 0
    var conname: String? = null
    var ipaddr: String? = null
    var port: String? = null
    var user: String? = null
    var password: String? = null

    companion object {
        @JvmStatic
        var instance: ServerConnection? = null
            get() {
                if (field == null) {
                    field = ServerConnection()
                }
                return field
            }
            private set
    }
}