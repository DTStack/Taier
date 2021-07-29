package com.dtstack.batch.vo;

import com.dtstack.engine.api.vo.ScheduleTaskVO;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * company: www.dtstack.com
 * author: jiangbo
 * create: 2017/7/5
 */
@Data
public class BatchTableInfoVO {

    /**
     * 表id
     */
    private Long id;

    private String tableName;

    private Integer tableType;

    private Long belongProjectId;

    private Long dataSourceId;

    /**
     * 项目mingc
     */
    private String project;

    /**
     * 项目别名
     */
    private String projectAlias;

    /**
     * 所属db or schema 名称
     */
    private String dbName;

    /**
     * 负责人
     */
    private String chargeUser;

    /**
     * 创建时间
     */
    private Timestamp gmtCreate;

    /**
     * 类目
     */
    private String catalogue;

    private Long catalogueId;

    /**
     * 描述
     */
    private String tableDesc;

    /**
     * 授权状态 0-未授权，1-已授权,2-待审批,null-全部
     */
    private Integer permissionStatus;

    /**
     * 生命周期，单位：day
     */
    private Integer lifeDay;

    /**
     * 表大小
     */
    private String tableSize;

    /**
     * 表下文件数量
     */
    private Long tableFileCount;

    /**
     * 是否分区
     */
    private Boolean isPartition = false;

    /**
     * 表结构最近修改时间
     */
    private Timestamp lastDdlTime;

    /**
     * 表数据最后修改时间
     */
    private Timestamp lastDmlTime;

    /**
     * 占用存储和文件数量最后更新时间
     */
    private Timestamp sizeUpdateTime;

    private Timestamp gmtModified;

    private List<ScheduleTaskVO> tasks;

    private String checkResult;

    private Integer listType;

    private Integer isCollect;

    /**
     * 层级
     */
    private String grade;

    /**
     * 主题域
     */
    private String subject;

    /**
     * 刷新频率
     */
    private String refreshRate;

    /**
     * 增量类型
     */
    private String increType;

    private Integer isDeleted;

    private Integer isIgnore;

    private Integer timeTeft;

    private Timestamp passTime;

    private String location;

    private String storedType;

    private String delim;

    /**
     * 表类型:EXTERNAL-外部表，MANAGED-内部表
     */
    private String externalOrManaged;

    public Integer getIsIgnore() {
        return isIgnore;
    }

    public void setIsIgnore(Integer isIgnore) {
        this.isIgnore = isIgnore;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }
    public Integer getIsCollect() {
        return isCollect;
    }

    public void setIsCollect(Integer isCollect) {
        this.isCollect = isCollect;
    }
    public boolean isPartition() {
        return isPartition;
    }

    public void setPartition(boolean partition) {
        this.isPartition = partition;
    }
}
