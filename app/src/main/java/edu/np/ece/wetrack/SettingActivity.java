package edu.np.ece.wetrack;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ToggleButton;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.btnScanning)
    ToggleButton btnScanning;

    @BindView(R.id.btnNoti)
    ToggleButton btnNoti;

    BluetoothAdapter bluetoothAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        toolbar.setTitle("Setting");


        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        String isScanning = sharedPref.getString("isScanning-WeTrack", "true");
        String isNoti = sharedPref.getString("isNoti-WeTrack", "true");

        if (isScanning.equals("true")) {
            btnScanning.setChecked(true);
        } else {
            btnScanning.setChecked(false);
        }

        btnScanning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = sharedPref.edit();

                if (btnScanning.isChecked()) {
                    editor.putString("isScanning-WeTrack", "true");
                    initBluetooth();
                } else {
                    editor.putString("isScanning-WeTrack", "false");
                }

                editor.commit();

            }
        });

        if (isNoti.equals("true")) {
            btnNoti.setChecked(true);
        } else {
            btnNoti.setChecked(false);
        }

        btnNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = sharedPref.edit();

                if (btnNoti.isChecked()) {
                    editor.putString("isNoti-WeTrack", "true");
                } else {
                    editor.putString("isNoti-WeTrack", "false");
                }

                editor.commit();

            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        Intent detailIntent = getIntent();

        Intent intent = new Intent(this, MainActivity.class);

        if (detailIntent != null) {
            try {
                String tmp = detailIntent.getStringExtra("fromWhat");
                if (tmp.equals("home")) {
                    intent.putExtra("whatParent", "home");
                }

                if (tmp.equals("detectedList")) {
                    intent.putExtra("whatParent", "detectedList");
                }

                if (tmp.equals("relativeList")) {
                    intent.putExtra("whatParent", "relativeList");
                }

                startActivity(intent);

            } catch (Exception e) {
                intent.putExtra("whatParent", "home");
                startActivity(intent);
            }

        }
        finish();
    }

    private void initBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, 9);

        }
    }
}
