package com.dtstack.taier.datasource.plugin.kafka.enums;

/**
 * kafka 消费方式
 *
 * @author ：wangchuan
 * date：Created in 上午10:21 2021/3/16
 * company: www.dtstack.com
 */
public enum EConsumeType {

    /**
     * 从当前topic最早位置开始消费
     */
    EARLIEST,

    /**
     * 从当前位置开始消费
     */
    LATEST,

    /**
     * 从指定时间开始消费
     */
    TIMESTAMP
}
