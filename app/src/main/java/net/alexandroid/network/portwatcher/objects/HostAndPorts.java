package net.alexandroid.network.portwatcher.objects;

import java.util.ArrayList;

public class HostAndPorts {

    private String host;
    private ArrayList<Integer> portsList;

    public HostAndPorts(String host, ArrayList<Integer> portsList) {
        this.host = host;
        this.portsList = portsList;
    }

    public String getHost() {
        return host;
    }

    public ArrayList<Integer> getPortsList() {
        return portsList;
    }
}
