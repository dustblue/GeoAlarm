package com.rakesh.geoalarm;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "Geo";
    private static final int PLACE_PICKER_REQUEST = 1;
    public static final int ACCURACY_LEVEL_LOW = 26;
    public static final int ACCURACY_LEVEL_MEDIUM = 27;
    public static final int ACCURACY_LEVEL_HIGH = 28;
    private TextView textView;
    PlacePicker.IntentBuilder builder;
    Intent intent;
    Bundle bundle;
    Boolean reached = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        registerForContextMenu(fab);

        Button button = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);
        builder = new PlacePicker.IntentBuilder();
        intent = new Intent(this, LocationService.class);
        bundle = getIntent().getExtras();

        if (bundle != null) reached = bundle.getBoolean("ifReached");
        if (reached) {
            textView.setText(getResources().getString(R.string.reached));
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(intent);
                try {
                    startActivityForResult(builder.build(MainActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.e(TAG, "GooglePlayServicesNotAvailableException");
                } catch (GooglePlayServicesRepairableException e) {
                    Log.e(TAG, "GooglePlayServicesRepairableException");
                }
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Choose Accuracy");
        menu.add(0, v.getId(), 0, "High Accuracy");
        menu.add(0, v.getId(), 0, "Medium Accuracy");
        menu.add(0, v.getId(), 0, "Low Accuracy");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Toast.makeText(this, "Changed to : " + item.getTitle(), Toast.LENGTH_SHORT).show();
        if (item.getTitle().equals("High Accuracy")) {
            intent.putExtra("accuracy", ACCURACY_LEVEL_HIGH);
        } else if (item.getTitle().equals("Medium Accuracy")) {
            intent.putExtra("accuracy", ACCURACY_LEVEL_MEDIUM);
        } else if (item.getTitle().equals("Low Accuracy")) {
            intent.putExtra("accuracy", ACCURACY_LEVEL_LOW);
        }
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            intent.putExtra("lat", PlacePicker.getPlace(this, data).getLatLng().latitude);
            intent.putExtra("lng", PlacePicker.getPlace(this, data).getLatLng().longitude);
            startService(intent);
            displayPlace(PlacePicker.getPlace(this, data));
        }
    }

    private void displayPlace(Place place) {
        if (place == null)
            return;
        String content = "You Chose:" + "\n";
        if (!TextUtils.isEmpty(place.getName())) {
            content += place.getName() + "\n";
        } else {
            content += "Lat: " + place.getLatLng().latitude + "\n";
            content += "Lng: " + place.getLatLng().longitude + "\n";
        }
        textView.setText(content);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static Double roundOff(Double value, int precision) {
        Double result = value;
        if (precision == ACCURACY_LEVEL_MEDIUM)
            result = Math.round(value * 1000.0) / 1000.0;
        else if (precision == ACCURACY_LEVEL_HIGH)
            result = Math.round(value * 10000.0) / 10000.0;
        else if (precision == ACCURACY_LEVEL_LOW)
            result = Math.round(value * 100.0) / 100.0;
        return result;
    }
}
