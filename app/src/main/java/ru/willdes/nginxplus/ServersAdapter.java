package ru.willdes.nginxplus;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ServersAdapter extends RecyclerView.Adapter<ServersAdapter.ServersViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    private List<ServersModel> list = new ArrayList<>();
    private Context context;

    public ServersAdapter(Context context, List<ServersModel> list) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ServersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_servers, parent, false);
        return new ServersViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ServersViewHolder serversViewHolder, final int i) {
        final ServersModel temp = this.list.get(i);
        serversViewHolder.srvname.setText(temp.getName());
        serversViewHolder.twActive.setText("" + temp.getActive());
        serversViewHolder.twRequests.setText("" + temp.getRequests());
        serversViewHolder.twActive.setBackgroundResource(android.R.color.transparent);
        serversViewHolder.twRequests.setBackgroundResource(android.R.color.transparent);

        switch (temp.getState()) {
            case "up":
                serversViewHolder.ivStat.setColorFilter(context.getResources().getColor(R.color.colorUp));
                if (temp.getActive() >= 2 && temp.getRequests() == 0) {
                    //Log.d("Orange", "name: " + temp.getName() + "; id: " + i + " set orange color" );
                    serversViewHolder.twActive.setBackgroundResource(R.color.orange);
                    serversViewHolder.twRequests.setBackgroundResource(R.color.orange);
                }
                break;

            case "down":
                serversViewHolder.ivStat.setColorFilter(context.getResources().getColor(R.color.colorDown));
                break;

            case "unavail":
                serversViewHolder.ivStat.setColorFilter(context.getResources().getColor(R.color.colorRemove));
                break;

            case "draining":
                serversViewHolder.ivStat.setColorFilter(context.getResources().getColor(R.color.colorDrain));
                break;

            case "checking":
                serversViewHolder.ivStat.setColorFilter(context.getResources().getColor(R.color.colorUp));
                break;

            case "unhealthy":
                serversViewHolder.ivStat.setColorFilter(context.getResources().getColor(R.color.colorRemove));
                break;

        }

        serversViewHolder.ibUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        new POSTtoServer(temp.getId(), temp.getName(), "up");
                    }
                };
                Thread thread = new Thread(runnable);
                thread.start();
            }
        });
        serversViewHolder.ibDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        new POSTtoServer(temp.getId(), temp.getName(), "down");
                    }
                };
                Thread thread = new Thread(runnable);
                thread.start();
            }
        });
        serversViewHolder.ibDrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        new POSTtoServer(temp.getId(), temp.getName(), "drain");
                    }
                };
                Thread thread = new Thread(runnable);
                thread.start();
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    public class ServersViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivStat;
        private TextView srvname;
        private TextView twActive;
        private TextView twRequests;
        private ImageButton ibUp;
        private ImageButton ibDown;
        private ImageButton ibDrain;


        public ServersViewHolder(@NonNull View itemView) {
            super(itemView);
            ivStat = itemView.findViewById(R.id.ivStat);
            srvname = itemView.findViewById(R.id.srvname);
            twActive = itemView.findViewById(R.id.twActive);
            twRequests = itemView.findViewById(R.id.twRequests);
            ibUp = itemView.findViewById(R.id.ibUp);
            ibDown = itemView.findViewById(R.id.ibDown);
            ibDrain = itemView.findViewById(R.id.ibDrain);

        }
    }
}
