package com.rakesh.geoalarm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AlarmsActivity extends AppCompatActivity {
    RecyclerView.LayoutManager layoutManager;
    RecyclerView recyclerView;
    private ListAdapter mAdapter;
    TextView emptyText;
    List<Alarm> alarmsList;
    DataBaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarms);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AlarmsActivity.this, MainActivity.class));
            }
        });

        alarmsList = new ArrayList<>();
        emptyText = (TextView) findViewById(R.id.empty_text);
        recyclerView = (RecyclerView) findViewById(R.id.list);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        db = new DataBaseHandler(this);
        new getAlarms(this).execute();

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Intent i = new Intent(AlarmsActivity.this, MainActivity.class);
                                i.putExtra("data", "");

                                AlarmsActivity.this.startActivityForResult(i, 17);
                            }
                        })
        );
    }

    public class getAlarms extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;

        getAlarms(Context context) {
            dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Getting Alarms...");
            dialog.setCancelable(false);
            dialog.show();
        }

        protected Void doInBackground(Void... args) {
            alarmsList = db.getAllAlarms();
            return null;
        }

        protected void onPostExecute(Void result) {
            try {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (alarmsList.isEmpty()) {
                emptyText.setVisibility(View.VISIBLE);
            } else {
                mAdapter = new ListAdapter(alarmsList);
                recyclerView.setAdapter(mAdapter);
            }
        }
    }



}
