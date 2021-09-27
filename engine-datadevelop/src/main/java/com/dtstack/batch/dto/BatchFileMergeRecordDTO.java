package com.dtstack.batch.dto;

import com.dtstack.engine.domain.TenantProjectEntity;
import lombok.Data;

import java.util.List;

/**
 * <p>小文件合并表记录查询对象
 *
 * @author ：wangchuan
 * date：Created in 11:32 上午 2020/12/14
 * company: www.dtstack.com
 */
@Data
public class BatchFileMergeRecordDTO extends TenantProjectEntity {

    /**
     * 规则Id
     */
    private Long ruleId;

    /**
     * hiveTableInfo的表id
     */
    private Long tableId;

    /**
     * 规则类型 一次性还是 周期
     */
    private Integer ruleType;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 合并状态
     * {@link com.dtstack.batch.common.enums.EFileMergeStatus}
     */
    private List<Integer> status;

    /**
     * 创建用户id
     */
    private Long createUserId;

    /**
     * 修改用户id
     */
    private Long modifyUserId;
}
