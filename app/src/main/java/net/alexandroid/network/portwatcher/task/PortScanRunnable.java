package net.alexandroid.network.portwatcher.task;

import net.alexandroid.network.portwatcher.helpers.MyLog;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;


public class PortScanRunnable implements Runnable {

    public static final int TIMEOUT_IN_MS = 2000;

    public static final int INIT = 0;
    public static final int OPEN = 1;
    public static final int WRONG_HOST = 1;
    public static final int TIMEOUT = 2;
    public static final int CLOSED = 3;

    private String host;
    private int port;
    private ScanResult mScanResult;

    private int state = INIT;

    public PortScanRunnable(String host, int port, ScanResult pScanResult) {
        this.host = host;
        this.port = port;
        this.mScanResult = pScanResult;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket();
            SocketAddress address = new InetSocketAddress(host, port);
            socket.connect(address, TIMEOUT_IN_MS);
            if (socket.isConnected() && socket.isBound()) {
                MyLog.d("Port is open, host: " + host + "  port: " + port);
                state = OPEN;
            }
            socket.close();
        } catch (UnknownHostException e) {
            MyLog.d("Wrong url, host: " + host + "  port: " + port);
            state = WRONG_HOST;
        } catch (SocketTimeoutException e) {
            MyLog.d("Time out, host: " + host + "  port: " + port);
            state = TIMEOUT;
        } catch (IOException e) {
            MyLog.d("Port is closed, host: " + host + "  port: " + port);
            state = CLOSED;
        }

        mScanResult.onResult(host, port, state);
    }
}
