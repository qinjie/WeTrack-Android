package edu.np.ece.wetrack;

import android.content.Context;
import android.os.Handler;
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
import edu.np.ece.wetrack.api.Constant;
import edu.np.ece.wetrack.model.Location;
import edu.np.ece.wetrack.model.Resident;
import edu.np.ece.wetrack.tasks.ImageLoadTask;

/**
 * Created by hoanglong on 10-Feb-17.
 */

public class RelativesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Resident> residentList = new ArrayList<>();
    private Context context;


    public RelativesAdapter(List<Resident> residentList) {
        this.residentList = residentList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_relative, parent, false);
        return new RelativesAdapter.BeaconViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Resident patient = residentList.get(position);
        bindResident(patient, (RelativesAdapter.BeaconViewHolder) holder);
    }

    private void bindResident(final Resident resident, final RelativesAdapter.BeaconViewHolder viewHolder) {
        viewHolder.tvPatient.setText(resident.getFullname());
        if (resident.getLatestLocation() != null && resident.getLatestLocation().size() > 0) {
            Location i = resident.getLatestLocation().get(0);
            viewHolder.tvInfo.setText("Last seen at " + i.getCreatedAt());
            viewHolder.tvLocation.setText(i.getAddress());
        } else {
            viewHolder.tvInfo.setText("No report yet");
            viewHolder.tvLocation.setText("");
        }

        if (resident.getThumbnailPath() == null || resident.getThumbnailPath().equals("")) {
            viewHolder.ivAvatar.setImageResource(R.drawable.default_avt);
        } else {
            new ImageLoadTask(Constant.BACKEND_URL + resident.getThumbnailPath(), viewHolder.ivAvatar, context).execute();
        }

        if (resident.getStatus() == 1) {
            viewHolder.imgStatus.setImageResource(R.drawable.ic_missing);
        } else {
            viewHolder.imgStatus.setImageResource(R.drawable.ic_available);

        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EventBus.getDefault().post(new FragmentAdapter.OpenEvent(residentList.indexOf(resident), resident, "relativeList"));
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
        return residentList.size();
    }

    public class BeaconViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvInfo)
        public TextView tvInfo;

        @BindView(R.id.tvLocation)
        public TextView tvLocation;

        @BindView(R.id.tvResident)
        public TextView tvPatient;

        @BindView(R.id.ivAvatar)
        public CircleImageView ivAvatar;

        @BindView(R.id.imgStatus)
        public ImageView imgStatus;

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
