package com.dtstack.taier.common.enums;

public enum KafkaTimeFeature {
    /**
     *
     */
    PROCTIME(1),
    /**
     *
     */
    EVENTTIME(2);

    private int value;

    KafkaTimeFeature(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}