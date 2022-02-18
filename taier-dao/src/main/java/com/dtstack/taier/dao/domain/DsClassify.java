package com.dtstack.taier.dao.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @author 全阅
 * @Description: 数据源分类实体
 * @Date: 2021/3/10
 */
@TableName("datasource_classify")
public class DsClassify extends BaseModel {

    /**
     * 类型栏唯一编码
     */
    @TableField("classify_code")
    private String classifyCode;

    /**
     * 类型栏排序字段 默认从0开始
     */
    @TableField("sorted")
    private Integer sorted;

    /**
     * 类型名称 包含全部和常用栏
     */
    @TableField("classify_name")
    private String classifyName;

    public String getClassifyCode() {
        return classifyCode;
    }

    public void setClassifyCode(String classifyCode) {
        this.classifyCode = classifyCode;
    }

    public Integer getSorted() {
        return sorted;
    }

    public void setSorted(Integer sorted) {
        this.sorted = sorted;
    }

    public String getClassifyName() {
        return classifyName;
    }

    public void setClassifyName(String classifyName) {
        this.classifyName = classifyName;
    }
}
