package com.dtstack.batch.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author: 尘二(chener @ dtstack.com)
 * @Date: 2019/3/13 17:37
 * @Description:
 */
@Data
public class KillJobVo {

    private Long tenantId;

    private Long projectId;
    /**
     *  业务日期
     */
    private Long bizStartDay;

    /**
     * 业务日期
     */
    private Long bizEndDay;

    /**
     * 0周期任务；1补数据实例。
     */
    private Integer type;

    /**
     * 调度周期,用逗号分割
     */
    private String taskPeriodId;

    /**
     * 任务状态，不能是成功状态。用逗号分割
     */
    private String jobStatuses;

    /**
     * 任务id列表
     */
    private List<Long> taskIds;

}
