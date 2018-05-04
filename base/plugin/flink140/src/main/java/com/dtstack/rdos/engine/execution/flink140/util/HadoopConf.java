package com.dtstack.rdos.engine.execution.flink140.util;

import com.dtstack.rdos.engine.execution.flink140.HadoopConfTool;
import com.dtstack.rdos.engine.execution.flink140.YarnConfTool;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author sishu.yss
 *
 */
public class HadoopConf {
	
	private static final Logger LOG = LoggerFactory.getLogger(HadoopConf.class);

	private Configuration configuration = new Configuration();

	private Configuration yarnConfiguration = new YarnConfiguration();

	public HadoopConf(){

    }

    public void initHadoopConf(Map<String, Object> conf){
	    String nameServices = HadoopConfTool.getDfsNameServices(conf);
	    String defaultFs = HadoopConfTool.getFSDefaults(conf);
	    String haNameNodesKey = HadoopConfTool.getDfsHaNameNodesKey(conf);
	    String haNameNodesVal = HadoopConfTool.getDfsHaNameNodes(conf, haNameNodesKey);
        List<String> nnRpcAddressList = HadoopConfTool.getDfsNameNodeRpcAddressKeys(conf);
        String proxyProviderKey = HadoopConfTool.getClientFailoverProxyProviderKey(conf);
        String proxyProvider = HadoopConfTool.getClientFailoverProxyProviderVal(conf, proxyProviderKey);
        String fsHdfsImpl = HadoopConfTool.getFsHdfsImpl(conf);
        String disableCache = HadoopConfTool.getFsHdfsImplDisableCache(conf);


        configuration.set(HadoopConfTool.DFS_NAME_SERVICES, nameServices);
        configuration.set(HadoopConfTool.FS_DEFAULTFS, defaultFs);
        configuration.set(haNameNodesKey, haNameNodesVal);
        nnRpcAddressList.forEach(key -> {
            String val = HadoopConfTool.getDfsNameNodeRpcAddress(conf, key);
            configuration.set(key, val);
        });

        //配置自动故障切换实现方式
        configuration.set(proxyProviderKey, proxyProvider);

        //非必须:针对hdfs的文件系统实现
        configuration.set(HadoopConfTool.FS_HDFS_IMPL, fsHdfsImpl);
        //非必须:如果多个hadoopclient之间不互相影响需要取消cache
        configuration.set(HadoopConfTool.FS_HDFS_IMPL_DISABLE_CACHE, disableCache);
    }

    public void initYarnConf(Map<String, Object> conf){

        String haRmIds = YarnConfTool.getYarnResourcemanagerHaRmIds(conf);
        List<String> addressKeys = YarnConfTool.getYarnResourceManagerAddressKeys(conf);
        String haEnabled = YarnConfTool.getYarnResourcemanagerHaEnabled(conf);

        yarnConfiguration.set(YarnConfTool.YARN_RESOURCEMANAGER_HA_RM_IDS, haRmIds);
        addressKeys.forEach(key -> {
            String rmMgrAddr = YarnConfTool.getYarnResourceManagerAddressVal(conf, key);
            yarnConfiguration.set(key, rmMgrAddr);
        });
        yarnConfiguration.set(YarnConfTool.YARN_RESOURCEMANAGER_HA_ENABLED, haEnabled);//必要
    }

    
	public Configuration getConfiguration(){
		return configuration;
	}
	
	public String getDefaultFs(){
		return configuration.get("fs.defaultFS");
	}

	public Configuration getYarnConfiguration() {
		return yarnConfiguration;
	}
}
