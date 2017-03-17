package edu.np.ece.wetrack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.np.ece.wetrack.api.RetrofitUtils;
import edu.np.ece.wetrack.api.ServerAPI;
import edu.np.ece.wetrack.model.BeaconInfo;
import edu.np.ece.wetrack.model.Relative;
import edu.np.ece.wetrack.model.Resident;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static edu.np.ece.wetrack.BeaconScanActivation.patientList;

/**
 * Created by hoanglong on 10-Feb-17.
 */

public class RelativesFragment extends Fragment {
    @BindView(R.id.rvRelatives)
    RecyclerView rvResident;

    private List<Resident> missingRelativesList = new ArrayList<>();

    public static RelativesFragment newInstance(String title) {
        Bundle args = new Bundle();
        args.putString("title", title);
        RelativesFragment fragment = new RelativesFragment();
        fragment.setArguments(args);

        return fragment;
    }

    private Handler handler;
    @BindView(R.id.srlUsers)
    SwipeRefreshLayout srlUser;


    private ServerAPI serverAPI;

    private RelativesAdapter relativeAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_relatives, container, false);
        ButterKnife.bind(this, rootView);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvResident.getContext(),
                DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider_line));
        rvResident.addItemDecoration(dividerItemDecoration);

        rvResident.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));

        serverAPI = RetrofitUtils.get().create(ServerAPI.class);

        handler = new Handler();


        srlUser.setDistanceToTriggerSync(550);
        srlUser.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (getContext() != null) {
                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
                            String token = sharedPref.getString("userToken-WeTrack", "");
                            serverAPI.getPatientList("Bearer " + token).enqueue(new Callback<List<Resident>>() {
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

                            relativeAdapter = new RelativesAdapter(missingRelativesList);
                            String userID = sharedPref.getString("userID-WeTrack", "");

                            if (patientList != null && !patientList.equals("") && patientList.size() > 0) {
                                for (Resident aPatient : patientList) {
                                    for (BeaconInfo aBeacon : aPatient.getBeacons()) {
                                        for (Relative aRelative : aPatient.getRelatives()) {
                                            if (String.valueOf(aRelative.getId()).equals(userID) && aBeacon.getStatus() == 1 && aPatient.getBeacons() != null && aPatient.getBeacons().size() > 0) {

                                                if (!missingRelativesList.contains(aPatient)) {
                                                    missingRelativesList.add(aPatient);
                                                } else {
                                                    missingRelativesList.set(missingRelativesList.indexOf(aPatient), aPatient);
                                                }

                                            }

//                                            else {
//                                                if (missingRelativesList.contains(aPatient)) {
//                                                    missingRelativesList.remove(aPatient);
//                                                }
//                                            }
                                        }
                                    }

                                }
                            }

                            rvResident.setAdapter(relativeAdapter);
                            srlUser.setRefreshing(false);
                        }


                    }
                }, 2000);


                if (getActivity() != null) {
//                    getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }

            }
        });

//        mHandler = new Handler();
//        startRepeatingTask();


        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String token = sharedPref.getString("userToken-WeTrack", "");

        serverAPI.getPatientList("Bearer " + token).enqueue(new Callback<List<Resident>>() {
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


//        if (patientList != null && !patientList.equals("") && patientList.size() > 0) {
//            for (Resident aPatient : patientList) {
//                for (BeaconInfo aBeacon : aPatient.getBeacons()) {
//                    if (aPatient.getStatus() == 1 && aBeacon.getStatus() == 1 && aPatient.getBeacons() != null && aPatient.getBeacons().size() > 0) {
//
//                        if (!missingRelativesList.contains(aPatient)) {
//                            missingRelativesList.add(aPatient);
//                        } else {
//                            missingRelativesList.set(missingRelativesList.indexOf(aPatient), aPatient);
//                        }
//
//                    } else {
//                        if (missingRelativesList.contains(aPatient)) {
//                            missingRelativesList.remove(aPatient);
//                        }
//                    }
//                }
//
//            }
//        }


        handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
        //TODO
        //thay the cai missing resident list
        //chinh lai vong for
        String userID = sharedPref.getString("userID-WeTrack", "");
        if (patientList != null && !patientList.equals("") && patientList.size() > 0) {
            for (Resident aPatient : patientList) {
                for (BeaconInfo aBeacon : aPatient.getBeacons()) {
                    for (Relative aRelative : aPatient.getRelatives()) {
                        if (String.valueOf(aRelative.getId()).equals(userID) && aBeacon.getStatus() == 1 && aPatient.getBeacons() != null && aPatient.getBeacons().size() > 0) {

                            if (!missingRelativesList.contains(aPatient)) {
                                missingRelativesList.add(aPatient);
                            } else {
                                missingRelativesList.set(missingRelativesList.indexOf(aPatient), aPatient);
                            }

                        }
//                        else {
//                            if (missingRelativesList.contains(aPatient)) {
//                                missingRelativesList.remove(aPatient);
//                            }
//                        }
                    }
                }

            }
        }

        relativeAdapter = new RelativesAdapter(missingRelativesList);
        rvResident.setAdapter(relativeAdapter);

                                }

                            }, 300
        );


        return rootView;
    }


//    private Handler mHandler;
//    private int mInterval = 3000;
//    Runnable mStatusChecker = new Runnable() {
//        @Override
//        public void run() {
//            if (getActivity() != null && !srlUser.isRefreshing()) {
//                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//            }
//
//
//            mHandler.postDelayed(mStatusChecker, mInterval);
//        }
//    };


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
//            homeAdapter = new HomeAdapter(missingRelativesList);
//
//            if (patientList != null && !patientList.equals("") && patientList.size() > 0) {
//                for (Resident aPatient : patientList) {
//                    for (BeaconInfo aBeacon : aPatient.getBeacons()) {
//                        if (aPatient.getStatus() == 1 && aBeacon.getStatus() == 1 && aPatient.getBeacons() != null && aPatient.getBeacons().size() > 0) {
//
//                            if (!missingRelativesList.contains(aPatient)) {
//                                missingRelativesList.add(aPatient);
//                            } else {
//                                missingRelativesList.set(missingRelativesList.indexOf(aPatient), aPatient);
//                            }
//
//                        } else {
//                            if (missingRelativesList.contains(aPatient)) {
//                                missingRelativesList.remove(aPatient);
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


//    void startRepeatingTask() {
//        mStatusChecker.run();
//        mRefreshlist.run();
//    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        String title = getArguments().getString("title");
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
//        EventBus.getDefault().register(this);

    }

    @Override
    public void onPause() {
        super.onPause();
//        EventBus.getDefault().unregister(this);

    }

    final int EDIT_USER = 69;

//    @Subscribe
//    public void onEvent(RelativesAdapter.OpenEvent event) {
////        EventBus.getDefault().unregister(this);
//        Intent intent = new Intent(getActivity(), ResidentDetailActivity.class);
//        intent.putExtra("patient", event.patient);
//        intent.putExtra("position", event.position);
//        intent.putExtra("fromWhat", "relativeList");
//        startActivityForResult(intent, EDIT_USER);
//        getActivity().finish();
//    }
}
