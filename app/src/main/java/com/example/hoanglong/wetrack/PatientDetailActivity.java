package com.example.hoanglong.wetrack;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_detail);
        ButterKnife.bind(this);
        Resident patient = getIntent().getParcelableExtra("patient");
        if(patient!=null) {
            name.setText(patient.getFullname());
            new ImageLoadTask("http://128.199.93.67/WeTrack/backend/web/"+patient.getAvatar().replace("thumbnail_",""), avt).execute();
            nric.setText(patient.getNric());
            status.setText(String.valueOf(patient.getStatus()));
            dob.setText(patient.getDob());
            created.setText(patient.getCreated());
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("isFromDetailActivity", true);
        startActivityForResult(intent, 101);

    }

    @OnClick(R.id.btnUpdate)
    public void onUpdateClick(){
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("isFromDetailActivity", true);
        startActivityForResult(intent, 101);

    }
}
