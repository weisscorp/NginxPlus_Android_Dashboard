package ru.willdes.nginxplus

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast

class AddConnections : AppCompatActivity(), CompoundButton.OnCheckedChangeListener {
    var db: db? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_connections)
        title = "Add new connection"
        val s = findViewById<Switch>(R.id.auth)
        s?.setOnCheckedChangeListener(this)
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        //Toast.makeText(this, "The Switch is " + (isChecked ? "on" : "off"),Toast.LENGTH_SHORT).show();
        if (isChecked) {
            val edUser = findViewById<EditText>(R.id.edUser)
            val edPasswd = findViewById<EditText>(R.id.edPasswd)
            edUser.isFocusable = true
            edUser.isEnabled = true
            edUser.isCursorVisible = true
            edUser.setText("")
            edPasswd.isFocusable = true
            edPasswd.isEnabled = true
            edPasswd.isCursorVisible = true
            edPasswd.setText("")
        } else {
            val edUser = findViewById<EditText>(R.id.edUser)
            val edPasswd = findViewById<EditText>(R.id.edPasswd)
            edUser.isFocusable = false
            edUser.isEnabled = false
            edUser.isCursorVisible = false
            edUser.setText("none")
            edPasswd.isFocusable = false
            edPasswd.isEnabled = false
            edPasswd.isCursorVisible = false
            edPasswd.setText("none")
        }
    }

    fun addNewServer() {
        val displayName = findViewById<EditText>(R.id.displayName)
        val edAddress = findViewById<EditText>(R.id.edAddress)
        val edPort = findViewById<EditText>(R.id.edPort)
        val edUser = findViewById<EditText>(R.id.edUser)
        val edPasswd = findViewById<EditText>(R.id.edPasswd)
        val sDisplayName = displayName.text.toString()
        val sAddress = edAddress.text.toString()
        val sPort = edPort.text.toString()
        val sUser = edUser.text.toString()
        val sPasswd = edPasswd.text.toString()
        val intent = Intent()
        if (displayName.text.toString() == "") {
            Toast.makeText(this, "Пустое поле имени", Toast.LENGTH_LONG).show()
        } else {
            if (edAddress.text.toString() == "") {
                Toast.makeText(this, "Пустое поле адреса", Toast.LENGTH_LONG).show()
            } else {
                db = db(this)
                db!!.open()
                db!!.addNewServer(sDisplayName, sAddress, sPort, sUser, sPasswd)
                db!!.close()
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    fun goBack() {
        val intent = Intent()
        setResult(Activity.RESULT_CANCELED, intent)
        finish()
    }
}