package com.dtstack.taiga.common.enums;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : hanbeikai
 * Date: 2021/12/15 11:48 下午
 * Description: No Description
 */
public enum ComputeType {

    STREAM(0),BATCH(1);

    private int type;

    ComputeType(int type){
        this.type = type;
    }

    public static ComputeType getComputeType(int type){
        ComputeType[] computeTypes = ComputeType.values();
        for(ComputeType computeType:computeTypes){
            if(computeType.type == type){
                return computeType;
            }
        }
        return null;
    }

    public int getType(){
        return this.type;
    }
}
