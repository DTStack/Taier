package com.dtstack.taier.dao.domain;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @author 全阅
 * @Description: 数据源类型信息类
 * @Date: 2021/3/10
 */
@TableName("datasource_type")
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


    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Long getDataClassifyId() {
        return dataClassifyId;
    }

    public void setDataClassifyId(Long dataClassifyId) {
        this.dataClassifyId = dataClassifyId;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Integer getSorted() {
        return sorted;
    }

    public void setSorted(Integer sorted) {
        this.sorted = sorted;
    }

    public Integer getInvisible() {
        return invisible;
    }

    public void setInvisible(Integer invisible) {
        this.invisible = invisible;
    }
}
