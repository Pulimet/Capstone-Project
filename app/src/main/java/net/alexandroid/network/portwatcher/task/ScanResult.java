package net.alexandroid.network.portwatcher.task;


public interface ScanResult {
    void onResult(String host, int port, int state);
}
