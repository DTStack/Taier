/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.base.util;

import com.dtstack.taier.pluginapi.exception.PluginDefineException;
import com.dtstack.taier.pluginapi.util.MathUtil;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeys;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

    private static final Logger LOG = LoggerFactory.getLogger(KerberosUtils.class);

    public static final String DFS_NAME_SERVICES = "dfs.nameservices";
    public static final String FS_DEFAULTFS = "fs.defaultFS";
    public static final String DFS_HA_NAMENODES = "dfs.ha.namenodes.%s";
    public static final String DFS_NAMENODE_RPC_ADDRESS = "dfs.namenode.rpc-address.%s.%s";
    public static final String DFS_CLIENT_FAILOVER_PROXY_PROVIDER = "dfs.client.failover.proxy.provider.%s";
    public static final String FS_HDFS_IMPL_DISABLE_CACHE = "fs.hdfs.impl.disable.cache";
    public static final String FS_LOCAL_IMPL_DISABLE_CACHE = "fs.file.impl.disable.cache";
    public static final String FS_HDFS_IMPL = "fs.hdfs.impl";
    public static final String HADOOP_AUTH_TYPE = "hadoop.security.authentication";
    public static final String IS_HADOOP_AUTHORIZATION = "hadoop.security.authorization";

    private static final String DEFAULT_DFS_CLIENT_FAILOVER_PROXY_PROVIDER = "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider";
    private static final String DEFAULT_FS_HDFS_IMPL = "org.apache.hadoop.hdfs.DistributedFileSystem";
    public static final String DFS_HTTP_POLICY = "dfs.http.policy";
    public static final String DFS_DATA_TRANSFER_PROTECTION = "dfs.data.transfer.protection";
    public static final String HADOOP_PROXYUSER_ADMIN_HOSTS = "hadoop.proxyuser.admin.hosts";
    public static final String HADOOP_PROXYUSER_ADMIN_GROUPS = "hadoop.proxyuser.admin.groups";

    public final static String KEY_JAVA_SECURITY_KRB5_CONF = "java.security.krb5.conf";
    public final static String PRINCIPAL = "principal";
    public final static String KEYTAB_PATH = "keytabPath";
    public final static String PRINCIPAL_FILE = "principalFile";

    public static String getAuthType(Map<String, Object> conf) {
        return MathUtil.getString(conf.get(HADOOP_AUTH_TYPE));
    }

    public static String getDfsNameServices(Map<String, Object> conf) {
        String nameServices = MathUtil.getString(conf.get(DFS_NAME_SERVICES));
        return nameServices;
    }

    public static String getFsDefaults(Map<String, Object> conf) {
        String defaultFs = MathUtil.getString(conf.get(FS_DEFAULTFS));
        Preconditions.checkNotNull(defaultFs, FS_DEFAULTFS + "can not empty");
        return defaultFs;
    }

    public static String getDfsHaNameNodesKey(Map<String, Object> conf) {
        String nameServices = getDfsNameServices(conf);
        return String.format(DFS_HA_NAMENODES, nameServices);
    }

    public static String getDfsHaNameNodes(Map<String, Object> conf, String key) {
        String dfsHaNameNodes = MathUtil.getString(conf.get(key));
        Preconditions.checkNotNull(dfsHaNameNodes, key + "can not empty");
        return dfsHaNameNodes;
    }

    public static List<String> getDfsNameNodeRpcAddressKeys(Map<String, Object> conf) {

        String nameServices = getDfsNameServices(conf);
        String dfsHaNameNodesKey = String.format(DFS_HA_NAMENODES, nameServices);
        String dfsHaNameNodes = MathUtil.getString(conf.get(dfsHaNameNodesKey));
        Preconditions.checkNotNull(dfsHaNameNodes, "dfs.ha.namenodes can not empty");
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
        Preconditions.checkNotNull(nnRpcAddress, key + "can not empty");
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

    public static void setFsHdfsImplDisableCache(Configuration conf) {
        conf.setBoolean(FS_HDFS_IMPL_DISABLE_CACHE, true);
    }

    public static void setFsLocalImplDisableCache(Configuration conf) {
        conf.setBoolean(FS_LOCAL_IMPL_DISABLE_CACHE, true);
    }

    public static void setDefaultYarnConf(Configuration yarnConf, Map<String, Object> yarnMap) {
        yarnConf.setBoolean(CommonConfigurationKeys.IPC_CLIENT_FALLBACK_TO_SIMPLE_AUTH_ALLOWED_KEY, true);

        if (yarnMap == null) {
            return;
        }

        if (yarnMap.get(YarnConfiguration.RESOURCEMANAGER_CONNECT_MAX_WAIT_MS) != null) {
            yarnConf.set(YarnConfiguration.RESOURCEMANAGER_CONNECT_MAX_WAIT_MS, (String) yarnMap.get(YarnConfiguration.RESOURCEMANAGER_CONNECT_MAX_WAIT_MS));
        } else {
            yarnConf.setLong(YarnConfiguration.RESOURCEMANAGER_CONNECT_MAX_WAIT_MS, 15000L);
        }

        if (yarnMap.get(YarnConfiguration.RESOURCEMANAGER_CONNECT_RETRY_INTERVAL_MS) != null) {
            yarnConf.set(YarnConfiguration.RESOURCEMANAGER_CONNECT_RETRY_INTERVAL_MS, (String) yarnMap.get(YarnConfiguration.RESOURCEMANAGER_CONNECT_RETRY_INTERVAL_MS));
        } else {
            yarnConf.setLong(YarnConfiguration.RESOURCEMANAGER_CONNECT_RETRY_INTERVAL_MS, 5000L);
        }

        LOG.info("yarn.resourcemanager.connect.max-wait.ms:{} yarn.resourcemanager.connect.retry-interval.ms:{}", yarnConf.getLong(YarnConfiguration.RESOURCEMANAGER_CONNECT_MAX_WAIT_MS, -1), yarnConf.getLong(YarnConfiguration.RESOURCEMANAGER_CONNECT_RETRY_INTERVAL_MS, -1));
    }

    public static byte[] serializeHadoopConf(Configuration hadoopConf) {

        try (
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                DataOutputStream dataout = new DataOutputStream(out);
        ) {
            hadoopConf.write(dataout);
            return out.toByteArray();
        } catch (Exception e) {
            LOG.error("Serialize hadoopConf happens error: {}", e.getMessage());
            throw new PluginDefineException(e);
        }
    }

    public static Configuration deserializeHadoopConf(byte[] bytes) {
        Configuration hadoopConf = new Configuration(false);
        try (
                ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                DataInputStream datain = new DataInputStream(in);
        ) {
            hadoopConf.readFields(datain);
            return hadoopConf;
        } catch (IOException e) {
            LOG.error("Deserialize hadoopConf happens error: {}", e.getMessage());
            throw new PluginDefineException(e);
        }
    }

    public static Configuration deserializeYanrConf(byte[] bytes) {
        Configuration hadoopConf = new Configuration(false);
        YarnConfiguration yanrConf = new YarnConfiguration(hadoopConf);
        try (
                ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                DataInputStream datain = new DataInputStream(in);
        ) {
            yanrConf.readFields(datain);
            return yanrConf;
        } catch (IOException e) {
            LOG.error("Deserialize yanrConf happens error: {}", e.getMessage());
            throw new PluginDefineException(e);
        }
    }

    public static void writeHadoopXml(Configuration hadoopConf, File outFile) {
        try (
                FileWriter fwrt = new FileWriter(outFile);
        ) {
            hadoopConf.writeXml(fwrt);
        } catch (Exception e) {
            LOG.error("WriteHadoopXml happens error: {}", e.getMessage());
            throw new PluginDefineException(e);
        }
    }
}
