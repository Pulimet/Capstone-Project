package net.alexandroid.network.portwatcher.objects;


import java.util.ArrayList;
import java.util.List;

public class ScanItem {
    public static final int INITAL = 0;
    public static final int STARTED = 1;
    public static final int FINISHED = 2;

    private int scanStatus;

    // systemTime, strDateTime - When scan finished
    private long systemTime;
    private String strHost, strPorts, strDateTime, strWereOpen;

    // Dummy adapter
    public ScanItem(String pStrHost, String pStrPorts, String pStrDateTime, String pStrWereOpen) {
        strHost = pStrHost;
        strPorts = pStrPorts;
        strDateTime = pStrDateTime;
        strWereOpen = pStrWereOpen;
    }

    public String getStrHost() {
        return strHost;
    }

    public void setStrHost(String pStrHost) {
        strHost = pStrHost;
    }

    public String getStrPorts() {
        return strPorts;
    }

    public void setStrPorts(String pStrPorts) {
        strPorts = pStrPorts;
    }

    public String getStrDateTime() {
        return strDateTime;
    }

    public void setStrDateTime(String pStrDateTime) {
        strDateTime = pStrDateTime;
    }

    public int getScanStatus() {
        return scanStatus;
    }

    public void setScanStatus(int pScanStatus) {
        scanStatus = pScanStatus;
    }

    public long getSystemTime() {
        return systemTime;
    }

    public void setSystemTime() {
        systemTime = System.currentTimeMillis();
    }


    public String getStrWereOpen() {
        return strWereOpen;
    }

    public void setStrWereOpen(String pStrWereOpen) {
        strWereOpen = pStrWereOpen;
    }
}
