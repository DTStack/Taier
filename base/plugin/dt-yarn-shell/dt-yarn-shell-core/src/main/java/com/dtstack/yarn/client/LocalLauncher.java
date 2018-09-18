package com.dtstack.yarn.client;


import com.dtstack.yarn.DtYarnConfiguration;

import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.yarn.exceptions.YarnException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class LocalLauncher {

    private static DtYarnConfiguration loadConfig() {
        DtYarnConfiguration conf = new DtYarnConfiguration();
        String hadoopConfDir = System.getenv("HADOOP_CONF_DIR");
        if(StringUtils.isNotBlank(hadoopConfDir)) {
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

    public static void main(String[] args) throws ClassNotFoundException, YarnException, ParseException, IOException {
        DtYarnConfiguration conf = loadConfig();

        conf.set(DtYarnConfiguration.PYTHON3_PATH, "/usr/bin/python");
        conf.set("jlogstash.root", "/opt/dtstack/jlogstash");
        conf.set("java.home", "/usr/java/default/bin");

        Client client = new Client(conf);
        client.submit(args);
    }

}
