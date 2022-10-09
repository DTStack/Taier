package com.dtstack.taier.datasource.api.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 11:11 2020/8/10
 * @Description：HDFS 文件类型
 */
@Data
@Builder
public class FileStatus implements Serializable {
    /**
     * 路径
     */
    private String path;

    /**
     * 文件大小 单位bytes
     */
    private long length = 0L;

    /**
     * 是否是文件夹
     */
    private boolean isdir = false;

    /**
     * 副本数
     */
    private short block_replication = 0;

    /**
     * 文件块大小
     */
    private long blocksize = 0L;

    /**
     * 文件更新时间 单位毫秒
     */
    private long modification_time = 0;

    /**
     * 文件的访问时间 单位毫秒
     */
    private long access_time = 0L;

    /**
     * 所有者
     */
    private String owner;

    /**
     * 组
     */
    private String group;

}
