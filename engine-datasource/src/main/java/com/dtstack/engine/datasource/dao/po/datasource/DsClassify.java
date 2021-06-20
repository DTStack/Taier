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
@TableName("dsc_classify")
public class DsClassify extends BaseModel<DsClassify> {

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
