
package com.dtstack.engine.common.util;

import java.io.File;


/**
 *
 * @author sishu.yss
 *
 */
public class SystemPropertyUtil {

    public static void setSystemUserDir() {
        String dir = System.getProperty("user.dir");
        String conf = String.format("%s/%s", new Object[]{dir, "conf"});
        File file = new File(conf);
        if(!file.exists()) {
            dir = dir.substring(0, dir.lastIndexOf("/"));
            conf = String.format("%s/%s", new Object[]{dir, "conf"});
            file = new File(conf);
            if(file.exists()) {
                System.setProperty("user.dir", dir);
            }
        }

        System.setProperty("user.dir.conf", System.getProperty("user.dir") + "/conf");

    }

    public static void setHadoopUserName(String userName) {
        System.setProperty("HADOOP_USER_NAME", userName);
    }
}
