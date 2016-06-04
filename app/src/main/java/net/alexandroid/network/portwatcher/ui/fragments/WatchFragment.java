package net.alexandroid.network.portwatcher.ui.fragments;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.DatabaseUtils;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import net.alexandroid.network.portwatcher.R;
import net.alexandroid.network.portwatcher.data.DbContract;
import net.alexandroid.network.portwatcher.data.DbHelper;
import net.alexandroid.network.portwatcher.helpers.MyLog;
import net.alexandroid.network.portwatcher.helpers.Utils;
import net.alexandroid.network.portwatcher.interfaces.OnSwipe;
import net.alexandroid.network.portwatcher.ui.adapters.WatchRecyclerAdapter;
import net.alexandroid.network.portwatcher.ui.adapters.decorators.SimpleDividerItemDecoration;

import java.util.ArrayList;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link WatchFragmentInteractionListener}
 * interface.
 */
public class WatchFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        OnSwipe {

    private static final int WATCHLIST_LOADER = 0;

    public static final int COL_ID = 0;
    public static final int COL_HOST = 1;
    public static final int COL_PORTS = 2;

    private WatchFragmentInteractionListener mListener;
    private WatchRecyclerAdapter mAdapter;
    private Paint mPaint = new Paint();
    private Cursor mCursor;
    private boolean dialogAddFlag;
    private EditText etHost, etPort;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WatchFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_watch, container, false);
        setRecyclerView(view);
        return view;
    }

    private void setRecyclerView(View pView) {
        RecyclerView recyclerView = (RecyclerView) pView.findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext().getApplicationContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        enableSwipe(recyclerView, this, R.drawable.ic_menu_edit_wh, R.drawable.ic_delete);
        View emptyView = pView.findViewById(R.id.empty);
        mAdapter = new WatchRecyclerAdapter(emptyView, mListener);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onSwipeLeft(int position) {
        mCursor.moveToPosition(position);
        String host = mCursor.getString(WatchFragment.COL_HOST);
        String ports = mCursor.getString(WatchFragment.COL_PORTS);
        deleteFromDb(host, ports);
    }

    private void deleteFromDb(String pHost, String pPorts) {
        ContentResolver contentResolver = getActivity().getApplicationContext().getContentResolver();
        String where = DbContract.WatchlistEntry.COLUMN_HOST + "=" + DatabaseUtils.sqlEscapeString(pHost) + " AND " +
                DbContract.WatchlistEntry.COLUMN_PORTS + "=" + DatabaseUtils.sqlEscapeString(pPorts);
        int numOfDeletedRows = contentResolver.delete(DbContract.WatchlistEntry.CONTENT_URI, where, null);
        MyLog.d("numOfDeletedRows: " + numOfDeletedRows);
    }

    @Override
    public void onSwipeRight(int position) {
        mAdapter.notifyDataSetChanged();

        mCursor.moveToPosition(position);
        String host = mCursor.getString(WatchFragment.COL_HOST);
        String ports = mCursor.getString(WatchFragment.COL_PORTS);

        dialogAddFlag = false;
        showAddOrEditDialog(host, ports);
    }

    private void showAddOrEditDialog(final String host, final String ports) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_watch_fragment, null);
        alertDialog.setTitle(dialogAddFlag ? getStr(R.string.add_new) : getStr(R.string.edit2));
        alertDialog.setView(view);
        alertDialog.setPositiveButton(
                dialogAddFlag ? getStr(R.string.add) : getStr(R.string.save),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        etHost = (EditText) view.findViewById(R.id.input_host);
        etPort = (EditText) view.findViewById(R.id.input_port);
        if (!dialogAddFlag) {
            etHost.setText(host);
            etPort.setText(ports);
        }

        final AlertDialog dialog = alertDialog.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                Button button = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @SuppressWarnings("ConstantConditions")
                    @Override
                    public void onClick(View v) {
                        MyLog.d("onClick");
                        String newHost = etHost.getText().toString().trim();
                        String newPorts = etPort.getText().toString().trim();
                        ArrayList<Integer> list = Utils.convertStringToIntegerList(newPorts);
                        String checkedPorts = Utils.convertIntegerListToString(list);
                        etPort.setText(checkedPorts);

                        MyLog.d("host: " + host);
                        MyLog.d("checkedPorts: " + checkedPorts);

                        if (newHost.length() > 5 && checkedPorts.length() > 0) {
                            if (dialogAddFlag) {
                                addToDb(newHost, checkedPorts);
                                Snackbar.make(getView(), R.string.added_to_watchlist, Snackbar.LENGTH_SHORT).show();
                                dialog.dismiss();
                            } else {
                                editRowDb(host, ports, newHost, checkedPorts);
                                Snackbar.make(getView(), R.string.saved, Snackbar.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        } else {
                            MyLog.d("Wrong parameters");
                            Snackbar.make(etHost, R.string.wrong_params, Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    private void editRowDb(String pHost, String pPorts, String pNewHost, String pNewPorts) {
        MyLog.d("editRowDb");
        ContentResolver contentResolver = getActivity().getApplicationContext().getContentResolver();
        String where = DbContract.WatchlistEntry.COLUMN_HOST + "=" + DatabaseUtils.sqlEscapeString(pHost) + " AND " +
                DbContract.WatchlistEntry.COLUMN_PORTS + "=" + DatabaseUtils.sqlEscapeString(pPorts);
        ContentValues cv = DbHelper.getWatchlistContentValues(pNewHost, pNewPorts);
        int numOfUpdatedRows = contentResolver.update(DbContract.WatchlistEntry.CONTENT_URI, cv, where, null);
        MyLog.d("numOfUpdatedRows: " + numOfUpdatedRows);
    }

    private void addToDb(String host, String ports) {
        MyLog.d("addToDb");
        ContentResolver contentResolver = getActivity().getApplicationContext().getContentResolver();
        ContentValues contentValues =
                DbHelper.getWatchlistContentValues(host, ports);
        contentResolver.insert(DbContract.WatchlistEntry.CONTENT_URI, contentValues);
    }

    private String getStr(int res) {
        return getActivity().getString(res);
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

    public void onFabClick() {
        dialogAddFlag = true;
        showAddOrEditDialog(null, null);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof WatchFragmentInteractionListener) {
            mListener = (WatchFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement WatchFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(WATCHLIST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    // Loader methods
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case WATCHLIST_LOADER:
                return new CursorLoader(getActivity(),
                        DbContract.WatchlistEntry.CONTENT_URI, null, null, null, DbContract.WatchlistEntry._ID + " DESC");
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface WatchFragmentInteractionListener {
        void onStartScan(ArrayList<Integer> pList, String host, int scanId);
    }

}
