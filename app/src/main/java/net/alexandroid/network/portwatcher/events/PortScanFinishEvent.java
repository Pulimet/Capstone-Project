package net.alexandroid.network.portwatcher.events;

import android.util.SparseIntArray;

public class PortScanFinishEvent {
    public final String host;
    public final SparseIntArray scanResults;
    public final boolean isListScanFinished;

    public PortScanFinishEvent(String host, SparseIntArray scanResults, boolean isListScanFinished) {
        this.host = host;
        this.scanResults = scanResults;
        this.isListScanFinished = isListScanFinished;
    }
}
