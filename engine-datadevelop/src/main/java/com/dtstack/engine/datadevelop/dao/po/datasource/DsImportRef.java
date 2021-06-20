package com.dtstack.engine.datadevelop.dao.po.datasource;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dtstack.engine.datadevelop.dao.po.BaseModel;
import lombok.Data;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Data
@TableName("dsc_import_ref")
public class DsImportRef extends BaseModel<DsImportRef> {

    /**
     * 数据源实例主键id
     */
    @TableField("data_info_id")
    private Long dataInfoId;

    /**
     * 各平台数据源id
     */
    @TableField("old_data_info_id")
    private Long oldDataInfoId;

    /**
     * 产品唯一编码 {@link DsAppList}
     */
    @TableField("app_type")
    private Integer appType;

    /**
     * 项目id
     */
    @TableField("project_id")
    private Long projectId;

    /**
     * dtuic租户id
     */
    @TableField("dtuic_tenant_id")
    private Long dtUicTenantId;

}
