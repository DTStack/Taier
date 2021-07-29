package com.dtstack.batch.vo;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author jiangbo
 * @time 2017/12/11
 */
@Data
public class BatchTableBloodInfoVO {

    private Long tableId;

    private String tableName;

    private String catalogue;

    private Long catalogueId = 0L;

    private String createUser;

    private Timestamp createTime;

    private String comment;

    private Integer upstreamLevelNum = 0;

    private Integer downstreamLevelNum = 0;

    private Integer totalUpstreamNum = 0;

    private Integer totalDownstreamNum = 0;

    private Integer directUpstreamNum = 0;

    private Integer directDownstreamNum = 0;

    /**
     * 在血缘关系页面展示，作为表的前缀，hive表为项目名称，其它表为数据源名称
     */
    private String dataSource;
}
