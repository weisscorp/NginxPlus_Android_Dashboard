package ru.willdes.nginxplus

import android.app.IntentService
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import org.json.JSONException
import org.json.JSONObject
import ru.willdes.nginxplus.nginxplus.COLUMN_ID
import ru.willdes.nginxplus.nginxplus.COLUMN_REQUESTS
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.NoRouteToHostException
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.TimeUnit

class getJson  //private ListView listView;
    : IntentService("json") {
    val LOG_TAG = "Service"
    fun showToast(message: String) {
        Handler(Looper.getMainLooper()).post {
            Log.d(LOG_TAG, "toast start")
            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onHandleIntent(workIntent: Intent) {
        Log.d(LOG_TAG, "onHandleIntent create")
        val id = ServerConnection.instance?.idconn
        val ipaddr = ServerConnection.instance?.ipaddr
        val port = ServerConnection.instance?.port
        val user = ServerConnection.instance?.user
        val passwd = ServerConnection.instance?.password
        var connection: HttpURLConnection? = null
        var reader: BufferedReader? = null
        val db: db = db(this@getJson)
        db.open()
        var flag = true
        while (flag) {
            try {
                if (id != ServerConnection.instance?.idconn) {
                    flag = false
                }
                val url = URL("http://$ipaddr:$port/api/2/http/upstreams/")
                connection = url.openConnection() as HttpURLConnection
                if (user !== "none") {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val basicAuth = Base64.getEncoder().encodeToString("$user:$passwd".toByteArray(StandardCharsets.UTF_8))
                        connection.setRequestProperty("Authorization", "Basic $basicAuth")
                        connection.setRequestProperty("Cookie", "nginxauth=$basicAuth")
                    }
                }
                connection.connect()
                val stream = connection.inputStream
                reader = BufferedReader(InputStreamReader(stream))
                val buffer = StringBuffer()
                var line = ""
                while (reader.readLine().also { line = it } != null) {
                    buffer.append("""
    $line

    """.trimIndent())
                    Log.d(LOG_TAG, "Response: > " + line);
                }
                val jObject = JSONObject(buffer.toString().trim { it <= ' ' })
                val keys: Iterator<*> = jObject.keys()
                while (keys.hasNext()) {
                    val key = keys.next() as String
                    if (jObject[key] is JSONObject) {
                        //Log.d(LOG_TAG, "Keys: > " + key);
                        val upstreamnow = jObject.getJSONObject(key)
                        val peers = upstreamnow.getJSONArray("peers")
                        val cur = id?.let { db.getAllDataFromUpstreamsById(it, key) }
                        if (cur != null) {
                            if (cur.count == 0) {
                                db.addRecToUpstreams(id, key)
                            }
                        }
                        if (cur != null) {
                            cur.close()
                        }
                        val curt = id?.let { db.getAllDataFromUpstreamsById(it, key) }
                        if (curt != null) {
                            curt.moveToFirst()
                        }
                        val idupstr = curt?.getInt(cur!!.getColumnIndex(COLUMN_ID))
                        for (n in 0 until peers.length()) {
                            val onesrvonupstr = peers.getJSONObject(n)
                            val idsrv = onesrvonupstr.getInt("id")
                            val nameserver = onesrvonupstr.getString("server")
                            val state = onesrvonupstr.getString("state")
                            val active = onesrvonupstr.getInt("active")
                            val requests = onesrvonupstr.getInt("requests")
                            val srvcur = db.getAllDataFromServersByName(idupstr, nameserver)
                            srvcur.moveToFirst()
                            if (srvcur.count == 0) {
                                srvcur.close()
                                db.addRecToServers(idsrv, idupstr, nameserver, state, active, requests, 0)
                            } else {
                                val idsrvrow = srvcur.getInt(srvcur.getColumnIndex("_id"))
                                val requestsold = srvcur.getInt(srvcur.getColumnIndex(COLUMN_REQUESTS))
                                var rps = requests - requestsold
                                if (rps < 0) {
                                    rps = 0
                                }
                                db.changeRecToServers(idsrvrow, idsrv, idupstr, nameserver, state, active, requests, rps)
                                srvcur.close()
                            }
                            srvcur.close()
                        }
                        curt?.close()
                    }
                }
            } catch (e2: ConnectException) {
                showToast("Server " + ServerConnection.instance?.conname + " not unavailable")
                e2.printStackTrace()
                flag = false
            } catch (e: NoRouteToHostException) {
                showToast("Server " + ServerConnection.instance?.conname + ": no route to host")
                e.printStackTrace()
                flag = false
            } catch (e1: IOException) {
                e1.printStackTrace()
            } catch (e1: JSONException) {
                e1.printStackTrace()
            } finally {
                connection?.disconnect()
                try {
                    reader?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            try {
                TimeUnit.SECONDS.sleep(1)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        } //for { while
        db.close()
        stopSelf()
    }

    override fun onDestroy() {
        Log.d(LOG_TAG, "onDestroy")
        stopSelf()
        super.onDestroy()
    }

    companion object {
        const val TAG = "getJson"
    }
}