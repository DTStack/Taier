package com.dtstack.engine.master.enums;

public enum KerberosKey {

    OPEN_KERBEROS("openKerberos"),PRINCIPAL("principal"),KEYTAB("keytab"),KRB5("krb5"),HDFS_CONFIG("hdfsConfig");

    private String key;

    KerberosKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
