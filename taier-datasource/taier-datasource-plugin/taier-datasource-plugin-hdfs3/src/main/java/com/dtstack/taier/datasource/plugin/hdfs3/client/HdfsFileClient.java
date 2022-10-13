package com.dtstack.taier.datasource.plugin.hdfs3.client;

import com.dtstack.taier.datasource.api.client.IHdfsFile;
import com.dtstack.taier.datasource.api.downloader.IDownloader;
import com.dtstack.taier.datasource.api.dto.ColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.FileStatus;
import com.dtstack.taier.datasource.api.dto.HDFSContentSummary;
import com.dtstack.taier.datasource.api.dto.HdfsQueryDTO;
import com.dtstack.taier.datasource.api.dto.HdfsWriterDTO;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.Hdfs3SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.HdfsSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.enums.FileFormat;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.plugin.common.downloader.IYarnDownloader;
import com.dtstack.taier.datasource.plugin.common.utils.ReflectUtil;
import com.dtstack.taier.datasource.plugin.hdfs3.YarnConfUtil;
import com.dtstack.taier.datasource.plugin.hdfs3.downloader.HdfsCsvDownload;
import com.dtstack.taier.datasource.plugin.hdfs3.downloader.HdfsFileDownload;
import com.dtstack.taier.datasource.plugin.hdfs3.downloader.HdfsORCDownload;
import com.dtstack.taier.datasource.plugin.hdfs3.downloader.HdfsParquetDownload;
import com.dtstack.taier.datasource.plugin.hdfs3.downloader.HdfsTextDownload;
import com.dtstack.taier.datasource.plugin.hdfs3.downloader.YarnLogDownload.YarnTFileDownload;
import com.dtstack.taier.datasource.plugin.hdfs3.fileMerge.core.CombineMergeBuilder;
import com.dtstack.taier.datasource.plugin.hdfs3.fileMerge.core.CombineServer;
import com.dtstack.taier.datasource.plugin.hdfs3.hdfswriter.HdfsOrcWriter;
import com.dtstack.taier.datasource.plugin.hdfs3.hdfswriter.HdfsParquetWriter;
import com.dtstack.taier.datasource.plugin.hdfs3.hdfswriter.HdfsTextWriter;
import com.dtstack.taier.datasource.plugin.hdfs3.reader.ReaderFactory;
import com.dtstack.taier.datasource.plugin.hdfs3.util.StringUtil;
import com.dtstack.taier.datasource.plugin.kerberos.core.hdfs.HadoopConfUtil;
import com.dtstack.taier.datasource.plugin.kerberos.core.hdfs.HdfsOperator;
import com.dtstack.taier.datasource.plugin.kerberos.core.util.KerberosLoginUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.ContentSummary;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.hive.ql.io.orc.OrcFile;
import org.apache.hadoop.yarn.logaggergation.filecontroller.iflie.LogAggregationIndexedFileController;

import java.io.IOException;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 14:50 2020/8/10
 * @Description：HDFS 文件操作实现类
 */
@Slf4j
public class HdfsFileClient implements IHdfsFile {

    private static final String PATH_DELIMITER = "/";

    // yarn聚合日志格式，默认TFIle
    private static final String LOG_FORMAT = "yarn.log-aggregation.file-formats";

    // null 名称的字段名
    private static final String NULL_COLUMN = "null";

    @Override
    public FileStatus getStatus(ISourceDTO iSource, String location) {
        org.apache.hadoop.fs.FileStatus hadoopFileStatus = getHadoopStatus(iSource, location);

        return FileStatus.builder()
                .length(hadoopFileStatus.getLen())
                .access_time(hadoopFileStatus.getAccessTime())
                .block_replication(hadoopFileStatus.getReplication())
                .blocksize(hadoopFileStatus.getBlockSize())
                .group(hadoopFileStatus.getGroup())
                .isdir(hadoopFileStatus.isDirectory())
                .modification_time(hadoopFileStatus.getModificationTime())
                .owner(hadoopFileStatus.getOwner())
                .path(hadoopFileStatus.getPath().toString())
                .build();
    }

    @Override
    public IDownloader getLogDownloader(ISourceDTO iSource, SqlQueryDTO queryDTO) {
        Hdfs3SourceDTO hdfsSourceDTO = (Hdfs3SourceDTO) iSource;
        return KerberosLoginUtil.loginWithUGI(hdfsSourceDTO.getKerberosConfig()).doAs(
                (PrivilegedAction<IDownloader>) () -> {
                    try {
                        return createYarnLogDownload(hdfsSourceDTO);
                    } catch (Exception e) {
                        throw new SourceException(String.format("create downloader exception,%s", e.getMessage()), e);
                    }
                }
        );
    }

    @Override
    public List<String> getTaskManagerList(ISourceDTO source, String appId) {
        Hdfs3SourceDTO hdfsSourceDTO = (Hdfs3SourceDTO) source;
        hdfsSourceDTO.setAppIdStr(appId);
        return KerberosLoginUtil.loginWithUGI(hdfsSourceDTO.getKerberosConfig()).doAs(
                (PrivilegedAction<List<String>>) () -> {
                    IYarnDownloader yarnLogDownload = null;
                    try {
                        yarnLogDownload = createYarnLogDownload(hdfsSourceDTO);
                        return yarnLogDownload.getTaskManagerList();
                    } catch (Exception e) {
                        throw new SourceException(String.format("create downloader exception,%s", e.getMessage()), e);
                    } finally {
                        if (Objects.nonNull(yarnLogDownload)) {
                            try {
                                yarnLogDownload.close();
                            } catch (Exception e) {
                                log.error("close download error: ", e);
                            }
                        }
                    }
                }
        );
    }

    /**
     * 创建yarn 聚合日志下载器，区分ifile、tfile格式
     *
     * @param hdfsSourceDTO 数据源信息
     * @return yarn日志下载器
     * @throws Exception 异常信息
     */
    private IYarnDownloader createYarnLogDownload(Hdfs3SourceDTO hdfsSourceDTO) throws Exception {
        IYarnDownloader yarnDownload;
        Configuration configuration = YarnConfUtil.getFullConfiguration(null, hdfsSourceDTO.getConfig(), hdfsSourceDTO.getYarnConf(), hdfsSourceDTO.getKerberosConfig());
        String fileFormat = configuration.get(LOG_FORMAT);
        boolean containerFiledExists = Arrays.stream(Hdfs3SourceDTO.class.getDeclaredFields())
                .anyMatch(field -> "ContainerId".equalsIgnoreCase(field.getName()));
        if (StringUtils.isNotBlank(fileFormat) && StringUtils.containsIgnoreCase(fileFormat, "IFile")) {
            yarnDownload = new LogAggregationIndexedFileController(hdfsSourceDTO.getDefaultFS(), hdfsSourceDTO.getUser(), hdfsSourceDTO.getConfig(), hdfsSourceDTO.getYarnConf(), hdfsSourceDTO.getAppIdStr(), hdfsSourceDTO.getReadLimit(), hdfsSourceDTO.getLogType(), hdfsSourceDTO.getKerberosConfig(), hdfsSourceDTO.getContainerId());
        } else {
            yarnDownload = new YarnTFileDownload(hdfsSourceDTO.getDefaultFS(), hdfsSourceDTO.getUser(), hdfsSourceDTO.getConfig(), hdfsSourceDTO.getYarnConf(), hdfsSourceDTO.getAppIdStr(), hdfsSourceDTO.getReadLimit(), hdfsSourceDTO.getLogType(), hdfsSourceDTO.getKerberosConfig(), hdfsSourceDTO.getContainerId());
        }
        yarnDownload.configure();
        return yarnDownload;
    }

    @Override
    public IDownloader getFileDownloader(ISourceDTO iSource, String path) {
        Hdfs3SourceDTO hdfsSourceDTO = (Hdfs3SourceDTO) iSource;
        return KerberosLoginUtil.loginWithUGI(hdfsSourceDTO.getKerberosConfig()).doAs(
                (PrivilegedAction<IDownloader>) () -> {
                    try {
                        HdfsFileDownload hdfsFileDownload = new HdfsFileDownload(hdfsSourceDTO.getDefaultFS(), hdfsSourceDTO.getConfig(), path, hdfsSourceDTO.getKerberosConfig());
                        hdfsFileDownload.configure();
                        return hdfsFileDownload;
                    } catch (Exception e) {
                        throw new SourceException(String.format("Create file downloader exception,%s", e.getMessage()), e);
                    }
                }
        );
    }

    /**
     * 获取 HADOOP 文件信息
     *
     * @param source
     * @param location
     * @return
     * @throws Exception
     */
    private org.apache.hadoop.fs.FileStatus getHadoopStatus(ISourceDTO source, String location) {
        Hdfs3SourceDTO hdfsSourceDTO = (Hdfs3SourceDTO) source;

        FileSystem fs = HdfsOperator.getFileSystem(hdfsSourceDTO.getKerberosConfig(), hdfsSourceDTO.getConfig(), hdfsSourceDTO.getDefaultFS());
        return HdfsOperator.getFileStatus(fs, location);
    }

    @Override
    public boolean downloadFileFromHdfs(ISourceDTO source, String remotePath, String localDir) {
        Hdfs3SourceDTO hdfsSourceDTO = (Hdfs3SourceDTO) source;
        FileSystem fs = HdfsOperator.getFileSystem(hdfsSourceDTO.getKerberosConfig(), hdfsSourceDTO.getConfig(), hdfsSourceDTO.getDefaultFS());
        HdfsOperator.copyToLocal(fs, remotePath, localDir);
        return true;
    }

    @Override
    public boolean uploadLocalFileToHdfs(ISourceDTO source, String localFilePath, String remotePath) {
        Hdfs3SourceDTO hdfsSourceDTO = (Hdfs3SourceDTO) source;
        FileSystem fs = HdfsOperator.getFileSystem(hdfsSourceDTO.getKerberosConfig(), hdfsSourceDTO.getConfig(), hdfsSourceDTO.getDefaultFS());
        HdfsOperator.uploadLocalFileToHdfs(fs, localFilePath, remotePath);
        return true;
    }

    @Override
    public boolean uploadInputStreamToHdfs(ISourceDTO source, byte[] bytes, String remotePath) {
        Hdfs3SourceDTO hdfsSourceDTO = (Hdfs3SourceDTO) source;
        FileSystem fs = HdfsOperator.getFileSystem(hdfsSourceDTO.getKerberosConfig(), hdfsSourceDTO.getConfig(), hdfsSourceDTO.getDefaultFS());
        return HdfsOperator.uploadInputStreamToHdfs(fs, bytes, remotePath);
    }

    @Override
    public String uploadStringToHdfs(ISourceDTO source, String str, String remotePath) {
        Hdfs3SourceDTO hdfsSourceDTO = (Hdfs3SourceDTO) source;
        FileSystem fs = HdfsOperator.getFileSystem(hdfsSourceDTO.getKerberosConfig(), hdfsSourceDTO.getConfig(), hdfsSourceDTO.getDefaultFS());
        HdfsOperator.uploadInputStreamToHdfs(fs, str.getBytes(), remotePath);
        return fs.getConf().get("fs.defaultFS") + remotePath;
    }

    @Override
    public boolean createDir(ISourceDTO source, String remotePath, Short permission) {
        Hdfs3SourceDTO hdfsSourceDTO = (Hdfs3SourceDTO) source;
        FileSystem fs = HdfsOperator.getFileSystem(hdfsSourceDTO.getKerberosConfig(), hdfsSourceDTO.getConfig(), hdfsSourceDTO.getDefaultFS());
        return HdfsOperator.createDir(fs, remotePath, permission);
    }

    @Override
    public boolean isFileExist(ISourceDTO source, String remotePath) {
        Hdfs3SourceDTO hdfsSourceDTO = (Hdfs3SourceDTO) source;
        FileSystem fs = HdfsOperator.getFileSystem(hdfsSourceDTO.getKerberosConfig(), hdfsSourceDTO.getConfig(), hdfsSourceDTO.getDefaultFS());
        return HdfsOperator.isFileExist(fs, remotePath);
    }

    @Override
    public boolean checkAndDelete(ISourceDTO source, String remotePath) {
        Hdfs3SourceDTO hdfsSourceDTO = (Hdfs3SourceDTO) source;
        FileSystem fs = HdfsOperator.getFileSystem(hdfsSourceDTO.getKerberosConfig(), hdfsSourceDTO.getConfig(), hdfsSourceDTO.getDefaultFS());
        return HdfsOperator.checkAndDelete(fs, remotePath);
    }

    @Override
    public boolean delete(ISourceDTO source, String remotePath, boolean recursive) {
        Hdfs3SourceDTO hdfsSourceDTO = (Hdfs3SourceDTO) source;
        return KerberosLoginUtil.loginWithUGI(hdfsSourceDTO.getKerberosConfig()).doAs(
                (PrivilegedAction<Boolean>) () -> {
                    try {
                        Configuration conf = HadoopConfUtil.getHdfsConf(hdfsSourceDTO.getDefaultFS(), hdfsSourceDTO.getConfig(), hdfsSourceDTO.getKerberosConfig());
                        FileSystem fs = FileSystem.get(conf);
                        log.info("delete hdfs file ,remotePath :{}", remotePath);
                        return fs.delete(new Path(remotePath), recursive);
                    } catch (Exception e) {
                        throw new SourceException(String.format("Target path deletion exception,%s", e.getMessage()), e);
                    }
                }
        );
    }

    @Override
    public boolean copyDirector(ISourceDTO source, String src, String dist) {
        Hdfs3SourceDTO hdfsSourceDTO = (Hdfs3SourceDTO) source;
        return KerberosLoginUtil.loginWithUGI(hdfsSourceDTO.getKerberosConfig()).doAs(
                (PrivilegedAction<Boolean>) () -> {
                    try {
                        Path srcPath = new Path(src);
                        Path distPath = new Path(dist);
                        Configuration conf = HadoopConfUtil.getHdfsConf(hdfsSourceDTO.getDefaultFS(), hdfsSourceDTO.getConfig(), hdfsSourceDTO.getKerberosConfig());
                        FileSystem fs = FileSystem.get(conf);
                        if (fs.exists(srcPath)) {
                            //判断是不是文件夹
                            if (fs.isDirectory(srcPath)) {
                                if (!FileUtil.copy(fs, srcPath, fs, distPath, false, conf)) {
                                    throw new SourceException("copy " + src + " to " + dist + " failed");
                                }
                            } else {
                                throw new SourceException(src + "is not a directory");
                            }
                        } else {
                            throw new SourceException(src + " is not exists");
                        }
                        return true;
                    } catch (Exception e) {
                        throw new SourceException(String.format("Target path deletion exception,%s", e.getMessage()), e);
                    }
                }
        );
    }

    @Override
    public boolean fileMerge(ISourceDTO source, String src, String mergePath, FileFormat fileFormat, Long maxCombinedFileSize, Long needCombineFileSizeLimit) {
        Hdfs3SourceDTO hdfsSourceDTO = (Hdfs3SourceDTO) source;
        return KerberosLoginUtil.loginWithUGI(hdfsSourceDTO.getKerberosConfig()).doAs(
                (PrivilegedAction<Boolean>) () -> {
                    try {
                        Configuration conf = HadoopConfUtil.getHdfsConf(hdfsSourceDTO.getDefaultFS(), hdfsSourceDTO.getConfig(), hdfsSourceDTO.getKerberosConfig());
                        CombineServer build = new CombineMergeBuilder()
                                .sourcePath(src)
                                .mergedPath(mergePath)
                                .fileType(fileFormat)
                                .maxCombinedFileSize(maxCombinedFileSize)
                                .needCombineFileSizeLimit(needCombineFileSizeLimit)
                                .configuration(conf)
                                .build();
                        build.combine();
                        return true;
                    } catch (Exception e) {
                        throw new SourceException(String.format("File merge exception：%s", e.getMessage()), e);
                    }
                }
        );
    }

    @Override
    public long getDirSize(ISourceDTO source, String remotePath) {
        Hdfs3SourceDTO hdfsSourceDTO = (Hdfs3SourceDTO) source;
        FileSystem fs = HdfsOperator.getFileSystem(hdfsSourceDTO.getKerberosConfig(), hdfsSourceDTO.getConfig(), hdfsSourceDTO.getDefaultFS());
        return HdfsOperator.getDirSize(fs, remotePath);
    }

    @Override
    public boolean deleteFiles(ISourceDTO source, List<String> fileNames) {
        Hdfs3SourceDTO hdfsSourceDTO = (Hdfs3SourceDTO) source;
        FileSystem fs = HdfsOperator.getFileSystem(hdfsSourceDTO.getKerberosConfig(), hdfsSourceDTO.getConfig(), hdfsSourceDTO.getDefaultFS());
        return HdfsOperator.deleteFiles(fs, fileNames);
    }

    @Override
    public boolean isDirExist(ISourceDTO source, String remotePath) {
        Hdfs3SourceDTO hdfsSourceDTO = (Hdfs3SourceDTO) source;
        FileSystem fs = HdfsOperator.getFileSystem(hdfsSourceDTO.getKerberosConfig(), hdfsSourceDTO.getConfig(), hdfsSourceDTO.getDefaultFS());
        return HdfsOperator.isDirExist(fs, remotePath);
    }

    @Override
    public boolean setPermission(ISourceDTO source, String remotePath, String mode) {
        Hdfs3SourceDTO hdfsSourceDTO = (Hdfs3SourceDTO) source;
        FileSystem fs = HdfsOperator.getFileSystem(hdfsSourceDTO.getKerberosConfig(), hdfsSourceDTO.getConfig(), hdfsSourceDTO.getDefaultFS());
        return HdfsOperator.setPermission(fs, remotePath, mode);
    }

    @Override
    public boolean rename(ISourceDTO source, String src, String dist) {
        Hdfs3SourceDTO hdfsSourceDTO = (Hdfs3SourceDTO) source;
        FileSystem fs = HdfsOperator.getFileSystem(hdfsSourceDTO.getKerberosConfig(), hdfsSourceDTO.getConfig(), hdfsSourceDTO.getDefaultFS());
        return HdfsOperator.rename(fs, src, dist);
    }

    @Override
    public boolean copyFile(ISourceDTO source, String src, String dist, boolean isOverwrite) {
        Hdfs3SourceDTO hdfsSourceDTO = (Hdfs3SourceDTO) source;
        FileSystem fs = HdfsOperator.getFileSystem(hdfsSourceDTO.getKerberosConfig(), hdfsSourceDTO.getConfig(), hdfsSourceDTO.getDefaultFS());
        try {
            return HdfsOperator.copyFile(fs, src, dist, isOverwrite);
        } catch (IOException e) {
            throw new SourceException(String.format("Copying files in hdfs is abnormal : %s", e.getMessage()), e);
        }
    }

    @Override
    public List<FileStatus> listStatus(ISourceDTO source, String remotePath) {
        Hdfs3SourceDTO hdfsSourceDTO = (Hdfs3SourceDTO) source;
        FileSystem fs = HdfsOperator.getFileSystem(hdfsSourceDTO.getKerberosConfig(), hdfsSourceDTO.getConfig(), hdfsSourceDTO.getDefaultFS());
        try {
            return transferFileStatus(HdfsOperator.listStatus(fs, remotePath));
        } catch (IOException e) {
            throw new SourceException(String.format("The status of the file or folder under the target path is abnormal : %s", e.getMessage()), e);
        }
    }

    @Override
    public List<String> listAllFilePath(ISourceDTO source, String remotePath) {
        Hdfs3SourceDTO hdfsSourceDTO = (Hdfs3SourceDTO) source;
        FileSystem fs = HdfsOperator.getFileSystem(hdfsSourceDTO.getKerberosConfig(), hdfsSourceDTO.getConfig(), hdfsSourceDTO.getDefaultFS());
        try {
            return HdfsOperator.listAllFilePath(fs, remotePath);
        } catch (IOException e) {
            throw new SourceException(String.format("Obtaining all files in the target path is abnormal : %s", e.getMessage()), e);
        }
    }

    @Override
    public List<FileStatus> listAllFiles(ISourceDTO source, String remotePath, boolean isIterate) {
        Hdfs3SourceDTO hdfsSourceDTO = (Hdfs3SourceDTO) source;
        FileSystem fs = HdfsOperator.getFileSystem(hdfsSourceDTO.getKerberosConfig(), hdfsSourceDTO.getConfig(), hdfsSourceDTO.getDefaultFS());
        return listFiles(fs, remotePath, isIterate);
    }

    @Override
    public boolean copyToLocal(ISourceDTO source, String srcPath, String dstPath) {
        Hdfs3SourceDTO hdfsSourceDTO = (Hdfs3SourceDTO) source;
        FileSystem fs = HdfsOperator.getFileSystem(hdfsSourceDTO.getKerberosConfig(), hdfsSourceDTO.getConfig(), hdfsSourceDTO.getDefaultFS());
        return HdfsOperator.copyToLocal(fs, srcPath, dstPath);
    }

    @Override
    public boolean copyFromLocal(ISourceDTO source, String srcPath, String dstPath, boolean overwrite) {
        Hdfs3SourceDTO hdfsSourceDTO = (Hdfs3SourceDTO) source;
        FileSystem fs = HdfsOperator.getFileSystem(hdfsSourceDTO.getKerberosConfig(), hdfsSourceDTO.getConfig(), hdfsSourceDTO.getDefaultFS());
        return HdfsOperator.copyFromLocal(fs, srcPath, dstPath, overwrite);
    }

    @Override
    public IDownloader getDownloaderByFormat(ISourceDTO source, String tableLocation, List<String> columnNames, String fieldDelimiter, String fileFormat) {
        throw new SourceException("not support!");
    }

    @Override
    public IDownloader getDownloaderByFormatWithType(ISourceDTO source, String tableLocation, List<ColumnMetaDTO> allColumns, List<String> filterColumns, Map<String, String> filterPartition, List<String> partitions, String fieldDelimiter, String fileFormat) {
        Hdfs3SourceDTO hdfsSourceDTO = (Hdfs3SourceDTO) source;
        // 普通字段集合
        ArrayList<ColumnMetaDTO> commonColumn = new ArrayList<>();
        // 分区字段集合
        ArrayList<String> partitionColumns = new ArrayList<>();
        if (CollectionUtils.isEmpty(allColumns)) {
            allColumns = new ArrayList<>();
        }
        for (ColumnMetaDTO columnMetaDatum : allColumns) {
            // 非分区字段
            if (columnMetaDatum.getPart()) {
                partitionColumns.add(columnMetaDatum.getKey());
                continue;
            }
            commonColumn.add(columnMetaDatum);
        }
        // 需要的字段索引（包括分区字段索引）
        List<Integer> needIndex = Lists.newArrayList();
        // columns字段不为空且不包含 * 时获取指定字段的数据
        if (CollectionUtils.isNotEmpty(filterColumns) && !filterColumns.contains("*")) {
            // 保证查询字段的顺序!
            for (String column : filterColumns) {
                if (NULL_COLUMN.equalsIgnoreCase(column)) {
                    needIndex.add(Integer.MAX_VALUE);
                    continue;
                }
                // 判断查询字段是否存在
                boolean check = false;
                for (int j = 0; j < allColumns.size(); j++) {
                    if (column.equalsIgnoreCase(allColumns.get(j).getKey())) {
                        needIndex.add(j);
                        check = true;
                        break;
                    }
                }
                if (!check) {
                    throw new SourceException("The query field does not exist! Field name：" + column);
                }
            }
        }
        return KerberosLoginUtil.loginWithUGI(hdfsSourceDTO.getKerberosConfig()).doAs(
                (PrivilegedAction<IDownloader>) () -> {
                    try {
                        Configuration conf = HadoopConfUtil.getHdfsConf(hdfsSourceDTO.getDefaultFS(), hdfsSourceDTO.getConfig(), hdfsSourceDTO.getKerberosConfig());
                        return createDownloader(fileFormat, conf, tableLocation, commonColumn, fieldDelimiter, partitionColumns, needIndex, filterPartition, partitions, hdfsSourceDTO.getKerberosConfig());
                    } catch (Exception e) {
                        throw new SourceException(String.format("create downloader exception : %s", e.getMessage()), e);
                    }
                }
        );
    }

    @Override
    public IDownloader getDownloaderByFormatWithType(ISourceDTO source, String tableLocation, List<ColumnMetaDTO> allColumns, List<String> filterColumns, Map<String, String> filterPartition, List<String> partitions, String fieldDelimiter, String fileFormat, Boolean isTransTable) {
        return getDownloaderByFormatWithType(source, tableLocation, allColumns, filterColumns, filterPartition, partitions, fieldDelimiter, fileFormat);
    }

    /**
     * 根据存储格式创建对应的hiveDownloader
     *
     * @param storageMode      存储格式
     * @param conf             配置
     * @param tableLocation    表hdfs路径
     * @param columns          字段集合
     * @param fieldDelimiter   textFile 表列分隔符
     * @param partitionColumns 分区字段集合
     * @param needIndex        需要查询的字段索引位置
     * @param filterPartitions 需要查询的分区
     * @param partitions       全部分区
     * @param kerberosConfig   kerberos 配置
     * @return downloader 数据下载器
     * @throws Exception 异常信息
     */
    private IDownloader createDownloader(String storageMode, Configuration conf, String tableLocation,
                                         List<ColumnMetaDTO> columns, String fieldDelimiter,
                                         ArrayList<String> partitionColumns, List<Integer> needIndex,
                                         Map<String, String> filterPartitions, List<String> partitions,
                                         Map<String, Object> kerberosConfig) throws Exception {
        List<String> columnNames = columns.stream().map(ColumnMetaDTO::getKey).collect(Collectors.toList());
        if (StringUtils.equalsIgnoreCase(FileFormat.TEXT.getVal(), storageMode)) {
            HdfsTextDownload hdfsTextDownload = new HdfsTextDownload(conf, tableLocation, columnNames,
                    fieldDelimiter, partitionColumns, filterPartitions,
                    needIndex, partitions, kerberosConfig);
            hdfsTextDownload.configure();
            return hdfsTextDownload;
        }

        if (StringUtils.equalsIgnoreCase(FileFormat.ORC.getVal(), storageMode)) {
            HdfsORCDownload hdfsORCDownload = new HdfsORCDownload(conf, tableLocation, columnNames,
                    partitionColumns, needIndex, partitions, kerberosConfig);
            hdfsORCDownload.configure();
            return hdfsORCDownload;
        }

        if (StringUtils.equalsIgnoreCase(FileFormat.PARQUET.getVal(), storageMode)) {
            HdfsParquetDownload hdfsParquetDownload = new HdfsParquetDownload(conf, tableLocation, columns,
                    partitionColumns, needIndex, filterPartitions, partitions, kerberosConfig);
            hdfsParquetDownload.configure();
            return hdfsParquetDownload;
        }


        if (StringUtils.equalsIgnoreCase("csv", storageMode)) {
            HdfsCsvDownload hdfsCsvDownload = new HdfsCsvDownload(conf, tableLocation, columnNames,
                    fieldDelimiter, partitionColumns, filterPartitions,
                    needIndex, partitions, kerberosConfig);
            hdfsCsvDownload.configure();
            return hdfsCsvDownload;
        }
        throw new SourceException("This storage type file is not currently supported for writing to hdfs");
    }


    @Override
    public List<ColumnMetaDTO> getColumnList(ISourceDTO source, SqlQueryDTO queryDTO, String fileFormat) {
        Hdfs3SourceDTO hdfsSourceDTO = (Hdfs3SourceDTO) source;
        try {
            return getColumnListOnFileFormat(hdfsSourceDTO, queryDTO, fileFormat);
        } catch (IOException e) {
            throw new SourceException(String.format("Failed to get column information : %s", e.getMessage()), e);
        }
    }

    @Override
    public int writeByPos(ISourceDTO source, HdfsWriterDTO hdfsWriterDTO) {
        Hdfs3SourceDTO hdfsSourceDTO = (Hdfs3SourceDTO) source;
        return KerberosLoginUtil.loginWithUGI(hdfsSourceDTO.getKerberosConfig()).doAs(
                (PrivilegedAction<Integer>) () -> {
                    try {
                        return writeByPosWithFileFormat(hdfsSourceDTO, hdfsWriterDTO);
                    } catch (Exception e) {
                        throw new SourceException(String.format("Obtaining the field information of the hdfs file is abnormal : %s", e.getMessage()), e);
                    }
                }
        );
    }

    @Override
    public int writeByName(ISourceDTO source, HdfsWriterDTO hdfsWriterDTO) {
        Hdfs3SourceDTO hdfsSourceDTO = (Hdfs3SourceDTO) source;
        return KerberosLoginUtil.loginWithUGI(hdfsSourceDTO.getKerberosConfig()).doAs(
                (PrivilegedAction<Integer>) () -> {
                    try {
                        return writeByNameWithFileFormat(hdfsSourceDTO, hdfsWriterDTO);
                    } catch (Exception e) {
                        throw new SourceException(String.format("Obtaining the field information of the hdfs file is abnormal : %s", e.getMessage()), e);
                    }
                }
        );
    }

    @Override
    public HDFSContentSummary getContentSummary(ISourceDTO source, String hdfsDirPath) {
        return getContentSummary(source, Lists.newArrayList(hdfsDirPath)).get(0);
    }

    @Override
    public List<HDFSContentSummary> getContentSummary(ISourceDTO source, List<String> hdfsDirPaths) {
        if (CollectionUtils.isEmpty(hdfsDirPaths)) {
            throw new SourceException("hdfs path cannot be empty！");
        }
        Hdfs3SourceDTO hdfsSourceDTO = (Hdfs3SourceDTO) source;
        List<HDFSContentSummary> hdfsContentSummaries = Lists.newArrayList();
        // kerberos认证
        return KerberosLoginUtil.loginWithUGI(hdfsSourceDTO.getKerberosConfig()).doAs(
                (PrivilegedAction<List<HDFSContentSummary>>) () -> {
                    try {
                        Configuration conf = HadoopConfUtil.getHdfsConf(hdfsSourceDTO.getDefaultFS(), hdfsSourceDTO.getConfig(), hdfsSourceDTO.getKerberosConfig());
                        FileSystem fs = FileSystem.get(conf);
                        for (String HDFSDirPath : hdfsDirPaths) {
                            Path hdfsPath = new Path(HDFSDirPath);
                            // 判断路径是否存在，不存在则返回空对象
                            HDFSContentSummary hdfsContentSummary;
                            if (!fs.exists(hdfsPath)) {
                                log.warn("execute method getContentSummary: path {} not exists!", HDFSDirPath);
                                hdfsContentSummary = HDFSContentSummary.builder()
                                        .directoryCount(0L)
                                        .fileCount(0L)
                                        .ModifyTime(0L)
                                        .spaceConsumed(0L)
                                        .build();
                                if (ReflectUtil.fieldExists(hdfsContentSummary.getClass(), "isExists")) {
                                    hdfsContentSummary.setIsExists(false);
                                }
                            } else {
                                org.apache.hadoop.fs.FileStatus fileStatus = fs.getFileStatus(hdfsPath);
                                ContentSummary contentSummary = fs.getContentSummary(hdfsPath);
                                hdfsContentSummary = HDFSContentSummary.builder()
                                        .directoryCount(contentSummary.getDirectoryCount())
                                        .fileCount(contentSummary.getFileCount())
                                        .ModifyTime(fileStatus.getModificationTime())
                                        .spaceConsumed(contentSummary.getLength()).build();
                                if (ReflectUtil.fieldExists(hdfsContentSummary.getClass(), "isExists")) {
                                    hdfsContentSummary.setIsExists(true);
                                }
                            }
                            hdfsContentSummaries.add(hdfsContentSummary);
                        }
                        return hdfsContentSummaries;
                    } catch (Exception e) {
                        throw new SourceException(String.format("Failed to obtain HDFS file information：%s", e.getMessage()), e);
                    }
                }
        );
    }

    private int writeByPosWithFileFormat(ISourceDTO source, HdfsWriterDTO hdfsWriterDTO) throws IOException {
        if (FileFormat.ORC.getVal().equals(hdfsWriterDTO.getFileFormat())) {
            return HdfsOrcWriter.writeByPos(source, hdfsWriterDTO);
        }
        if (FileFormat.PARQUET.getVal().equals(hdfsWriterDTO.getFileFormat())) {
            return HdfsParquetWriter.writeByPos(source, hdfsWriterDTO);
        }
        if (FileFormat.TEXT.getVal().equals(hdfsWriterDTO.getFileFormat())) {
            return HdfsTextWriter.writeByPos(source, hdfsWriterDTO);
        }
        throw new SourceException("This storage type file is not supported for writing to hdfs");
    }

    private int writeByNameWithFileFormat(ISourceDTO source, HdfsWriterDTO hdfsWriterDTO) throws IOException {
        if (FileFormat.ORC.getVal().equals(hdfsWriterDTO.getFileFormat())) {
            return HdfsOrcWriter.writeByName(source, hdfsWriterDTO);
        }
        if (FileFormat.PARQUET.getVal().equals(hdfsWriterDTO.getFileFormat())) {
            return HdfsParquetWriter.writeByName(source, hdfsWriterDTO);
        }
        if (FileFormat.TEXT.getVal().equals(hdfsWriterDTO.getFileFormat())) {
            return HdfsTextWriter.writeByName(source, hdfsWriterDTO);
        }
        throw new SourceException("This storage type file is not supported for writing to hdfs");
    }

    private List<ColumnMetaDTO> getColumnListOnFileFormat(Hdfs3SourceDTO hdfsSourceDTO, SqlQueryDTO queryDTO, String
            fileFormat) throws IOException {

        if (FileFormat.ORC.getVal().equals(fileFormat)) {
            return getOrcColumnList(hdfsSourceDTO, queryDTO);
        }

        throw new SourceException("The file field information acquisition of this storage type is not supported");
    }

    private List<ColumnMetaDTO> getOrcColumnList(Hdfs3SourceDTO hdfsSourceDTO, SqlQueryDTO queryDTO) throws IOException {
        ArrayList<ColumnMetaDTO> columnList = new ArrayList<>();
        Configuration conf = HdfsOperator.getConfig(hdfsSourceDTO.getKerberosConfig(), hdfsSourceDTO.getConfig(), hdfsSourceDTO.getDefaultFS());
        FileSystem fs = HdfsOperator.getFileSystem(hdfsSourceDTO.getKerberosConfig(), hdfsSourceDTO.getConfig(), hdfsSourceDTO.getDefaultFS());
        OrcFile.ReaderOptions readerOptions = OrcFile.readerOptions(conf);
        readerOptions.filesystem(fs);
        String fileName = hdfsSourceDTO.getDefaultFS() + PATH_DELIMITER + queryDTO.getTableName();
        fileName = handleVariable(fileName);

        Path path = new Path(fileName);
        org.apache.hadoop.hive.ql.io.orc.Reader reader = null;
        String typeStruct = null;
        if (fs.isDirectory(path)) {
            RemoteIterator<LocatedFileStatus> iterator = fs.listFiles(path, true);
            while (iterator.hasNext()) {
                org.apache.hadoop.fs.FileStatus fileStatus = iterator.next();
                if (fileStatus.isFile() && fileStatus.getLen() > 49) {
                    Path subPath = fileStatus.getPath();
                    reader = OrcFile.createReader(subPath, readerOptions);
                    typeStruct = reader.getObjectInspector().getTypeName();
                    if (StringUtils.isNotEmpty(typeStruct)) {
                        break;
                    }
                }
            }
            if (reader == null) {
                throw new SourceException("orcfile dir is empty!");
            }

        } else {
            reader = OrcFile.createReader(path, readerOptions);
            typeStruct = reader.getObjectInspector().getTypeName();
        }

        if (StringUtils.isEmpty(typeStruct)) {
            throw new SourceException("can't retrieve type struct from " + path);
        }

        int startIndex = typeStruct.indexOf("<") + 1;
        int endIndex = typeStruct.lastIndexOf(">");
        typeStruct = typeStruct.substring(startIndex, endIndex);
        List<String> cols = StringUtil.splitIgnoreQuota(typeStruct, ',');
        for (String col : cols) {
            List<String> colNameAndType = StringUtil.splitIgnoreQuota(col, ':');
            if (CollectionUtils.isEmpty(colNameAndType) || colNameAndType.size() != 2) {
                continue;
            }
            ColumnMetaDTO metaDTO = new ColumnMetaDTO();
            metaDTO.setKey(colNameAndType.get(0));
            metaDTO.setType(colNameAndType.get(1));
            columnList.add(metaDTO);
        }
        return columnList;
    }

    private static String handleVariable(String path) {
        if (path.endsWith(PATH_DELIMITER)) {
            path = path.substring(0, path.length() - 1);
        }

        int pos = path.lastIndexOf(PATH_DELIMITER);
        String file = path.substring(pos + 1, path.length());

        if (file.matches(".*\\$\\{.*\\}.*")) {
            return path.substring(0, pos);
        }

        return path;
    }

    private List<FileStatus> listFiles(FileSystem fs, String remotePath, boolean isIterate) {
        try {
            return transferFileStatus(HdfsOperator.listFiles(fs, remotePath, isIterate));
        } catch (IOException e) {
            throw new SourceException(String.format("Failed to get the file in the target path : %s", e.getMessage()), e);
        }
    }

    /**
     * Apache Status 转换
     *
     * @param fileStatuses
     * @return
     */
    private List<FileStatus> transferFileStatus(List<org.apache.hadoop.fs.FileStatus> fileStatuses) {
        List<FileStatus> fileStatusList = new ArrayList<>();
        for (org.apache.hadoop.fs.FileStatus fileStatus : fileStatuses) {
            FileStatus fileStatusTemp = FileStatus.builder()
                    .length(fileStatus.getLen())
                    .access_time(fileStatus.getAccessTime())
                    .block_replication(fileStatus.getReplication())
                    .blocksize(fileStatus.getBlockSize())
                    .group(fileStatus.getGroup())
                    .isdir(fileStatus.isDirectory())
                    .modification_time(fileStatus.getModificationTime())
                    .owner(fileStatus.getOwner())
                    .path(fileStatus.getPath().toString())
                    .build();
            fileStatusList.add(fileStatusTemp);
        }
        return fileStatusList;
    }

    @Override
    public String getHdfsWithScript(ISourceDTO source, String hdfsPath) {
        HdfsSourceDTO hdfsSourceDTO = (HdfsSourceDTO) source;
        return KerberosLoginUtil.loginWithUGI(hdfsSourceDTO.getKerberosConfig()).doAs(
                (PrivilegedAction<String>) () -> {
                    try {
                        Configuration conf = HadoopConfUtil
                                .getHdfsConf(
                                        hdfsSourceDTO.getDefaultFS(),
                                        hdfsSourceDTO.getConfig(),
                                        hdfsSourceDTO.getKerberosConfig());
                        return ReaderFactory
                                .getInstance(FileFormat.TEXT.getVal())
                                .readText(conf, hdfsPath);
                    } catch (Exception e) {
                        throw new SourceException(String.format(
                                "Obtaining the field information of the hdfs file is abnormal : %s", e.getMessage()), e);
                    }
                }
        );
    }

    @Override
    public List<String> getHdfsWithJob(ISourceDTO source, HdfsQueryDTO hdfsQueryDTO) {
        HdfsSourceDTO hdfsSourceDTO = (HdfsSourceDTO) source;
        return KerberosLoginUtil.loginWithUGI(hdfsSourceDTO.getKerberosConfig()).doAs(
                (PrivilegedAction<List<String>>) () -> {
                    try {
                        Configuration conf = HadoopConfUtil
                                .getHdfsConf(
                                        hdfsSourceDTO.getDefaultFS(),
                                        hdfsSourceDTO.getConfig(),
                                        hdfsSourceDTO.getKerberosConfig());
                        return ReaderFactory
                                .getInstance(hdfsQueryDTO.getFileType())
                                .readByType(conf, hdfsQueryDTO);
                    } catch (Exception e) {
                        throw new SourceException(String.format(
                                "Obtaining the field information of the hdfs file is abnormal : %s", e.getMessage()), e);
                    }
                }
        );
    }
}
