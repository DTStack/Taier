package com.dtstack.batch.dto;

import com.dtstack.engine.domain.TenantProjectEntity;
import lombok.Data;

import java.util.List;

/**
 * <p>小文件合并分区记录查询对象
 *
 * @author ：wangchuan
 * date：Created in 11:31 上午 2020/12/14
 * company: www.dtstack.com
 */
@Data
public class BatchFileMergePartitionDTO extends TenantProjectEntity {

    /**
     * 合并记录id
     */
    private Long recordId;

    /**
     * 是否是分区表，1为true，0为false
     * {@link com.dtstack.batch.common.enums.EFileMergeStatus}
     */
    private List<Integer> status;

    /**
     * 分区名称
     */
    private String partitionName;
}
