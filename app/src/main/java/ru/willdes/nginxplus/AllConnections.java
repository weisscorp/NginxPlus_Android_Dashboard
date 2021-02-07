package ru.willdes.nginxplus;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static ru.willdes.nginxplus.nginxplus.COLUMN_ADDRESS;
import static ru.willdes.nginxplus.nginxplus.COLUMN_ID;
import static ru.willdes.nginxplus.nginxplus.COLUMN_NAME;
import static ru.willdes.nginxplus.nginxplus.COLUMN_PASSWD;
import static ru.willdes.nginxplus.nginxplus.COLUMN_PORT;
import static ru.willdes.nginxplus.nginxplus.COLUMN_USER;

public class AllConnections extends AppCompatActivity {
    db db;
    final String LOG_TAG = "myLogs";
    Intent startserv;

    @SuppressLint({"RtlHardcoded", "SetTextI20n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connections);
        Toolbar mActionBarToolbar = findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mActionBarToolbar);
        setTitle(R.string.title_activity_connections);
        LinearLayout linearLayout = findViewById(R.id.allconnections);
        startserv = new Intent(this, getJson.class);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(AllConnections.this, AddConnections.class);
            startActivityForResult(intent, 1);
        });


        db = new db(this);
        db.open();
        final Cursor cur = db.getAllDataFromConnections();
        if (cur.getCount() == 0) {
            TextView textView = new TextView(this);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.result_font));
            textView.setText(R.string.empty_servers);
            linearLayout.addView(textView);
        } else {
            cur.moveToFirst();
            do {
                final int _id = cur.getInt(cur.getColumnIndex("id"));
                final String connname = cur.getString(cur.getColumnIndex(COLUMN_NAME));
                final String conAddr = cur.getString(cur.getColumnIndex(COLUMN_ADDRESS));
                final String conPort = cur.getString(cur.getColumnIndex(COLUMN_PORT));
                final String conUser = cur.getString(cur.getColumnIndex(COLUMN_USER));
                final String conPasswd = cur.getString(cur.getColumnIndex(COLUMN_PASSWD));
                Button button = new Button(this);
                button.setGravity(Gravity.CENTER);
                button.setTextSize(16);
                button.setText(connname);
                button.setId(_id);
                linearLayout.addView(button);
                registerForContextMenu(button);
                final Intent intent = new Intent(this, upstreams.class);
                button.setOnClickListener(view -> {
                    ServerConnection.getInstance().setIdconn(_id);
                    ServerConnection.getInstance().setIpaddr(conAddr);
                    ServerConnection.getInstance().setPort(conPort);
                    ServerConnection.getInstance().setUser(conUser);
                    ServerConnection.getInstance().setPassword(conPasswd);
                    ServerConnection.getInstance().setConname(connname);
                    startActivity(intent);
                    startService(startserv);
                });
            } while (cur.moveToNext());
            cur.close();
            db.close();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO: check service started
        if (startserv == null)
            //stopService(startserv);
            Log.d(LOG_TAG, "ALLconnections call onResume");
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Nginx сервер");
        menu.add(0, v.getId(), 0, "Редактировать");
        menu.add(0, v.getId(), 0, "Дублировать");
        menu.add(0, v.getId(), 0, "Обнулить");
        menu.add(0, v.getId(), 0, "Удалить");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Редактировать") {
            edit(item.getItemId());
        } else if (item.getTitle() == "Дублировать") {
            dublicate(item.getItemId());
        } else if (item.getTitle() == "Обнулить") {
            erase(item.getItemId());
        } else if (item.getTitle() == "Удалить") {
            delete(item.getItemId());
        } else {
            return false;
        }
        return true;
    }

    public void delete(int id) {
        db.open();
        Cursor cursor = db.getAllDataFromUpstreamsByServerId(id);
        cursor.moveToFirst();
        if (cursor.getCount() != 0) {
            do {
                int idupstr = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                db.delRecServers(idupstr);
            } while (cursor.moveToNext());
            db.delRecUpstream(id);
        }
        db.delRec(id);
        db.close();
        finish();
        Intent i = new Intent(this, this.getClass());
        this.startActivity(i);
    }

    public void edit(int id) {
        Intent intent = new Intent(this, EditConnections.class);
        startActivityForResult(intent.putExtra("id", id), 1);

    }


    public void erase(int id) {
        db.open();
        Cursor cursor = db.getAllDataFromUpstreamsByServerId(id);
        cursor.moveToFirst();
        if (cursor.getCount() != 0) {
            do {
                int idupstr = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                db.delRecServers(idupstr);
            } while (cursor.moveToNext());
            db.delRecUpstream(id);
        }
        db.close();
        Toast toast = Toast.makeText(getApplicationContext(),
                R.string.cleantext, Toast.LENGTH_SHORT);
        toast.show();


    }


    public void dublicate(int id) {
        Intent intent = new Intent(this, DubConnections.class);
        startActivityForResult(intent.putExtra("id", id), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finish();
        Intent i = new Intent(this, this.getClass());
        this.startActivity(i);

    }
}

