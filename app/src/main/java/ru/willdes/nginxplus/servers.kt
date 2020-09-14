package ru.willdes.nginxplus

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import ru.willdes.nginxplus.nginxplus.COLUMN_ACTIVE
import ru.willdes.nginxplus.nginxplus.COLUMN_REQPSEC
import ru.willdes.nginxplus.nginxplus.COLUMN_SERVER
import ru.willdes.nginxplus.nginxplus.COLUMN_STATE
import java.util.*

class servers : AppCompatActivity() {
    var db: db? = null
    val LOG_TAG = "servers"
    var list: MutableList<ServersModel> = ArrayList()
    var adapter = ServersAdapter(this, list)
    val idupstr = UpstreamName.upstreamName?.idupstr
    var t: Thread? = null
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_activity_servers, menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_servers)
        val mActionBarToolbar = findViewById<Toolbar>(R.id.toolbar_actionbar)
        setSupportActionBar(mActionBarToolbar)
        title = UpstreamName.upstreamName?.upstrname
        create_servers()
    }

    override fun onDestroy() {
        super.onDestroy()
        //db.close();
        if (t != null) {
            t!!.interrupt()
            t = null
        }
    }

    override fun onRestart() {
        super.onRestart()
        if (t == null) {
            refresh_servers()
        } else {
            t = null
            refresh_servers()
        }
    }

    override fun onResume() {
        super.onResume()
        if (idupstr == 0) {
            db!!.close()
            finish()
            val i = Intent(this, AllConnections::class.java)
            this.startActivity(i)
        }
        if (t == null) {
            Log.d("onResume", "start")
            refresh_servers()
        }
    }

    override fun onPause() {
        super.onPause()
        if (t != null) {
            t!!.interrupt()
            t = null
        }
    }

    fun action_refresh(item: MenuItem?) {
        refresh_servers()
    }

    private fun create_servers() {
        val db = db(this)
        db.open()
        val serverscur = db.getAllDataFromServersByIdupstr(idupstr)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        if (serverscur.count != 0) {
            serverscur.moveToFirst()
            var pos = 0
            do {
                val name = serverscur.getString(serverscur.getColumnIndex(COLUMN_SERVER))
                val state = serverscur.getString(serverscur.getColumnIndex(COLUMN_STATE))
                val active = serverscur.getInt(serverscur.getColumnIndex(COLUMN_ACTIVE))
                val requests = serverscur.getInt(serverscur.getColumnIndex(COLUMN_REQPSEC))
                val item = ServersModel(name, state, active, requests)
                list.add(pos, item)
                pos++
            } while (serverscur.moveToNext())
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter
            db.close()
        } else {
            val linearLayout = findViewById<LinearLayout>(R.id.linearlayoutServers)
            val textView = TextView(this)
            textView.gravity = Gravity.CENTER
            textView.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            textView.gravity = Gravity.CENTER
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.result_font))
            textView.setText(R.string.empty_servers)
            linearLayout.addView(textView)
        }
    }

    fun update_servers() {
        val db = db(this)
        db.open()
        if (idupstr == 0) {
            db.close()
            finish()
            val i = Intent(this, AllConnections::class.java)
            this.startActivity(i)
        }
        val serverscur = db.getAllDataFromServersByIdupstr(idupstr)
        serverscur.moveToFirst()
        var pos = 0
        do {
            val name = serverscur.getString(serverscur.getColumnIndex(COLUMN_SERVER))
            val state = serverscur.getString(serverscur.getColumnIndex(COLUMN_STATE))
            val active = serverscur.getInt(serverscur.getColumnIndex(COLUMN_ACTIVE))
            val requests = serverscur.getInt(serverscur.getColumnIndex(COLUMN_REQPSEC))
            val item = ServersModel(name, state, active, requests)
            //list.remove(pos);
            list[pos] = item
            pos++
        } while (serverscur.moveToNext())
        adapter.notifyDataSetChanged()
        db.close()
    }

    fun refresh_servers() {
        if (t != null) {
            t!!.interrupt()
            t = null
        }
        t = object : Thread() {
            override fun run() {
                try {
                    while (!isInterrupted) {
                        sleep(1000)
                        runOnUiThread { update_servers() }
                    }
                } catch (e: InterruptedException) {
                }
            }
        }
        (t as Thread).start()
    }
}