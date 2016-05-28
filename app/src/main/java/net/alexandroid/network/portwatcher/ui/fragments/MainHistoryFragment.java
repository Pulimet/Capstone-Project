package net.alexandroid.network.portwatcher.ui.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.alexandroid.network.portwatcher.R;
import net.alexandroid.network.portwatcher.data.DbContract;
import net.alexandroid.network.portwatcher.objects.ScanItem;
import net.alexandroid.network.portwatcher.ui.adapters.MainRecyclerAdapter;
import net.alexandroid.network.portwatcher.ui.adapters.decorators.SimpleDividerItemDecoration;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListOfMainFragmentInteractionListener}
 * interface.
 */
public class MainHistoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int HISTORY_LOADER = 0;

    public static final int COL_ID = 0;
    public static final int COL_HOST = 1;
    public static final int COL_PORTS = 2;
    public static final int COL_WERE_OPEN = 3;
    public static final int COL_WHEN = 4;


    private OnListOfMainFragmentInteractionListener mListener;
    private MainRecyclerAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MainHistoryFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_history, container, false);

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext().getApplicationContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        View emptyView = view.findViewById(R.id.empty);

        mAdapter = new MainRecyclerAdapter(emptyView, mListener);
        recyclerView.setAdapter(mAdapter);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListOfMainFragmentInteractionListener) {
            mListener = (OnListOfMainFragmentInteractionListener) context;
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

    // Loader methods

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                DbContract.HistoryEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
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
    public interface OnListOfMainFragmentInteractionListener {
        void onItemClick(ScanItem item);

        void onStarClick(ScanItem item);

        void onShareClick(ScanItem item);
    }


}
