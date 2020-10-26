package com.dtstack.engine.sql;

/**
 * 字段修改描述类
 *
 * @author jiangbo
 * @date 2018/5/22 14:04
 */
public class AlterColumnResult {

    /**
     * 修改之前的名称
     */
    private String oldColumn;

    /**
     * 修改之后的名称
     */
    private String newColumn;

    /**
     * 修改之后的类型
     */
    private String newType;

    /**
     * 修改之后的描述信息
     */
    private String newComment;

    /**
     * 修改后在哪个字段的后面
     */
    private String afterColumn;

    /**
     * 是不是排在第一个
     */
    private boolean isFirst;

    public String getOldColumn() {
        return oldColumn;
    }

    public void setOldColumn(String oldColumn) {
        this.oldColumn = oldColumn;
    }

    public String getNewColumn() {
        return newColumn;
    }

    public void setNewColumn(String newColumn) {
        this.newColumn = newColumn;
    }

    public String getNewType() {
        return newType;
    }

    public void setNewType(String newType) {
        this.newType = newType;
    }

    public String getNewComment() {
        return newComment;
    }

    public void setNewComment(String newComment) {
        this.newComment = newComment;
    }

    public String getAfterColumn() {
        return afterColumn;
    }

    public void setAfterColumn(String afterColumn) {
        this.afterColumn = afterColumn;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    @Override
    public String toString() {
        return "AlterColumnResult{" +
                "oldColumn='" + oldColumn + '\'' +
                ", newColumn='" + newColumn + '\'' +
                ", newType='" + newType + '\'' +
                ", newComment='" + newComment + '\'' +
                ", afterColumn='" + afterColumn + '\'' +
                ", isFirst=" + isFirst +
                '}';
    }
}
