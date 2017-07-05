package com.dtstack.rdos.engine.execution.flink120.sink.hdfs;

import com.dtstack.rdos.common.util.ClassUtil;
import com.dtstack.rdos.common.util.DateUtil;
import com.google.common.base.Preconditions;
import org.apache.flink.api.common.io.RichOutputFormat;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.types.Row;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.SnappyCodec;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.StringUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

import static jodd.datetime.JDateTimeDefault.format;

/**
 * Created by softfly on 17/7/4.
 */
public class HdfsTextOutputFormat extends HdfsOutputFormat {


    public final SimpleDateFormat FIELD_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    @Override
    public void configure(Configuration configuration) {
        super.configure(configuration);
        outputFormat = new TextOutputFormat();

        Class<? extends CompressionCodec> codecClass = null;
        if(compress == null){
            codecClass = null;
        } else if("GZIP".equalsIgnoreCase(compress)){
            codecClass = org.apache.hadoop.io.compress.GzipCodec.class;
        } else if ("BZIP2".equalsIgnoreCase(compress)) {
            codecClass = org.apache.hadoop.io.compress.BZip2Codec.class;
        } else {
            throw new IllegalArgumentException("Unsupported compress format: " + compress);
        }

        if(codecClass != null)
            this.outputFormat.setOutputCompressorClass(conf, codecClass);
    }

    @Override
    public void open(int taskNumber, int numTasks) throws IOException {
        if(taskNumber >= 0 && numTasks >= 1) {
            Date currentTime = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String dateString = formatter.format(currentTime);

            String pathStr = outputFilePath + "/" + taskNumber + "." + dateString + "." + UUID.randomUUID();
            System.out.println("pathStr=" + pathStr);

            outputFormat.setOutputPath(conf, new Path(pathStr));

            // 此处好像并没有什么卵用
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
            String attempt = "attempt_"+dateFormat.format(new Date())+"_0001_m_000000_" + taskNumber;
            conf.set("mapreduce.task.attempt.id", attempt);

            this.recordWriter = this.outputFormat.getRecordWriter(null, conf, pathStr, Reporter.NULL);

        } else {
            throw new IllegalArgumentException("TaskNumber: " + taskNumber + ", numTasks: " + numTasks);
        }
    }

    @Override
    public void writeRecord(Row row) throws IOException {

        String[] record = new String[columnNames.length];

        for(int i = 0; i < row.getArity(); ++i) {
            Object column = row.getField(i);

            String columnName = inputColumnNames[i];
            String fromType = inputColumnTypes[i];
            String toType = columnNameTypeMap.get(columnName);

            if(toType == null) {
                continue;
            }

            if(!fromType.equalsIgnoreCase(toType)) {
                column = ClassUtil.convertType(column, fromType, toType);
            }

            String rowData = column.toString();
            Object field = null;
            switch(toType.toUpperCase()) {
                case "TINYINT":
                    field = Byte.valueOf(rowData);
                    break;
                case "SMALLINT":
                    field = Short.valueOf(rowData);
                    break;
                case "INT":
                    field = Integer.valueOf(rowData);
                    break;
                case "BIGINT":
                    field = Long.valueOf(rowData);
                    break;
                case "FLOAT":
                    field = Float.valueOf(rowData);
                    ;                   break;
                case "DOUBLE":
                    field = Double.valueOf(rowData);
                    break;
                case "STRING":
                case "VARCHAR":
                case "CHAR":
                    field = rowData;
                    break;
                case "BOOLEAN":
                    field = Boolean.valueOf(rowData);
                    break;
                case "DATE":
                    field = DateUtil.columnToDate(column);
                    rowData = FIELD_DATE_FORMAT.format((Date)field);
                    break;
                case "TIMESTAMP":
                    //recordList.add(new java.sql.Timestamp(column.asDate().getTime()));
                    java.sql.Date d = DateUtil.columnToDate(column);
                    field = new java.sql.Timestamp(d.getTime());
                    rowData = FIELD_DATE_FORMAT.format((Date)field);
                    break;
                default:
                    throw new IllegalArgumentException();
            }

            record[columnNameIndexMap.get(columnName)] = rowData;

        }

        for(int i = 0; i < record.length; ++i) {
            if(record[i] == null) {
                record[i] = "";
            }
        }

        recordWriter.write(NullWritable.get(), new Text(StringUtils.join(delimiter, record)));
    }


}
