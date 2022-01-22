package com.dtstack.taiga.dao.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author 全阅
 * @Description: 数据源分类实体
 * @Date: 2021/3/10
 */
@Data
@TableName("dsc_classify")
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


}
