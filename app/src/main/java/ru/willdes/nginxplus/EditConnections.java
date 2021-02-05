package ru.willdes.nginxplus;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
        db = new db(this);
        db.open();
        Cursor cursor = db.getConnWhereId(id);
        cursor.moveToFirst();
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

        Toolbar mActionBarToolbar = findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mActionBarToolbar);
        setTitle("Edit " + conName);

        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch s = findViewById(R.id.auth);
        displayName.setText(conName);
        edAddress.setText(conAddr);
        edPort.setText(conPort);
        if (conUser.equals("none")) {
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

        Button btn1 = findViewById(R.id.clean);
        btn1.setVisibility(View.VISIBLE);
        btn1.setOnClickListener(view -> {
            db.open();
            Cursor cursor1 = db.getAllDataFromUpstreamsByServerId(id);
            cursor1.moveToFirst();
            if (cursor1.getCount() != 0) {
                do {
                    int idupstr = cursor1.getInt(cursor1.getColumnIndex(COLUMN_ID));
                    db.delRecServers(idupstr);
                } while (cursor1.moveToNext());
                db.delRecUpstream(id);
            }
            db.close();
            Toast toast = Toast.makeText(getApplicationContext(),
                    R.string.cleantext, Toast.LENGTH_SHORT);
            toast.show();
        });


        db.close();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
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