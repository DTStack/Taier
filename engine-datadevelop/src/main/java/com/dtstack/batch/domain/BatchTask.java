package com.dtstack.batch.domain;

import lombok.Data;

/**
 * Author: Rock.Stone
 * Date: 17/2/24
 * Dream:
 * ---------------------------------------------------------------------------------------------------------------------
 * I've heard tell of the existence of a legless bird in the real world.
 * A constant flutter in the space above constitutes its whole life.Prostrated with toil and strain,
 * it just takes repose in the wind. Throughout countless nights and days,
 * only once will its body brush the dust of the ground and that's the very time when it bids farewell to the world
 * ---------------------------------------------------------------------------------------------------------------------
 */
@Data
public class BatchTask extends TenantProjectEntity {

    /**
     * '任务名称'
     */
    private String name;

    /**
     * '任务类型 0 sql，1 mr，2 sync ，3 python
     */
    private Integer taskType;

    /**
     * '计算类型 0实时，1 离线'
     */
    private Integer computeType;

    /**
     * '执行引擎类型 0 flink, 1 spark'
     */
    private Integer engineType;

    /**
     * 'sql 文本'
     */
    private String sqlText;

    /**
     * '任务参数'
     */
    private String taskParams;

    /**
     * 调度配置
     */
    private String scheduleConf;

    /**
     * 周期类型
     */
    private Integer periodType;

    /**
     * 调度状态
     */
    private Integer scheduleStatus;

    private Integer submitStatus;

    /**
     * 任务发布状态，前端使用
     */
    private Integer status;

    /**
     * 最后修改task的用户
     */
    private Long modifyUserId;

    /**
     * 新建task的用户
     */
    private Long createUserId;

    /**
     * 负责人id
     */
    private Long ownerUserId;

    /**
     * 'task版本'
     */
    private Integer version;

    private Long nodePid;

    /**
     * 任务描述
     */
    private String taskDesc;

    /**
     * 入口类
     */
    private String mainClass;

    private String exeArgs;

    /**
     * 所属工作流id
     */
    private Long flowId = 0L;

    /**
     * 是否过期
     */
    private Integer isExpire;

    /**
     * 组件版本
     */
    private String componentVersion;

    public Integer getIsExpire() {
        return isExpire;
    }

    public void setIsExpire(Integer isExpire) {
        this.isExpire = isExpire;
    }
}
