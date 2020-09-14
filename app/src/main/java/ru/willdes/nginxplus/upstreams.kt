package ru.willdes.nginxplus

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import ru.willdes.nginxplus.ServerConnection.Companion.instance
import ru.willdes.nginxplus.getJson
import ru.willdes.nginxplus.nginxplus.COLUMN_ID
import ru.willdes.nginxplus.nginxplus.COLUMN_NAME

class upstreams : AppCompatActivity() {
    val LOG_TAG = "myLogs"
    val idconn = instance!!.idconn
    var db: db? = null
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_activity_servers, menu)
        return true
    }

    fun action_refresh(item: MenuItem?) {
        db!!.close()
        finish()
        val i = Intent(this, this.javaClass)
        this.startActivity(i)
        startService(Intent(this, getJson::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upstreams)
        val linearLayout = findViewById<LinearLayout>(R.id.upstreams)
        val lParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        val textView = TextView(this)
        textView.gravity = Gravity.CENTER
        title = instance!!.conname
        db = db(this)
        db!!.open()
        val cur = db!!.getAllDataFromUpstreamsByServerId(idconn)
        if (cur.count == 0) {
            textView.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            textView.gravity = Gravity.CENTER
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.result_font))
            textView.setText(R.string.empty_upstreams)
            linearLayout.addView(textView)
        } else {
            cur.moveToFirst()
            do {
                val button = Button(this)
                button.gravity = Gravity.LEFT
                val name = cur.getString(cur.getColumnIndex(COLUMN_NAME))
                val idupstr = cur.getInt(cur.getColumnIndex(COLUMN_ID))
                button.textSize = 16f
                button.text = "                                  $name"
                button.id = idupstr
                linearLayout.addView(button, lParam)
                val btn1 = findViewById<View>(idupstr)
                val intent = Intent(this, servers::class.java)
                btn1.setOnClickListener {
                    UpstreamName.upstreamName?.idupstr = idupstr
                    UpstreamName.upstreamName?.upstrname = name
                    startActivity(intent)
                }
            } while (cur.moveToNext())
            cur.close()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // закрываем подключение при выходе
        instance!!.idconn = 0
        db!!.close()
    }

    override fun onResume() {
        super.onResume()
        if (idconn == 0) {
            db!!.close()
            finish()
            val i = Intent(this, AllConnections::class.java)
            this.startActivity(i)
        }
    }
}