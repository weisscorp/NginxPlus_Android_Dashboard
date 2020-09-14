package ru.willdes.nginxplus

import android.os.Build
import android.util.Log
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.ProtocolException
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*

class POSTtoServer(private val idsrv: Int, private val srvname: String, private val state: String) {
    private val upstream: String
    private val ipaddr: String
    private val port: String
    private val user: String
    private val password: String

    companion object {
        private const val LOG_TAG = "POSTtoServer"
    }

    init {
        upstream = UpstreamName.upstreamName?.upstrname.toString()
        ipaddr = ServerConnection.instance?.ipaddr.toString()
        port = ServerConnection.instance?.port.toString()
        user = ServerConnection.instance?.user.toString()
        password = ServerConnection.instance?.password.toString()
        Log.d("POST", "id = " + idsrv + ", set state = " + state + ", upstream name = " + upstream)
        Log.d("SRVConn", ipaddr)
        var json: String? = null
        when (state) {
            "up" -> json = "{\"server\":\"" + srvname + "\",\"down\":false}"
            "down" -> json = "{\"server\":\"" + srvname + "\",\"down\":true}"
            "drain" -> json = "{\"server\":\"" + srvname + "\",\"drain\":true}"
        }
        var reader: BufferedReader? = null
        var bufferedWriter: BufferedWriter? = null
        var buffer: StringBuilder? = null
        try {
            val url = URL("http://" + ipaddr + ":" + port + "/api/2/http/upstreams/" + upstream + "/servers/" + idsrv)
            val httpcon = url.openConnection() as HttpURLConnection
            //Log.d("HTTP", url.toString());
            //Log.d("JSON", json);
            if (user !== "none") {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val basicAuth = Base64.getEncoder().encodeToString("$user:$password".toByteArray(StandardCharsets.UTF_8))
                    httpcon.setRequestProperty("Authorization", "Basic $basicAuth")
                    httpcon.setRequestProperty("Cookie", "nginxauth=$basicAuth")
                }
            }
            httpcon.readTimeout = 10000
            httpcon.connectTimeout = 10000
            httpcon.setRequestProperty("X-HTTP-Method-Override", "PATCH")
            httpcon.requestMethod = "PATCH"
            httpcon.doOutput = true
            httpcon.doInput = true
            httpcon.setRequestProperty("Content-Type", "application/json")
            httpcon.connect()
            val outputStream = httpcon.outputStream
            bufferedWriter = BufferedWriter(OutputStreamWriter(outputStream))
            bufferedWriter.write(json)
            bufferedWriter.flush()
            val status = httpcon.responseCode
            Log.d(LOG_TAG, "Code: > $status")
            reader = if (status < 400) {
                BufferedReader(InputStreamReader(httpcon.inputStream))
                //read response
            } else {
                BufferedReader(InputStreamReader(httpcon.errorStream))
                //read response
            }
            buffer = StringBuilder()
            var line = ""
            while (reader.readLine().also { line = it } != null) {
                buffer.append("""
    $line

    """.trimIndent())
                Log.d(LOG_TAG, "Response: > $line")
            }
        } catch (e: ProtocolException) {
            e.printStackTrace()
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}