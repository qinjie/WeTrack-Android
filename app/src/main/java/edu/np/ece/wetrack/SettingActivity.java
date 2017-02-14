package edu.np.ece.wetrack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ToggleButton;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.btnScanning)
    ToggleButton btnScanning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        String isScanning = sharedPref.getString("isScanning-WeTrack", "true");
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
                } else {
                    editor.putString("isScanning-WeTrack", "false");
                }

                editor.commit();

            }
        });

    }

//    @Override
//    public void onBackPressed() {
//        Intent detailIntent = getIntent();
//        Intent intent = new Intent(this, MainActivity.class);
//
//        if (detailIntent != null) {
//            Bundle b = detailIntent.getExtras();
//            try {
//                if (b != null) {
//                    String tmp = b.getString("fromWhat");
//                    if (tmp.equals("home")) {
//                        intent.putExtra("isFromDetailActivity", false);
//                    } else {
//                        intent.putExtra("isFromDetailActivity", true);
//                    }
//                    startActivityForResult(intent, 101);
//                }
//            } catch (Exception e) {
//                intent.putExtra("isFromDetailActivity", false);
//                startActivityForResult(intent, 101);
//
//            }
//
//        }
//
//
//    }
}
