package edu.np.ece.wetrack;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.np.ece.wetrack.api.RetrofitUtils;
import edu.np.ece.wetrack.api.ServerAPI;
import edu.np.ece.wetrack.model.Resident;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static edu.np.ece.wetrack.BeaconScanActivation.patientList;

public class ResidentDetailActivity extends AppCompatActivity {

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

    @BindView(R.id.reportedAt)
    TextView created;

    @BindView(R.id.lastSeen)
    TextView lastSeen;

    @BindView(R.id.lastLocation)
    TextView lastLocation;

    @BindView(R.id.remark)
    TextView remark;

    private Handler handler;

    private ServerAPI serverAPI;

    @BindView(R.id.srlUsers)
    SwipeRefreshLayout srlUser;

    String uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resident_detail);
        ButterKnife.bind(this);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String token = sharedPref.getString("userToken-WeTrack", "");

        if (token.equals("")) {
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
        } else {
            final ProgressDialog dialog = ProgressDialog.show(this, "We Track",
                    "Loading...Please wait...", true, false);

            final Resident patient = getIntent().getParcelableExtra("patient");


            serverAPI = RetrofitUtils.get().create(ServerAPI.class);

            displayDetail(patient, dialog);


            srlUser.setDistanceToTriggerSync(400);
            srlUser.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

                @Override
                public void onRefresh() {
                    dialog.show();

                    displayDetail(patient, dialog);

                    srlUser.setRefreshing(false);

                }
//                }, 100);
//            }
            });
        }


    }


    private void displayDetail(final Resident patient, final ProgressDialog dialog) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String token = sharedPref.getString("userToken-WeTrack", "");
        serverAPI.getPatientList(token).enqueue(new Callback<List<Resident>>() {
            @Override
            public void onResponse(Call<List<Resident>> call, Response<List<Resident>> response) {
                try {
                    patientList = response.body();

                    Gson gson = new Gson();
                    String jsonPatients = gson.toJson(patientList);
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("patientList-WeTrack", jsonPatients);
                    editor.commit();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<Resident>> call, Throwable t) {
                t.printStackTrace();
                Gson gson = new Gson();
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                String jsonPatients = sharedPref.getString("patientList-WeTrack", "");
                Type type = new TypeToken<List<Resident>>() {
                }.getType();
                patientList = gson.fromJson(jsonPatients, type);
            }
        });


        handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (patient != null) {


                    name.setText(patient.getFullname());

                    if (patient.getThumbnailPath() == null || patient.getThumbnailPath().equals("")) {
                        avt.setImageResource(R.drawable.default_avt);
                    } else {
                        new ImageLoadTask("http://128.199.93.67/WeTrack/backend/web/" + patient.getThumbnailPath().replace("thumbnail_", ""), avt).execute();
                    }

//                                new ImageLoadTask("http://128.199.93.67/WeTrack/backend/web/" + aPatient.getAvatar().replace("thumbnail_", ""), avt).execute();

                    remark.setText(patient.getRemark());

                    nric.setText(patient.getNric());
                    String tmp = "";
                    if (patient.getStatus() == 1) {
                        tmp = "Missing";
                    } else {
                        tmp = "Available";
                    }
                    status.setText(tmp);
                    dob.setText(patient.getDob());
                    created.setText(patient.getCreatedAt());
                    if (patient.getLatestLocation() != null && patient.getLatestLocation().size() > 0) {
                        lastSeen.setText(patient.getLatestLocation().get(0).getCreatedAt());
                        lastLocation.setText(patient.getLatestLocation().get(0).getAddress());
                    } else {
                        lastSeen.setText("Unknown");
                        lastLocation.setText("Unknown");
                    }

                    uri = "http://maps.google.com/maps?q=loc:" + patient.getLatestLocation().get(0).getLatitude() + "," + patient.getLatestLocation().get(0).getLongitude() + " (" + patient.getFullname() + ")";

                    dialog.dismiss();


//                    if (patientList != null && !patientList.equals("") && patientList.size() > 0) {
//                        for (Resident aPatient : patientList) {
//                            if (aPatient.getFullname().equals(patient.getFullname())) {
//
//                            }
//
//                        }
//
//                    }
                }
            }
        }, 2000);
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        final Resident patient = getIntent().getExtras().getParcelable("patient");
//        Log.i("longgggggggggggg", patient.getFullname());
//        super.onNewIntent(intent);
//    }

    @Override
    public void onBackPressed() {
        Intent detailIntent = getIntent();
        Intent intent = new Intent(this, MainActivity.class);

        if (detailIntent != null) {
            Bundle b = detailIntent.getExtras();
            try {
                if (b != null) {
                    String tmp = b.getString("fromWhat");
                    if (tmp.equals("home")) {
                        intent.putExtra("isFromDetailActivity", false);
                    } else {
                        intent.putExtra("isFromDetailActivity", true);
                    }
                    startActivityForResult(intent, 101);
                }
            } catch (Exception e) {
                intent.putExtra("isFromDetailActivity", false);
                startActivityForResult(intent, 101);

            }

        }


    }

    @OnClick(R.id.openMap)
    public void onUpdateClick() {
//        String uri = String.format(Locale.ENGLISH, "geo:%f,%f", 1.2829696, 103.8586867);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getBaseContext().startActivity(intent);

    }

}
