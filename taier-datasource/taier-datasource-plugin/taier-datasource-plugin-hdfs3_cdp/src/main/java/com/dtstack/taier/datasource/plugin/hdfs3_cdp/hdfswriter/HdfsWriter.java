package com.dtstack.taier.datasource.plugin.hdfs3_cdp.hdfswriter;

import com.dtstack.taier.datasource.plugin.common.utils.ReflectUtil;
import com.dtstack.taier.datasource.api.dto.HdfsWriterDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hive.common.type.HiveDecimal;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 写入hdfs工具类
 *
 * @author ：wangchuan
 * date：Created in 下午01:40 2020/8/11
 * company: www.dtstack.com
 */
public class HdfsWriter {

    private static final char DEFAULT_DELIM = ',';

    public static final String DEFAULT_NULL = "\\N";

    private static String TINYINT_TYPE = "tinyint";

    private static String SMALLINT_TYPE = "smallint";

    private static String INT_TYPE = "int";

    private static String INTEGER_TYPE = "integer";

    private static String BIGINT_TYPE = "bigint";

    private static String FLOAT_TYPE = "float";

    private static String DOUBLE_TYPE = "double";

    private static String DECIMAL_TYPE = "decimal";

    private static String TIMESTAMP_TYPE = "timestamp";

    private static String DATE_TYPE = "date";

    private static String CHAR_TYPE = "char";

    private static String VARCHAR_TYPE = "varchar";

    private static String STRING_TYPE = "string";

    private static String BOOLEAN_TYPE = "boolean";

    private static String BINARY_TYPE = "binary";

    public static InputStreamReader getReader(String fromFileName, String oriCharSet) throws Exception {
        File fromFile = new File(fromFileName);
        if (!fromFile.exists()) {
            throw new SourceException("file is not exist");
        }

        if (fromFile.isDirectory()) {
            throw new SourceException("Cannot select folder");
        }

        return new InputStreamReader(new BOMInputStream(new FileInputStream(fromFile)), oriCharSet);
    }

    public static char getDelim(String delimStr) {
        char delim = DEFAULT_DELIM;
        if (delimStr != null) {
            if (delimStr.length() > 1) {
                throw new SourceException("The length of the separator cannot be greater than 1");
            } else {
                delim = delimStr.charAt(0);
            }
        }

        return delim;
    }

    /**
     * 根据类型转换字段值
     *
     * @param columnType    字段类型
     * @param columnVal     字段值
     * @param dateFormat    日期格式化类型
     * @param hdfsWriterDTO hdfs 写入配置类
     * @return 转换后的对象
     * @throws ParseException 日期类型解析异常
     */
    public static Object convertToTargetType(String columnType, String columnVal, SimpleDateFormat dateFormat, HdfsWriterDTO hdfsWriterDTO) throws ParseException {
        // 是否设置默认值
        Boolean isSetDefault = ReflectUtil.getFieldValueNotThrow(Boolean.class, hdfsWriterDTO, "setDefault", true, true);
        return convertToTargetType(columnType, columnVal, dateFormat, isSetDefault);
    }

    public static Object convertToTargetType(String columnType, String columnVal, SimpleDateFormat dateFormat, boolean isSetDefault) throws ParseException {
        if (StringUtils.isBlank(columnVal)) {
            // 不设置默认值返回 null
            if (!isSetDefault) {
                return null;
            }
            //空白字符串需要给默认值
            if (columnType.startsWith(CHAR_TYPE) || columnType.startsWith(VARCHAR_TYPE) || columnType.startsWith(STRING_TYPE)) {
                return "";
            }
            if (columnType.startsWith(INT_TYPE) || (columnType.startsWith(TINYINT_TYPE))) {
                return 0;
            }
            if (columnType.startsWith(BIGINT_TYPE)) {
                return 0L;
            }
            if (columnType.startsWith(FLOAT_TYPE)) {
                return 0f;
            }
            if (columnType.startsWith(BOOLEAN_TYPE)) {
                return false;
            }
            if (columnType.startsWith(DOUBLE_TYPE)) {
                return 0.0;
            }
            if (columnType.startsWith(DATE_TYPE) || columnType.startsWith(TIMESTAMP_TYPE)) {
                return null;
            }
            return "";
        } else if (StringUtils.equalsIgnoreCase(columnVal, DEFAULT_NULL)) {
            // value 为 /N 返回 null，此处不考虑传入 CSV 文件使用其他标识空值符的情景
            return null;
        } else if (columnType.startsWith(CHAR_TYPE) || columnType.startsWith(VARCHAR_TYPE) || columnType.startsWith(STRING_TYPE)) {
            return columnVal;
        } else if (columnType.startsWith(INT_TYPE)) {
            return Integer.valueOf(columnVal);
        } else if (columnType.startsWith(TINYINT_TYPE)) {
            return Byte.valueOf(columnVal);
        } else if (columnType.startsWith(SMALLINT_TYPE)) {
            return Short.valueOf(columnVal);
        } else if (columnType.startsWith(BIGINT_TYPE)) {
            return Long.valueOf(columnVal);
        } else if (columnType.startsWith(FLOAT_TYPE)) {
            return Float.valueOf(columnVal);
        } else if (columnType.startsWith(DOUBLE_TYPE)) {
            return Double.valueOf(columnVal);
        } else if (columnType.startsWith(DECIMAL_TYPE)) {
            return HiveDecimal.create(new BigDecimal(columnVal));
        } else if (columnType.startsWith(BOOLEAN_TYPE)) {
            return Boolean.valueOf(columnVal);
        } else if (columnType.startsWith(TIMESTAMP_TYPE)) {
            if (dateFormat != null) {
                java.util.Date date = dateFormat.parse(columnVal);
                return new Timestamp(date.getTime());
            } else {
                // 格式必须符合'yyyy-MM-dd HH:mm:ss'
                return Timestamp.valueOf(columnVal);
            }
        } else if (columnType.startsWith(DATE_TYPE)) {
            if (dateFormat != null) {
                java.util.Date date = dateFormat.parse(columnVal);
                return new Date(date.getTime());
            } else {
                // 格式必须符合'yyyy-MM-dd'
                return Date.valueOf(columnVal);
            }
        } else if (columnType.startsWith(BINARY_TYPE)) {
            //binary 类型用文件类型导入只能导入字符串
            return columnVal;
        } else {
            throw new SourceException("not support of column type" + columnType);
        }
    }
}
