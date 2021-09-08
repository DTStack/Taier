package com.dtstack.batch.domain;

import com.dtstack.engine.domain.TenantProjectEntity;
import lombok.Data;

@Data
public class BatchFunction extends TenantProjectEntity {

    /**
     * 函数名称
     */
    private String name;

    /**
     * main函数类名
     */
    private String className;

    /**
     * 函数用途
     */
    private String purpose;

    /**
     * 函数命令格式
     */
    private String commandFormate;

    /**
     * 函数参数说明
     */
    private String paramDesc;

    /**
     * 父文件夹id
     */
    private Long nodePid;

    private Long createUserId;

    private Long modifyUserId;

    /**
     * 0：自定义函数  1：系统函数  2：存储过程
     */
    private Integer type;

    private Integer engineType;

    /**
     * 导入导出添加，函数资源名称
     */
    private String resourceName;

    /**
     * 存储过程sql
     */
    private String sqlText;

}
