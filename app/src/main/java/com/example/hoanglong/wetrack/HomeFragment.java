package com.example.hoanglong.wetrack;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.example.hoanglong.wetrack.model.BeaconInfo;
import com.example.hoanglong.wetrack.model.Resident;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.hoanglong.wetrack.BeaconScanActivation.missingPatientList;
import static com.example.hoanglong.wetrack.BeaconScanActivation.patientList;
import static com.example.hoanglong.wetrack.MainActivity.homeAdapter;

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


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
//        tvTitle = (TextView) rootView.findViewById(R.id.tvTitle);
        ButterKnife.bind(this, rootView);
        rvResident.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
        homeAdapter = new HomeAdapter(missingPatientList);
        rvResident.setAdapter(homeAdapter);

        handler = new Handler();
        srlUser.setDistanceToTriggerSync(550);
        srlUser.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        homeAdapter = new HomeAdapter(missingPatientList);


                        if (patientList != null && !patientList.equals("") && patientList.size() > 0) {
                            for (Resident aPatient : patientList) {
                                for (BeaconInfo aBeacon : aPatient.getPatientBeacon()) {
                                    if (aPatient.getStatus() == 1 && aBeacon.getStatus() == 1 && aPatient.getPatientBeacon() != null && aPatient.getPatientBeacon().size() > 0) {

                                        if (!missingPatientList.contains(aPatient)) {
                                            missingPatientList.add(aPatient);
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


                        rvResident.setAdapter(homeAdapter);
                        srlUser.setRefreshing(false);

                    }
                }, 1000);
                if (getActivity() != null) {
                    getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }

            }
        });

        mHandler = new Handler();
        startRepeatingTask();
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

    void startRepeatingTask() {
        mStatusChecker.run();
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
        Intent intent = new Intent(getActivity(), PatientDetailActivity.class);
        intent.putExtra("patient", event.patient);
        intent.putExtra("position", event.position);
        intent.putExtra("fromWhat", "home");
        startActivityForResult(intent, EDIT_USER);
//        Toast.makeText(this, "ahihi", Toast.LENGTH_SHORT).show();
    }
}
