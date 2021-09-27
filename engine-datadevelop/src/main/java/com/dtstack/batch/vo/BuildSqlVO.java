package com.dtstack.batch.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @company：dtstack.com
 * @author yunliu
 * @date 2020-04-28 15:43
 * @description 解析sql返回的参数
 */
@Data
@Accessors(chain = true)
public class BuildSqlVO {

    /**
     * 生成的jobid
     */
    private String jobId;

    /**
     * 解析之后的sql
     */
    private String sql;

    /**
     * sql运行需要的参数
     */
    private String taskParam;

    /**
     * 生成的临时表名
     */
    private String tempTable;

    /**
     * 是否是查询sql
     */
    private Integer isSelectSql = 0;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 项目id
     */
    private Long projectId;

    /**
     * 原本的sql
     */
    private String originSql;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 解析出来的列名
     */
    private String parsedColumns;
    /**
     * 引擎类型
     */
    private Integer engineType = 0;


}
