package ru.willdes.nginxplus

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast


class AllConnections : AppCompatActivity() {
    var db: db? = null
    val LOG_TAG = "myLogs"
    var startserv: Intent? = null

    @SuppressLint("RtlHardcoded", "SetTextI20n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connections)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val linearLayout = findViewById<LinearLayout>(R.id.allconnections)
        startserv = Intent(this, getJson::class.java)
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@AllConnections, AddConnections::class.java)
            startActivityForResult(intent, 1)
        }
        db = db(this)
        db!!.open()
        val cur = db!!.allDataFromConnections
        if (cur.count == 0) {
            val textView = TextView(this)
            textView.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            textView.gravity = Gravity.CENTER
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.result_font))
            textView.setText(R.string.empty_servers)
            linearLayout.addView(textView)
        } else {
            cur.moveToFirst()
            do {
                val lParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
                val button = Button(this)
                button.gravity = Gravity.CENTER
                button.textSize = 16f
                val connname = cur.getString(cur.getColumnIndex(nginxplus.COLUMN_NAME))
                button.text = connname
                //button.setBackgroundResource(R.drawable.round_button);
                val _id = cur.getInt(cur.getColumnIndex("id"))
                val conAddr = cur.getString(cur.getColumnIndex(nginxplus.COLUMN_ADDRESS))
                val conPort = cur.getString(cur.getColumnIndex(nginxplus.COLUMN_PORT))
                val conUser = cur.getString(cur.getColumnIndex(nginxplus.COLUMN_USER))
                val conPasswd = cur.getString(cur.getColumnIndex(nginxplus.COLUMN_PASSWD))
                button.id = _id
                linearLayout.addView(button, lParam)
                val btn1 = findViewById<View>(_id)
                registerForContextMenu(btn1)
                val intent = Intent(this, upstreams::class.java)
                btn1.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(view: View) {
                        ServerConnection.instance?.idconn = _id
                        ServerConnection.instance?.ipaddr = conAddr
                        ServerConnection.instance?.port = conPort
                        ServerConnection.instance?.user = conUser
                        ServerConnection.instance?.password = conPasswd
                        ServerConnection.instance?.conname = connname
                        startActivity(intent)
                        //processStartService(getJson.TAG);
                        startService(startserv)
                        Log.d(LOG_TAG, "Start Service getJson")
                    }

                    private fun processStartService(tag: String) {
                        val intent = Intent(applicationContext, getJson::class.java)
                        intent.addCategory(tag)
                        startService(intent)
                        Log.d(LOG_TAG, "Call function startService getJson")
                    }
                })
            } while (cur.moveToNext())
            cur.close()
            db!!.close()
        }
    }

    override fun onResume() {
        super.onResume()
        // TODO: check service started
        if (startserv == null) //stopService(startserv);
            Log.d(LOG_TAG, "ALLconnections call onResume")
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu?.setHeaderTitle("Nginx сервер")
        v?.id?.let { menu?.add(0, it, 0, "Редактировать") }
        v?.id?.let { menu?.add(0, it, 0, "Дублировать") }
        v?.id?.let { menu?.add(0, it, 0, "Обнулить") }
        v?.id?.let { menu?.add(0, it, 0, "Удалить") }
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        if (item?.title === "Редактировать") {
            edit(item?.itemId)
        } else if (item?.title === "Дублировать") {
            dublicate(item?.itemId)
        } else if (item?.title === "Обнулить") {
            erase(item?.itemId)
        } else if (item?.title === "Удалить") {
            delete(item?.itemId)
        } else {
            return false
        }
        return true
    }

    fun delete(id: Int) {
        db!!.open()
        val cursor = db!!.getAllDataFromUpstreamsByServerId(id)
        cursor.moveToFirst()
        if (cursor.count != 0) {
            do {
                val idupstr = cursor.getInt(cursor.getColumnIndex(nginxplus.COLUMN_ID))
                db!!.delRecServers(idupstr)
            } while (cursor.moveToNext())
            db!!.delRecUpstream(id)
        }
        db!!.delRec(id)
        db!!.close()
        finish()
        val i = Intent(this, this.javaClass)
        this.startActivity(i)
    }

    fun edit(id: Int) {
        val intent = Intent(this, EditConnections::class.java)
        startActivityForResult(intent.putExtra("id", id), 1)
    }

    fun erase(id: Int) {
        db!!.open()
        val cursor = db!!.getAllDataFromUpstreamsByServerId(id)
        cursor.moveToFirst()
        if (cursor.count != 0) {
            do {
                val idupstr = cursor.getInt(cursor.getColumnIndex(nginxplus.COLUMN_ID))
                db!!.delRecServers(idupstr)
            } while (cursor.moveToNext())
            db!!.delRecUpstream(id)
        }
        db!!.close()
        val toast = Toast.makeText(applicationContext,
                R.string.cleantext, Toast.LENGTH_SHORT)
        toast.show()
    }

    fun dublicate(id: Int) {
        val intent = Intent(this, DubConnections::class.java)
        startActivityForResult(intent.putExtra("id", id), 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        finish()
        val i = Intent(this, this.javaClass)
        this.startActivity(i)
    }
}