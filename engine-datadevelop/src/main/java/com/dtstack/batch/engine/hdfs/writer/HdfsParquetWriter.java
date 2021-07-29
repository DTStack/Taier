package com.dtstack.batch.engine.hdfs.writer;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.domain.BatchTableColumn;
import com.dtstack.batch.engine.core.domain.ImportColum;
import com.dtstack.batch.engine.rdbms.common.HadoopConf;
import com.dtstack.batch.engine.rdbms.common.HdfsOperator;
import com.dtstack.batch.engine.rdbms.common.enums.StoredType;
import com.dtstack.dtcenter.loader.dto.ColumnMetaDTO;
import com.dtstack.dtcenter.loader.dto.HDFSImportColumn;
import com.dtstack.dtcenter.loader.dto.HdfsWriterDTO;
import com.dtstack.dtcenter.loader.dto.source.HdfsSourceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 导入数据到hdfs parquet格式
 * @author jiangbo
 */
public class HdfsParquetWriter {

    private static final Logger logger = LoggerFactory.getLogger(HdfsParquetWriter.class);

    /**
     * 按位置写入
     * @return
     */
    public static int writeByPos(final Long dtuicTenantId, final String hdfsDirPath, final String fromLineDelimiter, final String fromFileName,
                                 final String oriCharSet, int startLine, final boolean topLineIsTitle, final List<BatchTableColumn> columnsList, final List<ImportColum> keyList) throws IOException{
        Map<String, Object> conf = HadoopConf.getConfiguration(dtuicTenantId);
        Map<String, Object> kerberosConf = HadoopConf.getHadoopKerberosConf(dtuicTenantId);

        HdfsSourceDTO hdfsSourceDTO = HdfsSourceDTO.builder()
                .defaultFS(conf.getOrDefault("fs.defaultFS","").toString())
                .kerberosConfig(kerberosConf)
                .config(JSONObject.toJSONString(conf)).build();

        List<ColumnMetaDTO> columnMetaDTOS = columnsList.stream().map(c -> {
            ColumnMetaDTO metaDTO = new ColumnMetaDTO();
            metaDTO.setKey(c.getColumnName());
            metaDTO.setType(c.getColumnType());
            metaDTO.setComment(c.getColumnDesc());
            return metaDTO;
        }).collect(Collectors.toList());

        List<HDFSImportColumn> hdfsKeyList = keyList.stream().map(c -> {
            HDFSImportColumn column = new HDFSImportColumn();
            column.setKey(c.getKey());
            column.setDateFormat(c.getDateFormat());
            column.setFormat(c.getFormat());
            return column;
        }).collect(Collectors.toList());
        try {
            HdfsWriterDTO writerDTO = new HdfsWriterDTO();
            writerDTO.setColumnsList(columnMetaDTOS);
            writerDTO.setKeyList(hdfsKeyList);
            writerDTO.setFromFileName(fromFileName);
            writerDTO.setHdfsDirPath(hdfsDirPath);
            writerDTO.setFromLineDelimiter(fromLineDelimiter);
            writerDTO.setToLineDelimiter(null);
            writerDTO.setOriCharSet(oriCharSet);
            writerDTO.setStartLine(startLine);
            writerDTO.setTopLineIsTitle(topLineIsTitle);
            writerDTO.setFileFormat(StoredType.PARQUET.getValue());
            return HdfsOperator.getHdfsFileClient(conf, kerberosConf).writeByPos(hdfsSourceDTO,writerDTO);
        } catch (Exception e) {
            logger.error("",e);
        }
        return 0;
    }

    /**
     * 按名称写入
     * @return
     */
    public static int writeByName(final Long dtuicTenantId, final String hdfsDirPath, final String fromLineDelimiter, final String fromFileName,
                                  final String oriCharSet, final int startLine, final boolean topLineIsTitle, final List<BatchTableColumn> columnsList,
                                  final List<ImportColum> keyList){

        Map<String, Object> conf = HadoopConf.getConfiguration(dtuicTenantId);
        Map<String, Object> kerberosConf = HadoopConf.getHadoopKerberosConf(dtuicTenantId);

        HdfsSourceDTO hdfsSourceDTO = HdfsSourceDTO.builder()
                .defaultFS(conf.getOrDefault("fs.defaultFS","").toString())
                .kerberosConfig(kerberosConf)
                .config(JSONObject.toJSONString(conf)).build();

        List<ColumnMetaDTO> columnMetaDTOS = columnsList.stream().map(c -> {
            ColumnMetaDTO metaDTO = new ColumnMetaDTO();
            metaDTO.setKey(c.getColumnName());
            metaDTO.setType(c.getColumnType());
            metaDTO.setComment(c.getColumnDesc());
            return metaDTO;
        }).collect(Collectors.toList());

        List<HDFSImportColumn> hdfsKeyList = keyList.stream().map(c -> {
            HDFSImportColumn column = new HDFSImportColumn();
            column.setKey(c.getKey());
            column.setDateFormat(c.getDateFormat());
            column.setFormat(c.getFormat());
            return column;
        }).collect(Collectors.toList());
        try {
            HdfsWriterDTO writerDTO = new HdfsWriterDTO();
            writerDTO.setColumnsList(columnMetaDTOS);
            writerDTO.setKeyList(hdfsKeyList);
            writerDTO.setFromFileName(fromFileName);
            writerDTO.setHdfsDirPath(hdfsDirPath);
            writerDTO.setFromLineDelimiter(fromLineDelimiter);
            writerDTO.setToLineDelimiter(null);
            writerDTO.setOriCharSet(oriCharSet);
            writerDTO.setStartLine(startLine);
            writerDTO.setTopLineIsTitle(topLineIsTitle);
            writerDTO.setFileFormat(StoredType.PARQUET.getValue());
            return HdfsOperator.getHdfsFileClient(conf, kerberosConf).writeByName(hdfsSourceDTO,writerDTO);
        } catch (Exception e) {
            logger.error("",e);
        }
        return 0;
    }
}
