package com.dtstack.taier.datasource.plugin.hdfs.hdfswriter;


import com.csvreader.CsvReader;
import com.dtstack.taier.datasource.api.dto.ColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.HDFSImportColumn;
import com.dtstack.taier.datasource.api.dto.HdfsWriterDTO;
import com.dtstack.taier.datasource.api.dto.source.HdfsSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.plugin.common.utils.ReflectUtil;
import com.dtstack.taier.datasource.plugin.kerberos.core.hdfs.HadoopConfUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.common.type.HiveDecimal;
import org.apache.hadoop.hive.ql.io.parquet.serde.ParquetHiveSerDe;
import org.apache.hadoop.hive.ql.io.parquet.timestamp.NanoTimeUtils;
import org.apache.hadoop.hive.serde2.io.DateWritable;
import org.apache.parquet.column.ParquetProperties;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.example.data.simple.SimpleGroup;
import org.apache.parquet.hadoop.ParquetFileWriter;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.example.ExampleParquetWriter;
import org.apache.parquet.hadoop.example.GroupWriteSupport;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.parquet.io.api.Binary;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.OriginalType;
import org.apache.parquet.schema.PrimitiveType;
import org.apache.parquet.schema.Types;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * parquet文件写入hdfs
 *
 * @author ：wangchuan
 * date：Created in 下午01:40 2020/8/11
 * company: www.dtstack.com
 */
public class HdfsParquetWriter {

    private static final String KEY_PRECISION = "precision";

    private static final String KEY_SCALE = "scale";

    /**
     * 按位置写入
     *
     * @param source        数据源信息
     * @param hdfsWriterDTO hdfs 写入配置类
     * @return 写入条数
     * @throws IOException io 异常
     */
    public static int writeByPos(ISourceDTO source, HdfsWriterDTO hdfsWriterDTO) throws IOException {
        HdfsSourceDTO hdfsSourceDTO = (HdfsSourceDTO) source;
        int startLine = hdfsWriterDTO.getStartLine();
        //首行是标题则内容从下一行开始
        if (BooleanUtils.isTrue(hdfsWriterDTO.getTopLineIsTitle())) {
            startLine++;
        }

        MessageType schema = buildSchema(hdfsWriterDTO.getColumnsList());
        ParquetWriter<Group> writer = getWriter(hdfsSourceDTO, hdfsWriterDTO.getHdfsDirPath(), hdfsWriterDTO.getColumnsList());
        Map<String, Map<String, Integer>> decimalColInfo = getDecimalColInfo(hdfsWriterDTO.getColumnsList());

        String val = null;
        String type = null;
        int currLineNum = 0;
        int writeLineNum = 0;
        Group group;
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

                group = new SimpleGroup(schema);
                for (int i = 0; i < size; i++) {
                    val = lineArray[i];
                    Boolean isSetDefault = ReflectUtil.getFieldValueNotThrow(Boolean.class, hdfsWriterDTO, "setDefault", true, true);
                    // val 为空且不设置默认值时 跳过本次循环
                    if (StringUtils.isBlank(val) && !isSetDefault) {
                        continue;
                    }
                    // 为 null 时跳过本次循环
                    if (StringUtils.equalsIgnoreCase(val, HdfsWriter.DEFAULT_NULL)) {
                        continue;
                    }
                    type = hdfsWriterDTO.getColumnsList().get(i).getType().toLowerCase();
                    switch (type) {
                        case "tinyint":
                        case "smallint":
                        case "int":
                        case "integer":
                            group.add(i, Integer.parseInt(val));
                            break;
                        case "bigint":
                            group.add(i, Long.parseLong(val));
                            break;
                        case "float":
                            group.add(i, Float.parseFloat(val));
                            break;
                        case "double":
                            group.add(i, Double.parseDouble(val));
                            break;
                        case "binary":
                            group.add(i, Binary.fromString(val));
                            break;
                        case "char":
                        case "varchar":
                        case "string":
                            group.add(i, val);
                            break;
                        case "boolean":
                            group.add(i, Boolean.parseBoolean(val));
                            break;
                        case "timestamp":
                            Timestamp ts = new Timestamp(getTime(val, hdfsWriterDTO.getKeyList().get(i).getDateFormat()));
                            group.add(i, NanoTimeUtils.getNanoTime(ts, false).toBinary());
                            break;
                        case "date":
                            group.add(i, DateWritable.millisToDays(getMillis(val, hdfsWriterDTO.getKeyList().get(i).getDateFormat())));
                            break;
                        default:
                            if (type.contains("decimal")) {
                                HiveDecimal hiveDecimal = HiveDecimal.create(new BigDecimal(val));
                                Map<String, Integer> decimalInfo = decimalColInfo.get(hdfsWriterDTO.getColumnsList().get(i).getKey());
                                if (decimalInfo != null) {
                                    group.add(i, decimalToBinary(hiveDecimal, decimalInfo.get(KEY_PRECISION), decimalInfo.get(KEY_SCALE)));
                                } else {
                                    group.add(i, Integer.parseInt(val));
                                }
                            } else {
                                group.add(i, val);
                            }
                            break;
                    }
                }

                writer.write(group);

                currLineNum++;
                writeLineNum++;
            }
        } catch (Exception e) {
            throw new SourceException(String.format(
                    "The %s row data is abnormal, dataType : %s, dataValue : %s", currLineNum, type, val), e);
        } finally {
            closeResource(writer, inputStreamReader, reader);
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
        HdfsSourceDTO hdfsSourceDTO = (HdfsSourceDTO) source;
        MessageType schema = buildSchema(hdfsWriterDTO.getColumnsList());
        ParquetWriter<Group> writer = getWriter(hdfsSourceDTO, hdfsWriterDTO.getHdfsDirPath(), hdfsWriterDTO.getColumnsList());
        Map<String, Map<String, Integer>> decimalColInfo = getDecimalColInfo(hdfsWriterDTO.getColumnsList());

        int currLineNum = 0;
        int writeLineNum = 0;
        int startLine = hdfsWriterDTO.getStartLine();
        Group group;
        List<Integer> indexList = Lists.newArrayList();
        InputStreamReader inputStreamReader = null;
        CsvReader reader = null;
        String columnName = null;
        String val = null;

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
                    for (HDFSImportColumn importColumn : hdfsWriterDTO.getKeyList()) {
                        if (StringUtils.isBlank(importColumn.getKey())) {
                            indexList.add(-1);
                        } else {
                            boolean isMatch = false;
                            for (int i = 0; i < columnArr.length; i++) {
                                String name = columnArr[i];
                                if (name.equals(importColumn.getKey())) {
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

                group = new SimpleGroup(schema);
                for (int i = 0; i < indexList.size(); i++) {
                    int index = indexList.get(i);
                    //根据schema进行类型转换
                    if (index != -1 && index <= (columnArr.length - 1)) {
                        columnName = hdfsWriterDTO.getColumnsList().get(index).getKey();
                        val = columnArr[index].trim();
                        Boolean isSetDefault = ReflectUtil.getFieldValueNotThrow(Boolean.class, hdfsWriterDTO, "setDefault", true, true);
                        // val 为空且不设置默认值时 跳过本次循环
                        if (StringUtils.isBlank(val) && !isSetDefault) {
                            continue;
                        }
                        // 为 null 时跳过本次循环
                        if (StringUtils.equalsIgnoreCase(val, HdfsWriter.DEFAULT_NULL)) {
                            continue;
                        }
                        String type = hdfsWriterDTO.getColumnsList().get(i).getType().toLowerCase();
                        switch (hdfsWriterDTO.getColumnsList().get(index).getType().toLowerCase()) {
                            case "tinyint":
                            case "smallint":
                            case "int":
                            case "integer":
                                group.add(columnName, Integer.parseInt(val));
                                break;
                            case "bigint":
                                group.add(columnName, Long.parseLong(val));
                                break;
                            case "float":
                                group.add(columnName, Float.parseFloat(val));
                                break;
                            case "double":
                                group.add(columnName, Double.parseDouble(val));
                                break;
                            case "binary":
                                group.add(columnName, Binary.fromString(val));
                                break;
                            case "char":
                            case "varchar":
                            case "string":
                                group.add(columnName, val);
                                break;
                            case "boolean":
                                group.add(columnName, Boolean.parseBoolean(val));
                                break;
                            case "timestamp":
                                Timestamp ts = new Timestamp(getTime(val, hdfsWriterDTO.getKeyList().get(i).getDateFormat()));
                                group.add(columnName, NanoTimeUtils.getNanoTime(ts, false).toBinary());
                                break;
                            case "date":
                                group.add(columnName, DateWritable.millisToDays(getMillis(val, hdfsWriterDTO.getKeyList().get(i).getDateFormat())));
                                break;
                            default:
                                if (type.contains("decimal")) {
                                    HiveDecimal hiveDecimal = HiveDecimal.create(new BigDecimal(val));
                                    Map<String, Integer> decimalInfo = decimalColInfo.get(hdfsWriterDTO.getKeyList().get(i).getKey());
                                    if (decimalInfo != null) {
                                        group.add(i, decimalToBinary(hiveDecimal, decimalInfo.get(KEY_PRECISION), decimalInfo.get(KEY_SCALE)));
                                    } else {
                                        group.add(i, Integer.parseInt(val));
                                    }
                                } else {
                                    group.add(i, val);
                                }
                                break;
                        }
                    }
                }

                writer.write(group);

                currLineNum++;
                writeLineNum++;
            }
        } catch (Exception e) {
            throw new SourceException(String.format(
                    "the %s row data is abnormal，dataType: %s, dataValue: %s", currLineNum, columnName, val), e);
        } finally {
            closeResource(writer, inputStreamReader, reader);
        }

        return writeLineNum;
    }

    private static ParquetWriter<Group> getWriter(HdfsSourceDTO sourceDTO, String hdfsDirPath, List<ColumnMetaDTO> columnsList) throws IOException {
        Configuration conf = HadoopConfUtil.getHdfsConf(sourceDTO.getDefaultFS(), sourceDTO.getConfig(), sourceDTO.getKerberosConfig());
        MessageType schema = buildSchema(columnsList);
        GroupWriteSupport.setSchema(schema, conf);
        Path writePath = new Path(hdfsDirPath, "part-" + UUID.randomUUID().toString() + ".parquet");

        ExampleParquetWriter.Builder builder = ExampleParquetWriter.builder(writePath)
                .withWriteMode(ParquetFileWriter.Mode.CREATE)
                .withWriterVersion(ParquetProperties.WriterVersion.PARQUET_1_0)
                .withCompressionCodec(CompressionCodecName.SNAPPY)
                .withConf(conf)
                .withType(schema);

        return builder.build();
    }

    private static void closeResource(ParquetWriter<Group> writer, InputStreamReader inputStreamReader, CsvReader reader) throws IOException {
        if (writer != null) {
            writer.close();
        }

        if (inputStreamReader != null) {
            inputStreamReader.close();
        }

        if (reader != null) {
            reader.close();
        }
    }

    private static Map<String, Map<String, Integer>> getDecimalColInfo(List<ColumnMetaDTO> columnsList) {
        Map<String, Map<String, Integer>> decimalColInfo = new HashMap<>();

        for (ColumnMetaDTO column : columnsList) {
            String colType = column.getType().toLowerCase();
            if (colType.contains("decimal")) {
                int precision = Integer.parseInt(colType.substring(colType.indexOf("(") + 1, colType.indexOf(",")).trim());
                int scale = Integer.parseInt(colType.substring(colType.indexOf(",") + 1, colType.indexOf(")")).trim());

                Map<String, Integer> decimalInfo = new HashMap<>();
                decimalInfo.put(KEY_PRECISION, precision);
                decimalInfo.put(KEY_SCALE, scale);
                decimalColInfo.put(column.getKey(), decimalInfo);
            }
        }

        return decimalColInfo;
    }

    private static Binary decimalToBinary(final HiveDecimal hiveDecimal, int prec, int scale) {
        byte[] decimalBytes = hiveDecimal.setScale(scale).unscaledValue().toByteArray();

        // Estimated number of bytes needed.
        int precToBytes = ParquetHiveSerDe.PRECISION_TO_BYTE_COUNT[prec - 1];
        if (precToBytes == decimalBytes.length) {
            // No padding needed.
            return Binary.fromByteArray(decimalBytes);
        }

        byte[] tgt = new byte[precToBytes];
        if (hiveDecimal.signum() == -1) {
            // For negative number, initializing bits to 1
            for (int i = 0; i < precToBytes; i++) {
                tgt[i] |= 0xFF;
            }
        }

        // Padding leading zeroes/ones.
        System.arraycopy(decimalBytes, 0, tgt, precToBytes - decimalBytes.length, decimalBytes.length);
        return Binary.fromByteArray(tgt);
    }

    private static long getTime(String val, SimpleDateFormat dateFormat) throws ParseException {
        if (StringUtils.isNumeric(val)) {
            return Long.parseLong(val);
        } else {
            Date date;
            if (dateFormat == null) {
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                date = sdf2.parse(val);
            } else {
                date = dateFormat.parse(val);
            }

            return date.getTime();
        }
    }

    private static MessageType buildSchema(List<ColumnMetaDTO> columnsList) {
        Types.MessageTypeBuilder typeBuilder = Types.buildMessage();
        String name;
        String colType;
        for (ColumnMetaDTO column : columnsList) {
            name = column.getKey();
            colType = column.getType().toLowerCase();
            switch (colType) {
                case "tinyint":
                case "smallint":
                case "int":
                    typeBuilder.optional(PrimitiveType.PrimitiveTypeName.INT32).named(name);
                    break;
                case "bigint":
                    typeBuilder.optional(PrimitiveType.PrimitiveTypeName.INT64).named(name);
                    break;
                case "float":
                    typeBuilder.optional(PrimitiveType.PrimitiveTypeName.FLOAT).named(name);
                    break;
                case "double":
                    typeBuilder.optional(PrimitiveType.PrimitiveTypeName.DOUBLE).named(name);
                    break;
                case "binary":
                    typeBuilder.optional(PrimitiveType.PrimitiveTypeName.BINARY).named(name);
                    break;
                case "char":
                case "varchar":
                case "string":
                    typeBuilder.optional(PrimitiveType.PrimitiveTypeName.BINARY).as(OriginalType.UTF8).named(name);
                    break;
                case "boolean":
                    typeBuilder.optional(PrimitiveType.PrimitiveTypeName.BOOLEAN).named(name);
                    break;
                case "timestamp":
                    typeBuilder.optional(PrimitiveType.PrimitiveTypeName.INT96).named(name);
                    break;
                case "date":
                    typeBuilder.optional(PrimitiveType.PrimitiveTypeName.INT32).as(OriginalType.DATE).named(name);
                    break;
                default:
                    if (colType.contains("decimal")) {
                        int precision = Integer.parseInt(colType.substring(colType.indexOf("(") + 1, colType.indexOf(",")).trim());
                        int scale = Integer.parseInt(colType.substring(colType.indexOf(",") + 1, colType.indexOf(")")).trim());
                        typeBuilder.optional(PrimitiveType.PrimitiveTypeName.FIXED_LEN_BYTE_ARRAY)
                                .as(OriginalType.DECIMAL)
                                .precision(precision)
                                .scale(scale)
                                .length(computeMinBytesForPrecision(precision))
                                .named(name);
                    } else {
                        typeBuilder.optional(PrimitiveType.PrimitiveTypeName.BINARY).named(name);
                    }
                    break;
            }
        }

        return typeBuilder.named("Pair");
    }

    private static int computeMinBytesForPrecision(int precision) {
        int numBytes = 1;
        while (Math.pow(2.0, 8.0 * numBytes - 1) < Math.pow(10.0, precision)) {
            numBytes += 1;
        }
        return numBytes;
    }

    private static long getMillis(String dateStr, SimpleDateFormat dateFormat) throws Exception {
        Date date;
        if (dateFormat != null) {
            date = dateFormat.parse(dateStr);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            date = sdf.parse(dateStr);
        }

        return date.getTime();
    }

}
