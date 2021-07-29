package com.dtstack.batch.domain;

import com.dtstack.batch.common.enums.EFileMergeStatus;

/**
 * <p>小文件合并规则实体类
 *
 * @author ：wangchuan
 * date：Created in 11:31 上午 2020/12/14
 * company: www.dtstack.com
 */
public class BatchFileMergeRule extends TenantProjectEntity {

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 周期配置  用于周期性治理 一次性为null
     */
    private String scheduleConf;

    /**
     * 治理类型 1 周期  2 一次性
     */
    private Integer mergeType;

    /**
     * 规则状态  是否启动 0启动 1停止
     * {@link EFileMergeStatus}
     */
    private Integer status;

    /**
     * 创建人用户id
     */
    private Long createUserId;

    /**
     * 修改人用户id
     */
    private Long modifyUserId;

    /**
     * 文件数量  治理的下限
     */
    private Long fileCount;

    /**
     * 容量大小 治理的下限 单位kb
     */
    private Long storage;

    public String getScheduleConf() {
        return scheduleConf;
    }

    public void setScheduleConf(String scheduleConf) {
        this.scheduleConf = scheduleConf;
    }

    public Integer getMergeType() {
        return mergeType;
    }

    public void setMergeType(Integer mergeType) {
        this.mergeType = mergeType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Long getModifyUserId() {
        return modifyUserId;
    }

    public void setModifyUserId(Long modifyUserId) {
        this.modifyUserId = modifyUserId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public Long getFileCount() {
        return fileCount;
    }

    public void setFileCount(Long fileCount) {
        this.fileCount = fileCount;
    }

    public Long getStorage() {
        return storage;
    }

    public void setStorage(Long storage) {
        this.storage = storage;
    }
}
