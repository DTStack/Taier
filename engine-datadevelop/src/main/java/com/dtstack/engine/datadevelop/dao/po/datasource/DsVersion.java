package com.dtstack.pubsvc.dao.po.datasource;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dtstack.pubsvc.dao.po.BaseModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author 全阅
 * @Description: 数据源版本类
 * @Date: 2021/3/10
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
