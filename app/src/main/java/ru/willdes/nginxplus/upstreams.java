package ru.willdes.nginxplus;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import static ru.willdes.nginxplus.nginxplus.COLUMN_ID;
import static ru.willdes.nginxplus.nginxplus.COLUMN_NAME;

public class upstreams extends AppCompatActivity {

    final String LOG_TAG = "myLogs";
    final int idconn = ServerConnection.getInstance().getIdconn();

    db db;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_servers, menu);
        return true;
    }

    public void action_refresh(MenuItem item) {
        db.close();
        finish();
        Intent i = new Intent(this, this.getClass());
        this.startActivity(i);

        startService(new Intent(this, getJson.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upstreams);
        final LinearLayout linearLayout = findViewById(R.id.upstreams);
        final LinearLayout.LayoutParams lParam = new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        final TextView textView = new TextView(this);
        textView.setGravity(Gravity.CENTER);
        setTitle(ServerConnection.getInstance().getConname());


        db = new db(this);
        db.open();
        Cursor cur = db.getAllDataFromUpstreamsByServerId(idconn);
        if (cur.getCount() == 0) {
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.result_font));
            textView.setText(R.string.empty_upstreams);
            linearLayout.addView(textView);
        } else {
            cur.moveToFirst();
            do {
                Button button = new Button(this);
                button.setGravity(Gravity.CENTER);
                final String name = cur.getString(cur.getColumnIndex(COLUMN_NAME));
                final int idupstr = cur.getInt(cur.getColumnIndex(COLUMN_ID));
                button.setTextSize(16);
                button.setText(name);
                //button.setBackgroundResource(R.drawable.round_button);
                //button.setBackgroundColor(getResources().getColor(R.color.colorBackgoundBlack));
                button.setId(idupstr);
                //Log.d(LOG_TAG, "Name: " + name);
                //Log.d(LOG_TAG, "ID Upstream: " + idupstr);


                linearLayout.addView(button, lParam);
                View btn1 = findViewById(idupstr);
                final Intent intent = new Intent(this, servers.class);
                btn1.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        UpstreamName.getUpstreamName().setIdupstr(idupstr);
                        UpstreamName.getUpstreamName().setUpstrname(name);
                        startActivity(intent);
                        //Log.d(LOG_TAG, "Go to servers in to upstream: " + name);
                    }
                });
            } while (cur.moveToNext());
            cur.close();
        }

    }

    protected void onDestroy() {
        super.onDestroy();
        // закрываем подключение при выходе
        ServerConnection.getInstance().setIdconn(0);
        db.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (idconn == 0) {
            db.close();
            finish();
            Intent i = new Intent(this, AllConnections.class);
            this.startActivity(i);
        }
    }
}