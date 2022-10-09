package com.dtstack.taier.datasource.plugin.hive3.downloader;

import com.dtstack.taier.datasource.plugin.common.utils.HiveUtil;
import com.dtstack.taier.datasource.plugin.common.utils.ListUtil;
import com.dtstack.taier.datasource.plugin.kerberos.core.hdfs.HdfsOperator;
import com.dtstack.taier.datasource.plugin.kerberos.core.util.KerberosLoginUtil;
import com.dtstack.taier.datasource.api.downloader.IDownloader;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;

import java.io.IOException;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 下载hive表:存储结构为Text
 * Date: 2020/6/3
 * Company: www.dtstack.com
 * @author wangchuan
 */
@Slf4j
public class HiveTextDownload implements IDownloader {
    private static final int SPLIT_NUM = 1;

    private static final String IMPALA_INSERT_STAGING = "_impala_insert_staging";

    private TextInputFormat inputFormat;
    private JobConf conf;
    private LongWritable key;
    private Text value;

    private RecordReader recordReader;
    private final String tableLocation;
    private final String fieldDelimiter;
    private final Configuration configuration;
    private final List<String> columnNames;

    // 需要查询的字段索引
    private final List<Integer> needIndex;

    private List<String> paths;
    private String currFile;
    private int currFileIndex = 0;

    private InputSplit[] splits;
    private int splitIndex = 0;
    private final List<String> partitionColumns;
    private final Map<String, Object> kerberosConfig;

    /**
     * 按分区下载
     */
    private final Map<String, String> filterPartition;

    /**
     * 所有分区
     */
    private final List<String> partitions;

    /**
     * 当前分区的值
     */
    private List<String> currentPartData;

    public HiveTextDownload(Configuration configuration, String tableLocation, List<String> columnNames, String fieldDelimiter,
                            List<String> partitionColumns, Map<String, String> filterPartition, List<Integer> needIndex,
                            List<String> partitions, Map<String, Object> kerberosConfig){
        this.tableLocation = tableLocation;
        this.columnNames = columnNames;
        this.fieldDelimiter = fieldDelimiter;
        this.partitionColumns = partitionColumns;
        this.configuration = configuration;
        this.filterPartition = filterPartition;
        this.kerberosConfig = kerberosConfig;
        this.partitions = partitions;
        this.needIndex = needIndex;
    }

    @Override
    public boolean configure() throws IOException {

        conf = new JobConf(configuration);
        paths = Lists.newArrayList();
        FileSystem fs =  FileSystem.get(conf);
        // 递归获取表路径下所有文件
        getAllPartitionPath(tableLocation, paths, fs);
        // 有可能表结构还存在metaStore中，但是表路径被删除，但是此时不应该报错
        if(paths.size() == 0){
            return true;
        }
        nextRecordReader();
        key = new LongWritable();
        value = new Text();
        return true;
    }

    /**
     * 递归获取文件夹下所有文件，排除隐藏文件和无关文件
     *
     * @param tableLocation hdfs文件路径
     * @param pathList 所有文件集合
     * @param fs HDFS 文件系统
     */
    public static void getAllPartitionPath(String tableLocation, List<String> pathList, FileSystem fs) throws IOException {
        Path inputPath = new Path(tableLocation);
        // 路径不存在直接返回
        if (!fs.exists(inputPath)) {
            return;
        }
        //剔除隐藏系统文件和无关文件
        FileStatus[] fsStatus = fs.listStatus(inputPath, path -> !path.getName().startsWith(".") && !path.getName().startsWith("_SUCCESS") && !path.getName().startsWith(IMPALA_INSERT_STAGING) && !path.getName().startsWith("_common_metadata") && !path.getName().startsWith("_metadata"));
        if(fsStatus == null || fsStatus.length == 0){
            return;
        }
        for (FileStatus status : fsStatus) {
            if (status.isFile()) {
                pathList.add(status.getPath().toString());
            }else {
                getAllPartitionPath(status.getPath().toString(), pathList, fs);
            }
        }
    }

    private boolean nextRecordReader() throws IOException {

        if(!nextFile()){
            return false;
        }

        Path inputPath = new Path(currFile);
        inputFormat = new TextInputFormat();

        FileInputFormat.setInputPaths(conf, inputPath);
        TextInputFormat inputFormat = new TextInputFormat();
        inputFormat.configure(conf);
        splits = inputFormat.getSplits(conf, SPLIT_NUM);
        if(splits.length == 0){
            return nextRecordReader();
        }
        nextSplitRecordReader();
        return true;
    }

    private boolean nextSplitRecordReader() throws IOException {
        if(splitIndex >= splits.length){
            return false;
        }

        InputSplit fileSplit = splits[splitIndex];
        splitIndex++;

        if(recordReader != null){
            close();
        }

        recordReader = inputFormat.getRecordReader(fileSplit, conf, Reporter.NULL);
        return true;
    }

    private boolean nextFile(){
        if(currFileIndex > (paths.size() - 1)){
            return false;
        }

        currFile = paths.get(currFileIndex);

        if(CollectionUtils.isNotEmpty(partitionColumns)){
            currentPartData = HdfsOperator.parsePartitionDataFromUrl(currFile,partitionColumns);
        }

        // 如果分区不存在或者不需要该分区则进行跳过
        if (!isPartitionExists() || !isRequiredPartition()){
            currFileIndex++;
            splitIndex = 0;
            return nextFile();
        }

        currFileIndex++;
        splitIndex = 0;
        return true;
    }

    public boolean nextRecord() throws IOException {

        if(recordReader.next(key, value)){
            return true;
        }

        //同一个文件夹下是否还存在剩余的split
        while(nextSplitRecordReader()){
            if(nextRecord()){
                return true;
            }
        }

        //查找下一个可读的文件夹
        while (nextRecordReader()){
            if(nextRecord()){
                return true;
            }
        }

        return false;
    }

    @Override
    public List<String> getMetaInfo() {
        List<String> metaInfo = new ArrayList<>(columnNames);
        if(CollectionUtils.isNotEmpty(partitionColumns)){
            metaInfo.addAll(partitionColumns);
        }
        return metaInfo;
    }

    @Override
    public List<String> readNext(){
        return KerberosLoginUtil.loginWithUGI(kerberosConfig).doAs(
                (PrivilegedAction<List<String>>) ()->{
                    try {
                        return readNextWithKerberos();
                    } catch (Exception e){
                        throw new SourceException(String.format("Abnormal reading file,%s", e.getMessage()), e);
                    }
                });
    }

    public List<String> readNextWithKerberos(){
        String line = value.toString();
        value.clear();
        String[] fields = HiveUtil.splitByDelimiterStr(line, fieldDelimiter);
        List<String> row = Lists.newArrayList(fields);
        if(CollectionUtils.isNotEmpty(partitionColumns)){
            row.addAll(currentPartData);
        }
        if (CollectionUtils.isNotEmpty(needIndex)) {
            List<String> rowNew = Lists.newArrayList();
            for (Integer index : needIndex) {
                if (index > row.size() -1) {
                    rowNew.add(null);
                } else {
                    rowNew.add(row.get(index));
                }
            }
            return rowNew;
        }
        return row;
    }

    @Override
    public boolean reachedEnd() {
        return KerberosLoginUtil.loginWithUGI(kerberosConfig).doAs(
                (PrivilegedAction<Boolean>) ()->{
                    try {
                        return recordReader == null || !nextRecord();
                    } catch (Exception e){
                        throw new SourceException(String.format("Download file is abnormal,%s", e.getMessage()), e);
                    }
                });
    }

    @Override
    public boolean close() throws IOException {
        if(recordReader != null){
            recordReader.close();
        }
        return true;
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
            if(part.contains("=")){
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
    private boolean isRequiredPartition(){
        if (filterPartition != null && !filterPartition.isEmpty()) {
            //获取当前路径下的分区信息
            Map<String,String> partColDataMap = new HashMap<>();
            for (String part : currFile.split("/")) {
                if(part.contains("=")){
                    String[] parts = part.split("=");
                    partColDataMap.put(parts[0],parts[1]);
                }
            }

            Set<String> keySet = filterPartition.keySet();
            boolean check = true;
            for (String key : keySet) {
                String partition = partColDataMap.get(key);
                String needPartition = filterPartition.get(key);
                if (!Objects.equals(partition, needPartition)){
                    check = false;
                    break;
                }
            }
            return check;
        }
        return true;
    }
}