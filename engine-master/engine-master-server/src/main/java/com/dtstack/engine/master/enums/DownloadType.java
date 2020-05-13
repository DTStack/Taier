package com.dtstack.engine.master.enums;

/**
 * @author yuebai
 * @date 2020-05-09
 */
public enum DownloadType {
    Kerberos(1),
    Config(2),
    Template(3);

    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    DownloadType(int code) {
        this.code = code;
    }
}
