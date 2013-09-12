package com.anusiewicz.MCForAndroid.model;

import java.util.ArrayList;

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

    public static ArrayList<MCDeviceCode> bitDevices() {

        ArrayList<MCDeviceCode> bitDevices = new ArrayList<MCDeviceCode>(6);
        bitDevices.add(X);
        bitDevices.add(Y);
        bitDevices.add(M);
        bitDevices.add(S);
        bitDevices.add(TS);
        bitDevices.add(CS);

        return bitDevices;
    }

    public static ArrayList<MCDeviceCode> wordDevices() {
        ArrayList<MCDeviceCode> wordDevices = new ArrayList<MCDeviceCode>(4);
        wordDevices.add(R);
        wordDevices.add(D);
        wordDevices.add(TN);
        wordDevices.add(CN);

        return wordDevices;
    }

    public static MCDeviceCode parseString(String string) {

        for (MCDeviceCode code : bitDevices()) {
            if (string.equals(code.toString())) {
                return code;
            }
        }

        for (MCDeviceCode code : wordDevices()) {
            if (string.equals(code.toString())) {
                return code;
            }
        }

        return null;
    }
}