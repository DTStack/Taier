package com.dtstack.taier.datasource.api.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * <p>HDFS文件内容摘要</>
 * 包括包括文件的数量，文件夹的数量，以及这个文件夹的大小等内容
 *
 * @author ：wangchuan
 * date：Created in 11:39 上午 2020/12/11
 * company: www.dtstack.com
 */
@Slf4j
@Data
@Builder
public class HDFSContentSummary implements Serializable {

    // 文件数量
    private Long fileCount;

    // 文件夹数量
    private Long directoryCount;

    // 占用存储
    private Long spaceConsumed;

    // 文件(夹)更新时间
    private Long ModifyTime;

    // 路径是否存在
    private Boolean isExists;
}
