package com.example.hoanglong.wetrack;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hoanglong.wetrack.model.BeaconInfo;
import com.example.hoanglong.wetrack.model.Resident;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hoanglong on 10/06/2016.
 */

public class BeaconListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //    private List<String> beacons;
//    private LinkedHashMap<String, Double> beaconsMap = new LinkedHashMap<>();
    private List<Resident> patientList = new ArrayList<>();
    private List<BeaconInfo> beaconList = new ArrayList<>();

//    public BeaconListAdapter(List<String> beacons, LinkedHashMap<String, Double> beaconsMap) {
//        this.beacons = beacons;
//        this.beaconsMap = beaconsMap;
//    }

    public BeaconListAdapter(List<Resident> patientList, List<BeaconInfo> beaconList) {
        this.patientList = patientList;
        this.beaconList = beaconList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_beacon, parent, false);
        return new BeaconViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Resident patient = patientList.get(position);
        BeaconInfo beacon = beaconList.get(position);
        bindBeacon(patient, beacon, (BeaconViewHolder) holder);
    }


    private void bindBeacon(final Resident patient, final BeaconInfo beacon, final BeaconViewHolder viewHolder) {
        viewHolder.tvPatient.setText(patient.getFullname());
        viewHolder.tvBeacon.setText("Beacon [00"+beacon.getId() + "] is detected.");
    }


    @Override
    public int getItemCount() {
        return beaconList.size();
    }

    public class BeaconViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvBeacon)
        public TextView tvBeacon;

        @BindView(R.id.tvPatient)
        public TextView tvPatient;

        public BeaconViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void add(List<Resident> patientList, List<BeaconInfo> beaconList) {
        this.patientList = patientList;
        this.beaconList = beaconList;
        notifyDataSetChanged();
    }


}
