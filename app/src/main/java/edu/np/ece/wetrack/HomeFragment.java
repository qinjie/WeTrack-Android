package edu.np.ece.wetrack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.np.ece.wetrack.api.RetrofitUtils;
import edu.np.ece.wetrack.api.ServerAPI;
import edu.np.ece.wetrack.model.BeaconInfo;
import edu.np.ece.wetrack.model.Resident;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static edu.np.ece.wetrack.BeaconScanActivation.missingPatientList;
import static edu.np.ece.wetrack.BeaconScanActivation.patientList;

/**
 * Created by hoanglong on 06-Dec-16.
 */

public class HomeFragment extends Fragment {
    @BindView(R.id.rvMissingResident)
    RecyclerView rvResident;

    public static HomeFragment newInstance(String title) {
        Bundle args = new Bundle();
        args.putString("title", title);
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private Handler handler;
    @BindView(R.id.srlUsers)
    SwipeRefreshLayout srlUser;


    private ServerAPI serverAPI;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
//        tvTitle = (TextView) rootView.findViewById(R.id.tvTitle);
        ButterKnife.bind(this, rootView);
        rvResident.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
        MainActivity.homeAdapter = new HomeAdapter(missingPatientList);
        rvResident.setAdapter(MainActivity.homeAdapter);

        serverAPI = RetrofitUtils.get().create(ServerAPI.class);

        handler = new Handler();
        srlUser.setDistanceToTriggerSync(550);
        srlUser.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        serverAPI.getPatientList().enqueue(new Callback<List<Resident>>() {
                            @Override
                            public void onResponse(Call<List<Resident>> call, Response<List<Resident>> response) {
                                try {
                                    patientList = response.body();

                                    Gson gson = new Gson();
                                    String jsonPatients = gson.toJson(patientList);
                                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
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
                                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
                                String jsonPatients = sharedPref.getString("patientList-WeTrack", "");
                                Type type = new TypeToken<List<Resident>>() {
                                }.getType();
                                patientList = gson.fromJson(jsonPatients, type);
                            }
                        });

                        MainActivity.homeAdapter = new HomeAdapter(missingPatientList);

                        if (patientList != null && !patientList.equals("") && patientList.size() > 0) {
                            for (Resident aPatient : patientList) {
                                for (BeaconInfo aBeacon : aPatient.getBeacons()) {
                                    if (aPatient.getStatus() == 1 && aBeacon.getStatus() == 1 && aPatient.getBeacons() != null && aPatient.getBeacons().size() > 0) {

                                        if (!missingPatientList.contains(aPatient)) {
                                            missingPatientList.add(aPatient);
                                        } else {
                                            missingPatientList.set(missingPatientList.indexOf(aPatient), aPatient);
                                        }

                                    } else {
                                        if (missingPatientList.contains(aPatient)) {
                                            missingPatientList.remove(aPatient);
//                                            forDisplay.logToDisplay2();
                                        }
                                    }
                                }

                            }
                        }

                        rvResident.setAdapter(MainActivity.homeAdapter);
                        srlUser.setRefreshing(false);

                    }
                }, 2000);
                if (getActivity() != null) {
                    getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }

            }
        });

        mHandler = new Handler();
        startRepeatingTask();




        serverAPI.getPatientList().enqueue(new Callback<List<Resident>>() {
                @Override
                public void onResponse(Call<List<Resident>> call, Response<List<Resident>> response) {
                    try {
                        patientList = response.body();

                        Gson gson = new Gson();
                        String jsonPatients = gson.toJson(patientList);
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
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
                    try {
                        Gson gson = new Gson();
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
                        String jsonPatients = sharedPref.getString("patientList-WeTrack", "");
                        Type type = new TypeToken<List<Resident>>() {
                        }.getType();
                        patientList = gson.fromJson(jsonPatients, type);
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }
            });

            MainActivity.homeAdapter = new HomeAdapter(missingPatientList);

            if (patientList != null && !patientList.equals("") && patientList.size() > 0) {
                for (Resident aPatient : patientList) {
                    for (BeaconInfo aBeacon : aPatient.getBeacons()) {
                        if (aPatient.getStatus() == 1 && aBeacon.getStatus() == 1 && aPatient.getBeacons() != null && aPatient.getBeacons().size() > 0) {

                            if (!missingPatientList.contains(aPatient)) {
                                missingPatientList.add(aPatient);
                            } else {
                                missingPatientList.set(missingPatientList.indexOf(aPatient), aPatient);
                            }

                        } else {
                            if (missingPatientList.contains(aPatient)) {
                                missingPatientList.remove(aPatient);
                            }
                        }
                    }

                }
            }

            rvResident.setAdapter(MainActivity.homeAdapter);

        return rootView;
    }

    private Handler mHandler;
    private int mInterval = 2000;
    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            if (getActivity() != null) {
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }


            mHandler.postDelayed(mStatusChecker, mInterval);
        }
    };


//    private int mInterval2 = 10000;
//    Runnable mRefreshlist = new Runnable() {
//        @Override
//        public void run() {
//            serverAPI.getPatientList().enqueue(new Callback<List<Resident>>() {
//                @Override
//                public void onResponse(Call<List<Resident>> call, Response<List<Resident>> response) {
//                    try {
//                        patientList = response.body();
//
//                        Gson gson = new Gson();
//                        String jsonPatients = gson.toJson(patientList);
//                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
//                        SharedPreferences.Editor editor = sharedPref.edit();
//                        editor.putString("patientList-WeTrack", jsonPatients);
//                        editor.commit();
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<List<Resident>> call, Throwable t) {
//                    t.printStackTrace();
//                    try {
//                        Gson gson = new Gson();
//                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
//                        String jsonPatients = sharedPref.getString("patientList-WeTrack", "");
//                        Type type = new TypeToken<List<Resident>>() {
//                        }.getType();
//                        patientList = gson.fromJson(jsonPatients, type);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//
//                    }
//                }
//            });
//
//            homeAdapter = new HomeAdapter(missingPatientList);
//
//            if (patientList != null && !patientList.equals("") && patientList.size() > 0) {
//                for (Resident aPatient : patientList) {
//                    for (BeaconInfo aBeacon : aPatient.getBeacons()) {
//                        if (aPatient.getStatus() == 1 && aBeacon.getStatus() == 1 && aPatient.getBeacons() != null && aPatient.getBeacons().size() > 0) {
//
//                            if (!missingPatientList.contains(aPatient)) {
//                                missingPatientList.add(aPatient);
//                            } else {
//                                missingPatientList.set(missingPatientList.indexOf(aPatient), aPatient);
//                            }
//
//                        } else {
//                            if (missingPatientList.contains(aPatient)) {
//                                missingPatientList.remove(aPatient);
////                                            forDisplay.logToDisplay2();
//                            }
//                        }
//                    }
//
//                }
//            }
//
//            rvResident.setAdapter(homeAdapter);
//
//
//            mHandler.postDelayed(mRefreshlist, mInterval2);
//        }
//    };


    void startRepeatingTask() {
        mStatusChecker.run();
//        mRefreshlist.run();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String title = getArguments().getString("title");
//        tvTitle.setText(title);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    final int EDIT_USER = 69;

    @Subscribe
    public void onEvent(HomeAdapter.OpenEvent event) {
        Intent intent = new Intent(getActivity(), ResidentDetailActivity.class);
        intent.putExtra("patient", event.patient);
        intent.putExtra("position", event.position);
        intent.putExtra("fromWhat", "home");
        startActivityForResult(intent, EDIT_USER);
//        Toast.makeText(this, "ahihi", Toast.LENGTH_SHORT).show();
    }
}
