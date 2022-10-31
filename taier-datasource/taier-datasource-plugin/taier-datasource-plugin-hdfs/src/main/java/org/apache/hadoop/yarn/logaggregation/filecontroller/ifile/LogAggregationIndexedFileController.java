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

package org.apache.hadoop.yarn.logaggregation.filecontroller.ifile;

import com.dtstack.taier.datasource.plugin.common.downloader.IYarnDownloader;
import com.dtstack.taier.datasource.plugin.kerberos.core.util.KerberosLoginUtil;
import com.dtstack.taier.datasource.plugin.hdfs.YarnConfUtil;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.HarFs;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.compress.Decompressor;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.records.ApplicationAccessType;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.logaggregation.LogAggregationUtils;
import org.apache.hadoop.yarn.util.ConverterUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * yarn 聚合日志 IFIle 类型下载
 *
 * @author ：wangchuan
 * date：Created in 3:24 下午 2021/1/25
 * company: www.dtstack.com
 */
@Slf4j
public class LogAggregationIndexedFileController implements IYarnDownloader {
    private static final int BUFFER_SIZE = 4095;

    private int readLimit = BUFFER_SIZE;

    private Configuration configuration;

    private YarnConfiguration yarnConfiguration;

    private Iterator<FileStatus> nodeFiles;

    private Map<String, Object> yarnConf;

    private String hdfsConfig;

    private String appIdStr;

    private boolean isReachedEnd = false;

    private FileStatus currFileStatus;

    private long currFileLength = 0;

    private String logPreInfo = null;

    private String logEndInfo = null;

    private String currLineValue = "";

    private Integer totalReadByte = 0;

    private byte[] buf = new byte[BUFFER_SIZE];

    private long curRead = 0L;

    private Map<String, Object> kerberosConfig;

    private String user;

    private String containerId;

    public static final String CHECK_SUM_FILE_SUFFIX = "-checksum";

    private Map<String, Long> checkSumFiles;

    private byte[] uuid = null;

    private ApplicationId appId = null;

    private String defaultFS = null;

    private InputStream currInputStream;

    private static final String FS_INPUT_BUF_SIZE_ATTR = "indexedFile.fs.input.buffer.size";

    private static final String CONTAINER_ON_NODE_PATTERN = "Container: %s on %s";

    // hadoop 3.0.0 后 yarn iFIle聚合日志根路径，会覆盖原有配置
    private static final String YARN_INDEX_FILE_REMOTE_APP_LOG_DIR = "yarn.log-aggregation.IFile.remote-app-log-dir";

    // hadoop 3.0.0 后 yarn iFIle聚合日志后缀路径，会覆盖原有配置
    private static final String YARN_INDEX_FILE_REMOTE_APP_LOG_DIR_SUFFIX = "yarn.log-aggregation.IFile.remote-app-log-dir-suffix";

    // hadoop 3.0.0 后 yarn iFIle聚合日志后缀默认路径
    private static final String YARN_INDEX_FILE_REMOTE_APP_LOG_DIR_SUFFIX_DEFAULT = "logs-ifile";

    private IndexedFileLogMeta indexedFileLogMeta;

    private Compression.Algorithm compressName;

    private FSDataInputStream fsIn;

    private Decompressor decompressor;

    private Iterator<IndexedFileLogMeta> fileLogMetaIterator;

    private static final String TASK_MANAGER_LOG = "TASKMANAGER";

    public LogAggregationIndexedFileController(String defaultFs, String user, String hdfsConfig, Map<String, Object> yarnConf, String appIdStr, Integer readLimit, String logType, Map<String, Object> kerberosConfig, String containerId) {
        this.kerberosConfig = kerberosConfig;
        this.user = user;
        this.appIdStr = appIdStr;
        this.yarnConf = yarnConf;
        this.hdfsConfig = hdfsConfig;
        this.containerId = containerId;
        this.defaultFS = defaultFs;
        if (readLimit == null || readLimit < BUFFER_SIZE) {
            log.warn("it is not available readLimit set,it must bigger then " + BUFFER_SIZE + ", and use default :" + BUFFER_SIZE);
        } else {
            this.readLimit = readLimit;
        }
    }

    @Override
    public boolean configure() throws Exception {
        configuration = YarnConfUtil.getFullConfiguration(defaultFS, hdfsConfig, yarnConf, kerberosConfig);
        configuration.set("fs.AbstractFileSystem.hdfs.impl", "org.apache.hadoop.fs.Hdfs");
        yarnConfiguration = new YarnConfiguration(configuration);
        String remoteRootLogDir = configuration.get(YARN_INDEX_FILE_REMOTE_APP_LOG_DIR, configuration.get(YarnConfiguration.NM_REMOTE_APP_LOG_DIR,
                YarnConfiguration.DEFAULT_NM_REMOTE_APP_LOG_DIR));
        Path remoteRootLogDirPath = new Path(remoteRootLogDir);

        // 聚合日志后缀文件夹
        String logDirSuffix = configuration.get(YARN_INDEX_FILE_REMOTE_APP_LOG_DIR_SUFFIX, YARN_INDEX_FILE_REMOTE_APP_LOG_DIR_SUFFIX_DEFAULT);

        appId = ConverterUtils.toApplicationId(appIdStr);
        //kerberos认证User
        String jobOwner = UserGroupInformation.getCurrentUser().getShortUserName();
        // 支持其他用户的日志下载
        if (StringUtils.isNotBlank(user)) {
            jobOwner = user;
        }
        log.info("applicationId:{},jobOwner:{}", appId, jobOwner);
        Path remoteAppLogDir = LogAggregationUtils.getRemoteAppLogDir(
                remoteRootLogDirPath, appId, jobOwner, logDirSuffix);

        log.info("applicationId:{},applicationLogPath:{}", appId, remoteAppLogDir.toString());
        checkSize(remoteAppLogDir.toString());
        try {
            Path qualifiedLogDir = FileContext.getFileContext(configuration).makeQualified(remoteAppLogDir);
            RemoteIterator<FileStatus> fileStatusRemoteIterator = FileContext.getFileContext(qualifiedLogDir.toUri(), configuration).listStatus(remoteAppLogDir);
            List<FileStatus> allNodeFiles = getAllNodeFiles(fileStatusRemoteIterator, appId);
            checkSumFiles = parseCheckSumFiles(allNodeFiles);
            nodeFiles = getNodeLogFileToRead(allNodeFiles).iterator();
            nextLogFile();
        } catch (FileNotFoundException fnf) {
            throw new SourceException("applicationId:" + appIdStr + " don't have any log file.");
        }
        return true;
    }

    // 检查文件夹大小
    private void checkSize(String tableLocation) throws IOException {
        Path inputPath = new Path(tableLocation);
        Configuration conf = new JobConf(yarnConfiguration);
        FileSystem fs = FileSystem.get(conf);

        FileStatus[] fsStatus = fs.listStatus(inputPath);
        boolean thr = false;
        if (fsStatus == null || fsStatus.length == 0) {
            thr = true;
        } else {
            long totalSize = 0L;
            for (FileStatus file : fsStatus) {
                totalSize += file.getLen();
            }
            if (totalSize == 0L) {
                thr = true;
            }
        }
        if (thr) {
            // 文件大小为0的时候不允许下载，需要重新调用configure接口
            throw new SourceException("path：" + tableLocation + " size = 0 ");
        }
    }

    @Override
    public List<String> getMetaInfo() {
        throw new SourceException("not support getMetaInfo of App log download");
    }

    @Override
    public Object readNext() {
        return currLineValue;
    }

    @Override
    public boolean reachedEnd() {
        return KerberosLoginUtil.loginWithUGI(kerberosConfig).doAs(
                (PrivilegedAction<Boolean>) ()->{
                    try {
                        return isReachedEnd || totalReadByte >= readLimit || !nextRecord();
                    } catch (Exception e) {
                        throw new SourceException(String.format("Abnormal reading file,%s", e.getMessage()), e);
                    }
                });
    }

    @Override
    public boolean close() throws Exception {
        if (currInputStream != null) {
            currInputStream.close();
        }
        return true;
    }

    /**
     * 读取下一个日志文件
     *
     * @return 是否读完
     * @throws IOException io异常
     */
    private boolean nextLogFile() throws IOException {

        // 是否获取全部 container
        boolean getAllContainers = (containerId == null
                || containerId.isEmpty());

        if (currInputStream != null) {
            currInputStream.close();
        }

        if (nodeFiles.hasNext()) {
            currFileStatus = nodeFiles.next();
            String nodeName = currFileStatus.getPath().getName();
            Long checkSumIndex = checkSumFiles.get(nodeName);
            long endIndex = -1;
            if (checkSumIndex != null) {
                endIndex = checkSumIndex;
            }
            IndexedLogsMeta indexedLogsMeta;
            try {
                indexedLogsMeta = loadIndexedLogsMeta(currFileStatus.getPath(),
                        endIndex, appId);
            } catch (Exception e) {
                log.warn("Can not load log meta from the log file:"
                        + currFileStatus.getPath() + "\n" + e.getMessage());
                return false;
            }
            if (indexedLogsMeta == null) {
                return false;
            }

            String compressAlgo = indexedLogsMeta.getCompressName();
            List<IndexedFileLogMeta> candidates = new ArrayList<>();
            for (IndexedPerAggregationLogMeta logMeta
                    : indexedLogsMeta.getLogMetas()) {
                for (Map.Entry<String, List<IndexedFileLogMeta>> meta
                        : logMeta.getLogMetas().entrySet()) {
                    for (IndexedFileLogMeta log : meta.getValue()) {
                        if (!getAllContainers && !log.getContainerId()
                                .equals(containerId)) {
                            continue;
                        }
                        candidates.add(log);
                    }
                }
            }

            if (candidates.isEmpty()) {
                return false;
            }
            compressName = Compression.getCompressionAlgorithmByName(compressAlgo);
            decompressor = compressName.getDecompressor();
            FileContext fileContext = FileContext.getFileContext(
                    currFileStatus.getPath().toUri(), configuration);
            fsIn = fileContext.open(currFileStatus.getPath());
            fileLogMetaIterator = candidates.iterator();
            // 读取下一个container
            nextContainer();
            // 读取下一个logType
            nextLogType();
            return true;
        } else {
            isReachedEnd = true;
            return false;
        }
    }

    /**
     * 读取下一个container
     */
    private Boolean nextContainer() throws IOException {
        // 关闭流
        if (currInputStream != null) {
            currInputStream.close();
        }
        if (fileLogMetaIterator.hasNext()) {
            IndexedFileLogMeta fileLogMeta = fileLogMetaIterator.next();
            indexedFileLogMeta = fileLogMeta;
            try {
                currInputStream = compressName.createDecompressionStream(
                        new BoundedRangeFileInputStream(fsIn,
                                fileLogMeta.getStartIndex(),
                                fileLogMeta.getFileCompressedSize()),
                        decompressor, getFSInputBufferSize(configuration));
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                compressName.returnDecompressor(decompressor);
            }
            return true;
        } else {
            return false;
        }
    }

    public static int getFSInputBufferSize(Configuration conf) {
        return conf.getInt(FS_INPUT_BUF_SIZE_ATTR, 256 * 1024);
    }

    public IndexedLogsMeta loadIndexedLogsMeta(Path remoteLogPath, long end, ApplicationId appId) throws IOException {
        FileContext fileContext =
                FileContext.getFileContext(remoteLogPath.toUri(), configuration);
        FSDataInputStream fsDataIStream = null;
        try {
            fsDataIStream = fileContext.open(remoteLogPath);
            if (end == 0) {
                return null;
            }
            long fileLength = end < 0 ? fileContext.getFileStatus(
                    remoteLogPath).getLen() : end;

            int UUID_LENGTH = 32;
            fsDataIStream.seek(fileLength - Integer.SIZE/ Byte.SIZE - UUID_LENGTH);
            int offset = fsDataIStream.readInt();
            // If the offset/log meta size is larger than 64M,
            // output a warn message for better debug.
            if (offset > 64 * 1024 * 1024) {
                log.warn("The log meta size read from " + remoteLogPath
                        + " is " + offset);
            }

            // Load UUID and make sure the UUID is correct.
            byte[] uuidRead = new byte[UUID_LENGTH];
            int uuidReadLen = fsDataIStream.read(uuidRead);
            if (this.uuid == null) {
                this.uuid = createUUID(appId);
            }
            if (uuidReadLen != UUID_LENGTH || !Arrays.equals(this.uuid, uuidRead)) {
                if (log.isDebugEnabled()) {
                    log.debug("the length of loaded UUID:" + uuidReadLen);
                    log.debug("the loaded UUID:" + new String(uuidRead,
                            StandardCharsets.UTF_8));
                    log.debug("the expected UUID:" + new String(this.uuid,
                            StandardCharsets.UTF_8));
                }
                throw new IOException("The UUID from "
                        + remoteLogPath + " is not correct. The offset of loaded UUID is "
                        + (fileLength - UUID_LENGTH));
            }

            // Load Log Meta
            byte[] array = new byte[offset];
            fsDataIStream.seek(
                    fileLength - offset - Integer.SIZE/ Byte.SIZE - UUID_LENGTH);
            fsDataIStream.readFully(array);
            return (IndexedLogsMeta) SerializationUtils
                    .deserialize(array);
        } finally {
            IOUtils.closeStream(fsDataIStream);
        }
    }

    private byte[] createUUID(ApplicationId appId) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(appId.toString().getBytes(
                    StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException ex) {
            throw new IOException(ex);
        }
    }

    private List<FileStatus> getAllNodeFiles(RemoteIterator<FileStatus> nodeFiles, ApplicationId appId)
            throws IOException {
        List<FileStatus> listOfFiles = new ArrayList<>();
        while (nodeFiles != null && nodeFiles.hasNext()) {
            FileStatus thisNodeFile = nodeFiles.next();
            String nodeName = thisNodeFile.getPath().getName();
            if (nodeName.equals(appId + ".har")) {
                Path p = new Path("har:///"
                        + thisNodeFile.getPath().toUri().getRawPath());
                nodeFiles = HarFs.get(p.toUri(), configuration).listStatusIterator(p);
                continue;
            }
            listOfFiles.add(thisNodeFile);
        }
        return listOfFiles;
    }

    /**
     * 读取下一个 container 日志类型
     */
    private void nextLogType() {
        if (Objects.isNull(indexedFileLogMeta)) {
            throw new SourceException("get container logger type error");
        }
        currFileLength = indexedFileLogMeta.getFileSize();
        StringBuilder sb = new StringBuilder();
        String containerStr = String.format(CONTAINER_ON_NODE_PATTERN, indexedFileLogMeta.containerId, currFileStatus.getPath().getName());
        sb.append(containerStr).append("\n");
        sb.append(StringUtils.repeat("=", containerStr.length())).append("\n");
        sb.append("LogType:").append(indexedFileLogMeta.fileName).append("\n");
        Date date = new Date(indexedFileLogMeta.lastModifiedTime);
        sb.append("LogLastModifiedTime:").append(date.toString()).append("\n");
        sb.append("LogLength:").append(indexedFileLogMeta.fileSize).append("\n");
        sb.append("LogContents:\n");
        logPreInfo = sb.toString();
        // 初始化读取位置
        curRead = 0L;
    }

    private boolean nextRecord() throws IOException {

        // 判断当前流是否关闭
        if (currInputStream == null && !nextLogFile()) {
            isReachedEnd = true;
            currLineValue = null;
            return false;
        }

        if (currFileLength == curRead && logPreInfo != null) {
            currLineValue = logPreInfo;
            logPreInfo = null;
            return true;
        }

        // 当前LogType已经读取完
        if (currFileLength == curRead) {
            logEndInfo = "End of LogType:" + indexedFileLogMeta.fileName + "\n";
            currLineValue = logPreInfo + logEndInfo;
            // 先判断是否还有未读取完的container
            Boolean hasNextContainer = nextContainer();
            if (!hasNextContainer) {
                // 再判断是否还有未读取完的日志文件
                boolean hasNextLogFile = nextLogFile();
                if (!hasNextLogFile) {
                    isReachedEnd = true;
                    return false;
                }
            }
            nextLogType();
        }

        if (currFileLength == 0) {
            currLineValue = logEndInfo;
            return true;
        }

        long pendingRead = currFileLength - curRead;
        int toRead = pendingRead > buf.length ? buf.length : (int) pendingRead;

        int readNum = currInputStream.read(buf, 0, toRead);
        curRead += readNum;

        if (readNum <= 0) {
            // 关闭读取流
            if (currInputStream != null) {
                currInputStream.close();
            }
            // 先判断是否还有未读取完的container
            Boolean hasNextContainer = nextContainer();
            if (!hasNextContainer) {
                // 再判断是否还有未读取完的日志文件
                boolean hasNextLogFile = nextLogFile();
                if (!hasNextLogFile) {
                    isReachedEnd = true;
                    return false;
                }
            }
            nextLogType();
            return nextRecord();
        }

        String readLine = new String(buf, 0, readNum);
        totalReadByte += readNum;

        if (logPreInfo != null) {
            readLine = logPreInfo + readLine;
            logPreInfo = null;
        }

        if (logEndInfo != null) {
            readLine = logEndInfo + readLine;
            logEndInfo = null;
        }

        currLineValue = readLine;
        return true;
    }

    @InterfaceAudience.Private
    @VisibleForTesting
    @Data
    public static class IndexedLogsMeta implements Serializable {

        private static final long serialVersionUID = 5439875373L;

        private int version;

        private String user;

        private String compressName;

        private Map<ApplicationAccessType, String> acls;

        private String nodeId;

        private List<IndexedPerAggregationLogMeta> logMetas = new ArrayList<>();
    }

    /**
     * 获取所有的 taskManager container ，此方法不能和其他方法同时使用
     * @return taskManager containerId 列表
     */
    public List<String> getTaskManagerList() {
        return KerberosLoginUtil.loginWithUGI(kerberosConfig).doAs(
                (PrivilegedAction<List<String>>) ()->{
                    try {
                        return getContainersWithKerberos();
                    } catch (Exception e){
                        throw new SourceException(String.format("get taskManager container exception,%s",e.getMessage()), e);
                    }
                });
    }

    public List<String> getContainersWithKerberos() throws Exception {
        HashSet<String> containers = Sets.newHashSet();

        while (fileLogMetaIterator != null && fileLogMetaIterator.hasNext()) {
            IndexedFileLogMeta logMeta = fileLogMetaIterator.next();
            if (logMeta.fileName.toUpperCase().startsWith(TASK_MANAGER_LOG)) {
                containers.add(logMeta.containerId);
            }
        }

        while (nextLogFile()) {
            IndexedFileLogMeta logMeta = fileLogMetaIterator.next();
            if (logMeta.fileName.toUpperCase().startsWith(TASK_MANAGER_LOG)) {
                containers.add(logMeta.containerId);
            }
        }
        if (currInputStream != null) {
            currInputStream.close();
        }
        return new ArrayList<>(containers);
    }

    @Data
    public static class IndexedPerAggregationLogMeta implements Serializable {

        private static final long serialVersionUID = 3929298383L;

        private String remoteNodeLogFileName;

        private Map<String, List<IndexedFileLogMeta>> logMetas = new HashMap<>();

        private long uploadTimeStamp;

    }

    @InterfaceAudience.Private
    @VisibleForTesting
    @Data
    public static class IndexedFileLogMeta implements Serializable {

        private static final long serialVersionUID = 1L;

        private String containerId;

        private String fileName;

        private long fileSize;

        private long fileCompressedSize;

        private long lastModifiedTime;

        private long startIndex;
    }

    public List<FileStatus> getNodeLogFileToRead(List<FileStatus> nodeFiles) {
        List<FileStatus> listOfFiles = new ArrayList<>();
        for (FileStatus thisNodeFile : nodeFiles) {
            String nodeName = thisNodeFile.getPath().getName();
            if (!nodeName.endsWith(
                    LogAggregationUtils.TMP_FILE_SUFFIX) &&
                    !nodeName.endsWith(CHECK_SUM_FILE_SUFFIX)) {
                listOfFiles.add(thisNodeFile);
            }
        }
        return listOfFiles;
    }

    public Map<String, Long> parseCheckSumFiles(List<FileStatus> fileList){
        Map<String, Long> checkSumFiles = new HashMap<>();
        Set<FileStatus> status = new HashSet<>(fileList);
        status = status.stream().filter(next -> next.getPath().getName().endsWith(
                CHECK_SUM_FILE_SUFFIX)).collect(Collectors.toSet());
        FileContext fc = null;
        for (FileStatus file : status) {
            FSDataInputStream checksumFileInputStream = null;
            try {
                if (fc == null) {
                    fc = FileContext.getFileContext(file.getPath().toUri(), configuration);
                }
                String nodeName;
                long index;
                checksumFileInputStream = fc.open(file.getPath());
                int nameLength = checksumFileInputStream.readInt();
                byte[] b = new byte[nameLength];
                int actualLength = checksumFileInputStream.read(b);
                if (actualLength == nameLength) {
                    nodeName = new String(b, StandardCharsets.UTF_8);
                    index = checksumFileInputStream.readLong();
                } else {
                    continue;
                }
                if (!nodeName.isEmpty()) {
                    checkSumFiles.put(nodeName, index);
                }
            } catch (IOException ex) {
                log.warn(ex.getMessage());
            } finally {
                IOUtils.closeStream(checksumFileInputStream);
            }
        }
        return checkSumFiles;
    }
}
