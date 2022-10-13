package com.dtstack.taier.datasource.plugin.hdfs3.fileMerge.core;

import com.dtstack.taier.datasource.api.enums.FileFormat;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.plugin.hdfs3.fileMerge.ECompressType;
import com.dtstack.taier.datasource.plugin.hdfs3.fileMerge.meta.FileMetaData;
import com.dtstack.taier.datasource.plugin.hdfs3.util.FileSystemUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Slf4j
public class TextCombineServer extends CombineServer {

    private int index;

    private String mergedFileName;

    private FileMetaData fileMetaData;

    public TextCombineServer() {
    }

    @Override
    protected void doCombine(ArrayList<FileStatus> combineFiles, Path mergedTempPath) throws IOException {
        init(combineFiles);

        //文件大小是128M

        //定义缓冲区大小4096
        byte[] buf = new byte[4096];
        OutputStream out = null;
        InputStream in = null;
        try {
            long currentSize = 0L;
            //todo 每次读取判断读取的字节数是否到128M个字节 到了就重新新建一个out流
            for (FileStatus fileStatus : combineFiles) {
                // 打开输入流
                in = getInputStream(fileStatus.getPath());
                log.info("start read {}", fileStatus.getPath());
                while (true) {
                    int read = in.read(buf, 0, 4096);
                    if (read == -1) {
                        IOUtils.closeStream(in);
                        //只有当前小文件读取完毕 才进行文件切割判断
                        if (currentSize >= fileMetaData.getLimitSize()) {
                            IOUtils.closeStream(out);
                            out = null;
                            currentSize = 0;
                        }
                        break;
                    }
                    currentSize += read;

                    if (out == null) {
                        out = getOutStream(index++, mergedFileName);
                    }

                    out.write(buf, 0, read);
                }
            }

        } catch (Exception e) {
            throw new SourceException(String.format("combine file failed,errorMessage :%s ", e.getMessage()), e);
        } finally {
            cleanSource(in, out);
        }

    }

    @Override
    public String getFileSuffix() {
        return FileFormat.TEXT.name();
    }

    /**
     * 初始化
     * 合并文件名字前缀
     */
    private void init(List<FileStatus> combineFiles) throws IOException {
        this.index = 0;
        this.mergedFileName = System.currentTimeMillis() + "";
        FileStatus fileStatus = combineFiles.stream()
                .sorted(Comparator.comparing(FileStatus::getLen).reversed())
                .collect(Collectors.toList()).get(0);

        FileMetaData metaData = new FileMetaData();
        if (FileSystemUtils.isBzip2(fileStatus.getPath(), fs)) {
            metaData.setCompressed(true);
            metaData.seteCompressType(ECompressType.TEXT_BZIP2);
        } else if (FileSystemUtils.isGzip(fileStatus.getPath(), fs)) {
            metaData.setCompressed(true);
            metaData.seteCompressType(ECompressType.TEXT_GZIP);
        } else {
            metaData.setCompressed(false);
            metaData.seteCompressType(ECompressType.TEXT_NONE);
        }
        //metaData.geteCompressType().getDeviation()  这个压缩比 是将压缩后的文件大小/压缩前大小得到的值
        metaData.setLimitSize(new BigDecimal(maxCombinedFileSize + "").divide(new BigDecimal(metaData.geteCompressType().getDeviation() + ""), 1, RoundingMode.HALF_UP).longValue());

        this.fileMetaData = metaData;
        log.info("FileMeatData info  {}", metaData);
    }

    private InputStream getInputStream(Path path) throws IOException {
        //打开文件流
        if (FileSystemUtils.isBzip2(path, fs)) {
            return new BZip2CompressorInputStream(fs.open(path));
        } else if (FileSystemUtils.isGzip(path, fs)) {
            return new GZIPInputStream(fs.open(path));
        } else {
            return fs.open(path);
        }
    }

    private OutputStream getOutStream(int index, String mergedFileName) throws IOException {
        String combineFileName = mergedTempPath.toString() + File.separator
                + mergedFileName + index + "." + getFileSuffix();
        if (fileMetaData.isCompressed()) {
            combineFileName += fileMetaData.geteCompressType().getSuffix();
        }

        log.info("switch writer,the new path is  {}", combineFileName);

        if (fileMetaData.isCompressed()) {
            if (fileMetaData.geteCompressType().equals(ECompressType.TEXT_BZIP2)) {
                return new BZip2CompressorOutputStream(fs.create(new Path(combineFileName)));
            } else if (fileMetaData.geteCompressType().equals(ECompressType.TEXT_GZIP)) {
                return new GZIPOutputStream(fs.create(new Path(combineFileName)));
            } else {
                throw new SourceException("not support " + fileMetaData.geteCompressType().getType() + " compress");
            }
        } else {
            return fs.create(new Path(combineFileName));
        }

    }

    private void cleanSource(InputStream in, OutputStream out) {
        try {
            if (in != null) {
                IOUtils.closeStream(in);
            }
            if (out != null) {
                IOUtils.closeStream(out);
            }
        } catch (Exception e) {
            log.warn("close inputStream and outStream failed" + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "TextCombineServer{" +
                ", fileMetaData=" + fileMetaData +
                ", sourcePath=" + sourcePath +
                ", mergedTempPath=" + mergedTempPath +
                ", configuration=" + FileSystemUtils.printConfiguration(configuration) +
                ", needCombineFileSizeLimit=" + needCombineFileSizeLimit +
                ", maxCombinedFileSize=" + maxCombinedFileSize +
                '}';
    }
}
