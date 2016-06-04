package net.alexandroid.network.portwatcher.ui.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.alexandroid.network.portwatcher.R;
import net.alexandroid.network.portwatcher.data.DbContract;
import net.alexandroid.network.portwatcher.helpers.MyLog;
import net.alexandroid.network.portwatcher.helpers.Utils;
import net.alexandroid.network.portwatcher.interfaces.OnSwipe;
import net.alexandroid.network.portwatcher.objects.ScanItem;
import net.alexandroid.network.portwatcher.ui.adapters.MainRecyclerAdapter;
import net.alexandroid.network.portwatcher.ui.adapters.decorators.SimpleDividerItemDecoration;

import java.util.ArrayList;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link MainFragmentInteractionListener}
 * interface.
 */
public class MainHistoryFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        OnSwipe {


    private static final int HISTORY_LOADER = 0;

    public static final int COL_ID = 0;
    public static final int COL_HOST = 1;
    public static final int COL_PORTS = 2;
    public static final int COL_WERE_OPEN = 3;
    public static final int COL_WHEN = 4;


    private MainFragmentInteractionListener mListener;
    private MainRecyclerAdapter mAdapter;
    private Paint mPaint = new Paint();
    private Cursor mCursor;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MainHistoryFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_history, container, false);
        setRecyclerView(view);
        return view;
    }

    private void setRecyclerView(View pView) {
        RecyclerView recyclerView = (RecyclerView) pView.findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext().getApplicationContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        enableSwipe(recyclerView, this, R.drawable.ic_refresh_wh, R.drawable.ic_delete);
        View emptyView = pView.findViewById(R.id.empty);
        mAdapter = new MainRecyclerAdapter(emptyView, mListener);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onSwipeLeft(int position) {
        ContentResolver contentResolver = getActivity().getApplicationContext().getContentResolver();

        mCursor.moveToPosition(position);
        String when = mCursor.getString(MainHistoryFragment.COL_WHEN);

        int numOfDeletedRows = contentResolver.delete(DbContract.HistoryEntry.CONTENT_URI,
                DbContract.HistoryEntry.COLUMN_DATE_TIME + "=" + when, null);
        MyLog.d("numOfDeletedRows: " + numOfDeletedRows);
    }

    @Override
    public void onSwipeRight(int position) {
        mAdapter.notifyDataSetChanged();

        mCursor.moveToPosition(position);
        String host = mCursor.getString(MainHistoryFragment.COL_HOST);
        String ports = mCursor.getString(MainHistoryFragment.COL_PORTS);
        ArrayList<Integer> portsList = Utils.convertStringToIntegerList(ports);

        mListener.onStartScan(portsList, host, -1);

        //noinspection ConstantConditions
        Snackbar.make(getView(), R.string.rescan2, Snackbar.LENGTH_SHORT).show();
    }

    private void enableSwipe(RecyclerView recyclerView, final OnSwipe pOnSwipe, final int rightSwipeRes, final int leftSwipeRes) {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT) {
                    MyLog.d("LEFT");
                    pOnSwipe.onSwipeLeft(position);
                } else {
                    MyLog.d("RIGHT");
                    pOnSwipe.onSwipeRight(position);
                }
            }


            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX > 0) {
                        mPaint.setColor(Color.parseColor("#4CAF50"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, mPaint);
                        icon = Utils.getBitmap(getContext(), rightSwipeRes);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, mPaint);
                    } else {
                        mPaint.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, mPaint);
                        icon = Utils.getBitmap(getContext(), leftSwipeRes);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, mPaint);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainFragmentInteractionListener) {
            mListener = (MainFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(HISTORY_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    public void clearHistory() {
        MyLog.d("clearHistory");
        ContentResolver contentResolver = getContext().getContentResolver();
        contentResolver.delete(DbContract.HistoryEntry.CONTENT_URI, null, null);
    }

    // Loader methods

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case HISTORY_LOADER:
                return new CursorLoader(getActivity(),
                        DbContract.HistoryEntry.CONTENT_URI, null, null, null, DbContract.HistoryEntry._ID + " DESC");
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursor = data;
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    // Interface

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface MainFragmentInteractionListener {
        void onItemClick(ScanItem item);

        void onStarClick(ScanItem item);

        void onShareClick(ScanItem item);

        void onStartScan(ArrayList<Integer> pList, String host, int scanId);
    }


}
