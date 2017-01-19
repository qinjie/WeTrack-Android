package com.example.hoanglong.wetrack;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.hoanglong.wetrack.BeaconScanActivation.detectedBeaconList;
import static com.example.hoanglong.wetrack.BeaconScanActivation.detectedPatientList;
import static com.example.hoanglong.wetrack.BeaconScanService.listBeacon;
import static com.example.hoanglong.wetrack.BeaconScanService.listBeaconAndRange;
import static com.example.hoanglong.wetrack.MainActivity.adapterDevice;

/**
 * Created by hoanglong on 06-Dec-16.
 */

public class BeaconListFragment extends Fragment {

    @BindView(R.id.rvBeacons)
    RecyclerView rvBeacons;

    private Handler handler;
    @BindView(R.id.srlUsers)
    SwipeRefreshLayout srlUser;


    public static BeaconListFragment newInstance(String title) {
        Bundle args = new Bundle();
        args.putString("title", title);
        BeaconListFragment fragment = new BeaconListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_beacon_list, container, false);
        ButterKnife.bind(this, rootView);
        rvBeacons.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
        adapterDevice = new BeaconListAdapter(detectedPatientList,detectedBeaconList );
        rvBeacons.setAdapter(adapterDevice);


        handler = new Handler();
        srlUser.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapterDevice = new BeaconListAdapter(detectedPatientList,detectedBeaconList );
                        rvBeacons.setAdapter(adapterDevice);
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

    final int EDIT_USER=69;

    @Subscribe
    public void onEvent(BeaconListAdapter.OpenEvent event) {
        Intent intent = new Intent(getActivity(),PatientDetailActivity.class);
        intent.putExtra("patient", event.patient);
        intent.putExtra("position", event.position);
        startActivityForResult(intent, EDIT_USER);
//        Toast.makeText(this, "ahihi", Toast.LENGTH_SHORT).show();
    }

}