package com.dtstack.taier.datasource.plugin.hdfs.hdfswriter;

import com.csvreader.CsvReader;
import com.dtstack.taier.datasource.plugin.kerberos.core.hdfs.HadoopConfUtil;
import com.dtstack.taier.datasource.api.dto.ColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.HDFSImportColumn;
import com.dtstack.taier.datasource.api.dto.HdfsWriterDTO;
import com.dtstack.taier.datasource.api.dto.source.HdfsSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.google.common.collect.Lists;
import org.apache.commons.compress.utils.Charsets;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 文本文件写入hdfs
 *
 * @author ：wangchuan
 * date：Created in 下午01:40 2020/8/11
 * company: www.dtstack.com
 */

public class HdfsTextWriter {

    private static final Logger logger = LoggerFactory.getLogger(HdfsTextWriter.class);

    /**
     * 换行符
     */
    private static final int NEWLINE = 10;

    /**
     * 从文件中读取行,根据提供的分隔符号分割,再根据提供的hdfs分隔符合并,写入hdfs
     * ---需要根据column信息判断导入的数据是否符合要求
     *
     * @param source        数据源信息
     * @param hdfsWriterDTO hdfs 写入配置配
     * @return 写入数据条数
     * @throws IOException io 异常
     */
    public static int writeByPos(ISourceDTO source, HdfsWriterDTO hdfsWriterDTO) throws IOException {
        HdfsSourceDTO hdfsSourceDTO = (HdfsSourceDTO) source;
        int startLine = hdfsWriterDTO.getStartLine();
        //首行是标题则内容从下一行开始
        if (BooleanUtils.isTrue(hdfsWriterDTO.getTopLineIsTitle())) {
            startLine++;
        }

        //FIXME 暂时根据uuid生成文件名称--需要改成和hive原始的名称eg: part-0000
        final String hdfsPath = hdfsWriterDTO.getHdfsDirPath() + "/" + UUID.randomUUID();
        final boolean overwrite = false;

        final Configuration conf = HadoopConfUtil.getHdfsConf(hdfsSourceDTO.getDefaultFS(), hdfsSourceDTO.getConfig(), hdfsSourceDTO.getKerberosConfig());
        final FileSystem fs = FileSystem.get(conf);
        final Path p = new Path(hdfsPath);
        final OutputStream stream = fs.create(p, overwrite);

        int writeLineNum = 0;
        int currLineNum = 0;

        InputStreamReader inputStreamReader = null;
        CsvReader reader = null;
        try {
            inputStreamReader = HdfsWriter.getReader(hdfsWriterDTO.getFromFileName(), hdfsWriterDTO.getOriCharSet());
            reader = new CsvReader(inputStreamReader, HdfsWriter.getDelim(hdfsWriterDTO.getFromLineDelimiter()));
            while (reader.readRecord()) {
                currLineNum++;
                if (currLineNum < startLine) {
                    continue;
                }

                final String[] lineArray = reader.getValues();
                final String recordStr = transformColumn(hdfsWriterDTO.getColumnsList(), hdfsWriterDTO.getKeyList(), lineArray, hdfsWriterDTO.getToLineDelimiter(), hdfsWriterDTO);

                final byte[] bytes = recordStr.getBytes(Charsets.UTF_8);
                stream.write(bytes);
                stream.write(HdfsTextWriter.NEWLINE);
                stream.flush();
                writeLineNum++;
            }
        } catch (final Exception e) {
            throw new SourceException(String.format(
                    "the %s row data is abnormal, please check, data import failed, detail: %s", currLineNum, e.getMessage()), e);
        } finally {
            stream.close();

            if (inputStreamReader != null) {
                inputStreamReader.close();
            }

            if (reader != null) {
                reader.close();
            }
        }

        return writeLineNum;
    }

    /**
     * 只有首行为标题行才可以使用名称匹配
     *
     * @param source        数据源信息
     * @param hdfsWriterDTO hdfs 写入配置类
     * @return 写入数据条数
     * @throws IOException io异常
     */
    public static int writeByName(ISourceDTO source, HdfsWriterDTO hdfsWriterDTO) throws IOException {
        HdfsSourceDTO hdfsSourceDTO = (HdfsSourceDTO) source;
        final boolean overwrite = false;
        final Configuration conf = HadoopConfUtil.getHdfsConf(hdfsSourceDTO.getDefaultFS(), hdfsSourceDTO.getConfig(), hdfsSourceDTO.getKerberosConfig());
        final FileSystem fs = FileSystem.get(conf);
        final String hdfsPath = hdfsWriterDTO.getHdfsDirPath() + "/" + UUID.randomUUID();
        final Path p = new Path(hdfsPath);
        final OutputStream stream = fs.create(p, overwrite);

        int currLineNum = 0;
        int writeLineNum = 0;
        int startLine = hdfsWriterDTO.getStartLine();
        final List<Integer> indexList = Lists.newArrayList();

        InputStreamReader inputStreamReader = null;
        CsvReader reader = null;
        try {
            inputStreamReader = HdfsWriter.getReader(hdfsWriterDTO.getFromFileName(), hdfsWriterDTO.getOriCharSet());
            reader = new CsvReader(inputStreamReader, HdfsWriter.getDelim(hdfsWriterDTO.getFromLineDelimiter()));
            while (reader.readRecord()) {
                if (currLineNum < (startLine - 1)) {
                    currLineNum++;
                    continue;
                }

                final String[] columnArr = reader.getValues();
                // 首行为标题行
                if (currLineNum == (startLine - 1)) {
                    // 计算出需要使用的索引位置
                    for (final HDFSImportColumn importColum : hdfsWriterDTO.getKeyList()) {
                        if (StringUtils.isBlank(importColum.getKey())) {
                            indexList.add(-1);
                        } else {
                            boolean isMatch = false;
                            for (int i = 0; i < columnArr.length; i++) {
                                final String name = columnArr[i];
                                if (name.equals(importColum.getKey())) {
                                    indexList.add(i);
                                    isMatch = true;
                                    break;
                                }
                            }

                            if (!isMatch) {
                                indexList.add(-1);
                            }
                        }
                    }

                    currLineNum++;
                    continue;
                }

                final StringBuffer sb = new StringBuffer();

                for (int i = 0; i < indexList.size(); i++) {
                    final Integer index = indexList.get(i);
                    if (index == -1) {
                        sb.append(hdfsWriterDTO.getToLineDelimiter());
                    } else if (index > (columnArr.length - 1)) {
                        sb.append(hdfsWriterDTO.getToLineDelimiter());
                    } else {
                        final ColumnMetaDTO columnMeta = hdfsWriterDTO.getColumnsList().get(i);
                        Object targetObj;
                        try {
                            targetObj = HdfsWriter.convertToTargetType(columnMeta.getType(), columnArr[index], hdfsWriterDTO.getKeyList().get(i).getDateFormat(), hdfsWriterDTO);
                        } catch (Exception e) {
                            throw new SourceException(String.format(
                                    "convert error,dataValue: %s,dataType: %s", columnMeta.getType(), columnArr[index]), e);
                        }
                        if (Objects.nonNull(targetObj)) {
                            sb.append(targetObj.toString());
                        }
                        sb.append(hdfsWriterDTO.getToLineDelimiter());
                    }
                }

                String recordStr = sb.toString();
                if (recordStr.endsWith(hdfsWriterDTO.getToLineDelimiter())) {
                    recordStr = recordStr.substring(0, recordStr.length() - 1);
                }

                final byte[] bytes = recordStr.getBytes(Charsets.UTF_8);
                stream.write(bytes);
                stream.write(NEWLINE);
                stream.flush();
                currLineNum++;
                writeLineNum++;
            }
        } catch (final Exception e) {
            throw new SourceException(String.format(
                    "The %s row data is abnormal, please check, data import failed, detail: %s", currLineNum, e.getMessage()), e);
        } finally {
            stream.close();

            if (inputStreamReader != null) {
                inputStreamReader.close();
            }

            if (reader != null) {
                reader.close();
            }
        }
        return writeLineNum;
    }

    private static String transformColumn(final List<ColumnMetaDTO> tableColumns, final List<HDFSImportColumn> keyList, final String[] columnValArr, final String delimiter, HdfsWriterDTO hdfsWriterDTO) throws ParseException {

        if (columnValArr == null) {
            throw new SourceException("Record not be empty");
        }

        final int length = columnValArr.length > tableColumns.size() ? tableColumns.size() : columnValArr.length;

        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            final String columnVal = columnValArr[i];
            final ColumnMetaDTO tableColumn = tableColumns.get(i);
            final String columnType = tableColumn.getType();
            Object targetVal;
            try {
                targetVal = HdfsWriter.convertToTargetType(columnType, columnVal, keyList.get(i).getDateFormat(), hdfsWriterDTO);
            }catch (Exception e){
                throw new SourceException(String.format("convert error, dataType : %s , dataValue : %s", columnType, columnVal));
            }
            if (targetVal != null) {
                sb.append(targetVal);
            }

            if (i != (length - 1)) {
                sb.append(delimiter);
            }
        }

        return sb.toString();
    }
}
