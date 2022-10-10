package com.dtstack.taier.datasource.plugin.hdfs3_cdp.fileMerge.core;

import com.dtstack.taier.datasource.plugin.hdfs3_cdp.util.FileSystemUtils;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@Slf4j
public abstract class CombineServer {

    /**
     * 需要合并的文件目录 源路径
     */
    protected Path sourcePath;

    /**
     * 合并的临时目录
     */
    protected Path mergedTempPath;

    /**
     * hadoop配置
     */
    protected Configuration configuration;

    /**
     * 需要合并的文件的阈值
     * 只有低于此大小的文件才会被合并
     */
    protected Long needCombineFileSizeLimit;


    /**
     * 合并后的文件的最大值
     */
    protected long maxCombinedFileSize;

    protected FileSystem fs;

    public CombineServer() {
    }

    public void combine() throws IOException {

        //源目录下的文件夹
        ArrayList<FileStatus> directors = new ArrayList<>(1024);
        //源目录下超过合并大小的大文件
        ArrayList<FileStatus> copyFiles = new ArrayList<>(1024);
        //源目录下需要合并的小文件
        ArrayList<FileStatus> combineFiles = new ArrayList<>(1024);
        //对文件夹副本下的文件/文件夹进行数据划分
        splitFile(sourcePath, directors, copyFiles, combineFiles);

        //源目录下假如存在文件夹 直接移动到 mergedTempPath目录下
        for (FileStatus director : directors) {
            FileSystemUtils.backupDirector(director.getPath(), new Path(mergedTempPath.toUri().getPath() + File.separator + director.getPath().getName()), fs, configuration);
        }

        //源目录下超过阈值的大文件直接进行复制 不需要合并
        for (FileStatus copyFile : copyFiles) {
            FileSystemUtils.backupFile(copyFile.getPath(), mergedTempPath, fs, configuration);
        }

        if (CollectionUtils.isEmpty(combineFiles)) {
            log.info("There are no small files to be merged in the source directory");
            return;
        }

        //小文件合并
        doCombine(combineFiles, mergedTempPath);
        log.info("merge {} to {} successful ", sourcePath, mergedTempPath);
    }

    /**
     * 小文件合并操作
     */
    protected abstract void doCombine(ArrayList<FileStatus> combineFiles, Path mergedTempPath) throws IOException;

    /**
     * 获取对应文件类型的后缀名
     */
    protected abstract String getFileSuffix();

    private void splitFile(Path sourcePath, ArrayList<FileStatus> directors, ArrayList<FileStatus> copyFiles, ArrayList<FileStatus> combineFiles) {

        //合并文件  移动文件 合并文件 移动文件夹
        FileStatus[] fileStatuses;
        try {
            fileStatuses = fs.listStatus(sourcePath);
        } catch (IOException e) {
            throw new SourceException(String.format("get path [" + sourcePath + "] info error,%s", e.getMessage()), e);
        }
        for (FileStatus fileInfo : fileStatuses) {
            if (fileInfo.isDirectory()) {
                directors.add(fileInfo);
            } else if (fileInfo.getLen() >= needCombineFileSizeLimit) {
                copyFiles.add(fileInfo);
            } else if (fileInfo.getLen() > 0L) {
                combineFiles.add(fileInfo);
            }
        }

        if (log.isDebugEnabled()) {
            Gson gson = new Gson();
            log.debug("directors on {} has {}", sourcePath, gson.toJson(directors));
            log.debug("bigFile on {} has {}", sourcePath, gson.toJson(copyFiles));
            log.debug("needMergedFile on {} has {}", sourcePath, gson.toJson(combineFiles));
        }
    }

    public void setSourcePath(Path sourcePath) {
        this.sourcePath = sourcePath;
    }

    public void setMergedTempPath(Path mergedTempPath) {
        this.mergedTempPath = mergedTempPath;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public void setNeedCombineFileSizeLimit(Long needCombineFileSizeLimit) {
        this.needCombineFileSizeLimit = needCombineFileSizeLimit;
    }

    public void setMaxCombinedFileSize(long maxCombinedFileSize) {
        this.maxCombinedFileSize = maxCombinedFileSize;
    }

    public void setFs(FileSystem fs) {
        this.fs = fs;
    }


    @Override
    public String toString() {
        return "CombineServer{" +
                "sourcePath=" + sourcePath +
                ", mergedTempPath=" + mergedTempPath +
                ", configuration=" + FileSystemUtils.printConfiguration(configuration) +
                ", needCombineFileSizeLimit=" + needCombineFileSizeLimit +
                ", maxCombinedFileSize=" + maxCombinedFileSize +
                '}';
    }
}
