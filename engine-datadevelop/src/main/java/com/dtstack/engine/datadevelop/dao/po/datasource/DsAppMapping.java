package com.dtstack.pubsvc.dao.po.datasource;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dtstack.pubsvc.dao.po.BaseModel;
import lombok.Data;

/**
 * @author 全阅
 * @Description: 数据源和产品映射类
 * @Date: 2021/3/10
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
