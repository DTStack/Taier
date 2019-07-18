package com.dtstack.yarn.client;


import com.dtstack.yarn.DtYarnConfiguration;

import org.apache.commons.lang.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;

public class LocalLauncher {

    private static DtYarnConfiguration loadConfig() {
        DtYarnConfiguration conf = new DtYarnConfiguration();
        String hadoopConfDir = System.getenv("HADOOP_CONF_DIR");
        if (StringUtils.isNotBlank(hadoopConfDir)) {
            try {
                conf.addResource(new URL("file://" + hadoopConfDir + "/" + "core-site.xml"));
                conf.addResource(new URL("file://" + hadoopConfDir + "/" + "hdfs-site.xml"));
                conf.addResource(new URL("file://" + hadoopConfDir + "/" + "yarn-site.xml"));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        }
        return conf;
    }

}
