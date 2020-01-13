package ru.willdes.nginxplus;

import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class POSTtoServer {
    private static final String LOG_TAG = "POSTtoServer";
    private int idsrv;
    private String state;
    private String srvname;
    private String upstream;
    private String ipaddr;
    private String port;
    private String user;
    private String password;

    public POSTtoServer(int idsrv, String srvname, String state) {
        this.idsrv = idsrv;
        this.state = state;
        this.srvname = srvname;
        this.upstream = UpstreamName.getUpstreamName().getUpstrname();
        this.ipaddr = ServerConnection.getInstance().getIpaddr();
        this.port = ServerConnection.getInstance().getPort();
        this.user = ServerConnection.getInstance().getUser();
        this.password = ServerConnection.getInstance().getPassword();
        Log.d("POST", "id = " + this.idsrv + ", set state = " + this.state + ", upstream name = " + this.upstream);
        Log.d("SRVConn", ipaddr);
        String json = null;
        switch (this.state) {
            case "up":
                json = "{\"server\":\""+this.srvname+"\",\"down\":false}";
                break;
            case "down":
                json = "{\"server\":\""+this.srvname+"\",\"down\":true}";
                break;
            case "drain":
                json = "{\"server\":\""+this.srvname+"\",\"drain\":true}";
                break;    
        }

        BufferedReader reader = null;
        BufferedWriter bufferedWriter = null;
        StringBuilder buffer = null;
        try {
            URL url = new URL("http://" + this.ipaddr + ":" + this.port + "/api/2/http/upstreams/" + this.upstream + "/servers/" + this.idsrv);
            HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
            //Log.d("HTTP", url.toString());
            //Log.d("JSON", json);
            if (user != "none") {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    String basicAuth = Base64.getEncoder().encodeToString((user + ":" + password).getBytes(StandardCharsets.UTF_8));
                    httpcon.setRequestProperty("Authorization", "Basic " + basicAuth);
                    httpcon.setRequestProperty("Cookie", "nginxauth=" + basicAuth);
                }
            }
            httpcon.setReadTimeout(10000);
            httpcon.setConnectTimeout(10000);
            httpcon.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            httpcon.setRequestMethod("PATCH");
            httpcon.setDoOutput(true);
            httpcon.setDoInput(true);
            httpcon.setRequestProperty("Content-Type", "application/json");
            httpcon.connect();

            OutputStream outputStream = httpcon.getOutputStream();
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            bufferedWriter.write(json);
            bufferedWriter.flush();

            int status =httpcon.getResponseCode();
            Log.d(LOG_TAG, "Code: > " + status);

            if(status<400){
                reader = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
                //read response
            }else{
                reader = new BufferedReader(new InputStreamReader(httpcon.getErrorStream()));
                //read response
            }

            buffer = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
                Log.d(LOG_TAG, "Response: > " + line);
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

}