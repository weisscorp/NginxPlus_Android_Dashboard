package ru.willdes.nginxplus;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import static ru.willdes.nginxplus.nginxplus.COLUMN_ID;
import static ru.willdes.nginxplus.nginxplus.COLUMN_NAME;

public class upstreams extends AppCompatActivity {

    final String LOG_TAG = "myLogs";

    db db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upstreams);
        final LinearLayout linearLayout = findViewById(R.id.upstreams);
        final LinearLayout.LayoutParams lParam = new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        final TextView textView = new TextView(this);
        textView.setGravity(Gravity.CENTER);
        Intent getintent = getIntent();
        String connname = getintent.getStringExtra("connname");
        setTitle(connname);


        db = new db(this);
        db.open();
        Cursor cur = db.getAllDataFromUpstreamsByServerId(ServerConnection.getInstance().getIdconn());
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
                button.setText(name);
                button.setBackgroundResource(R.drawable.round_button);
                button.setId(idupstr);
                Log.d(LOG_TAG, "Name: " + name);
                Log.d(LOG_TAG, "ID Upstream: " + idupstr);


                linearLayout.addView(button, lParam);
                View btn1 = findViewById(idupstr);
                final Intent intent = new Intent(this, servers.class);
                btn1.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        UpstreamName.getUpstreamName().setIdupstr(idupstr);
                        UpstreamName.getUpstreamName().setUpstrname(name);
                        startActivity(intent);
                        Log.d(LOG_TAG, "Go to servers in to upstream: " + name);
                    }
                });
            } while (cur.moveToNext());
            cur.close();
        }

    }
    protected void onDestroy() {
        super.onDestroy();
        // закрываем подключение при выходе

        db.close();
    }
}