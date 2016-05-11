package net.alexandroid.network.portwatcher.objects;


import java.util.ArrayList;
import java.util.List;

public class ScanHistoryItem {
    String strIp, strPorts, strDateTime;

    public ScanHistoryItem(String pStrIp, String pStrPorts, String pStrDateTime) {
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

    public static List<ScanHistoryItem> getDummyList() {
        List<ScanHistoryItem> items = new ArrayList<>();
        items.add(new ScanHistoryItem("192.168.0.1", "80,8080", "04/02/2016 16:56"));
        items.add(new ScanHistoryItem("10.0.0.138", "90", "01/03/2016 11:33"));
        items.add(new ScanHistoryItem("192.168.100.10", "2000-2005", "04/01/2016 10:34"));
        return items;
    }
}
