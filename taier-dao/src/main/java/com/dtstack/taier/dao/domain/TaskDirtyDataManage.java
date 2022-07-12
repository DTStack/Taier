package com.dtstack.taier.dao.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author zhichen
 */
@NoArgsConstructor
@SuperBuilder
@TableName("task_dirty_data_manage")
public class TaskDirtyDataManage extends BaseEntity {

    /**
     * 租户 ID
     */
    @TableField(value = "tenant_id")
    private Long tenantId;
    /**
     * 任务id
     */
    @TableField(value = "task_id")
    private Long taskId;

    /**
     * 输出类型1.log2.jdbc
     */
    @TableField(value = "output_type")
    private String outputType;

    /**
     * 日志打印频率
     */
    @TableField(value = "log_print_interval")
    private Integer logPrintInterval;

    /**
     * 连接信息json
     */
    @TableField(value = "link_info")
    private String linkInfo;

    /**
     * 脏数据最大值
     */
    @TableField(value = "max_rows")
    private Integer maxRows;

    /**
     * 失败条数
     */
    @TableField(value = "max_collect_failed_rows")
    private Integer maxCollectFailedRows;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getOutputType() {
        return outputType;
    }

    public void setOutputType(String outputType) {
        this.outputType = outputType;
    }

    public Integer getLogPrintInterval() {
        return logPrintInterval;
    }

    public void setLogPrintInterval(Integer logPrintInterval) {
        this.logPrintInterval = logPrintInterval;
    }

    public String getLinkInfo() {
        return linkInfo;
    }

    public void setLinkInfo(String linkInfo) {
        this.linkInfo = linkInfo;
    }

    public Integer getMaxRows() {
        return maxRows;
    }

    public void setMaxRows(Integer maxRows) {
        this.maxRows = maxRows;
    }

    public Integer getMaxCollectFailedRows() {
        return maxCollectFailedRows;
    }

    public void setMaxCollectFailedRows(Integer maxCollectFailedRows) {
        this.maxCollectFailedRows = maxCollectFailedRows;
    }
}
