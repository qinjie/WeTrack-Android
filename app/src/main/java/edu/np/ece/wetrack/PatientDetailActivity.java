package edu.np.ece.wetrack;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class PatientDetailActivity extends AppCompatActivity {

    @BindView(R.id.name)
    TextView name;

    @BindView(R.id.avatar)
    ImageView avatar;

    @BindView(R.id.status)
    TextView status;

    @BindView(R.id.nric)
    TextView nric;

    @BindView(R.id.dob)
    TextView dob;

    @BindView(R.id.createdAt)
    TextView createdAt;

    @BindView(R.id.lastSeen)
    TextView lastSeen;

    @BindView(R.id.lastLocation)
    TextView lastLocation;

    private Handler handler;

    private ServerAPI serverAPI;

    @BindView(R.id.srlUsers)
    SwipeRefreshLayout srlUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_detail);
        ButterKnife.bind(this);
        final ProgressDialog dialog = ProgressDialog.show(this, "We Track",
                "Loading...Please wait...", true, false);

        final Resident patient = getIntent().getParcelableExtra("patient");

//        onNewIntent(getIntent());
//        final String patientFromNotification = getIntent().getStringExtra("fromNotification");

        serverAPI = RetrofitUtils.get().create(ServerAPI.class);

//        if (patientFromNotification != null && !patientFromNotification.equals("")) {
//            displayDetailForNotification(patientFromNotification, dialog);
//        } else {
        displayDetail(patient, dialog);
//        }

//        serverAPI.getPatientList().enqueue(new Callback<List<Resident>>() {
//            @Override
//            public void onResponse(Call<List<Resident>> call, Response<List<Resident>> response) {
//                try {
//                    patientList = response.body();
//
//                    Gson gson = new Gson();
//                    String jsonPatients = gson.toJson(patientList);
//                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//                    SharedPreferences.Editor editor = sharedPref.edit();
//                    editor.putString("patientList-WeTrack", jsonPatients);
//                    editor.commit();
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Resident>> call, Throwable t) {
//                t.printStackTrace();
//                Gson gson = new Gson();
//                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//                String jsonPatients = sharedPref.getString("patientList-WeTrack", "");
//                Type type = new TypeToken<List<Resident>>() {
//                }.getType();
//                patientList = gson.fromJson(jsonPatients, type);
//            }
//        });
//
//
//        handler = new Handler();
//
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (patient != null) {
//                    if (patientList != null && !patientList.equals("") && patientList.size() > 0) {
//                        for (Resident aPatient : patientList) {
//                            if (aPatient.getFullname().equals(patient.getFullname())) {
//                                name.setText(aPatient.getFullname());
//                                new ImageLoadTask("http://128.199.93.67/WeTrack/backend/web/" + aPatient.getThumbnailPath().replace("thumbnail_", ""), avatar).execute();
//                                nric.setText(aPatient.getNric());
//                                String tmp = "";
//                                if (aPatient.getStatus() == 1) {
//                                    tmp = "Missing";
//                                } else {
//                                    tmp = "Available";
//                                }
//                                status.setText(tmp);
//                                dob.setText(aPatient.getDob());
//                                createdAt.setText(aPatient.getCreatedAt());
//                                if (aPatient.getLatestLocation() != null && aPatient.getLatestLocation().size() > 0) {
//                                    lastSeen.setText(aPatient.getLatestLocation().get(0).getCreatedAt());
//                                    lastLocation.setText(aPatient.getLatestLocation().get(0).getAddress());
//                                } else {
//                                    lastSeen.setText("Unknown");
//                                    lastLocation.setText("Unknown");
//                                }
//
//                            }
//
//                        }
//                        dialog.dismiss();
//                    }
//                }
//            }
//        }, 2000);


        srlUser.setDistanceToTriggerSync(400);
        srlUser.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
                dialog.show();


//                if(patientFromNotification != null && !patientFromNotification.equals("")){
//                    displayDetailForNotification(patientFromNotification, dialog);
//                }else{
                displayDetail(patient, dialog);
//                }

//                displayDetail(patient, dialog);

                srlUser.setRefreshing(false);

            }
//                }, 100);
//            }
        });

    }


//    private void displayDetailForNotification(final String id, final ProgressDialog dialog) {
//        serverAPI.getPatientList().enqueue(new Callback<List<Resident>>() {
//            @Override
//            public void onResponse(Call<List<Resident>> call, Response<List<Resident>> response) {
//                try {
//                    patientList = response.body();
//
//                    Gson gson = new Gson();
//                    String jsonPatients = gson.toJson(patientList);
//                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//                    SharedPreferences.Editor editor = sharedPref.edit();
//                    editor.putString("patientList-WeTrack", jsonPatients);
//                    editor.commit();
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Resident>> call, Throwable t) {
//                t.printStackTrace();
//                Gson gson = new Gson();
//                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//                String jsonPatients = sharedPref.getString("patientList-WeTrack", "");
//                Type type = new TypeToken<List<Resident>>() {
//                }.getType();
//                patientList = gson.fromJson(jsonPatients, type);
//            }
//        });
//
//
//        handler = new Handler();
//
//
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (patientList != null && !patientList.equals("") && patientList.size() > 0) {
//                    for (Resident aPatient : patientList) {
//                        if (String.valueOf(aPatient.getId()).equals(id)) {
//                            name.setText(aPatient.getFullname());
//                            new ImageLoadTask("http://128.199.93.67/WeTrack/backend/web/" + aPatient.getThumbnailPath().replace("thumbnail_", ""), avatar).execute();
//                            nric.setText(aPatient.getNric());
//                            String tmp = "";
//                            if (aPatient.getStatus() == 1) {
//                                tmp = "Missing";
//                            } else {
//                                tmp = "Available";
//                            }
//                            status.setText(tmp);
//                            dob.setText(aPatient.getDob());
//                            createdAt.setText(aPatient.getCreatedAt());
//                            if (aPatient.getLatestLocation() != null && aPatient.getLatestLocation().size() > 0) {
//                                lastSeen.setText(aPatient.getLatestLocation().get(0).getCreatedAt());
//                                lastLocation.setText(aPatient.getLatestLocation().get(0).getAddress());
//                            } else {
//                                lastSeen.setText("Unknown");
//                                lastLocation.setText("Unknown");
//                            }
//
//                        }
//
//                    }
//                    dialog.dismiss();
//                }
//
//            }
//        }, 2000);
//    }


    private void displayDetail(final Resident patient, final ProgressDialog dialog) {
        serverAPI.getPatientList().enqueue(new Callback<List<Resident>>() {
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
                    if (patientList != null && !patientList.equals("") && patientList.size() > 0) {
                        for (Resident aPatient : patientList) {
                            if (aPatient.getFullname().equals(patient.getFullname())) {
                                name.setText(aPatient.getFullname());
                                new ImageLoadTask("http://128.199.93.67/WeTrack/backend/web/" + aPatient.getThumbnailPath().replace("thumbnail_", ""), avatar).execute();
                                nric.setText(aPatient.getNric());
                                String tmp = "";
                                if (aPatient.getStatus() == 1) {
                                    tmp = "Missing";
                                } else {
                                    tmp = "Available";
                                }
                                status.setText(tmp);
                                dob.setText(aPatient.getDob());
                                createdAt.setText(aPatient.getCreatedAt());
                                if (aPatient.getLatestLocation() != null && aPatient.getLatestLocation().size() > 0) {
                                    lastSeen.setText(aPatient.getLatestLocation().get(0).getCreatedAt());
                                    lastLocation.setText(aPatient.getLatestLocation().get(0).getAddress());
                                } else {
                                    lastSeen.setText("Unknown");
                                    lastLocation.setText("Unknown");
                                }

                            }

                        }
                        dialog.dismiss();
                    }
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
