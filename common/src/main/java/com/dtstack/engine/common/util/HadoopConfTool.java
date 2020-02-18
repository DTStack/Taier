package com.dtstack.engine.common.util;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;

import java.util.List;
import java.util.Map;

/**
 * 解析配置获取Hadoop配置
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
    public static final String HADOOP_AUTH_TYPE = "hadoop.security.authentication";
    public static final String IS_HADOOP_AUTHORIZATION = "hadoop.security.authorization";

    private static final String DEFAULT_DFS_CLIENT_FAILOVER_PROXY_PROVIDER = "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider";
    private static final String DEFAULT_FS_HDFS_IMPL = "org.apache.hadoop.hdfs.DistributedFileSystem";
    public static final String DFS_HTTP_POLICY = "dfs.http.policy";
    public static final String DFS_DATA_TRANSFER_PROTECTION = "dfs.data.transfer.protection";
    public static final String HADOOP_PROXYUSER_ADMIN_HOSTS = "hadoop.proxyuser.admin.hosts";
    public static final String HADOOP_PROXYUSER_ADMIN_GROUPS = "hadoop.proxyuser.admin.groups";

    public static String getAuthType(Map<String, Object> conf){
        return MathUtil.getString(conf.get(HADOOP_AUTH_TYPE));
    }

    public static String getDfsNameServices(Map<String, Object> conf){
        String nameServices = MathUtil.getString(conf.get(DFS_NAME_SERVICES));
        return nameServices;
    }

    public static String getFsDefaults(Map<String, Object> conf){
        String defaultFs = MathUtil.getString(conf.get(FS_DEFAULTFS));
        Preconditions.checkNotNull(defaultFs, FS_DEFAULTFS + "can not empty");
        return defaultFs;
    }

    public static String getDfsHaNameNodesKey(Map<String, Object> conf){
        String nameServices = getDfsNameServices(conf);
        return String.format(DFS_HA_NAMENODES, nameServices);
    }

    public static String getDfsHaNameNodes(Map<String, Object> conf, String key){
        String dfsHaNameNodes = MathUtil.getString(conf.get(key));
        Preconditions.checkNotNull(dfsHaNameNodes, key + "can not empty");
        return dfsHaNameNodes;
    }

    public static List<String> getDfsNameNodeRpcAddressKeys(Map<String, Object> conf){

        String nameServices = getDfsNameServices(conf);
        String dfsHaNameNodesKey = String.format(DFS_HA_NAMENODES, nameServices);
        String dfsHaNameNodes = MathUtil.getString(conf.get(dfsHaNameNodesKey));
        Preconditions.checkNotNull(dfsHaNameNodes, "dfs.ha.namenodes can not empty");
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
        Preconditions.checkNotNull(nnRpcAddress, key + "can not empty");
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
        if(Strings.isNullOrEmpty(disableCache)){
            return "true";
        }

        return disableCache;
    }

    public static void setFsHdfsImplDisableCache(Configuration conf){
        conf.setBoolean(FS_HDFS_IMPL_DISABLE_CACHE, true);
    }
}
