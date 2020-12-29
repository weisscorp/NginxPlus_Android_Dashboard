package ru.willdes.nginxplus;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import static ru.willdes.nginxplus.nginxplus.COLUMN_ADDRESS;
import static ru.willdes.nginxplus.nginxplus.COLUMN_ID;
import static ru.willdes.nginxplus.nginxplus.COLUMN_NAME;
import static ru.willdes.nginxplus.nginxplus.COLUMN_PASSWD;
import static ru.willdes.nginxplus.nginxplus.COLUMN_PORT;
import static ru.willdes.nginxplus.nginxplus.COLUMN_USER;

public class EditConnections extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    db db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_connections);
        Intent getintent = getIntent();
        int id = getintent.getIntExtra("id", 0);
        setTitle("Edit " + id);
        db = new db(this);
        db.open();
        Cursor cursor = db.getConnWhereId(id);
        cursor.moveToFirst();
        Switch s = findViewById(R.id.auth);

        EditText displayName = findViewById(R.id.displayName);
        EditText edAddress = findViewById(R.id.edAddress);
        EditText edPort = findViewById(R.id.edPort);
        EditText edUser = findViewById(R.id.edUser);
        EditText edPasswd = findViewById(R.id.edPasswd);

        String conAddr = cursor.getString(cursor.getColumnIndex(COLUMN_ADDRESS));
        String conPort = cursor.getString(cursor.getColumnIndex(COLUMN_PORT));
        String conUser = cursor.getString(cursor.getColumnIndex(COLUMN_USER));
        String conPasswd = cursor.getString(cursor.getColumnIndex(COLUMN_PASSWD));
        String conName = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));

        displayName.setText(conName);
        edAddress.setText(conAddr);
        edPort.setText(conPort);
        if (conUser == "none") {
            s.setChecked(false);

        } else {
            s.setChecked(true);
            edUser.setFocusable(true);
            edUser.setEnabled(true);
            edUser.setCursorVisible(true);
            edPasswd.setFocusable(true);
            edPasswd.setEnabled(true);
            edPasswd.setCursorVisible(true);
            edUser.setText(conUser);
            edPasswd.setText(conPasswd);
        }


        if (s != null) {
            s.setOnCheckedChangeListener(this);
        }

        Button button = new Button(this);
        button.setGravity(Gravity.CENTER);
        button.setTextSize(16);
        button.setText("Отчистить");
        //button.setBackgroundResource(R.drawable.round_button);
        int _id = 10000;
        button.setId(_id);
        LinearLayout linearLayout = findViewById(R.id.leaneraddconn);
        linearLayout.addView(button);
        final View btn1 = findViewById(_id);
        registerForContextMenu(btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
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
        });


        db.close();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //Toast.makeText(this, "The Switch is " + (isChecked ? "on" : "off"),Toast.LENGTH_SHORT).show();
        if (isChecked) {
            final EditText edUser = findViewById(R.id.edUser);
            final EditText edPasswd = findViewById(R.id.edPasswd);
            edUser.setFocusable(true);
            edUser.setEnabled(true);
            edUser.setCursorVisible(true);
            edPasswd.setFocusable(true);
            edPasswd.setEnabled(true);
            edPasswd.setCursorVisible(true);

        } else {
            final EditText edUser = findViewById(R.id.edUser);
            final EditText edPasswd = findViewById(R.id.edPasswd);
            edUser.setFocusable(false);
            edUser.setEnabled(false);
            edUser.setCursorVisible(false);
            edUser.setText("none");
            edPasswd.setFocusable(false);
            edPasswd.setEnabled(false);
            edPasswd.setCursorVisible(false);
            edPasswd.setText("none");
        }
    }

    public void addNewServer(View view) {
        Intent getintent = getIntent();
        int id = getintent.getIntExtra("id", 0);
        EditText displayName = findViewById(R.id.displayName);
        EditText edAddress = findViewById(R.id.edAddress);
        EditText edPort = findViewById(R.id.edPort);
        EditText edUser = findViewById(R.id.edUser);
        EditText edPasswd = findViewById(R.id.edPasswd);
        String sDisplayName = displayName.getText().toString();
        String sAddress = edAddress.getText().toString();
        String sPort = edPort.getText().toString();
        String sUser = edUser.getText().toString();
        String sPasswd = edPasswd.getText().toString();
        Intent intent = new Intent();

        if (displayName.getText().toString().equals("")) {
            Toast.makeText(this, "Пустое поле имени", Toast.LENGTH_LONG).show();
        } else {
            if (edAddress.getText().toString().equals("")) {
                Toast.makeText(this, "Пустое поле адреса", Toast.LENGTH_LONG).show();
            } else {
                db = new db(this);
                db.open();
                db.changeRec(id, sDisplayName, sAddress, sPort, sUser, sPasswd);
                db.close();
            }
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    public void goBack(View view) {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }


}