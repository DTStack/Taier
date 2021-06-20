package com.dtstack.engine.datadevelop.dao.po.datasource;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dtstack.engine.datadevelop.dao.po.BaseModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Data
@Accessors(chain = true)
@TableName("dsc_version")
public class DsVersion extends BaseModel<DsVersion> {

    /**
     * 数据源类型唯一 如Mysql, Oracle, Hive
     */
    @TableField("data_type")
    private String dataType;

    /**
     * 数据源版本 如1.x, 0.9
     */
    @TableField("data_version")
    private String dataVersion;

    /**
     * 版本排序字段,高版本排序,默认从0开始
     */
    @TableField("sorted")
    private Integer sorted;


}
