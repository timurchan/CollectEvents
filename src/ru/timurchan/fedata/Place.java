package ru.timurchan.fedata;



import java.io.Serializable;

public class Place extends CityBase  {
    private String title;
    private String address;
    public String category;
    public String fsid;
    public double lat = 0.0;
    public double lng = 0.0;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    static private boolean isEmptyData(final String data) {
        return data == null || data.isEmpty();
    }

    static private boolean isEmptyData(int data) {
        return data == 0;
    }

    public boolean isEmptyData() {
        boolean res = isEmptyData(title);
        res = res && isEmptyData(address);
        res = res && isEmptyData(category);
        res = res && isEmptyData(fsid);
        res = res && (int)lat == 0 && (int)lng == 0;
        return res;
    }
}
