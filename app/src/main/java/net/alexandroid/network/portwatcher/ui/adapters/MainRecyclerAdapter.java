package net.alexandroid.network.portwatcher.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.alexandroid.network.portwatcher.R;
import net.alexandroid.network.portwatcher.objects.ScanItem;
import net.alexandroid.network.portwatcher.ui.fragments.MainHistoryFragment;

import java.util.List;

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder> implements View.OnClickListener {

    private final List<ScanItem> mValues;
    private final MainHistoryFragment.OnListOfMainFragmentInteractionListener mListener;

    public MainRecyclerAdapter(List<ScanItem> items, MainHistoryFragment.OnListOfMainFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_fragment_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        holder.tvIp.setText(holder.mItem.getStrIp());
        holder.tvPorts.setText(holder.mItem.getStrPorts());
        holder.tvWhen.setText(holder.mItem.getStrDateTime());


        holder.itemView.setOnClickListener(this);
        holder.itemView.setTag(holder);
        holder.tvStar.setOnClickListener(this);
        holder.tvStar.setTag(holder);
        holder.tvShare.setOnClickListener(this);
        holder.tvShare.setTag(holder);
    }

    @Override
    public void onClick(View v) {
        ViewHolder holder = (ViewHolder) v.getTag();
        if (v.getId() == holder.mView.getId()) {
            mListener.onItemClick(holder.mItem);
        } else if (v.getId() == holder.tvStar.getId()) {
            mListener.onStarClick(holder.mItem);
        } else if (v.getId() == holder.tvShare.getId()) {
            mListener.onShareClick(holder.mItem);
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tvIp;
        public final TextView tvPorts;
        public final TextView tvWhen;
        public final ImageView tvStar;
        public final ImageView tvShare;
        public ScanItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvIp = (TextView) view.findViewById(R.id.tvIp);
            tvPorts = (TextView) view.findViewById(R.id.tvPorts);
            tvWhen = (TextView) view.findViewById(R.id.tvWhen);
            tvStar = (ImageView) view.findViewById(R.id.imgStar);
            tvShare = (ImageView) view.findViewById(R.id.imgShare);
        }
    }
}
