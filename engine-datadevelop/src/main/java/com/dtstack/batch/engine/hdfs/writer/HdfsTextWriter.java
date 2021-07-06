package com.dtstack.batch.engine.hdfs.writer;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.domain.BatchTableColumn;
import com.dtstack.batch.engine.core.domain.ImportColum;
import com.dtstack.batch.engine.rdbms.common.HadoopConf;
import com.dtstack.batch.engine.rdbms.common.HdfsOperator;
import com.dtstack.dtcenter.loader.dto.ColumnMetaDTO;
import com.dtstack.dtcenter.loader.dto.HDFSImportColumn;
import com.dtstack.dtcenter.loader.dto.HdfsWriterDTO;
import com.dtstack.dtcenter.loader.dto.source.HdfsSourceDTO;
import com.dtstack.dtcenter.loader.enums.FileFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 处理导入hdfs text
 * FIXME 未做数据列的关联关系
 * Date: 2017/9/11
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class HdfsTextWriter {

    private static final Logger logger = LoggerFactory.getLogger(HdfsTextWriter.class);

    /**
     * 从文件中读取行,根据提供的分隔符号分割,再根据提供的hdfs分隔符合并,写入hdfs
     * ---需要根据column信息判断导入的数据是否符合要求
     *
     * @param hdfsDirPath
     * @param fromLineDelimiter
     * @param toLineDelimiter
     * @param fromFileName
     * @throws IOException
     */
    public static int writeByPos(Long dtuicTenantId, String hdfsDirPath, String fromLineDelimiter, String toLineDelimiter, String fromFileName,
                                 String oriCharSet, int startLine, boolean topLineIsTitle, List<BatchTableColumn> tableColumns, List<ImportColum> keyList) {
        Map<String, Object> conf = HadoopConf.getConfiguration(dtuicTenantId);
        Map<String, Object> kerberosConf = HadoopConf.getHadoopKerberosConf(dtuicTenantId);

        HdfsSourceDTO hdfsSourceDTO = HdfsSourceDTO.builder()
                .defaultFS(conf.getOrDefault("fs.defaultFS","").toString())
                .kerberosConfig(kerberosConf)
                .config(JSONObject.toJSONString(conf)).build();

        List<ColumnMetaDTO> columnMetaDTOS = tableColumns.stream().map(c -> {
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
            writerDTO.setToLineDelimiter(toLineDelimiter);
            writerDTO.setOriCharSet(oriCharSet);
            writerDTO.setStartLine(startLine);
            writerDTO.setTopLineIsTitle(topLineIsTitle);
            writerDTO.setFileFormat(FileFormat.TEXT.getVal());
            return HdfsOperator.getHdfsFileClient(conf, kerberosConf).writeByPos(hdfsSourceDTO,writerDTO);
        } catch (Exception e) {
            logger.error("",e);
        }
        return 0;
    }

    /**
     * 只有首行为标题行才可以使用名称匹配
     *
     * @param hdfsDirPath
     * @param fromLineDelimiter
     * @param toLineDelimiter
     * @param fromFileName
     * @param startLine
     * @param
     * @return
     * @throws IOException
     */
    public static int writeByName(Long dtuicTenantId, String hdfsDirPath, String fromLineDelimiter, String toLineDelimiter, String fromFileName,
                                  String oriCharSet, int startLine, List<ImportColum> keyList, List<BatchTableColumn> tableColumns) {
        Map<String, Object> conf = HadoopConf.getConfiguration(dtuicTenantId);
        Map<String, Object> kerberosConf = HadoopConf.getHadoopKerberosConf(dtuicTenantId);

        HdfsSourceDTO hdfsSourceDTO = HdfsSourceDTO.builder()
                .defaultFS(conf.getOrDefault("fs.defaultFS","").toString())
                .kerberosConfig(kerberosConf)
                .config(JSONObject.toJSONString(conf)).build();

        List<ColumnMetaDTO> columnMetaDTOS = tableColumns.stream().map(c -> {
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
            writerDTO.setToLineDelimiter(toLineDelimiter);
            writerDTO.setOriCharSet(oriCharSet);
            writerDTO.setStartLine(startLine);
            writerDTO.setTopLineIsTitle(true);
            writerDTO.setFileFormat(FileFormat.TEXT.getVal());
            return HdfsOperator.getHdfsFileClient(conf,kerberosConf).writeByPos(hdfsSourceDTO,writerDTO);
        } catch (Exception e) {
            logger.error("",e);
        }
        return 0;
    }

}
