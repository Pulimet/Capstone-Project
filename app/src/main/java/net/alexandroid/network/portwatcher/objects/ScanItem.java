package net.alexandroid.network.portwatcher.objects;


import android.os.Parcel;
import android.os.Parcelable;

public class ScanItem implements Parcelable {

    // systemTime, strDateTime - When scan finished
    private long systemTime;
    private String strHost, strPorts, strDateTime, strWereOpen;

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.systemTime);
        dest.writeString(this.strHost);
        dest.writeString(this.strPorts);
        dest.writeString(this.strDateTime);
        dest.writeString(this.strWereOpen);
    }

    protected ScanItem(Parcel in) {
        this.systemTime = in.readLong();
        this.strHost = in.readString();
        this.strPorts = in.readString();
        this.strDateTime = in.readString();
        this.strWereOpen = in.readString();
    }

    public static final Parcelable.Creator<ScanItem> CREATOR = new Parcelable.Creator<ScanItem>() {
        @Override
        public ScanItem createFromParcel(Parcel source) {
            return new ScanItem(source);
        }

        @Override
        public ScanItem[] newArray(int size) {
            return new ScanItem[size];
        }
    };
}
