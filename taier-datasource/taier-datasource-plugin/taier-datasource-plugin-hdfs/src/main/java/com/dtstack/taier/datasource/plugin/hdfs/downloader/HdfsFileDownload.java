package com.dtstack.taier.datasource.plugin.hdfs.downloader;

import com.dtstack.taier.datasource.api.downloader.IDownloader;
import com.dtstack.taier.datasource.plugin.kerberos.core.hdfs.HdfsOperator;
import com.dtstack.taier.datasource.plugin.kerberos.core.util.KerberosLoginUtil;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.google.common.collect.Lists;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * hdfs文件读取下载器
 *
 * @author luming
 * @date 2022年05月06日
 */
public class HdfsFileDownload implements IDownloader {

    private static final String CRLF = System.lineSeparator();
    /**
     * 一次读取限制，所有文件加在一起限制1000行
     */
    private static final int READ_LIMIT = 1000;
    /**
     * 当前读取行数
     */
    private int readNum = 0;
    /**
     * 入参的hdfs路径
     */
    private final String path;
    /**
     * 入参根路径下的所有hdfs文件路径
     */
    private List<String> paths;
    /**
     * 当前读取文件路径
     */
    private String currFile;
    /**
     * 当前读取文件在paths中的索引位置
     */
    private int currFileIndex = 0;
    /**
     * 当前行value值
     */
    private String value;
    /**
     * 是否到达最后一行
     */
    private boolean reachEnd;

    private final Map<String, Object> kerberosConfig;
    private final FileSystem fs;
    private FSDataInputStream fsIs;
    private Reader reader;
    private BufferedReader bufferedReader;

    public HdfsFileDownload(String defaultFs, String hdfsConfig, String path, Map<String, Object> kerberosConfig) {
        this.kerberosConfig = kerberosConfig;
        this.path = path;
        fs = HdfsOperator.getFileSystem(kerberosConfig, hdfsConfig, defaultFs);
    }

    @Override
    public boolean configure() throws Exception {
        paths = checkPath(path);
        if (paths.size() == 0) {
            throw new RuntimeException("Illegal path:" + path);
        }
        nextRecordReader();
        return true;
    }

    /**
     * 获取下一个hdfs文件的读取bufferReader
     *
     * @return 是否获取成功
     */
    private boolean nextRecordReader() throws IOException {
        if (!nextFile()) {
            return false;
        }

        //获取下一个之前先关闭上一个文件的流
        close();

        Path inputPath = new Path(currFile);
        fsIs = fs.open(inputPath);
        reader = new InputStreamReader(fsIs, StandardCharsets.UTF_8);
        bufferedReader = new BufferedReader(reader);

        return true;
    }

    private boolean nextFile() {
        if (currFileIndex > (paths.size() - 1)) {
            return false;
        }

        currFile = paths.get(currFileIndex);
        currFileIndex++;

        return true;
    }

    public boolean nextRecord() throws IOException {
        if ((value = bufferedReader.readLine()) != null) {
            return true;
        }

        //查找下一个可读的文件夹
        while (nextRecordReader()) {
            if (nextRecord()) {
                return true;
            }
        }

        return false;
    }


    @Override
    public List<String> getMetaInfo() {
        return Collections.emptyList();
    }

    @Override
    public String readNext() {
        return KerberosLoginUtil.loginWithUGI(kerberosConfig).doAs(
                (PrivilegedAction<String>) () -> {
                    try {
                        return readNextWithKerberos();
                    } catch (Exception e) {
                        throw new SourceException(
                                String.format("Abnormal reading file,%s", e.getMessage()), e);
                    }
                });

    }

    private String readNextWithKerberos() {
        readNum++;
        return value.endsWith(CRLF) ? value : value + CRLF;
    }

    @Override
    public boolean reachedEnd() {
        return KerberosLoginUtil.loginWithUGI(kerberosConfig).doAs(
                (PrivilegedAction<Boolean>) () -> {
                    try {
                        if (readNum > READ_LIMIT || !nextRecord()) {
                            reachEnd = true;
                        }
                        return reachEnd;
                    } catch (Exception e) {
                        throw new SourceException(
                                String.format("Abnormal reading file,%s", e.getMessage()), e);
                    }
                });
    }

    @Override
    public boolean close() throws IOException {
        if (bufferedReader != null) {
            bufferedReader.close();
        }
        if (reader != null) {
            reader.close();
        }
        if (fsIs != null) {
            fsIs.close();
        }
        if (reachEnd && fs != null) {
            fs.close();
        }
        return true;
    }

    private List<String> checkPath(String tableLocation) throws IOException {
        Path inputPath = new Path(tableLocation);
        List<String> pathList = Lists.newArrayList();

        //剔除隐藏系统文件
        FileStatus[] fsStatus = fs.listStatus(inputPath, path -> !path.getName().startsWith("."));

        checkSize(fsStatus, tableLocation);

        for (FileStatus status : fsStatus) {
            if (status.isDirectory()) {
                pathList.addAll(checkPath(status.getPath().toString()));
            } else {
                pathList.add(status.getPath().toString());
            }
        }
        return pathList;
    }

    private void checkSize(FileStatus[] fsStatus, String tableLocation) {
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
            //文件大小为0的时候不允许下载，需要重新调用configure接口
            throw new SourceException("path：" + tableLocation + " size = 0 ");
        }
    }
}
