package com.dtstack.taier.datasource.plugin.common.downloader;

import com.dtstack.taier.datasource.api.downloader.IDownloader;

import java.util.List;

/**
 * @author ：wangchuan
 * date：Created in 下午1:23 2021/12/27
 * company: www.dtstack.com
 */
public interface IYarnDownloader extends IDownloader {

    /**
     * 获取 yarn tm 集合
     *
     * @return tm 集合
     */
    List<String> getTaskManagerList();
}
