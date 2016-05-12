package net.alexandroid.network.portwatcher.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.alexandroid.network.portwatcher.R;
import net.alexandroid.network.portwatcher.helpers.MyLog;
import net.alexandroid.network.portwatcher.task.Ping;
import net.alexandroid.network.portwatcher.ui.activities.MainActivity;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@lin OnListOfMainFragmentInteractionListener}
 * interface.
 */
public class ScanFragment extends Fragment {

    private static boolean sPingResult;

    private TextView tvQuery, tvStatus;
    private ProgressBar progressBar;
    //private OnListOfMainFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ScanFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        setViews(view);
        start();
        return view;
    }


    private void setViews(View v) {
        tvQuery = (TextView) v.findViewById(R.id.tvQuery);
        tvStatus = (TextView) v.findViewById(R.id.tvStatus);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
    }

    public void refresh() {
        MyLog.d("refresh");
        tvStatus.setText("");
        progressBar.setVisibility(View.VISIBLE);
        start();
    }

    private void start() {
        tvQuery.setText(MainActivity.strLastQuery);
        checkAndPing(MainActivity.strLastQuery);
    }


    private void checkAndPing(String pStrLastQuery) {
        // TODO Add internet connection check

        new Thread(new Ping(pStrLastQuery, new Ping.CallBack() {
            @Override
            public void onResult(String strHost, boolean pingResult) {
                sPingResult = pingResult;
                getActivity().runOnUiThread(onPingResult);
            }
        })).start();
    }

    private Runnable onPingResult = new Runnable() {
        @Override
        public void run() {
            progressBar.setVisibility(View.GONE);
            tvStatus.setText(sPingResult ? getString(R.string.success) : getString(R.string.fail));
            tvStatus.setTextColor(sPingResult ? getResources().getColor(R.color.colorAccent) : Color.RED);
        }
    };

/*
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

    *//**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *//*
    public interface OnListOfMainFragmentInteractionListener {
        void onItemClick(ScanItem item);
    }*/
}
