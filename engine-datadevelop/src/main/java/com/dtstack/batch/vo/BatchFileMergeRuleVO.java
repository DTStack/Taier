package com.dtstack.batch.vo;

import com.dtstack.batch.common.enums.EFileMergeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * <p>小文件合并规则前端展示VO</>
 *
 * @author ：wangchuan
 * date：Created in 11:41 上午 2020/12/15
 * company: www.dtstack.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchFileMergeRuleVO {

    /**
     * 治理规则id
     */
    private Long ruleId;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 项目id
     */
    private Long projectId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 创建人名称
     */
    private String createUser;

    /**
     * 修改人名称
     */
    private String modifyUser;

    /**
     * 周期配置  用于周期性治理 一次性为null
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
     * 规则状态  是否 0 启动  1停止
     * {@link EFileMergeStatus}
     */
    private Integer ruleStatus;
}
