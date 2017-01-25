package edu.np.ece.wetrack;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import edu.np.ece.wetrack.model.Resident;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hoanglong on 19-Jan-17.
 */

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //    private List<String> beacons;
//    private LinkedHashMap<String, Double> beaconsMap = new LinkedHashMap<>();
    private List<Resident> residentList = new ArrayList<>();

    public HomeAdapter(List<Resident> residentList) {
        this.residentList = residentList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_beacon, parent, false);
        return new HomeAdapter.BeaconViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Resident patient = residentList.get(position);
        bindResident(patient, (HomeAdapter.BeaconViewHolder) holder);
    }


    private void bindResident(final Resident patient, final BeaconViewHolder viewHolder) {
        viewHolder.tvPatient.setText(patient.getFullname());
        if (patient.getLatestLocation() != null && patient.getLatestLocation().size() > 0) {
            viewHolder.tvBeacon.setText("Last seen at " + patient.getLatestLocation().get(0).getCreated());
        }else{
            viewHolder.tvBeacon.setText("No report yet");
        }
        new ImageLoadTask("http://128.199.93.67/WeTrack/backend/web/" + patient.getAvatar(), viewHolder.ivAvatar).execute();
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new OpenEvent(residentList.indexOf(patient), patient));
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
        return residentList.size();
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

    public void add(List<Resident> patientList) {
        this.residentList = patientList;
        notifyDataSetChanged();
    }

}

