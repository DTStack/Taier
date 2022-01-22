package com.dtstack.taiga.common.enums;

public enum TaskLockStatus {
    TO_UPDATE(0),
    TO_CONFIRM(1),
    UPDATE_COMPLETED(2);

    private int val;
    TaskLockStatus(int val){
        this.val = val;
    }
    public int getVal() {
        return val;
    }
}
