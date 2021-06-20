package com.dtstack.engine.datasource.dao.po.datasource;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dtstack.engine.datasource.dao.po.BaseModel;
import lombok.Data;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Data
@TableName("dsc_app_mapping")
public class DsAppMapping extends BaseModel<DsAppMapping> {

    /**
     * 产品唯一编码
     */
    @TableField("app_type")
    private Integer appType;

    /**
     * 数据源类型唯一
     */
    @TableField("data_type")
    private String dataType;

    /**
     * 数据源版本
     */
    @TableField("data_version")
    private String dataVersion;


}
