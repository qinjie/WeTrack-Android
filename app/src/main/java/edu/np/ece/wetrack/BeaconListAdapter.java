package edu.np.ece.wetrack;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import edu.np.ece.wetrack.tasks.ImageLoadTask;
import edu.np.ece.wetrack.api.Constant;
import edu.np.ece.wetrack.model.BeaconInfo;
import edu.np.ece.wetrack.model.Resident;

/**
 * Created by hoanglong on 10/06/2016.
 */

public class BeaconListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Resident> patientList = new ArrayList<>();
    private List<BeaconInfo> beaconList = new ArrayList<>();
    private Context context;

    public BeaconListAdapter(List<Resident> patientList, List<BeaconInfo> beaconList) {
        this.patientList = patientList;
        this.beaconList = beaconList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
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
        viewHolder.tvInfo.setText("is nearby.");

        if (patient.getThumbnailPath() == null || patient.getThumbnailPath().equals("")) {
            viewHolder.ivAvatar2.setImageResource(R.drawable.default_avt);
        } else {
            new ImageLoadTask(Constant.BACKEND_URL+ patient.getThumbnailPath(), viewHolder.ivAvatar2, context).execute();
        }

//        if (!TextUtils.isEmpty(patient.getThumbnailPath()))
//            new ImageLoadTask( + patient.getThumbnailPath(), viewHolder.ivAvatar).execute();

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new FragmentAdapter.OpenEvent(patientList.indexOf(patient), patient, "detectedList"));
            }
        });
    }

//    public class OpenEvent {
//        public final int position;
//        public final Resident patient;
//
//        public OpenEvent(int position, Resident patient) {
//            this.position = position;
//            this.patient = patient;
//        }
//    }

    @Override
    public int getItemCount() {
        return beaconList.size();
    }

    public class BeaconViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvInfo)
        public TextView tvInfo;

        @BindView(R.id.tvResident)
        public TextView tvPatient;

        @BindView(R.id.ivAvatar)
        public CircleImageView ivAvatar2;

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
