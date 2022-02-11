package com.dtstack.taiga.dao.domain;

import com.baomidou.mybatisplus.annotation.TableName;
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
public class DsVersion extends BaseModel {

    /**
     * 数据源类型唯一 如Mysql, Oracle, Hive
     */
    private String dataType;

    /**
     * 数据源版本 如1.x, 0.9
     */
    private String dataVersion;

    /**
     * 版本排序字段,高版本排序,默认从0开始
     */
    private Integer sorted;


}
