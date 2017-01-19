package com.example.hoanglong.wetrack;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hoanglong.wetrack.model.BeaconInfo;
import com.example.hoanglong.wetrack.model.Resident;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
        new ImageLoadTask("http://128.199.93.67/WeTrack/backend/web/"+patient.getAvatar(), viewHolder.ivAvatar).execute();
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new OpenEvent(patientList.indexOf(patient), patient));
            }
        });
    }

    public class OpenEvent {
        public final int position;
        public final Resident patient;

        public OpenEvent(int position, Resident patient) {
            this.position = position;
            this.patient = patient;
        }
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

        @BindView(R.id.ivAvatar)
        public ImageView ivAvatar;

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


    public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("src",src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap","returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        }
    }


//    public class ImageLoadTask2 extends AsyncTask<Void, Void, Bitmap> {
//
//        private String url;
//        private ImageView imageView;
//
//        public ImageLoadTask2(String url, ImageView imageView) {
//            this.url = url;
//            this.imageView = imageView;
//        }
//
//        @Override
//        protected Bitmap doInBackground(Void... params) {
//            try {
//                URL urlConnection = new URL(url);
//                HttpURLConnection connection = (HttpURLConnection) urlConnection
//                        .openConnection();
//                connection.setDoInput(true);
//                connection.connect();
//                InputStream input = connection.getInputStream();
//                Bitmap myBitmap = BitmapFactory.decodeStream(input);
//                return myBitmap;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Bitmap result) {
//            super.onPostExecute(result);
//            imageView.setImageBitmap(result);
//        }
//
//    }


}
