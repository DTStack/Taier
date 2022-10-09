package com.dtstack.taier.datasource.plugin.hdfs3.fileMerge.core;

import com.dtstack.taier.datasource.plugin.hdfs3.fileMerge.ECompressType;
import com.dtstack.taier.datasource.plugin.hdfs3.fileMerge.meta.ParquetMetaData;
import com.dtstack.taier.datasource.plugin.hdfs3.util.FileSystemUtils;
import com.dtstack.taier.datasource.api.enums.FileFormat;
import com.dtstack.taier.datasource.api.exception.SourceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.column.ParquetProperties;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.format.CompressionCodec;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.example.ExampleParquetWriter;
import org.apache.parquet.hadoop.example.GroupReadSupport;
import org.apache.parquet.hadoop.metadata.BlockMetaData;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ROUND_HALF_UP;

@Slf4j
public class ParquetCombineServer extends CombineServer {

    private int index;

    private String mergedFileName;

    private ParquetMetaData metadata;

    public ParquetCombineServer() {
    }

    @Override
    protected void doCombine(ArrayList<FileStatus> combineFiles, Path mergedTempPath) throws IOException {
        init(combineFiles);

        List<Path> paths = combineFiles.stream().map(FileStatus::getPath).collect(Collectors.toList());

        ParquetWriter<Group> writer = null;

        long currentCount = 0L;
        try {
            for (Path path : paths) {
                log.info("start read {}",path);
                GroupReadSupport readSupport = new GroupReadSupport();
                ParquetReader.Builder<Group> builder = ParquetReader.builder(readSupport, path).withConf(configuration);
                ParquetReader<Group> reader = builder.build();
                Group line;
                while ((line = reader.read()) != null) {
                    if (writer == null) {
                        writer = getWriter(index++, mergedFileName);
                    }

                    currentCount++;
                    writer.write(line);

                    if (currentCount >= metadata.getLimitSize()) {
                        currentCount = 0L;
                        writer.close();
                        writer = null;
                    }
                }
            }
        } catch (Exception e) {
            throw new SourceException(String.format("combine file failed,errorMessage: %s", e.getMessage()), e);
        } finally {
            cleanSource(writer);
        }
    }

    @Override
    public String getFileSuffix() {
        return FileFormat.PARQUET.name();
    }


    /**
     * 初始化
     * 文件写入数量阈值 rowCountSplit
     * 合并文件名字前缀
     */
    private void init(ArrayList<FileStatus> combineFiles) throws IOException {

        FileStatus fileStatus = combineFiles.stream()
                .sorted(Comparator.comparing(FileStatus::getLen).reversed())
                .collect(Collectors.toList()).get(0);

        this.index = 0;
        this.mergedFileName = System.currentTimeMillis() + "";
        this.metadata = getFileMetaData(fileStatus);
    }


    private ParquetWriter<Group> getWriter(int id, String mergedFileName) throws IOException {
        String combineFileName = mergedTempPath.toString() + File.separator
                + mergedFileName + id + "." + getFileSuffix();

        if (metadata.isCompressed()) {
            combineFileName += metadata.geteCompressType().getSuffix();
        }

        ExampleParquetWriter.Builder builder = ExampleParquetWriter
                .builder(new Path(combineFileName))
                .withWriteMode(org.apache.parquet.hadoop.ParquetFileWriter.Mode.CREATE)
                .withWriterVersion(ParquetProperties.WriterVersion.PARQUET_1_0)
                .withRowGroupSize(org.apache.parquet.hadoop.ParquetWriter.DEFAULT_BLOCK_SIZE)
                .withConf(configuration)
                .withType(metadata.getSchema());

        if (metadata.isCompressed()) {
            builder.withCompressionCodec(CompressionCodecName.fromConf(metadata.geteCompressType().getType()));
        } else {
            builder.withCompressionCodec(CompressionCodecName.UNCOMPRESSED);
        }

        log.info("switch writer,the new path is {}", combineFileName);
        return builder.build();
    }

    private void cleanSource(ParquetWriter<Group> writer) {
        if (writer != null) {
            try {
                writer.close();
            } catch (Exception e) {
                log.warn("close inputStream and outStream failed" + e.getMessage());
            }
        }
    }

    protected ParquetMetaData getFileMetaData(FileStatus fileStatus) {
        ParquetMetaData metaData = new ParquetMetaData();
        try (ParquetFileReader parquetFileReader = ParquetFileReader.open(configuration, fileStatus.getPath())) {
            metaData.setSchema(parquetFileReader.getFileMetaData().getSchema());
            List<BlockMetaData> blocks = parquetFileReader.getFooter().getBlocks();
            if (CollectionUtils.isNotEmpty(blocks)) {
                BlockMetaData blockMetaData = blocks.get(0);
                CompressionCodec compressionCodec = CompressionCodec.valueOf(blockMetaData.getColumns().get(0).getCodec().toString());

                //小文件的数据大小
                long compressedSize = blockMetaData.getCompressedSize();
                //小文件的行数
                long rowSize = blockMetaData.getRowCount();
                BigDecimal limitRows = new BigDecimal(rowSize + "").divide(new BigDecimal(compressedSize + ""), 8, ROUND_HALF_UP);
                metaData.seteCompressType(ECompressType.getByTypeAndFileType(compressionCodec.name(), "parquet"));
                metaData.setCompressed(metaData.geteCompressType() != null && !compressionCodec.equals(CompressionCodec.UNCOMPRESSED));
                metaData.setLimitSize(new BigDecimal(maxCombinedFileSize + "").multiply(limitRows).longValue());
            }
        } catch (IOException e) {
            throw new SourceException(String.format("ParquetFileReader open error,%s", e.getMessage()), e);
        }
        log.info("FileMeatData info   {}", metaData);
        return metaData;

    }

    @Override
    public String toString() {
        return "ParquetCombineServer{" +
                ", metadata=" + metadata +
                ", sourcePath=" + sourcePath +
                ", mergedTempPath=" + mergedTempPath +
                ", configuration=" + FileSystemUtils.printConfiguration(configuration) +
                ", needCombineFileSizeLimit=" + needCombineFileSizeLimit +
                ", maxCombinedFileSize=" + maxCombinedFileSize +
                '}';
    }
}
