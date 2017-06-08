package edu.np.ece.wetrack;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.np.ece.wetrack.api.RetrofitUtils;
import edu.np.ece.wetrack.api.ServerAPI;
import edu.np.ece.wetrack.model.BeaconInfo;
import edu.np.ece.wetrack.model.Relative;
import edu.np.ece.wetrack.model.Resident;
import edu.np.ece.wetrack.tasks.ImageLoadTask;
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

    @BindView(R.id.beaconList)
    TextView tvBeaconList;

    @BindView(R.id.reportedAt)
    TextView reportedAt;

    @BindView(R.id.lastSeen)
    TextView lastSeenBeacon;

    @BindView(R.id.lastLocation)
    TextView lastLocation;

    @BindView(R.id.remark)
    TextView remark;

    private Handler handler;

    private ServerAPI serverAPI;

    @BindView(R.id.srlUsers)
    SwipeRefreshLayout srlUser;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.statusArea)
    LinearLayout statusArea;

    @BindView(R.id.mySwitch)
    ToggleButton toggleButton;

    @BindView(R.id.tvRemind)
    TextView remind;

    String uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resident_detail);
        ButterKnife.bind(this);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String token = sharedPref.getString("userToken-WeTrack", "");

        final Resident patient = getIntent().getParcelableExtra("patient");

        if (token.equals("")) {
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            intent.putExtra("whatParent", "yyy");
            startActivity(intent);
        } else {
            final ProgressDialog dialog = ProgressDialog.show(this, "We Track",
                    "Loading...Please wait...", true, false);

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


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        toggleButton.setVisibility(View.GONE);
        statusArea.setVisibility(View.GONE);


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


    private void displayDetail(final Resident patient, final ProgressDialog dialog) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final String token = sharedPref.getString("userToken-WeTrack", "");
        serverAPI.getPatientList("Bearer " + token).enqueue(new Callback<List<Resident>>() {
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
                        for (final Resident aPatient : patientList) {
                            if (aPatient.getId() == patient.getId()) {
                                name.setText(aPatient.getFullname());

                                if (aPatient.getThumbnailPath() == null || aPatient.getThumbnailPath().equals("")) {
                                    avt.setImageResource(R.drawable.default_avt);
                                } else {
                                    new ImageLoadTask("http://128.199.93.67/WeTrack/backend/web/" + aPatient.getThumbnailPath().replace("thumbnail_",""), avt, getBaseContext()).execute();
//                                    avt.setMaxHeight(150);
//                                    avt.setMaxWidth(150);
                                }

                                if (aPatient.getRemark().equals("")) {
                                    remark.setText("None");

                                } else {
                                    remark.setText(aPatient.getRemark());

                                }

                                nric.setText(aPatient.getNric());
                                String tmp = "";
                                if (aPatient.getStatus() == 1) {
                                    tmp = "Missing";
                                    //TODO
                                    toggleButton.setChecked(true);
                                } else {
                                    tmp = "Available";
                                    //TODO
                                    toggleButton.setChecked(false);
                                }

                                status.setText(tmp);

                                final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                                String userID = sharedPref.getString("userID-WeTrack", "");
                                if (userID.equals("0")) {
                                    toggleButton.setVisibility(View.GONE);
                                    statusArea.setVisibility(View.GONE);
                                } else {
                                    if (!userID.equals("")) {
                                        for (Relative aRelative : aPatient.getRelatives()) {
                                            if (String.valueOf(aRelative.getId()).equals(userID)) {
                                                toggleButton.setVisibility(View.VISIBLE);
                                                statusArea.setVisibility(View.VISIBLE);

                                                toggleButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        final String token = sharedPref.getString("userToken-WeTrack", "");

                                                        final Gson gson = new GsonBuilder()
                                                                .setLenient()
                                                                .create();

                                                        final EditText input = new EditText(ResidentDetailActivity.this);
                                                        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

//                                                        ContextThemeWrapper ctw = new ContextThemeWrapper(this, AlertDialog.);
                                                        AlertDialog alertDialog = new AlertDialog.Builder(ResidentDetailActivity.this).create();
                                                        alertDialog.setTitle("Remark");

                                                        alertDialog.setView(input);
                                                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                                                new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int which) {

//                                                                        Date aDate = new Date();
//                                                                        SimpleDateFormat curFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                                                                        String dateObj = curFormatter.format(aDate);
//                                                                        reportedAt.setText(dateObj);

                                                                        if(input.getText().toString().equals(""))
                                                                        {
                                                                            remark.setText("Unknown");

                                                                        }else{
                                                                            remark.setText(input.getText().toString());

                                                                        }

                                                                        aPatient.setRemark(input.getText().toString());
                                                                        JsonObject obj = gson.toJsonTree(aPatient).getAsJsonObject();

                                                                        serverAPI.changeStatus("Bearer " + token, "application/json", obj).enqueue(new Callback<Resident>() {
                                                                            @Override
                                                                            public void onResponse(Call<Resident> call, Response<Resident> response) {
                                                                                if (status.getText().equals("Missing")) {
                                                                                    status.setText("Available");
                                                                                    remind.setVisibility(View.GONE);
                                                                                } else {
                                                                                    status.setText("Missing");
                                                                                    reportedAt.setText("Unknown");
                                                                                    lastSeenBeacon.setText("Unknown");
                                                                                    lastLocation.setText("Unknown");
                                                                                    remind.setVisibility(View.VISIBLE);
                                                                                }

                                                                            }

                                                                            @Override
                                                                            public void onFailure(Call<Resident> call, Throwable t) {

                                                                            }
                                                                        });
                                                                    }
                                                                });

                                                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                                                                new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        if (status.getText().equals("Missing")) {
                                                                            toggleButton.setChecked(true);
                                                                            remind.setVisibility(View.VISIBLE);
                                                                        } else {
                                                                            toggleButton.setChecked(false);
                                                                            remind.setVisibility(View.GONE);
                                                                        }
                                                                        dialog.dismiss();
                                                                    }
                                                                });


                                                        if (status.getText().equals("Available")) {
                                                            alertDialog.show();
                                                        } else {

                                                            final JsonObject obj = gson.toJsonTree(aPatient).getAsJsonObject();

                                                            serverAPI.changeStatus("Bearer " + token, "application/json", obj).enqueue(new Callback<Resident>() {
                                                                @Override
                                                                public void onResponse(Call<Resident> call, Response<Resident> response) {
                                                                    if (status.getText().equals("Missing")) {
                                                                        status.setText("Available");
                                                                        remind.setVisibility(View.GONE);
                                                                    } else {
                                                                        status.setText("Missing");
                                                                        remind.setVisibility(View.VISIBLE);
                                                                    }

                                                                }

                                                                @Override
                                                                public void onFailure(Call<Resident> call, Throwable t) {

                                                                }
                                                            });
                                                        }


                                                    }
                                                });
                                            }
                                        }


                                    }
                                }

                                dob.setText(aPatient.getDob());

                                String beacons = "";

                                if (aPatient.getBeacons() != null && aPatient.getBeacons().size() > 0) {
                                    for (BeaconInfo temp : aPatient.getBeacons()) {
                                        beacons += "\t► ID: " + temp.getId() + " ☼ Major: " + temp.getMajor() + " | Minor: " + temp.getMinor() + "\n";
                                    }
                                }
                                tvBeaconList.setText(beacons);

//                                reportedAt.setText(aPatient.getCreatedAt());


                                if (aPatient.getLatestLocation().size() != 0) {
                                    if (aPatient.getLatestLocation() != null && aPatient.getLatestLocation().size() > 0) {
                                        reportedAt.setText(aPatient.getLatestLocation().get(0).getCreatedAt());


                                        String beaconDetected= "Unknown";
                                        if (aPatient.getBeacons() != null && aPatient.getBeacons().size() > 0) {
                                            for (BeaconInfo temp : aPatient.getBeacons()) {
                                                if(aPatient.getLatestLocation().get(0).getBeaconId() == temp.getId()) {
                                                    beaconDetected = "\t► ID: " + temp.getId() + " ☼ Major: " + temp.getMajor() + " | Minor: " + temp.getMinor() + "\n";
                                                }
                                            }
                                        }


                                        lastSeenBeacon.setText(beaconDetected);


                                        lastLocation.setText(aPatient.getLatestLocation().get(0).getAddress());
                                    }

                                    uri = "http://maps.google.com/maps?q=loc:" + aPatient.getLatestLocation().get(0).getLatitude() + "," + aPatient.getLatestLocation().get(0).getLongitude() + " (" + aPatient.getFullname() + ")";

                                } else {
                                    reportedAt.setText("Unknown");
                                    lastSeenBeacon.setText("Unknown");
                                    lastLocation.setText("Unknown");
                                }
                            }

                        }

                    }

                    dialog.dismiss();

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

        Bundle c = new Bundle();

        if (detailIntent != null) {
            try {
                String tmp = detailIntent.getStringExtra("fromWhat");
                if (tmp.equals("home")) {
                    c.putString("whatParent", "home");
                    intent.putExtras(c);

                }
                if (tmp.equals("detectedList")) {
                    c.putString("whatParent", "detectedList");
                    intent.putExtras(c);

                }

                if (tmp.equals("relativeList")) {
                    c.putString("whatParent", "relativeList");
                    intent.putExtras(c);

                }

                startActivity(intent);
            } catch (Exception e) {
                c.putString("isFromDetailActivity", "false");
                intent.putExtras(c);
                startActivity(intent);

            }

        }
        finish();


    }

    @OnClick(R.id.openMap)
    public void onUpdateClick() {
        if (uri != null && !uri.equals("") && !lastLocation.getText().equals("Unknown") ) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("whatParent", "xxx");
            getBaseContext().startActivity(intent);
        }

    }

}
