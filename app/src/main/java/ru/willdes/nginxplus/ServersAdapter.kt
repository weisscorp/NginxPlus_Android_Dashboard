package ru.willdes.nginxplus

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import ru.willdes.nginxplus.ServersAdapter.ServersViewHolder
import java.util.*

class ServersAdapter(context: Context, list: List<ServersModel>) : RecyclerView.Adapter<ServersViewHolder>() {
    private var list: List<ServersModel> = ArrayList()
    private val context: Context
    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ServersViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_servers, parent, false)
        return ServersViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(serversViewHolder: ServersViewHolder, i: Int) {
        //Log.d(TAG, "onBindViewHolder: called.");
        val temp = list[i]
        serversViewHolder.srvname.text = "       " + temp.name
        serversViewHolder.twActive.text = "" + temp.active
        serversViewHolder.twRequests.text = "" + temp.requests
        serversViewHolder.twActive.setBackgroundResource(android.R.color.transparent)
        serversViewHolder.twRequests.setBackgroundResource(android.R.color.transparent)
        when (temp.state) {
            "up" -> {
                serversViewHolder.ivStat.setColorFilter(context.resources.getColor(R.color.colorUp))
                if (temp.active >= 2 && temp.requests == 0) {
                    Log.d("Orange", "name: " + temp.name + "; id: " + i + " set orange color")
                    serversViewHolder.twActive.setBackgroundResource(R.color.orange)
                    serversViewHolder.twRequests.setBackgroundResource(R.color.orange)
                }
            }
            "down" -> serversViewHolder.ivStat.setColorFilter(context.resources.getColor(R.color.colorDown))
            "unavail" -> serversViewHolder.ivStat.setColorFilter(context.resources.getColor(R.color.colorRemove))
            "draining" -> serversViewHolder.ivStat.setColorFilter(context.resources.getColor(R.color.colorDrain))
            "checking" -> serversViewHolder.ivStat.setColorFilter(context.resources.getColor(R.color.colorUp))
            "unhealthy" -> serversViewHolder.ivStat.setColorFilter(context.resources.getColor(R.color.colorRemove))
        }
        serversViewHolder.ibUp.setOnClickListener {
            val runnable = Runnable { POSTtoServer(i, temp.name, "up") }
            val thread = Thread(runnable)
            thread.start()
        }
        serversViewHolder.ibDown.setOnClickListener {
            val runnable = Runnable { POSTtoServer(i, temp.name, "down") }
            val thread = Thread(runnable)
            thread.start()
        }
        serversViewHolder.ibDrain.setOnClickListener {
            val runnable = Runnable { POSTtoServer(i, temp.name, "drain") }
            val thread = Thread(runnable)
            thread.start()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ServersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val ivStat: ImageView
        internal val srvname: TextView
        internal val twActive: TextView
        internal val twRequests: TextView
        internal val ibUp: ImageButton
        internal val ibDown: ImageButton
        internal val ibDrain: ImageButton

        init {
            ivStat = itemView.findViewById(R.id.ivStat)
            srvname = itemView.findViewById(R.id.srvname)
            twActive = itemView.findViewById(R.id.twActive)
            twRequests = itemView.findViewById(R.id.twRequests)
            ibUp = itemView.findViewById(R.id.ibUp)
            ibDown = itemView.findViewById(R.id.ibDown)
            ibDrain = itemView.findViewById(R.id.ibDrain)
        }
    }

    companion object {
        private const val TAG = "RecyclerViewAdapter"
    }

    init {
        this.list = list
        this.context = context
    }
}