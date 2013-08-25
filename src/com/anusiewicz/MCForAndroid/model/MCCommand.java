package com.anusiewicz.MCForAndroid.model;


/**
 * Created with IntelliJ IDEA.
 * User: Szymon Anusiewicz
 * Date: 24.08.13
 * Time: 23:53
 */
public enum MCCommand {
    READ_BIT("00"),
    READ_WORD("01"),
    WRITE_BIT("02"),
    WRITE_WORD("03"),
    PLC_RUN("13"),
    PLC_STOP("14");
    private String commandCode;
    private MCCommand(String code) {
        commandCode = code;
    }

    public String getCommandCode() {
        return commandCode;
    }

}
