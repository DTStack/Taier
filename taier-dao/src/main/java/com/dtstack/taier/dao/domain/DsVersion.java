package com.dtstack.taier.dao.domain;

import com.baomidou.mybatisplus.annotation.TableName;


/**
 * @author 全阅
 * @Description: 数据源版本类
 * @Date: 2021/3/10
 */
@TableName("datasource_version")
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

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDataVersion() {
        return dataVersion;
    }

    public void setDataVersion(String dataVersion) {
        this.dataVersion = dataVersion;
    }

    public Integer getSorted() {
        return sorted;
    }

    public void setSorted(Integer sorted) {
        this.sorted = sorted;
    }
}
