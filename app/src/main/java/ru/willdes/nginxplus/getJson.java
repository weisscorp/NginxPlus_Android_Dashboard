package ru.willdes.nginxplus;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import static ru.willdes.nginxplus.nginxplus.COLUMN_ID;
import static ru.willdes.nginxplus.nginxplus.COLUMN_REQUESTS;

public class getJson extends IntentService {
    final String LOG_TAG = "Service";

    //private ListView listView;
    public getJson() {
        super("json");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                int id = ServerConnection.getInstance().getIdconn();
                String ipaddr = ServerConnection.getInstance().getIpaddr();
                String port = ServerConnection.getInstance().getPort();
                String user = ServerConnection.getInstance().getUser();
                String passwd = ServerConnection.getInstance().getPassword();

                HttpURLConnection connection = null;
                BufferedReader reader = null;
                db db;
                db = new db(getJson.this);
                db.open();
            while (true) {
                try {
                    URL url = new URL("http://" + ipaddr + ":" + port + "/api/2/http/upstreams/");
                    connection = (HttpURLConnection) url.openConnection();
                    if (user != "none") {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            String basicAuth = Base64.getEncoder().encodeToString((user + ":" + passwd).getBytes(StandardCharsets.UTF_8));
                            connection.setRequestProperty("Authorization", "Basic " + basicAuth);
                            connection.setRequestProperty("Cookie", "nginxauth=" + basicAuth);
                        }
                    }
                    connection.connect();
                    InputStream stream = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuffer buffer = new StringBuffer();
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                        //Log.d(LOG_TAG, "Response: > " + line);
                    }
                    JSONObject jObject = new JSONObject(buffer.toString().trim());
                    Iterator<?> keys = jObject.keys();

                    while (keys.hasNext()) {
                        String key = (String) keys.next();
                        if (jObject.get(key) instanceof JSONObject) {
                            //Log.d(LOG_TAG, "Keys: > " + key);
                            JSONObject upstreamnow = jObject.getJSONObject(key);
                            JSONArray peers = upstreamnow.getJSONArray("peers");
                            Cursor cur = db.getAllDataFromUpstreamsById(id, key);

                            if (cur.getCount() == 0) {
                                db.addRecToUpstreams(id, key);

                            }
                            cur.close();
                            Cursor curt = db.getAllDataFromUpstreamsById(id, key);
                            curt.moveToFirst();
                            int idupstr = curt.getInt(cur.getColumnIndex(COLUMN_ID));
                            for(int n = 0; n < peers.length(); n++)
                            {
                                JSONObject onesrvonupstr = peers.getJSONObject(n);
                                int idsrv = onesrvonupstr.getInt("id");
                                String nameserver = onesrvonupstr.getString("server");
                                String state = onesrvonupstr.getString("state");
                                int active = onesrvonupstr.getInt("active");
                                int requests = onesrvonupstr.getInt("requests");
                                Cursor srvcur = db.getAllDataFromServersByName(idupstr, nameserver);
                                srvcur.moveToFirst();
                                if (srvcur.getCount() == 0) {
                                    srvcur.close();
                                    db.addRecToServers(idsrv, idupstr, nameserver, state, active, requests, 0);
                                }else{
                                    int idsrvrow = srvcur.getInt(srvcur.getColumnIndex("_id"));
                                    int requestsold = srvcur.getInt(srvcur.getColumnIndex(COLUMN_REQUESTS));
                                    int rps = requests - requestsold;
                                    if (rps < 0) {
                                        rps = 0;
                                    }
                                    //Log.d("SRV", nameserver + " old: " + requestsold + " now: " + requests + " persec: " + rps);
                                    db.changeRecToServers(idsrvrow, idsrv, idupstr, nameserver, state, active, requests, rps);
                                    srvcur.close();
                                }
                                srvcur.close();
                            }
                            curt.close();
                        }
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }//for { while
            }
        }).start();
    }

    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
        super.onDestroy();

    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
