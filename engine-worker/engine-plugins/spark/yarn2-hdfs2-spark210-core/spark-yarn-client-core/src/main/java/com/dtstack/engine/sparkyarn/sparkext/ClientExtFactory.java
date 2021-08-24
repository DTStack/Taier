package com.dtstack.engine.sparkyarn.sparkext;

import com.dtstack.engine.base.filesystem.FilesystemManager;
import org.apache.hadoop.conf.Configuration;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.yarn.ClientArguments;


/**
 * Reason:
 * Date: 2019/1/21
 * Company: www.dtstack.com
 * @author xuchao
 */

public class ClientExtFactory {

    public static ClientExt getClientExt(FilesystemManager filesystemManager,
                                         ClientArguments args,
                                         Configuration hadoopConf,
                                         SparkConf sparkConf,
                                         boolean isCarbondata){

        if(!isCarbondata){
            return new ClientExt(filesystemManager, args, hadoopConf, sparkConf);
        }else{
            return new CarbondataClientExt(filesystemManager, args, hadoopConf, sparkConf);
        }
    }
}
