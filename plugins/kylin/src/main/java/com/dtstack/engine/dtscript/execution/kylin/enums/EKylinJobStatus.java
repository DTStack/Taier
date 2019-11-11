package com.dtstack.engine.dtscript.execution.kylin.enums;

import com.dtstack.engine.common.enums.RdosTaskStatus;

/**
 * @author jiangbo
 * @date 2019/7/2
 */
public enum EKylinJobStatus {

    NEW(RdosTaskStatus.WAITCOMPUTE, 0),

    PENDING(RdosTaskStatus.WAITCOMPUTE, 1),

    RUNNING(RdosTaskStatus.RUNNING, 2),

    FINISHED(RdosTaskStatus.FINISHED, 4),

    ERROR(RdosTaskStatus.FAILED, 8),

    STOPPED(RdosTaskStatus.CANCELED, 32),

    DISCARDED(RdosTaskStatus.CANCELED, 16);

    private RdosTaskStatus rdosStatus;

    private int code;

    EKylinJobStatus(RdosTaskStatus rdosStatus, int code) {
        this.rdosStatus = rdosStatus;
        this.code = code;
    }

    public static EKylinJobStatus getByName(String name){
        for (EKylinJobStatus value : EKylinJobStatus.values()) {
            if(value.name().equals(name)){
                return value;
            }
        }

        throw new IllegalArgumentException("No enum constant " + name);
    }

    public RdosTaskStatus getRdosStatus() {
        return rdosStatus;
    }

    public int getCode() {
        return code;
    }
}
