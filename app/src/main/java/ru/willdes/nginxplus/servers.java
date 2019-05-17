package ru.willdes.nginxplus;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
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
    db db;
    final String LOG_TAG = "servers";
    RecyclerView recyclerView = findViewById(R.id.recyclerview);
    List<ServersModel> list = new ArrayList<>();
    ServersAdapter adapter = new ServersAdapter(this, list);
    final int idupstr = UpstreamName.getUpstreamName().getIdupstr();


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_servers, menu);
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servers);
        Toolbar mActionBarToolbar = findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mActionBarToolbar);
        setTitle(UpstreamName.getUpstreamName().getUpstrname());
        create_servers();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();

    }

    @Override
    protected void onRestart(){
        super.onRestart();
        create_servers();
    }

    @Override
    protected void onResume(){
        super.onResume();
        create_servers();
    }

    public void action_refresh(MenuItem item) {
        update_servers();
    }

    private void create_servers() {
        db db = new db(this);
        db.open();
        Cursor serverscur = db.getAllDataFromServersByIdupstr(idupstr);
        if (serverscur.getCount() != 0) {
            serverscur.moveToFirst();
            do {
                String name = serverscur.getString(serverscur.getColumnIndex(COLUMN_SERVER));
                String state = serverscur.getString(serverscur.getColumnIndex(COLUMN_STATE));
                int active = serverscur.getInt(serverscur.getColumnIndex(COLUMN_ACTIVE));
                int requests = serverscur.getInt(serverscur.getColumnIndex(COLUMN_REQPSEC));
                ServersModel item = new ServersModel(name, state, active, requests);
                list.add(item);
            } while (serverscur.moveToNext());
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        } else {
            LinearLayout linearLayout = findViewById(R.id.linearlayoutServers);
            TextView textView = new TextView(this);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.result_font));
            textView.setText(R.string.empty_servers);
            linearLayout.addView(textView);
        }
        db.close();
    }
        private void update_servers() {
            db db = new db(this);
            db.open();
            Cursor serverscur = db.getAllDataFromServersByIdupstr(idupstr);
            serverscur.moveToFirst();
            do {
                String name = serverscur.getString(serverscur.getColumnIndex(COLUMN_SERVER));
                String state = serverscur.getString(serverscur.getColumnIndex(COLUMN_STATE));
                int active = serverscur.getInt(serverscur.getColumnIndex(COLUMN_ACTIVE));
                int requests = serverscur.getInt(serverscur.getColumnIndex(COLUMN_REQPSEC));
                ServersModel item = new ServersModel(name, state, active, requests);
                list.add(item);
            } while (serverscur.moveToNext());
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            db.close();
        }
}