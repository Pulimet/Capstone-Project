package net.alexandroid.network.portwatcher.objects;


import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

public class ScanItem {
    public static final int INITAL = 0;
    public static final int STARTED = 1;
    public static final int FINISHED = 2;

    private int scanStatus;

    // systemTime, strDateTime - When scan finished
    private long systemTime;
    private String strIp, strPorts, strDateTime;
    private SparseArray<Boolean> results = new SparseArray<>();

    // Dummy adapter
    public ScanItem(String pStrIp, String pStrPorts, String pStrDateTime) {
        strIp = pStrIp;
        strPorts = pStrPorts;
        strDateTime = pStrDateTime;
    }

    public String getStrIp() {
        return strIp;
    }

    public void setStrIp(String pStrIp) {
        strIp = pStrIp;
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

    public SparseArray<Boolean> getResults() {
        return results;
    }

    public void setResults(SparseArray<Boolean> pResults) {
        results = pResults;
    }

    public void addResult(int port, boolean result) {
        results.setValueAt(port, result);
    }

    public long getSystemTime() {
        return systemTime;
    }

    public void setSystemTime() {
        systemTime = System.currentTimeMillis();
    }

    public static List<ScanItem> getDummyList() {
        List<ScanItem> items = new ArrayList<>();
        items.add(new ScanItem("192.168.0.1", "80,8080", "04/02/2016 16:56"));
        items.add(new ScanItem("10.0.0.138", "90", "01/03/2016 11:33"));
        items.add(new ScanItem("192.168.100.10", "2000-2005", "04/01/2016 10:34"));
        items.add(new ScanItem("192.168.0.1", "80,8080", "04/02/2016 16:56"));
        items.add(new ScanItem("10.0.0.138", "90", "01/03/2016 11:33"));
        items.add(new ScanItem("192.168.100.10", "2000-2005", "04/01/2016 10:34"));
        items.add(new ScanItem("192.168.0.1", "80,8080", "04/02/2016 16:56"));
        items.add(new ScanItem("10.0.0.138", "90", "01/03/2016 11:33"));
        items.add(new ScanItem("192.168.100.10", "2000-2005", "04/01/2016 10:34"));
        items.add(new ScanItem("192.168.0.1", "80,8080", "04/02/2016 16:56"));
        items.add(new ScanItem("10.0.0.138", "90", "01/03/2016 11:33"));
        items.add(new ScanItem("192.168.100.10", "2000-2005", "04/01/2016 10:34"));
        return items;
    }
}
