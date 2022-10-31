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

package com.dtstack.taier.datasource.plugin.hdfs3.downloader.YarnLogDownload;

import com.dtstack.taier.datasource.plugin.common.downloader.IYarnDownloader;
import com.dtstack.taier.datasource.plugin.hdfs3.YarnConfUtil;
import com.dtstack.taier.datasource.plugin.kerberos.core.util.KerberosLoginUtil;
import com.dtstack.taier.datasource.api.exception.SourceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.logaggregation.AggregatedLogFormat;
import org.apache.hadoop.yarn.logaggregation.LogAggregationUtils;
import org.apache.hadoop.yarn.util.ConverterUtils;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 16:53 2020/8/7
 * @Description：Yarn 日志下载
 */
@Slf4j
public class YarnTFileDownload implements IYarnDownloader {
    private static final int BUFFER_SIZE = 4095;

    private int readLimit = BUFFER_SIZE;

    private Configuration configuration;

    private YarnConfiguration yarnConfiguration;

    private RemoteIterator<FileStatus> nodeFiles;

    private Map<String, Object> yarnConf;

    private String hdfsConfig;

    private static Configuration defaultConfiguration = new Configuration(false);

    private String appIdStr;

    private boolean isReachedEnd = false;

    private FileStatus currFileStatus;

    private DataInputStream currValueStream;

    private String currFileType = "";

    private long currFileLength = 0;

    private String logPreInfo = null;

    private String logEndInfo = null;

    private String currLineValue = "";

    private Integer totalReadByte = 0;

    private byte[] buf = new byte[BUFFER_SIZE];

    private long curRead = 0L;

    private String logType = null;

    private Map<String, Object> kerberosConfig;

    private AggregatedLogFormat.LogKey currLogKey;

    private AggregatedLogFormat.LogReader currReader;

    private String user;

    private String containerId;

    private String defaultFS = null;

    private YarnTFileDownload(String hdfsConfig, Map<String, Object> yarnConf, String appIdStr, Integer readLimit) {
        this.appIdStr = appIdStr;
        this.yarnConf = yarnConf;
        this.hdfsConfig = hdfsConfig;

        if (readLimit == null || readLimit < BUFFER_SIZE) {
            log.warn("it is not available readLimit set,it must bigger then " + BUFFER_SIZE + ", and use default :" + BUFFER_SIZE);
        } else {
            this.readLimit = readLimit;
        }
    }

    public YarnTFileDownload(String defaultFS, String user, String hdfsConfig, Map<String, Object> yarnConf, String appIdStr, Integer readLimit, String logType, Map<String, Object> kerberosConfig) {
        this(hdfsConfig, yarnConf, appIdStr, readLimit);
        this.logType = logType;
        this.kerberosConfig = kerberosConfig;
        this.user = user;
        this.defaultFS = defaultFS;
    }

    public YarnTFileDownload(String defaultFS, String user, String hdfsConfig, Map<String, Object> yarnConf, String appIdStr, Integer readLimit, String logType, Map<String, Object> kerberosConfig, String containerId) {
        this(defaultFS, user, hdfsConfig, yarnConf, appIdStr, readLimit, logType, kerberosConfig);
        this.containerId = containerId;
    }

    @Override
    public boolean configure() throws Exception {
        configuration = YarnConfUtil.getFullConfiguration(defaultFS, hdfsConfig, yarnConf, kerberosConfig);

        //TODO 暂时在这个地方加上
        configuration.set("fs.AbstractFileSystem.hdfs.impl", "org.apache.hadoop.fs.Hdfs");

        yarnConfiguration = new YarnConfiguration(configuration);

        Path remoteRootLogDir = new Path(configuration.get(
                YarnConfiguration.NM_REMOTE_APP_LOG_DIR,
                YarnConfiguration.DEFAULT_NM_REMOTE_APP_LOG_DIR));

        String logDirSuffix = LogAggregationUtils.getRemoteNodeLogDirSuffix(configuration);
        // TODO Change this to get a list of files from the LAS.

        ApplicationId appId = ConverterUtils.toApplicationId(appIdStr);

        //kerberos认证User
        String jobOwner = UserGroupInformation.getCurrentUser().getShortUserName();
        // 支持其他用户的日志下载
        if (StringUtils.isNotBlank(user)) {
            jobOwner = user;
        }
        log.info("applicationId:{},jobOwner:{}", appId, jobOwner);
        Path remoteAppLogDir = LogAggregationUtils.getRemoteAppLogDir(
                remoteRootLogDir, appId, jobOwner, logDirSuffix);
        log.info("applicationId:{},applicationLogPath:{}", appId, remoteAppLogDir.toString());
        checkSize(remoteAppLogDir.toString());
        try {
            Path qualifiedLogDir = FileContext.getFileContext(configuration).makeQualified(remoteAppLogDir);
            nodeFiles = FileContext.getFileContext(qualifiedLogDir.toUri(), configuration).listStatus(remoteAppLogDir);
            nextLogFile();
        } catch (FileNotFoundException fnf) {
            throw new SourceException("applicationId:" + appIdStr + " don't have any log file.");
        }
        return true;
    }

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
        if (currValueStream != null) {
            currValueStream.close();
        }
        return true;
    }

    /**
     * 下一个日志文件
     *
     * @return
     * @throws IOException
     */
    private boolean nextLogFile() throws IOException {

        if (currReader != null) {
            currReader.close();
        }

        if (nodeFiles.hasNext()) {
            currFileStatus = nodeFiles.next();
            if (!currFileStatus.getPath().getName()
                    .endsWith(LogAggregationUtils.TMP_FILE_SUFFIX)) {

                currReader = new AggregatedLogFormat.LogReader(configuration, currFileStatus.getPath());
                nextStream();
                try {
                    nextLogType();
                } catch (EOFException e) {
                    //当前logfile已经读取完
                    currLineValue = logEndInfo;
                    logEndInfo = null;
                    if (!nextStream()) {
                        return nextLogFile();
                    }
                    try {
                        nextLogType();
                    } catch (EOFException e1) {
                        if (!nextStream()) {
                            return nextLogFile();
                        }
                        return false;
                    }
                }
                return true;
            } else {
                return nextLogFile();
            }
        } else {
            isReachedEnd = true;
            return false;
        }
    }

    /**
     * 更换下一个container读取
     *
     * @return
     * @throws IOException
     */
    private boolean nextStream() throws IOException {
        currLogKey = new AggregatedLogFormat.LogKey();
        currValueStream = currReader.next(currLogKey);
        return currValueStream != null;
    }

    /**
     * 下一个日志类型
     *
     * @return
     * @throws IOException
     */
    private boolean nextLogType() throws IOException {
        currFileType = currValueStream.readUTF();
        String fileLengthStr = currValueStream.readUTF();
        currFileLength = Long.parseLong(fileLengthStr);

        if (StringUtils.isNotBlank(logType) && !currFileType.toUpperCase().startsWith(logType)) {
            currValueStream.skipBytes(Integer.valueOf(fileLengthStr));
            return nextLogType();
        } else if (StringUtils.isNotBlank(containerId) && !containerId.equals(currLogKey.toString())) {
            currValueStream.skipBytes(Integer.valueOf(fileLengthStr));
            return nextLogType();
        }

        logPreInfo = "\n\nContainer: " + currLogKey + " on " + currFileStatus.getPath().getName() + "\n";
        logPreInfo = logPreInfo + StringUtils.repeat("=", logPreInfo.length()) + "\n";
        logPreInfo = logPreInfo + "LogType:" + currFileType + "\n";
        logPreInfo = logPreInfo + "LogLength:" + currFileLength + "\n";
        logPreInfo = logPreInfo + "Log Contents:\n";
        curRead = 0L;

        return true;
    }

    private boolean nextRecord() throws IOException {
        if (currValueStream == null && !nextLogFile()) {
            isReachedEnd = true;
            currLineValue = null;
            return false;
        }

        if (currFileLength == curRead && logPreInfo != null) {
            currLineValue = logPreInfo;
            logPreInfo = null;
            return true;
        }

        //当前logtype已经读取完
        if (currFileLength == curRead) {
            logEndInfo = "End of LogType:" + currFileType + "\n";
            try {
                nextLogType();
            } catch (EOFException e) {
                //当前logfile已经读取完
                currLineValue = logEndInfo;
                logEndInfo = null;
                if (!nextStream()) {
                    return nextLogFile();
                }
                try {
                    nextLogType();
                } catch (EOFException e1) {
                    if (!nextStream()) {
                        return nextLogFile();
                    }
                    return false;
                }
                return nextRecord();
            }
        }

        if (currFileLength == 0) {
            currLineValue = logEndInfo;
            return true;
        }

        long pendingRead = currFileLength - curRead;
        int toRead = pendingRead > buf.length ? buf.length : (int) pendingRead;

        int readNum = currValueStream.read(buf, 0, toRead);
        curRead += readNum;

        if (readNum <= 0) {
            //close stream
            currValueStream.close();

            boolean hasNext = nextLogFile();
            if (!hasNext) {
                isReachedEnd = true;
                return false;
            }

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

    @Override
    public List<String> getTaskManagerList() {
        return KerberosLoginUtil.loginWithUGI(kerberosConfig).doAs(
                (PrivilegedAction<List<String>>) ()->{
                    try {
                        return getContainersWithKerberos();
                    } catch (Exception e){
                        throw new SourceException(String.format("Abnormal reading file,%s",e.getMessage()), e);
                    }
                });
    }


    public List<String> getContainersWithKerberos() throws Exception {
        HashSet<String> containers = new HashSet();
        if (this.currValueStream != null) {
            if (this.currFileType.toUpperCase().startsWith("TASKMANAGER")) {
                containers.add(this.currLogKey.toString());
            }

            this.currValueStream.close();
        }

        while(this.nextStream()) {
            if (this.currValueStream != null) {
                this.currFileType = this.currValueStream.readUTF();
                if (this.currFileType.toUpperCase().startsWith("TASKMANAGER")) {
                    containers.add(this.currLogKey.toString());
                }

                this.currValueStream.close();
            }
        }

        while(this.nextLogFile()) {
            if (this.currValueStream != null) {
                if (this.currFileType.toUpperCase().startsWith("TASKMANAGER")) {
                    containers.add(this.currLogKey.toString());
                }

                this.currValueStream.close();
            }
        }

        return new ArrayList(containers);
    }
}
