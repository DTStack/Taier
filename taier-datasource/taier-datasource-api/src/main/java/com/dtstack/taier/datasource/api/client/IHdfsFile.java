/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.datasource.api.client;

import com.dtstack.taier.datasource.api.base.Client;
import com.dtstack.taier.datasource.api.downloader.IDownloader;
import com.dtstack.taier.datasource.api.dto.ColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.FileStatus;
import com.dtstack.taier.datasource.api.dto.HDFSContentSummary;
import com.dtstack.taier.datasource.api.dto.HdfsQueryDTO;
import com.dtstack.taier.datasource.api.dto.HdfsWriterDTO;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.enums.FileFormat;

import java.util.List;
import java.util.Map;

/**
 * HDFS 客户端接口
 *
 * @author ：wangchuan
 * date：Created in 下午3:38 2021/12/17
 * company: www.dtstack.com
 */
public interface IHdfsFile extends Client {

    /**
     * 获取 hdfs 对应地址信息
     *
     * @param source     数据源信息
     * @param remotePath hdfs 路径
     * @return 路径对应信息
     */
    FileStatus getStatus(ISourceDTO source, String remotePath);

    /**
     * 获取 yarn 日志下载器
     *
     * @param source   数据源信息
     * @param queryDTO 构造信息
     * @return yarn 日志下载器
     */
    IDownloader getLogDownloader(ISourceDTO source, SqlQueryDTO queryDTO);

    /**
     * 获取 taskManager 列表
     *
     * @param source     数据源信息
     * @param appId      yarn application id
     * @return yarn 日志下载器
     */
    List<String> getTaskManagerList(ISourceDTO source, String appId);

    /**
     * 获取 hdfs 文件下载器
     *
     * @param source 数据源信息
     * @param path   hdfs 文件路径
     * @return 文件下载器
     */
    IDownloader getFileDownloader(ISourceDTO source, String path);

    /**
     * 从 hdfs 上下载文件到本地
     *
     * @param source     数据源信息
     * @param remotePath hdfs 远程路径
     * @param localDir   本地文件夹
     * @return 是否成功
     */
    boolean downloadFileFromHdfs(ISourceDTO source, String remotePath, String localDir);

    /**
     * 上传本地文件到 hdfs
     *
     * @param source        数据源信息
     * @param localFilePath 上传的文件本地路径
     * @param remotePath    hdfs 远程路径
     * @return 是否上传成功
     */
    boolean uploadLocalFileToHdfs(ISourceDTO source, String localFilePath, String remotePath);

    /**
     * 上传字节数组到 hdfs
     *
     * @param source     数据源信息
     * @param bytes      字节数组
     * @param remotePath 目标路径
     * @return 是否上传成功
     */
    boolean uploadInputStreamToHdfs(ISourceDTO source, byte[] bytes, String remotePath);

    /**
     * 上传字符串作为文件到 hdfs, 返回文件 hdfs 路径
     *
     * @param source     数据源信息
     * @param str        字符串信息
     * @param remotePath 目标路径
     * @return hdfs 文件路径
     */
    String uploadStringToHdfs(ISourceDTO source, String str, String remotePath);

    /**
     * 创建 HDFS 路径
     *
     * @param source     数据源信息
     * @param remotePath hdfs 目标路径
     * @param permission 路径权限
     * @return 是否创建成功
     */
    boolean createDir(ISourceDTO source, String remotePath, Short permission);

    /**
     * 路径文件是否存在
     *
     * @param source     数据源信息
     * @param remotePath hdfs 目标路径
     * @return 是否存在
     */
    boolean isFileExist(ISourceDTO source, String remotePath);

    /**
     * 检测文件是否存在并删除
     *
     * @param source     数据源信息
     * @param remotePath hdfs 目标路径
     * @return 是否删除成功
     */
    boolean checkAndDelete(ISourceDTO source, String remotePath);

    /**
     * 直接删除 hdfs 目标路径文件
     *
     * @param source     数据源信息
     * @param remotePath 目标路径
     * @param recursive  是否递归删除
     * @return 删除结果
     */
    boolean delete(ISourceDTO source, String remotePath, boolean recursive);

    /**
     * 获取路径文件大小
     *
     * @param source     数据源信息
     * @param remotePath hdfs 目标路径
     * @return 文件大小
     */
    long getDirSize(ISourceDTO source, String remotePath);

    /**
     * 删除 hdfs 文件
     *
     * @param source    数据源信息
     * @param fileNames hdfs 文件路径集合
     * @return 是否删除成功
     */
    boolean deleteFiles(ISourceDTO source, List<String> fileNames);

    /**
     * hdfs 路径目录是否存在
     *
     * @param source     数据源信息
     * @param remotePath 目标文件夹路径
     * @return 是否存在
     */
    boolean isDirExist(ISourceDTO source, String remotePath);

    /**
     * 设置 hdfs 路径权限
     *
     * @param source     数据源信息
     * @param remotePath hdfs 目标文件
     * @param mode       权限
     * @return 是否执行成功
     */
    boolean setPermission(ISourceDTO source, String remotePath, String mode);

    /**
     * hdfs 内文件重命名
     *
     * @param source 数据源信息
     * @param src    hdfs 原路径
     * @param dist   hdfs 目标路径
     * @return 是否执行成功
     */
    boolean rename(ISourceDTO source, String src, String dist);

    /**
     * hdfs 内复制文件
     *
     * @param source      数据源信息
     * @param src         hdfs 原文件路径
     * @param dist        hdfs 目标文件路径
     * @param isOverwrite 是否覆盖
     * @return 是否执行成功
     */
    boolean copyFile(ISourceDTO source, String src, String dist, boolean isOverwrite);

    /**
     * hdfs 内复制文件夹
     *
     * @param source 数据源信息
     * @param src    hdfs 原文件夹路径
     * @param dist   hdfs 目标文件夹路径
     */
    boolean copyDirector(ISourceDTO source, String src, String dist);

    /**
     * 合并小文件
     *
     * @param source                   数据源信息
     * @param src                      合并 hdfs 路径
     * @param mergePath                目标 hdfs 路径
     * @param fileFormat               文件类型 ： text、orc、parquet {@link FileFormat}
     * @param maxCombinedFileSize      合并后的文件大小
     * @param needCombineFileSizeLimit 小文件的最大值，超过此阈值的小文件不会合并
     * @return 是否合并成功
     */
    boolean fileMerge(ISourceDTO source, String src, String mergePath, FileFormat fileFormat, Long maxCombinedFileSize, Long needCombineFileSizeLimit);

    /**
     * 获取目录或者文件的属性, 非递归
     *
     * @param source     数据源信息
     * @param remotePath hdfs 上路径
     * @return 文件夹下文件属性
     */
    List<FileStatus> listStatus(ISourceDTO source, String remotePath);

    /**
     * 获取目录下所有文件路径, 递归获取
     *
     * @param source     数据源信息
     * @param remotePath hdfs 上文件路径
     * @return 当前目录下所有的文件路径
     */
    List<String> listAllFilePath(ISourceDTO source, String remotePath);

    /**
     * 获取目录下所有文件的属性集
     *
     * @param source     数据源信息
     * @param remotePath hdfs 上文件路径
     * @param isIterate  是否递归
     * @return 文件信息
     */
    List<FileStatus> listAllFiles(ISourceDTO source, String remotePath, boolean isIterate);

    /**
     * 从hdfs copy文件到本地
     *
     * @param source  数据源信息
     * @param srcPath hdfs 上文件路径
     * @param dstPath 下载到本地的文件夹路径
     * @return 是否执行成功
     */
    boolean copyToLocal(ISourceDTO source, String srcPath, String dstPath);

    /**
     * 从本地 copy 到 hdfs, 执行成功后会删除本地文件
     *
     * @param source    数据源信息
     * @param srcPath   本地文件路径
     * @param dstPath   hdfs 文件路径
     * @param overwrite 是否覆盖文件呢
     * @return 是否执行成功
     */
    boolean copyFromLocal(ISourceDTO source, String srcPath, String dstPath, boolean overwrite);

    /**
     * 根据文件格式获取对应的下载器
     *
     * @param source         数据源信息
     * @param tableLocation  表路径信息
     * @param fieldDelimiter 字段分隔符, 只有 textFile 格式表需要
     * @param fileFormat     存储格式
     * @return 表数据下载器
     */
    IDownloader getDownloaderByFormat(ISourceDTO source, String tableLocation, List<String> columnNames, String fieldDelimiter, String fileFormat);

    /**
     * 根据文件格式获取对应的downlaoder
     *
     * @param source          数据源信息
     * @param tableLocation   表路径
     * @param allColumns      所有字段信息
     * @param filterColumns   需要查询的字段信息
     * @param filterPartition 过滤分区信息 key：分区字段, value: 分区值
     * @param partitions      分区表所有分区(如果是分区表需要传过来,不然有可能会查询到非关联分区),也可以用来过滤分区
     * @param fieldDelimiter  字段分隔符
     * @param fileFormat      存储类型
     * @return 数据下载器
     */
    IDownloader getDownloaderByFormatWithType(ISourceDTO source, String tableLocation,
                                              List<ColumnMetaDTO> allColumns, List<String> filterColumns, Map<String, String> filterPartition,
                                              List<String> partitions, String fieldDelimiter, String fileFormat);

    /**
     * 根据文件格式获取对应的downlaoder
     *
     * @param source          数据源信息
     * @param tableLocation   表路径
     * @param allColumns      所有字段信息
     * @param filterColumns   需要查询的字段信息
     * @param filterPartition 过滤分区信息 key：分区字段, value: 分区值
     * @param partitions      分区表所有分区(如果是分区表需要传过来,不然有可能会查询到非关联分区),也可以用来过滤分区
     * @param fieldDelimiter  字段分隔符
     * @param fileFormat      存储类型
     * @param isTransTable    是否是事务表
     * @return 数据下载器
     */
    IDownloader getDownloaderByFormatWithType(ISourceDTO source, String tableLocation,
                                              List<ColumnMetaDTO> allColumns, List<String> filterColumns, Map<String, String> filterPartition,
                                              List<String> partitions, String fieldDelimiter, String fileFormat, Boolean isTransTable);

    /**
     * 获取hdfs上存储文件的字段信息
     *
     * @param source     数据源信息
     * @param queryDTO   查询条件
     * @param fileFormat 存储格式
     * @return 字段信息集合
     */
    List<ColumnMetaDTO> getColumnList(ISourceDTO source, SqlQueryDTO queryDTO, String fileFormat);

    /**
     * 按位置写入
     *
     * @param source        数据源信息
     * @param hdfsWriterDTO 写入配置信息
     * @return 写入数据条数
     */
    int writeByPos(ISourceDTO source, HdfsWriterDTO hdfsWriterDTO);

    /**
     * 按名称匹配写入
     *
     * @param source        数据源信息
     * @param hdfsWriterDTO 写入配置信息
     * @return 写入数据条数
     */
    int writeByName(ISourceDTO source, HdfsWriterDTO hdfsWriterDTO);

    /**
     * 批量统计文件夹内容摘要，包括文件的数量，文件夹的数量，文件变动时间，以及这个文件夹的占用存储等内容
     *
     * @param source       数据源信息
     * @param hdfsDirPaths hdfs上文件路径集合
     * @return 文件摘要信息
     */
    List<HDFSContentSummary> getContentSummary(ISourceDTO source, List<String> hdfsDirPaths);

    /**
     * 统计文件夹内容摘要，包括文件的数量，文件夹的数量，文件变动时间，以及这个文件夹的占用存储等内容
     *
     * @param source      数据源信息
     * @param hdfsDirPath hdfs上文件路径
     * @return 文件摘要信息
     */
    HDFSContentSummary getContentSummary(ISourceDTO source, String hdfsDirPath);

    /**
     * 读取hdfs数据，执行脚本任务时（shell,python等）
     *
     * @param hdfsPath hdfs文件路径，绝对路径
     * @return
     */
    String getHdfsWithScript(ISourceDTO source, String hdfsPath);

    /**
     * 读取hdfs数据，执行job任务时（hive,spark等）
     *
     * @param hdfsQueryDTO 查询对象
     * @return 文件内数据, key:hdfsPath , value:数据
     */
    List<String> getHdfsWithJob(ISourceDTO source, HdfsQueryDTO hdfsQueryDTO);
}
