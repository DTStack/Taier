package com.dtstack.taier.datasource.plugin.hive3.downloader;

import com.dtstack.taier.datasource.api.downloader.IDownloader;
import com.dtstack.taier.datasource.api.dto.ColumnMetaDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.plugin.common.enums.ColumnType;
import com.dtstack.taier.datasource.plugin.common.utils.ListUtil;
import com.dtstack.taier.datasource.plugin.common.utils.StringUtil;
import com.dtstack.taier.datasource.plugin.hive3.GroupTypeIgnoreCase;
import com.dtstack.taier.datasource.plugin.kerberos.core.hdfs.HdfsOperator;
import com.dtstack.taier.datasource.plugin.kerberos.core.util.KerberosLoginUtil;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.example.GroupReadSupport;
import org.apache.parquet.io.api.Binary;
import org.apache.parquet.schema.DecimalMetadata;
import org.apache.parquet.schema.PrimitiveType;
import org.apache.parquet.schema.Type;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.PrivilegedAction;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 下载hive表:存储结构为PARQUET
 * Date: 2020/6/3
 * Company: www.dtstack.com
 *
 * @author wangchuan
 */
@Slf4j
public class HiveParquetDownload implements IDownloader {

    private final String tableLocation;

    private final List<ColumnMetaDTO> columns;

    // 查询字段在所有字段中的索引
    private final List<Integer> needIndex;

    private final List<String> partitionColumns;

    private final Configuration conf;

    private ParquetReader<Group> build;

    private Group currentLine;

    private List<String> paths;

    private int currFileIndex = 0;

    private String currFile;

    private List<String> currentPartData;

    private final GroupReadSupport readSupport = new GroupReadSupport();

    private static final int JULIAN_EPOCH_OFFSET_DAYS = 2440588;

    private static final long MILLIS_IN_DAY = TimeUnit.DAYS.toMillis(1);

    private static final long NANOS_PER_MILLISECOND = TimeUnit.MILLISECONDS.toNanos(1);

    private final Map<String, Object> kerberosConfig;

    /**
     * 按分区下载
     */
    private final Map<String, String> filterPartition;

    /**
     * 所有分区
     */
    private final List<String> partitions;

    private static final String IMPALA_INSERT_STAGING = "_impala_insert_staging";

    public HiveParquetDownload(Configuration conf, String tableLocation, List<ColumnMetaDTO> columns,
                               List<String> partitionColumns, List<Integer> needIndex, Map<String, String> filterPartition,
                               List<String> partitions, Map<String, Object> kerberosConfig) {
        this.conf = conf;
        this.tableLocation = tableLocation;
        this.columns = columns;
        this.partitionColumns = partitionColumns;
        this.needIndex = needIndex;
        this.filterPartition = filterPartition;
        this.partitions = partitions;
        this.kerberosConfig = kerberosConfig;
    }

    @Override
    public boolean configure() throws Exception {
        paths = Lists.newArrayList();
        FileSystem fs = FileSystem.get(conf);
        // 递归获取表路径下所有文件
        getAllPartitionPath(tableLocation, paths, fs);
        return true;
    }

    private void nextSplitRecordReader() throws Exception {
        if (currFileIndex > paths.size() - 1) {
            return;
        }

        currFile = paths.get(currFileIndex);

        // 如果分区不存在或者不需要该分区则进行跳过
        if (!isPartitionExists() || !isRequiredPartition()) {
            currFileIndex++;
            nextSplitRecordReader();
            return;
        }

        ParquetReader.Builder<Group> reader = ParquetReader.builder(readSupport, new Path(currFile)).withConf(conf);
        build = reader.build();

        if (CollectionUtils.isNotEmpty(partitionColumns)) {
            currentPartData = HdfsOperator.parsePartitionDataFromUrl(currFile, partitionColumns);
        }

        currFileIndex++;
    }

    private boolean nextRecord() throws Exception {
        if (build == null && currFileIndex <= paths.size() - 1) {
            nextSplitRecordReader();
        }

        if (build == null) {
            return false;
        }

        currentLine = build.read();

        if (currentLine == null) {
            build = null;
            nextRecord();
        }

        return currentLine != null;
    }

    @Override
    public List<String> getMetaInfo() {
        List<String> metaInfo = columns.stream().map(ColumnMetaDTO::getKey).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(partitionColumns)) {
            metaInfo.addAll(partitionColumns);
        }
        return metaInfo;
    }

    @Override
    public List<String> readNext() {
        return KerberosLoginUtil.loginWithUGI(kerberosConfig).doAs(
                (PrivilegedAction<List<String>>) () -> {
                    try {
                        return readNextWithKerberos();
                    } catch (Exception e) {
                        throw new SourceException(String.format("Abnormal reading file,%s", e.getMessage()), e);
                    }
                });
    }

    private List<String> readNextWithKerberos() {
        List<String> line = null;
        if (currentLine != null) {
            line = new ArrayList<>();
            // needIndex不为空表示获取指定字段
            if (CollectionUtils.isNotEmpty(needIndex)) {
                for (Integer index : needIndex) {
                    // 表示该字段为分区字段
                    if (index > columns.size() - 1 && CollectionUtils.isNotEmpty(currentPartData)) {
                        // 分区字段的索引
                        int partIndex = index - columns.size();
                        if (partIndex < currentPartData.size()) {
                            line.add(currentPartData.get(partIndex));
                        } else {
                            line.add(null);
                        }
                    } else if (index < columns.size()) {
                        Integer fieldIndex = isFieldExists(columns.get(index).getKey());
                        if (fieldIndex != -1) {
                            line.add(getFieldByIndex(columns.get(index).getType(), fieldIndex));
                        } else {
                            line.add(null);
                        }
                    } else {
                        line.add(null);
                    }
                }
                // needIndex为空表示获取所有字段
            } else {
                for (int index = 0; index < columns.size(); index++) {
                    Integer fieldIndex = isFieldExists(columns.get(index).getKey());
                    if (fieldIndex != -1) {
                        line.add(getFieldByIndex(columns.get(index).getType(), fieldIndex));
                    } else {
                        line.add(null);
                    }
                }
                if (CollectionUtils.isNotEmpty(partitionColumns)) {
                    line.addAll(currentPartData);
                }
            }
        }
        return line;
    }

    /**
     * 判断字段是否存在，存在则返回字段索引
     * 每行数据重新判断，对于parquet来说，如果对应schema下没有该列，
     * currentLine.getType().getFields()返回值的size可能会不同，导致数组越界异常!
     * bug 连接：http://redmine.prod.dtstack.cn/issues/33045
     *
     * @param columnName 字段名
     * @return 字段索引
     */
    private Integer isFieldExists(String columnName) {
        GroupTypeIgnoreCase groupType = new GroupTypeIgnoreCase(currentLine.getType());
        if (!groupType.containsField(columnName)) {
            return -1;
        }
        return groupType.getFieldIndex(columnName);
    }

    // 获取指定index下的字段值
    private String getFieldByIndex(String type, int index) {
        Object data = null;
        ColumnType columnType = ColumnType.fromString(type);

        try {
            if (index == -1 || currentLine.getFieldRepetitionCount(index) == 0) {
                return null;
            }

            Type colSchemaType = currentLine.getType().getType(index);
            switch (columnType.name().toLowerCase()) {
                case "tinyint":
                case "smallint":
                case "int":
                    data = currentLine.getInteger(index, 0);
                    break;
                case "bigint":
                    data = currentLine.getLong(index, 0);
                    break;
                case "float":
                    data = currentLine.getFloat(index, 0);
                    break;
                case "double":
                    data = currentLine.getDouble(index, 0);
                    break;
                case "binary":
                    Binary binaryData = currentLine.getBinary(index, 0);
                    data = StringUtil.encodeHex(binaryData.getBytes());
                    break;
                case "char":
                case "varchar":
                case "string":
                    data = currentLine.getString(index, 0);
                    break;
                case "boolean":
                    data = currentLine.getBoolean(index, 0);
                    break;
                case "timestamp": {
                    long time = getTimestampMillis(currentLine.getInt96(index, 0));
                    data = new Timestamp(time);
                    break;
                }
                case "decimal": {
                    DecimalMetadata dm = ((PrimitiveType) colSchemaType).getDecimalMetadata();
                    String primitiveTypeName = currentLine.getType().getType(index).asPrimitiveType().getPrimitiveTypeName().name();
                    if (ColumnType.INT32.name().equals(primitiveTypeName)) {
                        int intVal = currentLine.getInteger(index, 0);
                        data = longToDecimalStr(intVal, dm.getScale());
                    } else if (ColumnType.INT64.name().equals(primitiveTypeName)) {
                        long longVal = currentLine.getLong(index, 0);
                        data = longToDecimalStr(longVal, dm.getScale());
                    } else {
                        Binary binary = currentLine.getBinary(index, 0);
                        data = binaryToDecimalStr(binary, dm.getScale());
                    }
                    break;
                }
                case "date": {
                    String val = currentLine.getValueToString(index, 0);
                    data = new Timestamp(Integer.parseInt(val) * MILLIS_IN_DAY).toString().substring(0, 10);
                    break;
                }
                default:
                    data = currentLine.getValueToString(index, 0);
                    break;
            }
        } catch (Exception e) {
            // ignore error, 报错不影响结果返回, 不打印报错日志, 容易磁盘打爆
        }

        return String.valueOf(data);
    }


    private static String binaryToDecimalStr(Binary binary, int scale) {
        BigInteger bi = new BigInteger(binary.getBytes());
        BigDecimal bg = new BigDecimal(bi, scale);

        return bg.toString();
    }

    private static String longToDecimalStr(long value, int scale) {
        BigInteger bi = BigInteger.valueOf(value);
        BigDecimal bg = new BigDecimal(bi, scale);

        return bg.toString();
    }

    /**
     * @param timestampBinary
     * @return
     */
    private long getTimestampMillis(Binary timestampBinary) {
        if (timestampBinary.length() != 12) {
            return 0;
        }
        byte[] bytes = timestampBinary.getBytes();

        long timeOfDayNanos = Longs.fromBytes(bytes[7], bytes[6], bytes[5], bytes[4], bytes[3], bytes[2], bytes[1], bytes[0]);
        int julianDay = Ints.fromBytes(bytes[11], bytes[10], bytes[9], bytes[8]);

        return julianDayToMillis(julianDay) + (timeOfDayNanos / NANOS_PER_MILLISECOND);
    }

    private long julianDayToMillis(int julianDay) {
        return (julianDay - JULIAN_EPOCH_OFFSET_DAYS) * MILLIS_IN_DAY;
    }

    @Override
    public boolean reachedEnd() {
        return KerberosLoginUtil.loginWithUGI(kerberosConfig).doAs(
                (PrivilegedAction<Boolean>) () -> {
                    try {
                        return !nextRecord();
                    } catch (Exception e) {
                        throw new SourceException(String.format("Download file is abnormal,%s", e.getMessage()), e);
                    }
                });
    }

    @Override
    public boolean close() throws Exception {
        if (build != null) {
            build.close();
        }
        return true;
    }

    /**
     * 递归获取文件夹下所有文件，排除隐藏文件和无关文件
     *
     * @param tableLocation hdfs文件路径
     * @param pathList      所有文件集合
     * @param fs            HDFS 文件系统
     */
    public static void getAllPartitionPath(String tableLocation, List<String> pathList, FileSystem fs) throws IOException {
        Path inputPath = new Path(tableLocation);
        // 路径不存在直接返回
        if (!fs.exists(inputPath)) {
            return;
        }
        //剔除隐藏系统文件和无关文件
        FileStatus[] fsStatus = fs.listStatus(inputPath, path -> !path.getName().startsWith(".") && !path.getName().startsWith("_SUCCESS") && !path.getName().startsWith(IMPALA_INSERT_STAGING) && !path.getName().startsWith("_common_metadata") && !path.getName().startsWith("_metadata"));
        if (fsStatus == null || fsStatus.length == 0) {
            return;
        }
        for (FileStatus status : fsStatus) {
            if (status.isFile()) {
                pathList.add(status.getPath().toString());
            } else {
                getAllPartitionPath(status.getPath().toString(), pathList, fs);
            }
        }
    }

    /**
     * 判断分区是否存在
     *
     * @return 分区是否存在
     */
    private boolean isPartitionExists() {
        // 如果 partitions 为 null，表示非分区表，返回 true
        if (Objects.isNull(partitions)) {
            return true;
        }
        // 如果为空标识是分区表，但是无分区信息，返回 false
        if (CollectionUtils.isEmpty(partitions)) {
            return false;
        }
        String curPathPartition = getCurPathPartition();
        if (StringUtils.isBlank(curPathPartition)) {
            return false;
        }
        return ListUtil.containsIgnoreCase(partitions, curPathPartition);
    }

    /**
     * 获取当前路径的分区路径
     *
     * @return 分区
     */
    private String getCurPathPartition() {
        StringBuilder curPart = new StringBuilder();
        for (String part : currFile.split("/")) {
            if (part.contains("=")) {
                curPart.append(part).append("/");
            }
        }
        String curPartString = curPart.toString();
        if (StringUtils.isNotBlank(curPartString)) {
            return curPartString.substring(0, curPartString.length() - 1);
        }
        return curPartString;
    }

    /**
     * 判断是否是指定的分区，支持多级分区
     *
     * @return 是否需要该分区
     */
    private boolean isRequiredPartition() {
        if (filterPartition != null && !filterPartition.isEmpty()) {
            //获取当前路径下的分区信息
            Map<String, String> partColDataMap = new HashMap<>();
            for (String part : currFile.split("/")) {
                if (part.contains("=")) {
                    String[] parts = part.split("=");
                    partColDataMap.put(parts[0], parts[1]);
                }
            }

            Set<String> keySet = filterPartition.keySet();
            boolean check = true;
            for (String key : keySet) {
                String partition = partColDataMap.get(key);
                String needPartition = filterPartition.get(key);
                if (!Objects.equals(partition, needPartition)) {
                    check = false;
                    break;
                }
            }
            return check;
        }
        return true;
    }
}
