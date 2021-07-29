package com.dtstack.batch.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * company: www.dtstack.com
 * author: jiangbo
 * create: 2017/7/5
 */
@Data
public class BatchTableSearchVO {

    /**
     * 基本查询条件
     */
    private Long tenantId;
    private Long projectId;
    @JsonProperty("pId")
    private Long pId;
    private Long userId;
    private String tableName;
    private Integer tableType;
    private String columnName;
    private Long catalogueId;
    private String sortColumn = "gmt_modified";
    private String sort = "desc";

    /**
     * 责任人id
     */
    private Long chargeUserId;

    /**
     * 0-全部,1-最近操作的，2-个人账户的，3-我管理的表，4-被授权的表，5-我收藏的表
     */
    private Integer listType = 0;

    /**
     * 授权状态 0-未授权，1-已授权,2-待审批,null-全部
     */
    private Integer permissionStatus;

    /**
     * 分页条件
     */
    private Integer pageSize = 10;
    private Integer pageIndex = 1;

    /**
     * 脏数据
     */
    private Long taskId;
    private Integer isDirtyDataTable = 0;

    /**
     * 模型查询条件
     */
    private String grade;
    private String subject;
    private String refreshRate;
    private String increType;
    private Integer ignore;
    private Integer type;
    private List<Integer> triggerType;

    /**
     * 指定project查询
     */
    private Long appointProjectId;

    /**
     * 时间段过滤
     */
    private Timestamp mdfBeginTime;

    private Timestamp mdfEndTime;

    private Long tableModifyUserId;

    private Timestamp startTime;

    private Timestamp endTime;

    private Boolean showDeleted = false;

    /**
     * 0为升序 1为倒序
     */
    private Integer lifeDayOrder;

    // 按文件数量排序，逻辑同上：0为升序 1为倒序
    private Integer fileCountOrder;

    // 按文件占用存储排序，逻辑同上：0为升序 1为倒序
    private Integer tableSizeOrder;

    public Integer getIsDirtyDataTable() {
        return isDirtyDataTable;
    }

    public void setIsDirtyDataTable(Integer isDirtyDataTable) {
        this.isDirtyDataTable = isDirtyDataTable;
    }
}
