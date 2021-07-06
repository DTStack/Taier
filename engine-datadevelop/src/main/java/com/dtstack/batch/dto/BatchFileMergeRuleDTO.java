package com.dtstack.batch.dto;

import com.dtstack.batch.domain.TenantProjectEntity;
import lombok.Data;

/**
 * <p>小文件合并规则查询对象
 *
 * @author ：wangchuan
 * date：Created in 11:31 上午 2020/12/14
 * company: www.dtstack.com
 */
@Data
public class BatchFileMergeRuleDTO extends TenantProjectEntity {

    /**
     * table info id
     */
    private Long projectId;

    /**
     * 合并规则状态
     */
    private Integer status;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 创建人用户id
     */
    private Long createUserId;

    /**
     * 治理方式 1 周期  2 一次性
     */
    private Integer mergeType;
}
