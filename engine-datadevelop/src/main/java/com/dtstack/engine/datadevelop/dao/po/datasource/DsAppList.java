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
@TableName("dsc_app_list")
public class DsAppList extends BaseModel<DsAppList> {

    /**
     * 产品type code
     */
    @TableField("app_type")
    private Integer appType;

    /**
     * 产品编码
     */
    @TableField("app_code")
    private String appCode;

    /**
     * 产品名称
     */
    @TableField("app_name")
    private String appName;

    /**
     * 是否隐藏 0-不隐藏 1-隐藏
     */
    @TableField("invisible")
    private Integer invisible;

    /**
     * 排序值
     */
    @TableField("sorted")
    private Integer sorted;
}
