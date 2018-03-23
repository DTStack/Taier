package com.dtstack.rdos.engine.execution.flink140.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;

/**
 * 
 * @author sishu.yss
 *
 */
public class HadoopConf {
	
	private static Logger logger = LoggerFactory.getLogger(HadoopConf.class);

	private final static String HADOOP_CONFIGE = System.getProperty("user.dir")+"/conf/hadoop/";

	private final static String HADOOP_CONF_DIR = System.getenv("HADOOP_CONF_DIR");
	
	private static Configuration configuration = new Configuration();

	private static Configuration yarnConfiguration = new YarnConfiguration();
	
    static{
    	try {
    		String dir = StringUtils.isNotBlank(HADOOP_CONF_DIR) ? HADOOP_CONF_DIR : HADOOP_CONFIGE;
    		File dirFile = new File(dir);
    		if(!dirFile.exists()){
    			logger.error("-----------not set env for HADOOP_CONF_DIR!!!");
			}else if(!dirFile.isDirectory()){
				logger.error("HADOOP_CONF_DIR:{} is not dir.", dir);
			}else{
				configuration.set("fs.hdfs.impl", DistributedFileSystem.class.getName());
				File[] xmlFileList = new File(dir).listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						if(name.endsWith(".xml"))
							return true;
						return false;
					}
				});

				if(xmlFileList != null) {
					for(File xmlFile : xmlFileList) {
						configuration.addResource(xmlFile.toURI().toURL());
						yarnConfiguration.addResource(xmlFile.toURI().toURL());
					}
				}
			}
		} catch (Exception e) {
			logger.error("",e);
		}
    }
    
	public static Configuration getConfiguration(){
		return configuration;
	}
	
	public static String getDefaultFs(){
		return configuration.get("fs.defaultFS");
	}

	public static Configuration getYarnConfiguration() {
		return yarnConfiguration;
	}
}
