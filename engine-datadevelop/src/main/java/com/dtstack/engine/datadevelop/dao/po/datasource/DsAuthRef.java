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
@TableName("dsc_auth_ref")
public class DsAuthRef extends BaseModel<DsAuthRef> {

    /**
     * 数据源实例主键id {@link DsInfo}
     */
    @TableField("data_info_id")
    private Long dataInfoId;

    /**
     * 产品唯一编码 {@link DsAppList}
     */
    @TableField("app_type")
    private Integer appType;

}
