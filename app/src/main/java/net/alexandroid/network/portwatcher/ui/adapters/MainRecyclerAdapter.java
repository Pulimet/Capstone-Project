package net.alexandroid.network.portwatcher.ui.adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.alexandroid.network.portwatcher.R;
import net.alexandroid.network.portwatcher.helpers.Utils;
import net.alexandroid.network.portwatcher.objects.ScanItem;
import net.alexandroid.network.portwatcher.ui.fragments.MainHistoryFragment;

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder> implements View.OnClickListener {

    private Cursor mCursor;
    private View mEmptyView;
    private final MainHistoryFragment.OnListOfMainFragmentInteractionListener mListener;

    public MainRecyclerAdapter(View emptyView, MainHistoryFragment.OnListOfMainFragmentInteractionListener listener) {
        mEmptyView = emptyView;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_fragment_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        holder.tvHost.setText(mCursor.getString(MainHistoryFragment.COL_HOST));
        holder.tvPorts.setText(mCursor.getString(MainHistoryFragment.COL_PORTS));
        holder.tvWhen.setText(Utils.convertTimeFormMs(mCursor.getString(MainHistoryFragment.COL_WHEN)));

        holder.itemView.setOnClickListener(this);
        holder.itemView.setTag(position);
        holder.tvStar.setOnClickListener(this);
        holder.tvStar.setTag(position);
        holder.tvShare.setOnClickListener(this);
        holder.tvShare.setTag(position);
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        mCursor.moveToPosition(position);

        ScanItem scanItem = new ScanItem(
                mCursor.getString(MainHistoryFragment.COL_HOST),
                mCursor.getString(MainHistoryFragment.COL_PORTS),
                mCursor.getString(MainHistoryFragment.COL_WHEN),
                mCursor.getString(MainHistoryFragment.COL_WERE_OPEN)
        );

        switch (v.getId()) {
            case R.id.imgStar:
                mListener.onStarClick(scanItem);
                break;
            case R.id.imgShare:
                mListener.onShareClick(scanItem);
                break;
            default:
                mListener.onItemClick(scanItem);
        }
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tvHost;
        public final TextView tvPorts;
        public final TextView tvWhen;
        public final ImageView tvStar;
        public final ImageView tvShare;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvHost = (TextView) view.findViewById(R.id.tvIp);
            tvPorts = (TextView) view.findViewById(R.id.tvPorts);
            tvWhen = (TextView) view.findViewById(R.id.tvWhen);
            tvStar = (ImageView) view.findViewById(R.id.imgStar);
            tvShare = (ImageView) view.findViewById(R.id.imgShare);
        }
    }
}
