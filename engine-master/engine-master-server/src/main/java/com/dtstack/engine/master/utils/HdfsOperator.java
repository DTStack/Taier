package com.dtstack.engine.master.utils;

import com.dtstack.engine.common.exception.RdosDefineException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author sishu.yss
 */
public class HdfsOperator {

	private static ThreadLocal<FileSystem> tfs= new ThreadLocal<FileSystem>();

    private static final Logger logger = LoggerFactory.getLogger(HdfsOperator.class);

    private static final String HDFS_PATTERN = "(hdfs://[^/]+)(.*)";

    private static Pattern pattern = Pattern.compile(HDFS_PATTERN);

    public static final BigDecimal BASE_VALUE = new BigDecimal("1024");


    public static boolean uploadInputStreamToHdfs(Configuration conf,byte[] bytes, String hdfsPath) throws URISyntaxException, IOException {
       /* ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        FileSystem fs = getFileSystem(conf);
        Path destP = new Path(hdfsPath);
        FSDataOutputStream os = fs.create(destP);
        IOUtils.copyBytes(is, os, 4096, true);
        if(logger.isDebugEnabled()){
            logger.debug("submit file {} to hdfs success.", hdfsPath);
        }*/
        return true;
    }

    public static Pair<String, String> parseHdfsUri(String path) {
        Matcher matcher = pattern.matcher(path);
        if (matcher.find() && matcher.groupCount() == 2) {
            String hdfsUri = matcher.group(1);
            String hdfsPath = matcher.group(2);
            return new MutablePair<>(hdfsUri, hdfsPath);
        }
        return null;
    }


   /* public static FileSystem getFileSystem(Configuration conf) throws IOException{
        com.dtstack.engine.master.utils.HadoopConf.setDefaultConf(conf);
        if(DtKerberosUtils.needLoginKerberos(conf)){
            try {
                DtKerberosUtils.loginKerberosHdfs(conf);
            } catch (Exception e){
                logger.error("kerberos认证失败:{}",e);
            }
        }

		return FileSystem.get(conf);
    }*/


}
