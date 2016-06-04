package net.alexandroid.network.portwatcher.ui.adapters;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import net.alexandroid.network.portwatcher.R;
import net.alexandroid.network.portwatcher.data.DbContract;
import net.alexandroid.network.portwatcher.data.DbHelper;
import net.alexandroid.network.portwatcher.helpers.MyLog;
import net.alexandroid.network.portwatcher.ui.fragments.ScheduleFragment;

public class ScheduleRecyclerAdapter extends RecyclerView.Adapter<ScheduleRecyclerAdapter.ViewHolder> implements
        View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    private Cursor mCursor;
    private View mEmptyView;
    private final ScheduleFragment.ScheduleFragmentInteractionListener mListener;

    public ScheduleRecyclerAdapter(View emptyView, ScheduleFragment.ScheduleFragmentInteractionListener listener) {
        mEmptyView = emptyView;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_schedule_fragment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        holder.tvHost.setText(mCursor.getString(ScheduleFragment.COL_HOST));
        holder.tvPorts.setText(mCursor.getString(ScheduleFragment.COL_PORTS));
        holder.tvInterval.setText(mCursor.getString(ScheduleFragment.COL_INTERVAL));
        holder.toggleBtn.setChecked(mCursor.getInt(ScheduleFragment.COL_ENABLED) == 1);

        holder.itemView.setOnClickListener(this);
        holder.itemView.setTag(position);

        holder.toggleBtn.setOnCheckedChangeListener(this);
        holder.toggleBtn.setTag(position);
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        mCursor.moveToPosition(position);

        String host = mCursor.getString(ScheduleFragment.COL_HOST);
        String ports = mCursor.getString(ScheduleFragment.COL_PORTS);
        String interval = mCursor.getString(ScheduleFragment.COL_INTERVAL);
        mListener.onScheduleItemClick(host, ports, interval);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int position = (int) buttonView.getTag();
        mCursor.moveToPosition(position);

        String host = mCursor.getString(ScheduleFragment.COL_HOST);
        String ports = mCursor.getString(ScheduleFragment.COL_PORTS);
        String interval = mCursor.getString(ScheduleFragment.COL_INTERVAL);

        Context context = buttonView.getContext().getApplicationContext();
        ContentResolver contentResolver = context.getContentResolver();
        String where =
                DbContract.ScheduleEntry.COLUMN_HOST + "=" + DatabaseUtils.sqlEscapeString(host) + " AND " +
                        DbContract.ScheduleEntry.COLUMN_INTERVAL + "=" + DatabaseUtils.sqlEscapeString(interval) + " AND " +
                        DbContract.ScheduleEntry.COLUMN_PORTS + "=" + DatabaseUtils.sqlEscapeString(ports);
        ContentValues cv = DbHelper.getScheduleContentValues(host, ports, Long.valueOf(interval), isChecked ? 1 : 0);
        int numOfUpdatedRows = contentResolver.update(DbContract.ScheduleEntry.CONTENT_URI, cv, where, null);
        MyLog.d("numOfUpdatedRows: " + numOfUpdatedRows);

        // TODO Disable/Enable scan schedule
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
        public final TextView tvInterval;
        public final ToggleButton toggleBtn;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvHost = (TextView) view.findViewById(R.id.tvIp);
            tvPorts = (TextView) view.findViewById(R.id.tvPorts);
            tvInterval = (TextView) view.findViewById(R.id.tvInterval);
            toggleBtn = (ToggleButton) view.findViewById(R.id.toggleButton);
        }
    }
}
