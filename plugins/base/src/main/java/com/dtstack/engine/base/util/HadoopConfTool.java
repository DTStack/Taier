package com.dtstack.engine.base.util;

import org.apache.hadoop.conf.Configuration;

/**
 * @Auther: jiangjunjie
 * @Date: 2020/1/13 15:50
 * @Description:
 */
public class HadoopConfTool {

    public static final String FS_HDFS_IMPL_DISABLE_CACHE = "fs.hdfs.impl.disable.cache";

    public static void setFsHdfsImplDisableCache(Configuration conf){
        conf.setBoolean(FS_HDFS_IMPL_DISABLE_CACHE, true);
    }
}
