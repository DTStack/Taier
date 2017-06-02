package com.dtstack.rdos.engine.execution.base.enumeration;


/**
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 *
 * @author sishu.yss
 */
public enum ComputeType {

    STREAM(0), BATCH(1);

    private Integer type;

    ComputeType(Integer type) {
        this.type = type;
    }

    public static ComputeType getComputeType(int type) {
        ComputeType[] computeTypes = ComputeType.values();
        for (ComputeType computeType : computeTypes) {
            if (computeType.type == type) {
                return computeType;
            }
        }
        return null;
    }

    public Integer getComputeType() {
        return this.type;
    }

}
