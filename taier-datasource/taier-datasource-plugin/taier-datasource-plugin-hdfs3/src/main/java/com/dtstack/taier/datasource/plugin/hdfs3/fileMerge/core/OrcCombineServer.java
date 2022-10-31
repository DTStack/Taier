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

package com.dtstack.taier.datasource.plugin.hdfs3.fileMerge.core;

import com.dtstack.taier.datasource.plugin.hdfs3.fileMerge.ECompressType;
import com.dtstack.taier.datasource.plugin.hdfs3.fileMerge.meta.OrcMetaData;
import com.dtstack.taier.datasource.plugin.hdfs3.util.FileSystemUtils;
import com.dtstack.taier.datasource.api.enums.FileFormat;
import com.dtstack.taier.datasource.api.exception.SourceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.exec.vector.VectorizedRowBatch;
import org.apache.orc.CompressionKind;
import org.apache.orc.OrcFile;
import org.apache.orc.Reader;
import org.apache.orc.RecordReader;
import org.apache.orc.TypeDescription;
import org.apache.orc.Writer;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ROUND_HALF_UP;

@Slf4j
public class OrcCombineServer extends CombineServer {

    //文件编号
    private int index;

    private String mergedFileName;

    //orc文件的元信息
    private OrcMetaData metaData;

    public OrcCombineServer() {
    }

    @Override
    protected void doCombine(ArrayList<FileStatus> combineFiles, Path mergedTempPath) throws IOException {
        init(combineFiles);

        Writer writer = null;
        long currentCount = 0L;

        try {
            for (FileStatus fileStatus : combineFiles) {
                log.info("start read {}",fileStatus.getPath());
                Reader reader = getReader(fileStatus);

                RecordReader rows = reader.rows();
                TypeDescription schema = reader.getSchema();
                VectorizedRowBatch batch = schema.createRowBatch();

                while (rows.nextBatch(batch)) {
                    if (writer == null) {
                        writer = getWriter(index++, mergedFileName);
                    }
                    currentCount += batch.size;
                    if (batch.size != 0) {
                        writer.addRowBatch(batch);
                        batch.reset();
                    }
                    //如果达到了写入数量阈值 此文件就不会再进行写入
                    if (currentCount >= metaData.getLimitSize()) {
                        currentCount = 0L;
                        writer.close();
                        writer = null;
                    }
                }
                rows.close();
            }
        } catch (Exception e) {
            throw new SourceException(String.format("combine file failed,errorMessage :%s", e.getMessage()), e);
        } finally {
            cleanPackage(writer);
        }
    }


    @Override
    public String getFileSuffix() {
        return FileFormat.ORC.name();
    }

    /**
     * 初始化
     * 文件写入数量阈值 rowCountSplit
     * 合并文件名字前缀
     *
     * @param combineFiles
     * @throws IOException
     */
    private void init(ArrayList<FileStatus> combineFiles) throws IOException {

        FileStatus fileStatus = combineFiles.stream()
                .sorted(Comparator.comparing(FileStatus::getLen).reversed())
                .collect(Collectors.toList()).get(0);

        this.index = 0;
        this.mergedFileName = System.currentTimeMillis() + "";

        metaData = getFileMetaData(fileStatus);
    }


    private Writer getWriter(int index, String radonPath) throws IOException {

        String combineFileName = mergedTempPath.toString() + File.separator
                + radonPath + index + "." + getFileSuffix();

        if (metaData.isCompressed()) {
            combineFileName += metaData.geteCompressType().getSuffix();
        }

        try {
            OrcFile.WriterOptions writerOptions = OrcFile.writerOptions(configuration)
                    .fileSystem(fs)
                    //Writer 操作单元，stripe 内容先写入内存，内存满了之后Flush到磁盘
                    .stripeSize(64L * 1024 * 1024)
                    .setSchema(metaData.getSchema());

            //设置压缩格式
            if (metaData.isCompressed()) {
                writerOptions.compress(CompressionKind.valueOf(metaData.geteCompressType().getType()));
            } else {
                writerOptions.compress(CompressionKind.NONE);
            }

            log.info("switch writer,the new path is {},compress is {}", combineFileName, writerOptions.getCompress());
            return OrcFile.createWriter(new Path(combineFileName), writerOptions);

        } catch (IOException e) {
            throw new SourceException(String.format("switch writer failed,%s", e.getMessage()), e);
        }
    }


    private Reader getReader(FileStatus fileStatus) throws IOException {
        return OrcFile.createReader(fileStatus.getPath(),
                OrcFile.readerOptions(configuration));
    }

    private void cleanPackage(Writer writer) {
        if (writer != null) {
            try {
                writer.close();
            } catch (Exception e) {
                log.warn("close inputStream and outStream failed" + e.getMessage());
            }
        }
    }

    protected OrcMetaData getFileMetaData(FileStatus fileStatus) throws IOException {
        OrcMetaData orcMetaData = new OrcMetaData();
        Reader reader = getReader(fileStatus);
        CompressionKind compressionKind = reader.getCompressionKind();
        orcMetaData.setSchema(reader.getSchema());
        orcMetaData.seteCompressType(ECompressType.getByTypeAndFileType(compressionKind.name(), "orc"));
        orcMetaData.setCompressed(orcMetaData.geteCompressType() != null && !compressionKind.equals(CompressionKind.NONE));

        long rowSize = reader.getNumberOfRows();
        BigDecimal divide = new BigDecimal(rowSize + "").divide(new BigDecimal(fileStatus.getLen() + ""), 8, ROUND_HALF_UP);
        orcMetaData.setLimitSize(new BigDecimal(maxCombinedFileSize + "").multiply(divide).longValue());

        log.info("FileMeatData info {}", orcMetaData);
        return orcMetaData;
    }

    @Override
    public String toString() {
        return "OrcCombineServer{" +
                ", metaData=" + metaData +
                ", sourcePath=" + sourcePath +
                ", mergedTempPath=" + mergedTempPath +
                ", configuration=" + FileSystemUtils.printConfiguration(configuration) +
                ", needCombineFileSizeLimit=" + needCombineFileSizeLimit +
                ", maxCombinedFileSize=" + maxCombinedFileSize +
                '}';
    }

}
