package com.dtstack.engine.datadevelop.dao.po.datasource;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dtstack.engine.datadevelop.dao.po.TenantModel;
import lombok.Data;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Data
@TableName("dsc_info")
public class DsInfo extends TenantModel<DsInfo> {

    /**
     * 数据源类型唯一
     */
    @TableField("data_type")
    private String dataType;

    /**
     * 数据源类型编码
     */
    @TableField("data_type_code")
    private Integer dataTypeCode;

    /**
     * 数据源版本
     */
    @TableField("data_version")
    private String dataVersion;

    /**
     * 数据源名称
     */
    @TableField("data_name")
    private String dataName;

    /**
     * 数据源描述
     */
    @TableField("data_desc")
    private String dataDesc;

    /**
     * 数据源连接信息
     */
    @TableField("link_json")
    private String linkJson;

    /**
     * 数据源填写的表单信息
     */
    @TableField("data_json")
    private String dataJson;

    /**
     * 连接状态
     */
    @TableField("status")
    private Integer status;

    /**
     * 是否有meta标志
     */
    @TableField("is_meta")
    private Integer isMeta;

//    /**
//     * 租户主键id
//     */
//    @TableField("tenant_id")
//    private Long tenantId;
}
