package com.dtstack.taier.common.metric.stream.prometheus;

/**
 * @author ：wangchuan
 * date：Created in 下午3:57 2021/4/16
 * company: www.dtstack.com
 */
public interface ICustomMetric<T> {

    /**
     * 获取指标信息
     *
     * @param maxPoint 最大指标点数
     * @return 指标信息
     */
    T getMetric(Integer maxPoint);
}
