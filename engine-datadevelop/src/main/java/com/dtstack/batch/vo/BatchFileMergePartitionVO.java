package com.dtstack.batch.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>小文件合并分区历史前端展示VO</>
 *
 * @author ：wangchuan
 * date：Created in 11:43 上午 2020/12/15
 * company: www.dtstack.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchFileMergePartitionVO {

    private Long id;

    /**
     * 分区名
     */
    private String partitionName;

    /**
     * 占用存储
     */
    private String storage;

    /**
     * 合并状态
     */
    private Integer status;

    /**
     * 治理前文件数
     */
    private Long countBefore;

    /**
     * 治理后文件数
     */
    private Long countAfter;

    /**
     * 合并失败原因
     */
    private String errorMsg;

    private Long projectId;

}
