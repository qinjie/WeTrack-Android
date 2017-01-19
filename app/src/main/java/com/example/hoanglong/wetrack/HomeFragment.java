package com.example.hoanglong.wetrack;

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

import com.example.hoanglong.wetrack.model.BeaconInfo;
import com.example.hoanglong.wetrack.model.Resident;

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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
//        tvTitle = (TextView) rootView.findViewById(R.id.tvTitle);
        ButterKnife.bind(this, rootView);
        rvResident.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
        homeAdapter = new HomeAdapter(missingPatientList);
        rvResident.setAdapter(homeAdapter);

        handler = new Handler();
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

            }
        });

        return rootView;
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
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
