package com.jamille.android.network;

/**
 * Created by Jamille on 07/06/2018.
 */

public class ArpEntry {
    public String ipAddress;
    public int hardwareType;
    public int flag;
    public String macAddress;
    public String mask;

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String online = "Online";

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String device;
    public String vendor;
    public int id;

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArpEntry(String ipAddress, int hardwareType, int flag, String macAddress, String mask,
                    String device, String vendor, String online) {
        this.ipAddress = ipAddress;
        this.hardwareType = hardwareType;
        this.flag = flag;
        this.macAddress = macAddress;
        this.online = online;
        this.mask = mask;
        this.device = device;
        this.vendor = vendor;
    }

    public ArpEntry(int id, String ipAddress, String macAddress) {
        this.id = id;
        this.ipAddress = ipAddress;
        this.macAddress = macAddress;
    }

    public ArpEntry() {
        super();
    }
}
