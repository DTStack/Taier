package com.dtstack.batch.vo;

import com.dtstack.batch.domain.BatchFileMergeRule;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>小文件合并规则传输对象
 *
 * @author ：wangchuan
 * date：Created in 3:46 下午 2020/12/15
 * company: www.dtstack.com
 */
@Data
public class FileMergeRuleVO {

    /**
     * 治理规则id ：ruleId > 0 表示更新操作
     */
    private Long ruleId = 0L;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 治理的表的信息 一次性治理才会用到这个字段
     */
    private List<FileMergeTableVO> tableInfo;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 项目id
     */
    @JsonProperty("pId")
    private Long pId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     *周期配置  用于周期性治理 一次性为null
     */
    private String scheduleConf;

    /**
     * 治理类型 1 周期  2 一次性
     */
    private Integer mergeType;

    /**
     * 文件数量  治理的下限
     */
    private Long fileCount;

    /**
     * 容量大小 治理的下限
     */
    private BigDecimal storage;


    /**
     * dto对象转换为entity对象
     * @return 转换后对象
     */
    public BatchFileMergeRule toEntity() {
        BatchFileMergeRule mergeRule = new BatchFileMergeRule();
        mergeRule.setId(this.getRuleId() == null? 0L : this.getRuleId());
        mergeRule.setScheduleConf(this.getScheduleConf());
        mergeRule.setTenantId(this.getTenantId());
        mergeRule.setMergeType(this.getMergeType());
        mergeRule.setFileCount(this.getFileCount());
        mergeRule.setStorage(this.getStorage()==null ? 1024L : this.getStorage().multiply(new BigDecimal("1024")).multiply(new BigDecimal("1024")).longValue());
        mergeRule.setProjectId(this.getPId());
        mergeRule.setRuleName(this.getRuleName());
        mergeRule.setProjectId(this.getPId());
        //默认是开启的
        mergeRule.setStatus(0);
        return mergeRule;
    }

}
