package com.dtstack.engine.master.enums;

public enum AccountType {
    TiDB(31),
    Oracle(2),
    GREENPLUM6(36),

    //非数据源账号体系从201开始递增
    LDAP(201);

    AccountType(int val) {
        this.val = val;
    }

    private int val;

    public int getVal() {
        return this.val;
    }
}