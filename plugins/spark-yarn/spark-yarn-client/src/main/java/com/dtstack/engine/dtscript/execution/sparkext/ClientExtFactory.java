package com.dtstack.engine.dtscript.execution.sparkext;

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

    public static ClientExt getClientExt(ClientArguments args,
                                         Configuration hadoopConf,
                                         SparkConf sparkConf,
                                         boolean isCarbondata){

        if(!isCarbondata){
            return new ClientExt(args, hadoopConf, sparkConf);
        }else{
            return new CarbondataClientExt(args, hadoopConf, sparkConf);
        }
    }
}
