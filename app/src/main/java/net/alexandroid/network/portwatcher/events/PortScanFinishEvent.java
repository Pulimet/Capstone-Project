package net.alexandroid.network.portwatcher.events;

import java.util.concurrent.ConcurrentHashMap;

public class PortScanFinishEvent {
    public final String host;
    public final ConcurrentHashMap<Integer, Integer>  scanResults;
    public final boolean isListScanFinished;

    public PortScanFinishEvent(String host, ConcurrentHashMap<Integer, Integer> scanResults, boolean isListScanFinished) {
        this.host = host;
        this.scanResults = scanResults;
        this.isListScanFinished = isListScanFinished;
    }
}
