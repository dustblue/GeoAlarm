package com.rakesh.geoalarm;

/**
 * Created by Rakesh on 10-03-2018.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder> {

    private List<Alarm> mAlarms;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
        }
    }

    ListAdapter(List<Alarm> alarms) {
        this.mAlarms = alarms;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.alarm_row, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.name.setText("");

    }

    @Override
    public int getItemCount() {
        return mAlarms.size();
    }
}