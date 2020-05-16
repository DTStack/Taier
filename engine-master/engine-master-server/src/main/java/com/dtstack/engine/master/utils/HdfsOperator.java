package com.dtstack.engine.master.utils;

import org.apache.hadoop.conf.Configuration;

import java.io.IOException;
import java.net.URISyntaxException;


/**
 * @author sishu.yss
 */
public class HdfsOperator {

    private static final String HDFS_PATTERN = "(hdfs://[^/]+)(.*)";

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



}
