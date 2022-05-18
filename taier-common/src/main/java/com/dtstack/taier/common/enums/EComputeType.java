package com.dtstack.taier.common.enums;

import com.dtstack.taier.pluginapi.enums.ComputeType;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : hanbeikai
 * Date: 2021/12/15 11:48 下午
 * Description: No Description
 */
public enum EComputeType {

    STREAM(0),BATCH(1);

    private int type;

    EComputeType(int type){
        this.type = type;
    }

    public static EComputeType getComputeType(int type){
        EComputeType[] computeTypes = EComputeType.values();
        for(EComputeType computeType:computeTypes){
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
