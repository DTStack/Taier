package com.dtstack.pubsvc.dao.po.datasource;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dtstack.pubsvc.dao.po.BaseModel;
import lombok.Data;

/**
 * @author 全阅
 * @Description: 数据源与授权产品引入相关联
 * @Date: 2021/3/22
 */
@Data
@TableName("dsc_import_ref")
public class DsImportRef extends BaseModel<DsImportRef> {

    /**
     * 数据源实例主键id {@link DsInfo}
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
