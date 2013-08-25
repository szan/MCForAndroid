package com.anusiewicz.MCForAndroid.model;

/**
 * Created with IntelliJ IDEA.
 * User: Szymon Anusiewicz
 * Date: 24.08.13
 * Time: 23:51
 */
public enum MCDeviceCode {
    D("4420", 7999),
    R("5220", 32767),
    TN("544E", 511),
    TS("5453", 511),
    CN("434E", 199),
    CS("4353", 199),
    X("5820", 377),
    Y("5920", 377),
    M("4D20", 7679),
    S("5320", 4095);

    private String deviceCode;
    private int deviceRange;

    private MCDeviceCode(String code, int range) {
        deviceCode = code;
        deviceRange = range;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public int getDeviceRange() {
        return deviceRange;
    }

    public static MCDeviceCode[] bitDevices() {

        MCDeviceCode[] bitDevices = new MCDeviceCode[6];
        bitDevices[0] = X;
        bitDevices[1] = Y;
        bitDevices[2] = M;
        bitDevices[3] = S;
        bitDevices[4] = TS;
        bitDevices[5] = CS;

        return bitDevices;
    }

    public static MCDeviceCode[] wordDevices() {
        MCDeviceCode[] wordDevices = new MCDeviceCode[4];
        wordDevices[0] = R;
        wordDevices[1] = D;
        wordDevices[2] = TN;
        wordDevices[3] = CN;

        return wordDevices;
    }
}