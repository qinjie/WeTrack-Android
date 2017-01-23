package com.example.hoanglong.wetrack;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hoanglong.wetrack.model.Resident;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PatientDetailActivity extends AppCompatActivity {

    @BindView(R.id.name)
    TextView name;

    @BindView(R.id.avatar)
    ImageView avt;

    @BindView(R.id.status)
    TextView status;

    @BindView(R.id.nric)
    TextView nric;

    @BindView(R.id.dob)
    TextView dob;

    @BindView(R.id.created)
    TextView created;

    @BindView(R.id.lastSeen)
    TextView lastSeen;

    @BindView(R.id.lastLocation)
    TextView lastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_detail);
        ButterKnife.bind(this);
        Resident patient = getIntent().getParcelableExtra("patient");
        if (patient != null) {
            name.setText(patient.getFullname());
            new ImageLoadTask("http://128.199.93.67/WeTrack/backend/web/" + patient.getAvatar().replace("thumbnail_", ""), avt).execute();
            nric.setText(patient.getNric());
            String tmp = "";
            if (patient.getStatus() == 1) {
                tmp = "Missing";
            } else {
                tmp = "Available";
            }
            status.setText(tmp);
            dob.setText(patient.getDob());
            created.setText(patient.getCreated());
            if (patient.getLatestLocation() != null && patient.getLatestLocation().size() > 0){
                lastSeen.setText(patient.getLatestLocation().get(0).getCreated());
                lastLocation.setText(patient.getLatestLocation().get(0).getAddr());
            }else{
                lastSeen.setText("Unknown");
                lastLocation.setText("Unknown");
            }

        }
    }

    @Override
    public void onBackPressed() {
        Intent detailIntent = getIntent();
        if (detailIntent != null) {
            Bundle b = detailIntent.getExtras();
            if (b != null) {
                String tmp = b.getString("fromWhat");
                Intent intent = new Intent(this, MainActivity.class);
                if (tmp.equals("home")) {
                    intent.putExtra("isFromDetailActivity", false);
                } else {
                    intent.putExtra("isFromDetailActivity", true);
                }
                startActivityForResult(intent, 101);
            }
        }


    }

    @OnClick(R.id.btnUpdate)
    public void onUpdateClick() {
        Intent detailIntent = getIntent();
        if (detailIntent != null) {
            Bundle b = detailIntent.getExtras();
            if (b != null) {
                String tmp = b.getString("fromWhat");
                Intent intent = new Intent(this, MainActivity.class);
                if (tmp.equals("home")) {
                    intent.putExtra("isFromDetailActivity", false);
                } else {
                    intent.putExtra("isFromDetailActivity", true);
                }
                startActivityForResult(intent, 101);
            }
        }

    }
}
