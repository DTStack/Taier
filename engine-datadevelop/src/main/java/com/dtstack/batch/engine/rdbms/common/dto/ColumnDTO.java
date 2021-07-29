package com.dtstack.batch.engine.rdbms.common.dto;

/**
 * @author jiangbo
 * @date 2018/5/26 10:14
 */
public class ColumnDTO {

    private Long id;

    private String columnName;

    private String columnType;

    private String comment;

    private Integer index;

    private Integer precision;

    private Integer scale;

    private Integer isDeleted = 0;

    private String charLen;

    public String getCharLen() {
        return charLen;
    }

    public void setCharLen(String charLen) {
        this.charLen = charLen;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getPrecision() {
        return precision;
    }

    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    @Override
    public String toString() {
        return "ColumnDTO{" +
                "id=" + id +
                ", columnName='" + columnName + '\'' +
                ", columnType='" + columnType + '\'' +
                ", comment='" + comment + '\'' +
                ", index=" + index +
                ", precision=" + precision +
                ", scale=" + scale +
                ", isDeleted=" + isDeleted +
                ", charLen='" + charLen + '\'' +
                '}';
    }
}
