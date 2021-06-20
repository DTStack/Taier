package com.dtstack.pubsvc.dao.po.datasource;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dtstack.pubsvc.dao.po.BaseModel;
import lombok.Data;

/**
 * @author 全阅
 * @Description: 数据源类型信息类
 * @Date: 2021/3/10
 */
@Data
@TableName("dsc_type")
public class DsType extends BaseModel<DsType> {

    /**
     * 数据源类型唯一 如Mysql, Oracle, Hive
     */
    @TableField("data_type")
    private String dataType;

    /**
     * 数据源分类栏主键id
     */
    @TableField("data_classify_id")
    private Long dataClassifyId;

    /**
     * 数据源权重
     */
    @TableField("weight")
    private Double weight;

    /**
     * 数据源logo图片地址
     */
    @TableField("img_url")
    private String imgUrl;

    /**
     * 排序值
     */
    @TableField("sorted")
    private Integer sorted;

    /**
     * 是否隐藏
     */
    @TableField("invisible")
    private Integer invisible;


}
