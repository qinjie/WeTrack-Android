package com.example.hoanglong.wetrack;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hoanglong on 10/06/2016.
 */

public class BeaconListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<String> beacons;
    private LinkedHashMap<String, Double> beaconsMap = new LinkedHashMap<>();


    public BeaconListAdapter(List<String> beacons, LinkedHashMap<String, Double> beaconsMap) {
        this.beacons = beacons;
        this.beaconsMap = beaconsMap;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_beacon, parent, false);
        return new BeaconViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String beacon = beacons.get(position);
        bindBeacon(beacon, (BeaconViewHolder) holder);
    }


    private void bindBeacon(final String beacon, final BeaconViewHolder viewHolder) {
        viewHolder.tvBeacon.setText(beacon + " is " + beaconsMap.get(beacon) + " meters away.");
    }


    @Override
    public int getItemCount() {
        return beacons.size();
    }

    public class BeaconViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvBeacon)
        public TextView tvBeacon;

        public BeaconViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void add(List<String> beacon, LinkedHashMap<String, Double> beaconsMap) {
        this.beacons = beacon;
        this.beaconsMap = beaconsMap;
        notifyDataSetChanged();
    }


}
