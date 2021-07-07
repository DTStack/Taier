package com.dtstack.batch.vo;

import lombok.Data;

/**
 * @author chener
 * @Classname SyncErrorCountInfo
 * @Description 数据同步脏数据countInfo信息
 * @Date 2020/10/10 19:12
 * @Created chener@dtstack.com
 */
@Data
public class SyncErrorCountInfoVO {
    private Long nullErrors;
    private Long duplicateErrors;
    private Long conversionErrors;
    private Long otherErrors;
    private Long numError;
}
