package com.dtstack.engine.common.enums;


/**
 *
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

    public static ComputeType getType(int type) {
        ComputeType[] computeTypes = ComputeType.values();
        for (ComputeType computeType : computeTypes) {
            if (computeType.type == type) {
                return computeType;
            }
        }
        return null;
    }

    public Integer getType() {
        return this.type;
    }

    public boolean typeEqual(Integer targetType){
        if(type.equals(targetType)){
            return true;
        }

        return false;
    }

}
