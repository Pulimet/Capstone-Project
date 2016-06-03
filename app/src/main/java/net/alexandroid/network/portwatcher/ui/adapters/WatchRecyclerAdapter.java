package net.alexandroid.network.portwatcher.ui.adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.alexandroid.network.portwatcher.R;
import net.alexandroid.network.portwatcher.helpers.Utils;
import net.alexandroid.network.portwatcher.objects.ScanItem;
import net.alexandroid.network.portwatcher.ui.fragments.MainHistoryFragment;
import net.alexandroid.network.portwatcher.ui.fragments.WatchFragment;

public class WatchRecyclerAdapter extends RecyclerView.Adapter<WatchRecyclerAdapter.ViewHolder> implements View.OnClickListener {

    private Cursor mCursor;
    private View mEmptyView;
    private final WatchFragment.WatchFragmentInteractionListener mListener;

    public WatchRecyclerAdapter(View emptyView, WatchFragment.WatchFragmentInteractionListener listener) {
        mEmptyView = emptyView;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_watch_fragment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        holder.tvHost.setText(mCursor.getString(MainHistoryFragment.COL_HOST));
        holder.tvPorts.setText(mCursor.getString(MainHistoryFragment.COL_PORTS));

        holder.itemView.setOnClickListener(this);
        holder.itemView.setTag(position);
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        mCursor.moveToPosition(position);

        String host = mCursor.getString(WatchFragment.COL_HOST);
        String ports = mCursor.getString(WatchFragment.COL_PORTS);

        mListener.onStartScan(Utils.convertStringToIntegerList(ports), host, -1);
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

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvHost = (TextView) view.findViewById(R.id.tvIp);
            tvPorts = (TextView) view.findViewById(R.id.tvPorts);
        }
    }
}
