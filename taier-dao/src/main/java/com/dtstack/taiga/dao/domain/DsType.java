package com.dtstack.taiga.dao.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author 全阅
 * @Description: 数据源类型信息类
 * @Date: 2021/3/10
 */
@Data
@TableName("dsc_type")
public class DsType extends BaseModel {

    /**
     * 数据源类型唯一 如Mysql, Oracle, Hive
     */
    private String dataType;

    /**
     * 数据源分类栏主键id
     */
    private Long dataClassifyId;

    /**
     * 数据源权重
     */
    private Double weight;

    /**
     * 数据源logo图片地址
     */
    private String imgUrl;

    /**
     * 排序值
     */
    private Integer sorted;

    /**
     * 是否隐藏
     */
    private Integer invisible;


}
