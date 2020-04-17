package com.dtstack.engine.master.utils;

import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.MathUtil;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 解析配置获取Hadoop配置
 * Date: 2018/5/3
 * Company: www.dtstack.com
 *
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
    public static final String DEFAULT_FS_HDFS_IMPL = "org.apache.hadoop.hdfs.DistributedFileSystem";

    public static final String IS_HADOOP_AUTHORIZATION = "hadoop.security.authorization";
    public static final String HADOOP_AUTH_TYPE = "hadoop.security.authentication";
    public static final String BEELINE_PRINCIPAL = "beeline.hs2.connection.principal";
    public static final String HIVE_BIND_HOST = "hive.server2.thrift.bind.host";
    public static final String DFS_HTTP_POLICY = "dfs.http.policy";
    public static final String DFS_DATA_TRANSFER_PROTECTION = "dfs.data.transfer.protection";
    public static final String HADOOP_PROXYUSER_ADMIN_HOSTS = "hadoop.proxyuser.admin.hosts";
    public static final String HADOOP_PROXYUSER_ADMIN_GROUPS = "hadoop.proxyuser.admin.groups";
    public static final String DFS_NAMENODE_KERBEROS_PRINCIPAL = "dfs.namenode.kerberos.principal";
    public static final String DFS_NAMENODE_KEYTAB_FILE = "dfs.namenode.keytab.file";
    public static final String HIVE_SERVER2_AUTHENTICATION_KERBEROS_PRINCIPAL = "hive.server2.authentication.kerberos.principal";
    public static final String HIVE_SERVER2_AUTHENTICATION_KERBEROS_KEYTAB = "hive.server2.authentication.kerberos.keytab";
    public static final String HIVE_METASTORE_KERBEROS_PRINCIPAL = "hive.metastore.kerberos.principal";
    public static final String HIVE_METASTORE_KERBEROS_KEYTAB_FILE = "hive.metastore.kerberos.keytab.file";
    public final static String KEY_HBASE_MASTER_KERBEROS_PRINCIPAL = "hbase.master.kerberos.principal";
    public final static String KEY_HBASE_MASTER_KEYTAB_FILE = "hbase.master.keytab.file";
    public final static String KEY_JAVA_SECURITY_KRB5_CONF = "java.security.krb5.conf";
    public final static String PRINCIPAL = "principal";
    public final static String KEYTAB_PATH = "keytabPath";
    public final static String PRINCIPAL_FILE = "principalFile";

    public static List<String> PRINCIPAL_KEYS = Arrays.asList(
            "hive.server2.authentication.kerberos.principal",
            "hive.metastore.kerberos.principal",
            "beeline.hs2.connection.principal",
            "yarn.resourcemanager.principal",
            "yarn.nodemanager.principal",
            "dfs.namenode.kerberos.principal",
            "dfs.datanode.kerberos.principal",
            "dfs.journalnode.kerberos.principal"
    );

    public static List<String> KEYTAB_FILE_KEYS = Arrays.asList(
            "hive.server2.authentication.kerberos.keytab",
            "hive.metastore.kerberos.keytab.file",
            "dfs.namenode.keytab.file",
            "dfs.datanode.keytab.file",
            "dfs.journalnode.keytab.file",
            "dfs.web.authentication.kerberos.keytab",
            "hive.metastore.kerberos.keytab.file",
            "yarn.resourcemanager.keytab",
            "yarn.nodemanager.keytab"
    );

    public static String getKeyTabFile(Configuration conf){
        String keyTabFile = null;

        int i = 0;
        while (StringUtils.isEmpty(keyTabFile) && i < KEYTAB_FILE_KEYS.size()){
            keyTabFile = conf.get(KEYTAB_FILE_KEYS.get(i));
            i++;
        }

        return keyTabFile;
    }

    public static String getPrincipal(Configuration conf){
        String principal = null;

        int i = 0;
        while (StringUtils.isEmpty(principal) && i < PRINCIPAL_KEYS.size()){
            principal = conf.get(PRINCIPAL_KEYS.get(i));
            i++;
        }

        return principal;
    }

    public static String getHdfsPrincipal(Configuration conf) {
        String principal = conf.get(DFS_NAMENODE_KERBEROS_PRINCIPAL);
        if(StringUtils.isBlank(principal)) {
            throw new RdosDefineException(DFS_NAMENODE_KERBEROS_PRINCIPAL + "不能为空");
        }
        return principal;
    }

    public static String getHdfsKeytab(Configuration conf) {
        String keytab = conf.get(DFS_NAMENODE_KEYTAB_FILE);
        if(StringUtils.isBlank(keytab)) {
            throw new RdosDefineException(DFS_NAMENODE_KEYTAB_FILE + "不能为空");
        }
        return keytab;
    }


    public static String getHivePrincipal(Configuration conf) {
        String principal = conf.get(HIVE_SERVER2_AUTHENTICATION_KERBEROS_PRINCIPAL);
        if(StringUtils.isBlank(principal)) {
            throw new RdosDefineException(HIVE_SERVER2_AUTHENTICATION_KERBEROS_PRINCIPAL + "不能为空");
        }
        return principal;
    }

    public static String getHiveKeytab(Configuration conf) {
        String keytab = conf.get(HIVE_SERVER2_AUTHENTICATION_KERBEROS_KEYTAB);
        if(StringUtils.isBlank(keytab)) {
            throw new RdosDefineException(HIVE_SERVER2_AUTHENTICATION_KERBEROS_KEYTAB + "不能为空");
        }
        return keytab;
    }

    public static String getAuthType(Map<String, Object> conf){
        return MathUtil.getString(conf.get(HADOOP_AUTH_TYPE));
    }

    public static String getDfsNameServices(Map<String, Object> conf) {
        String nameServices = MathUtil.getString(conf.get(DFS_NAME_SERVICES));
        return nameServices;
    }

    public static String getFSDefaults(Map<String, Object> conf) {
        String defaultFs = MathUtil.getString(conf.get(FS_DEFAULTFS));
        Preconditions.checkNotNull(defaultFs, FS_DEFAULTFS + "不能为空");
        return defaultFs;
    }

    public static String getDfsHaNameNodesKey(Map<String, Object> conf) {
        String nameServices = getDfsNameServices(conf);
        return String.format(DFS_HA_NAMENODES, nameServices);
    }

    public static String getDfsHaNameNodes(Map<String, Object> conf, String key) {
        String dfsHaNameNodes = MathUtil.getString(conf.get(key));
        Preconditions.checkNotNull(dfsHaNameNodes, key + "不能为空");
        return dfsHaNameNodes;
    }

    public static List<String> getDfsNameNodeRpcAddressKeys(Map<String, Object> conf) {

        String nameServices = getDfsNameServices(conf);
        String dfsHaNameNodesKey = String.format(DFS_HA_NAMENODES, nameServices);
        String dfsHaNameNodes = MathUtil.getString(conf.get(dfsHaNameNodesKey));
        Preconditions.checkNotNull(dfsHaNameNodes, "dfs.ha.namenodes 不能为空");
        String[] nameNodeArr = dfsHaNameNodes.split(",");

        List<String> nameNodeRpcAddressKeys = Lists.newArrayList();
        for (String nameNode : nameNodeArr) {
            String nameNodePrcAddressKey = String.format(DFS_NAMENODE_RPC_ADDRESS, nameServices, nameNode);
            nameNodeRpcAddressKeys.add(nameNodePrcAddressKey);
        }

        return nameNodeRpcAddressKeys;
    }

    public static String getDfsNameNodeRpcAddress(Map<String, Object> conf, String key) {
        String nnRpcAddress = MathUtil.getString(conf.get(key));
        Preconditions.checkNotNull(nnRpcAddress, key + "不能为空");
        return nnRpcAddress;
    }

    public static String getClientFailoverProxyProviderKey(Map<String, Object> conf) {
        String nameServices = getDfsNameServices(conf);
        String failoverProxyProviderKey = String.format(DFS_CLIENT_FAILOVER_PROXY_PROVIDER, nameServices);
        return failoverProxyProviderKey;
    }

    public static String getClientFailoverProxyProviderVal(Map<String, Object> conf, String key) {
        String failoverProxyProvider = MathUtil.getString(conf.get(key));
        if (StringUtils.isEmpty(failoverProxyProvider)) {
            return DEFAULT_DFS_CLIENT_FAILOVER_PROXY_PROVIDER;
        }

        return failoverProxyProvider;
    }

    public static String getFsHdfsImpl(Map<String, Object> conf) {
        String fsHdfsImpl = MathUtil.getString(conf.get(FS_HDFS_IMPL));
        if (StringUtils.isEmpty(fsHdfsImpl)) {
            return DEFAULT_FS_HDFS_IMPL;
        }

        return fsHdfsImpl;
    }

    public static String getFsHdfsImplDisableCache(Map<String, Object> conf) {
        String disableCache = MathUtil.getString(conf.get(FS_HDFS_IMPL_DISABLE_CACHE));
        if (Strings.isNullOrEmpty(disableCache)) {
            return "true";
        }

        return disableCache;
    }


    /**
     * Configuration
     */
    public static String getDfsNameServices(Configuration conf) {
        String nameServices = MathUtil.getString(conf.get(DFS_NAME_SERVICES));
        return nameServices;
    }

    public static String getFSDefaults(Configuration conf) {
        String defaultFs = MathUtil.getString(conf.get(FS_DEFAULTFS));
        Preconditions.checkNotNull(defaultFs, FS_DEFAULTFS + "不能为空");
        return defaultFs;
    }

    public static String getDfsHaNameNodesKey(Configuration conf) {
        String nameServices = getDfsNameServices(conf);
        return String.format(DFS_HA_NAMENODES, nameServices);
    }

    public static String getDfsHaNameNodes(Configuration conf, String key) {
        String dfsHaNameNodes = MathUtil.getString(conf.get(key));
        Preconditions.checkNotNull(dfsHaNameNodes, key + "不能为空");
        return dfsHaNameNodes;
    }

    public static List<String> getDfsNameNodeRpcAddressKeys(Configuration conf) {

        String nameServices = getDfsNameServices(conf);
        String dfsHaNameNodesKey = String.format(DFS_HA_NAMENODES, nameServices);
        String dfsHaNameNodes = MathUtil.getString(conf.get(dfsHaNameNodesKey));
        Preconditions.checkNotNull(dfsHaNameNodes, "dfs.ha.namenodes 不能为空");
        String[] nameNodeArr = dfsHaNameNodes.split(",");

        List<String> nameNodeRpcAddressKeys = Lists.newArrayList();
        for (String nameNode : nameNodeArr) {
            String nameNodePrcAddressKey = String.format(DFS_NAMENODE_RPC_ADDRESS, nameServices, nameNode);
            nameNodeRpcAddressKeys.add(nameNodePrcAddressKey);
        }

        return nameNodeRpcAddressKeys;
    }

    public static String getDfsNameNodeRpcAddress(Configuration conf, String key) {
        String nnRpcAddress = MathUtil.getString(conf.get(key));
        Preconditions.checkNotNull(nnRpcAddress, key + "不能为空");
        return nnRpcAddress;
    }

    public static String getClientFailoverProxyProviderKey(Configuration conf) {
        String nameServices = getDfsNameServices(conf);
        String failoverProxyProviderKey = String.format(DFS_CLIENT_FAILOVER_PROXY_PROVIDER, nameServices);
        return failoverProxyProviderKey;
    }

    public static String getClientFailoverProxyProviderVal(Configuration conf, String key) {
        String failoverProxyProvider = MathUtil.getString(conf.get(key));
        if (StringUtils.isEmpty(failoverProxyProvider)) {
            return DEFAULT_DFS_CLIENT_FAILOVER_PROXY_PROVIDER;
        }

        return failoverProxyProvider;
    }

    public static String getFsHdfsImpl(Configuration conf) {
        String fsHdfsImpl = MathUtil.getString(conf.get(FS_HDFS_IMPL));
        if (StringUtils.isEmpty(fsHdfsImpl)) {
            return DEFAULT_FS_HDFS_IMPL;
        }

        return fsHdfsImpl;
    }

    public static String getFsHdfsImplDisableCache(Configuration conf) {
        String disableCache = MathUtil.getString(conf.get(FS_HDFS_IMPL_DISABLE_CACHE));
        if (Strings.isNullOrEmpty(disableCache)) {
            return "true";
        }

        return disableCache;
    }
}
