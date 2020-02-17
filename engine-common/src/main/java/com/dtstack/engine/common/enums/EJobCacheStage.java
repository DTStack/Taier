package com.dtstack.engine.common.enums;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/01/17
 */
public enum EJobCacheStage {
    //JOB 在DB中，未加到优先级队列
    DB(1),
    //JOB 在优先级队列，等待提交
    PRIORITY(2),
    //JOB 因为失败进入重试队列，等待重试的delay时间后，可以重新提交
    RESTART(3),
    //JOB 已经提交，处于状态轮询中
    SUBMITTED(4);


    int stage;

    EJobCacheStage(int stage) {
        this.stage = stage;
    }

    public int getStage() {
        return stage;
    }

    public static List<Integer> unSubmitted() {
        return Lists.newArrayList(
                DB.getStage(),
                PRIORITY.getStage(),
                RESTART.getStage()
        );
    }

    public static List<Integer> allStage() {
        return Arrays.stream(EJobCacheStage.values()).map(EJobCacheStage::getStage).collect(Collectors.toList());
    }
}
