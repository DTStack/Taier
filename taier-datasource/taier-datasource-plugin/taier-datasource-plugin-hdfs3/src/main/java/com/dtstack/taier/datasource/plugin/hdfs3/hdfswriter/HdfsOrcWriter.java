package com.dtstack.taier.datasource.plugin.hdfs3.hdfswriter;

import com.csvreader.CsvReader;
import com.dtstack.taier.datasource.api.dto.ColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.HDFSImportColumn;
import com.dtstack.taier.datasource.api.dto.HdfsWriterDTO;
import com.dtstack.taier.datasource.api.dto.source.Hdfs3SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.plugin.common.utils.DateUtil;
import com.dtstack.taier.datasource.plugin.common.utils.MathUtil;
import com.dtstack.taier.datasource.plugin.common.utils.TableUtil;
import com.dtstack.taier.datasource.plugin.hdfs3.OrcColumnTypeConverter;
import com.dtstack.taier.datasource.plugin.kerberos.core.hdfs.HadoopConfUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.common.type.HiveDecimal;
import org.apache.hadoop.hive.ql.io.orc.OrcFile;
import org.apache.hadoop.hive.ql.io.orc.Writer;
import org.apache.hadoop.hive.serde2.io.HiveDecimalWritable;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.typeinfo.StructTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * orc文件写入hdfs
 *
 * @author ：wangchuan
 * date：Created in 下午01:50 2020/8/11
 * company: www.dtstack.com
 */
public class HdfsOrcWriter {

    private static final Logger logger = LoggerFactory.getLogger(HdfsOrcWriter.class);

    /**
     * 10行刷新到远程一次
     **/
    public static final int FLUSH_LINE_NUM = 1000;

    /**
     * 按位置写入
     *
     * @param source        数据源信息
     * @param hdfsWriterDTO hdfs 写入配置类
     * @return 写入条数
     * @throws IOException io 异常
     */
    public static int writeByPos(ISourceDTO source, HdfsWriterDTO hdfsWriterDTO) throws IOException {

        TableUtil.dealColumnType(hdfsWriterDTO.getColumnsList(), OrcColumnTypeConverter::apply);
        Hdfs3SourceDTO hdfsSourceDTO = (Hdfs3SourceDTO) source;
        Boolean topLineIsTitle = hdfsWriterDTO.getTopLineIsTitle();
        int startLine = hdfsWriterDTO.getStartLine();
        // 首行是标题则内容从下一行开始
        if (BooleanUtils.isTrue(topLineIsTitle)) {
            startLine++;
        }

        Configuration conf = HadoopConfUtil.getHdfsConf(hdfsSourceDTO.getDefaultFS(), hdfsSourceDTO.getConfig(), hdfsSourceDTO.getKerberosConfig());
        String typeInfoStr = buildTypeInfo(hdfsWriterDTO.getColumnsList());

        TypeInfo typeInfo = TypeInfoUtils.getTypeInfoFromTypeString(typeInfoStr);
        ObjectInspector inspector = TypeInfoUtils.getStandardJavaObjectInspectorFromTypeInfo(typeInfo);
        OrcFile.WriterOptions options = OrcFile.writerOptions(conf).inspector(inspector);
        List<TypeInfo> structList = ((StructTypeInfo) typeInfo).getAllStructFieldTypeInfos();

        //FIXME 暂时根据uuid生成文件名称--需要改成和hive原始的名称eg: part-0000
        Path path = new Path(hdfsWriterDTO.getHdfsDirPath(), UUID.randomUUID().toString());
        Writer writer = OrcFile.createWriter(path, options);
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

                String[] lineArray = reader.getValues();
                int size = 0;
                if (lineArray.length > hdfsWriterDTO.getColumnsList().size()) {
                    size = hdfsWriterDTO.getColumnsList().size();
                } else {
                    size = lineArray.length;
                }

                List<Object> targetList = Lists.newArrayList();
                for (int i = 0; i < size; i++) {
                    try {
                        targetList.add(HdfsWriter.convertToTargetType(structList.get(i).getTypeName(), lineArray[i], hdfsWriterDTO.getKeyList().get(i).getDateFormat(), hdfsWriterDTO));
                    } catch (Exception e) {
                        throw new SourceException(String.format("convert error, dataType : %s , dataValue : %s",
                                structList.get(i).getTypeName(), lineArray[i]));
                    }
                }

                writer.addRow(targetList);
                if (writeLineNum % FLUSH_LINE_NUM == 0) {
                    writer.writeIntermediateFooter();
                }

                writeLineNum++;
            }
        } catch (Exception e) {
            throw new SourceException("The" + (currLineNum - (topLineIsTitle ? 1 : 0))
                    + " row of data is abnormal, please check , detail: " + e.getMessage(), e);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }

                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }

                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                logger.error("Close connection exception", e);
            }
        }

        return writeLineNum;
    }

    /**
     * 按名称写入
     *
     * @param source        数据源信息
     * @param hdfsWriterDTO hdfs 写入配置类
     * @return 写入条数
     * @throws IOException io 异常
     */
    public static int writeByName(ISourceDTO source, HdfsWriterDTO hdfsWriterDTO) throws IOException {

        TableUtil.dealColumnType(hdfsWriterDTO.getColumnsList(), OrcColumnTypeConverter::apply);
        Hdfs3SourceDTO hdfsSourceDTO = (Hdfs3SourceDTO) source;
        Configuration conf = HadoopConfUtil.getHdfsConf(hdfsSourceDTO.getDefaultFS(), hdfsSourceDTO.getConfig(), hdfsSourceDTO.getKerberosConfig());
        String typeInfoStr = buildTypeInfo(hdfsWriterDTO.getColumnsList());

        int startLine = hdfsWriterDTO.getStartLine();
        TypeInfo typeInfo = TypeInfoUtils.getTypeInfoFromTypeString(typeInfoStr);
        ObjectInspector inspector = TypeInfoUtils.getStandardJavaObjectInspectorFromTypeInfo(typeInfo);
        OrcFile.WriterOptions options = OrcFile.writerOptions(conf).inspector(inspector);

        //FIXME 暂时根据uuid生成文件名称--需要改成和hive原始的名称eg: part-0000
        Path path = new Path(hdfsWriterDTO.getHdfsDirPath(), UUID.randomUUID().toString());
        Writer writer = OrcFile.createWriter(path, options);

        int currLineNum = 0;
        int writeLineNum = 0;
        List<Integer> indexList = Lists.newArrayList();
        List<TypeInfo> structList = ((StructTypeInfo) typeInfo).getAllStructFieldTypeInfos();

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

                String[] columnArr = reader.getValues();
                // 首行为标题行
                if (currLineNum == (startLine - 1)) {
                    // 计算出需要使用的索引位置
                    for (HDFSImportColumn importColum : hdfsWriterDTO.getKeyList()) {
                        if (StringUtils.isBlank(importColum.getKey())) {
                            indexList.add(-1);
                        } else {
                            boolean isMatch = false;
                            for (int i = 0; i < columnArr.length; i++) {
                                String name = columnArr[i];
                                if (StringUtils.isNotEmpty(name)) {
                                    name = name.trim();
                                }
                                String key = importColum.getKey();
                                if (StringUtils.isNotEmpty(key)) {
                                    key = key.trim();
                                }
                                if (name.equals(key)) {
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

                Object[] recordArr = new Object[indexList.size()];

                for (int i = 0; i < indexList.size(); i++) {
                    Integer index = indexList.get(i);
                    String val = null;
                    //根据schema进行类型转换
                    if (index != -1 && index <= (columnArr.length - 1)) {
                        val = columnArr[index].trim();
                    }

                    TypeInfo struct = structList.get(i);
                    try {
                        Object record = HdfsWriter.convertToTargetType(struct.getTypeName(), val, hdfsWriterDTO.getKeyList().get(i).getDateFormat(), hdfsWriterDTO);
                        recordArr[i] = record;
                    } catch (Exception e) {
                        throw new SourceException(String.format("convert error, dataType : %s , dataValue : %s",
                                struct.getTypeName(), val));
                    }
                }

                writer.addRow(Arrays.asList(recordArr));
                if (writeLineNum % FLUSH_LINE_NUM == 0) {
                    writer.writeIntermediateFooter();
                }

                currLineNum++;
                writeLineNum++;
            }
        } catch (Exception e) {
            throw new SourceException("The" + currLineNum
                    + " row of data is abnormal, please check , detail: " + e.getMessage(), e);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }

                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }

                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                logger.error("Close connection exception", e);
            }
        }

        return writeLineNum;
    }

    /**
     * FIXME columnsList 必须是经过index排序过的
     * hive orc type 格式 struct<columnName:columnType,....>
     *
     * @param columnsList
     * @return
     */
    public static String buildTypeInfo(List<ColumnMetaDTO> columnsList) {
        StringBuffer sb = new StringBuffer("");
        for (ColumnMetaDTO columns : columnsList) {
            sb.append(columns.getKey())
                    .append(":")
                    .append(columns.getType())
                    .append(",");
        }

        String typeInfo = sb.toString();
        if (typeInfo.endsWith(",")) {
            typeInfo = typeInfo.substring(0, typeInfo.length() - 1);
        }

        typeInfo = "struct<" + typeInfo + ">";
        return typeInfo;
    }

    /**
     * 根据hive定义的列类型将数据转换为相应的格式
     *
     * @param typeName
     * @param val
     * @return
     */
    public static Object convertVal(String typeName, Object val) {
        Object record;
        switch (typeName) {
            case "char":
            case "varchar":
            case "string":
                record = MathUtil.getString(val);
                break;
            case "tinyint":
                record = MathUtil.getByte(val);
                break;
            case "smallint":
                record = MathUtil.getShort(val);
                break;
            case "bigint":
                record = MathUtil.getLongVal(val);
                break;
            case "boolean":
                record = MathUtil.getBoolean(val);
                break;
            case "int":
                record = MathUtil.getIntegerVal(val);
                break;
            case "float":
                record = MathUtil.getFloatVal(val);
                break;
            case "double":
                record = MathUtil.getDoubleVal(val);
                break;
            case "timestamp":
                record = DateUtil.getSqlTimeStampVal(val);
                break;
            case "date":
                record = DateUtil.getSqlDate(val);
                break;
            case "decimal":
                if (val == null) {
                    return null;
                }
                HiveDecimal hiveDecimal = HiveDecimal.create(new BigDecimal(val.toString()));
                record = new HiveDecimalWritable(hiveDecimal);
                break;
            default:
                record = val;
                break;
        }

        return record;
    }

}