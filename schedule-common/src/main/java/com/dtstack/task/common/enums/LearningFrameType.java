package com.dtstack.task.common.enums;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public enum LearningFrameType {

    TensorFlow(0,"tensorflow"),MXNet(1,"mxnet");

    private int type;

    private String name;

    LearningFrameType(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static LearningFrameType getByType(int type){
        for (LearningFrameType frameType : LearningFrameType.values()) {
            if(frameType.getType() == type){
                return frameType;
            }
        }

        return null;
    }

    public static LearningFrameType getByName(String name){
        for (LearningFrameType frameType : LearningFrameType.values()) {
            if(frameType.getName().equals(name)){
                return frameType;
            }
        }

        return TensorFlow;
    }
}
