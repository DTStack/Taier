package com.dtstack.engine.common.enums;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/7/18
 */
public enum DictType {

    DATA_SOURCE(1),//数据源类型
    HIVE_COLUMN_TYPE(2),//hive表字段类型
    SCRIPT_TYPE(3),//脚本类型
    BATCH_CATALOGUE(4),//离线root目录类型
    STREAM_CATALOGUE(5),//实时root目录类型
    BATCH_CATALOGUE_L1(6),//离线一级目录类型
    STREAM_CATALOGUE_L1(7),//实时一级目录类型
    AUTH_HIDE(8),//权限树隐藏的权限点
    MODEL_COLUMN_TYPE(9),//数据模型原子指标和衍生指标的支持的数据类型

    BATCH_TASK_TYPE_YARN(10),//集群部署时支持的任务类型
    BATCH_TASK_TYPE_STANDALONE(11),//单机部署支持的任务类型
    BATCH_TASK_TYPE_GaussDB(12),//GaussDB 引擎类型支持的任务类型

    BATCH_FUNCTION(13),//引擎支持方法列表

    DATASYNC_CATALOGUE(14), //数据集成root目录类型
    DATASYNC_CATALOGUE_L1(15); //数据集成一级目录类型

    private int type;

    DictType(int type) {
        this.type = type;
    }

    public int getValue() {
        return type;
    }
}
