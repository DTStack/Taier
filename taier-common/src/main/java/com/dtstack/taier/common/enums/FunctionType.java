package com.dtstack.taier.common.enums;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : hanbeikai
 * Date: 2021/12/15 11:42 下午
 * Description: No Description
 */
public enum FunctionType {

    /**
     * 自定义函数
     */
    USER(0),
    /**
     * 系统函数
     */
    SYSTEM(1);

    private int type;

    FunctionType(int type){
        this.type = type;
    }

    public int getType(){
        return this.type;
    }
}
