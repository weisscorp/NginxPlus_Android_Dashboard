package ru.willdes.nginxplus;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static ru.willdes.nginxplus.nginxplus.COLUMN_ACTIVE;
import static ru.willdes.nginxplus.nginxplus.COLUMN_REQPSEC;
import static ru.willdes.nginxplus.nginxplus.COLUMN_SERVER;
import static ru.willdes.nginxplus.nginxplus.COLUMN_STATE;

public class servers extends AppCompatActivity {
    final String LOG_TAG = "servers";
    db db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servers);
        final int idupstr = UpstreamName.getUpstreamName().getIdupstr();
        setTitle(UpstreamName.getUpstreamName().getUpstrname());
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
            db = new db(this);
            db.open();
            Cursor serverscur = db.getAllDataFromServersByIdupstr(idupstr);
            List<ServersModel> list = new ArrayList<>();
            if (serverscur.getCount() == 0) {
                LinearLayout linearLayout = findViewById(R.id.linearlayoutServers);
                TextView textView = new TextView(this);
                textView.setGravity(Gravity.CENTER);
                textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                textView.setGravity(Gravity.CENTER);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.result_font));
                textView.setText(R.string.empty_servers);
                linearLayout.addView(textView);
            } else {
                serverscur.moveToFirst();
                do {
                    String name = serverscur.getString(serverscur.getColumnIndex(COLUMN_SERVER));
                    String state = serverscur.getString(serverscur.getColumnIndex(COLUMN_STATE));
                    int active = serverscur.getInt(serverscur.getColumnIndex(COLUMN_ACTIVE));
                    int requests = serverscur.getInt(serverscur.getColumnIndex(COLUMN_REQPSEC));
                    ServersModel item = new ServersModel(name, state, active, requests);
                    list.add(item);
                } while (serverscur.moveToNext());
                ServersAdapter adapter = new ServersAdapter(this, list);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(adapter);
            }
            db.close();
    }

    protected void onDestroy() {
        super.onDestroy();
        db.close();

    }

}

