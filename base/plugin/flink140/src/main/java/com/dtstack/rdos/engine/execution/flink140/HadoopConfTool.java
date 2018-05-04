package com.dtstack.rdos.engine.execution.flink140;

import avro.shaded.com.google.common.collect.Lists;
import com.dtstack.rdos.common.util.MathUtil;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hdfs.DistributedFileSystem;

import java.util.List;
import java.util.Map;

/**
 * FIXME 公共配置--之后抽到common
 * Date: 2018/5/3
 * Company: www.dtstack.com
 * @author xuchao
 */

public class HadoopConfTool {

    public static final String DFS_NAME_SERVICES = "dfs.nameservices";
    public static final String FS_DEFAULTFS = "fs.defaultFS";
    public static final String DFS_HA_NAMENODES = "dfs.ha.namenodes.%s";
    public static final String DFS_NAMENODE_RPC_ADDRESS = "dfs.namenode.rpc-address.%s.%s";
    public static final String DFS_CLIENT_FAILOVER_PROXY_PROVIDER = "dfs.client.failover.proxy.provider.%s";
    public static final String FS_HDFS_IMPL_DISABLE_CACHE = "fs.hdfs.impl.disable.cache";
    public static final String FS_HDFS_IMPL = "fs.hdfs.impl";

    private static final String DEFAULT_DFS_CLIENT_FAILOVER_PROXY_PROVIDER = "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider";
    private static final String DEFAULT_FS_HDFS_IMPL = DistributedFileSystem.class.getName();


    public static String getDfsNameServices(Map<String, Object> conf){
        String nameServices = MathUtil.getString(conf.get(DFS_NAME_SERVICES));
        Preconditions.checkNotNull(nameServices, DFS_NAME_SERVICES + "不能为空");
        return nameServices;
    }

    public static String getFSDefaults(Map<String, Object> conf){
        String defaultFs = MathUtil.getString(conf.get(FS_DEFAULTFS));
        Preconditions.checkNotNull(defaultFs, FS_DEFAULTFS + "不能为空");
        return defaultFs;
    }

    public static String getDfsHaNameNodesKey(Map<String, Object> conf){
        String nameServices = getDfsNameServices(conf);
        return String.format(DFS_HA_NAMENODES, nameServices);
    }

    public static String getDfsHaNameNodes(Map<String, Object> conf, String key){
        String dfsHaNameNodes = MathUtil.getString(conf.get(key));
        Preconditions.checkNotNull(dfsHaNameNodes, key + "不能为空");
        return dfsHaNameNodes;
    }

    public static List<String> getDfsNameNodeRpcAddressKeys(Map<String, Object> conf){

        String nameServices = getDfsNameServices(conf);
        String dfsHaNameNodesKey = String.format(DFS_HA_NAMENODES, nameServices);
        String dfsHaNameNodes = MathUtil.getString(conf.get(dfsHaNameNodesKey));
        Preconditions.checkNotNull(dfsHaNameNodes, "dfs.ha.namenodes 不能为空");
        String[] nameNodeArr = dfsHaNameNodes.split(",");

        List<String> nameNodeRpcAddressKeys = Lists.newArrayList();
        for(String nameNode : nameNodeArr){
            String nameNodePrcAddressKey = String.format(DFS_NAMENODE_RPC_ADDRESS, nameServices, nameNode);
            nameNodeRpcAddressKeys.add(nameNodePrcAddressKey);
        }

        return nameNodeRpcAddressKeys;
    }

    public static String getDfsNameNodeRpcAddress(Map<String, Object> conf, String key){
        String nnRpcAddress = MathUtil.getString(conf.get(key));
        Preconditions.checkNotNull(nnRpcAddress, key + "不能为空");
        return nnRpcAddress;
    }

    public static String getClientFailoverProxyProviderKey(Map<String, Object> conf){
        String nameServices = getDfsNameServices(conf);
        String failoverProxyProviderKey = String.format(DFS_CLIENT_FAILOVER_PROXY_PROVIDER, nameServices);
        return failoverProxyProviderKey;
    }

    public static String getClientFailoverProxyProviderVal(Map<String, Object> conf, String key){
        String failoverProxyProvider = MathUtil.getString(conf.get(key));
        if(StringUtils.isEmpty(failoverProxyProvider)){
            return DEFAULT_DFS_CLIENT_FAILOVER_PROXY_PROVIDER;
        }

        return failoverProxyProvider;
    }

    public static String getFsHdfsImpl(Map<String, Object> conf){
        String fsHdfsImpl = MathUtil.getString(conf.get(FS_HDFS_IMPL));
        if(StringUtils.isEmpty(fsHdfsImpl)){
            return DEFAULT_FS_HDFS_IMPL;
        }

        return fsHdfsImpl;
    }

    public static String getFsHdfsImplDisableCache(Map<String, Object> conf){
        String disableCache = MathUtil.getString(conf.get(FS_HDFS_IMPL_DISABLE_CACHE));
        return disableCache;
    }
}
