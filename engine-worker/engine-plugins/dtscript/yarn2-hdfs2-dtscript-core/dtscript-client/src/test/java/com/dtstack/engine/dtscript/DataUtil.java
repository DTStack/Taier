package com.dtstack.engine.dtscript;

import com.alibaba.fastjson.JSON;
import com.dtstack.engine.common.pojo.ParamAction;
import com.dtstack.engine.common.JobClient;

/**
 * @description:
 * @program: engine-all
 * @author: lany
 * @create: 2020/11/25 15:52
 */
public class DataUtil {

    public static String getPluginInfo() throws Exception {
        return getJobClient().getPluginInfo();
    }

    public static JobClient getJobClient() throws Exception {
        ParamAction paramAction = JSON.parseObject(getRequestJson(), ParamAction.class);
        return new JobClient(paramAction);
    }

    private static String getRequestJson() {
        return "{\n" +
                "    \"isFailRetry\": true,\n" +
                "    \"sqlText\": \"#name shixi_shell\\n#type Shell\\n#author admin@dtstack.com\\n#create time 2020-08-18 21:12:20\\n#desc \\n\\n\\necho 'ok'\",\n" +
                "    \"computeType\": 1,\n" +
                "    \"exeArgs\": \"--files hdfs://ns1/rdos/batch/shell_1_51_shixi_shell_1598239679560.sh --python-version 0 --app-type shell --app-name shixi_shell\",\n" +
                "    \"pluginInfo\": {\n" +
                "        \"cluster\": \"default\",\n" +
                "        \"pythonConf\": {},\n" +
                "        \"jupyterConf\": {\n" +
                "            \"c.NotebookApp.token\": \"''\",\n" +
                "            \"c.NotebookApp.allow_remote_access\": \"True\",\n" +
                "            \"c.NotebookApp.ip\": \"'*'\",\n" +
                "            \"c.NotebookApp.default_url\": \"'/lab'\",\n" +
                "            \"c.NotebookApp.open_browser\": \"False\"\n" +
                "        },\n" +
                "        \"typeName\": \"dtscript-hadoop3\",\n" +
                "        \"hadoopConf\": {\n" +
                "            \"fs.defaultFS\": \"hdfs://ns1\",\n" +
                "            \"dfs.namenode.shared.edits.dir\": \"qjournal://127.0.0.1:8485;127.0.0.1:8485;127.0.0.1:8485/namenode-ha-data\",\n" +
                "            \"hadoop.proxyuser.admin.groups\": \"*\",\n" +
                "            \"dfs.replication\": \"2\",\n" +
                "            \"dfs.ha.fencing.methods\": \"sshfence\",\n" +
                "            \"dfs.client.failover.proxy.provider.ns1\": \"org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider\",\n" +
                "            \"dfs.ha.fencing.ssh.private-key-files\": \"~/.ssh/id_rsa\",\n" +
                "            \"dfs.nameservices\": \"ns1\",\n" +
                "            \"dfs.safemode.threshold.pct\": \"0.5\",\n" +
                "            \"dfs.ha.namenodes.ns1\": \"nn1,nn2\",\n" +
                "            \"ha.zookeeper.session-timeout.ms\": \"5000\",\n" +
                "            \"hadoop.tmp.dir\": \"/data/hadoop_${user.name}\",\n" +
                "            \"dfs.journalnode.edits.dir\": \"/data/dtstack/hadoop/journal\",\n" +
                "            \"dfs.namenode.http-address.ns1.nn2\": \"127.0.0.1:50070\",\n" +
                "            \"dfs.journalnode.rpc-address\": \"0.0.0.0:8485\",\n" +
                "            \"dfs.namenode.http-address.ns1.nn1\": \"127.0.0.1:50070\",\n" +
                "            \"dfs.journalnode.http-address\": \"0.0.0.0:8480\",\n" +
                "            \"hadoop.proxyuser.admin.hosts\": \"*\",\n" +
                "            \"dfs.namenode.rpc-address.ns1.nn2\": \"127.0.0.1:9000\",\n" +
                "            \"dfs.namenode.rpc-address.ns1.nn1\": \"127.0.0.1:9000\",\n" +
                "            \"ha.zookeeper.quorum\": \"127.0.0.1:2181,127.0.0.1:2181,127.0.0.1:2181\",\n" +
                "            \"dfs.ha.automatic-failover.enabled\": \"true\"\n" +
                "        },\n" +
                "        \"md5zip\": \"\",\n" +
                "        \"tenantId\": 81,\n" +
                "        \"yarnConf\": {\n" +
                "            \"yarn.resourcemanager.zk-address\": \"127.0.0.1:2181,127.0.0.1:2181,127.0.0.1:2181\",\n" +
                "            \"yarn.resourcemanager.admin.address.rm1\": \"127.0.0.1:8033\",\n" +
                "            \"yarn.resourcemanager.webapp.address.rm2\": \"127.0.0.1:8088\",\n" +
                "            \"yarn.log.server.url\": \"http://127.0.0.1:19888/jobhistory/logs/\",\n" +
                "            \"yarn.resourcemanager.admin.address.rm2\": \"127.0.0.1:8033\",\n" +
                "            \"yarn.resourcemanager.webapp.address.rm1\": \"127.0.0.1:8088\",\n" +
                "            \"yarn.resourcemanager.ha.rm-ids\": \"rm1,rm2\",\n" +
                "            \"yarn.resourcemanager.ha.automatic-failover.zk-base-path\": \"/yarn-leader-election\",\n" +
                "            \"yarn.client.failover-proxy-provider\": \"org.apache.hadoop.yarn.client.ConfiguredRMFailoverProxyProvider\",\n" +
                "            \"yarn.resourcemanager.scheduler.address.rm1\": \"127.0.0.1:8030\",\n" +
                "            \"yarn.resourcemanager.scheduler.address.rm2\": \"127.0.0.1:8030\",\n" +
                "            \"yarn.nodemanager.delete.debug-delay-sec\": \"600\",\n" +
                "            \"yarn.resourcemanager.address.rm1\": \"127.0.0.1:8032\",\n" +
                "            \"yarn.log-aggregation.retain-seconds\": \"2592000\",\n" +
                "            \"yarn.nodemanager.resource.memory-mb\": \"8000\",\n" +
                "            \"yarn.resourcemanager.ha.enabled\": \"true\",\n" +
                "            \"yarn.resourcemanager.address.rm2\": \"127.0.0.1:8032\",\n" +
                "            \"yarn.resourcemanager.cluster-id\": \"yarn-rm-cluster\",\n" +
                "            \"yarn.scheduler.minimum-allocation-mb\": \"512\",\n" +
                "            \"yarn.nodemanager.aux-services\": \"mapreduce_shuffle\",\n" +
                "            \"yarn.resourcemanager.resource-tracker.address.rm1\": \"127.0.0.1:8031\",\n" +
                "            \"yarn.nodemanager.resource.cpu-vcores\": \"10\",\n" +
                "            \"yarn.resourcemanager.resource-tracker.address.rm2\": \"127.0.0.1:8031\",\n" +
                "            \"yarn.nodemanager.pmem-check-enabled\": \"false\",\n" +
                "            \"yarn.nodemanager.remote-app-log-dir\": \"/tmp/logs\",\n" +
                "            \"yarn.resourcemanager.ha.automatic-failover.enabled\": \"true\",\n" +
                "            \"yarn.nodemanager.vmem-check-enabled\": \"false\",\n" +
                "            \"yarn.resourcemanager.hostname.rm2\": \"127.0.0.1\",\n" +
                "            \"yarn.nodemanager.webapp.address\": \"127.0.0.1:8042\",\n" +
                "            \"yarn.resourcemanager.hostname.rm1\": \"127.0.0.1\",\n" +
                "            \"yarn.nodemanager.aux-services.mapreduce_shuffle.class\": \"org.apache.hadoop.mapred.ShuffleHandler\",\n" +
                "            \"yarn.resourcemanager.recovery.enabled\": \"true\",\n" +
                "            \"yarn.log-aggregation-enable\": \"true\",\n" +
                "            \"yarn.resourcemanager.store.class\": \"org.apache.hadoop.yarn.server.resourcemanager.recovery.ZKRMStateStore\",\n" +
                "            \"yarn.nodemanager.vmem-pmem-ratio\": \"4\",\n" +
                "            \"yarn.resourcemanager.zk-state-store.address\": \"127.0.0.1:2181,127.0.0.1:2181,127.0.0.1:2181\",\n" +
                "            \"ha.zookeeper.quorum\": \"127.0.0.1:2181,127.0.0.1:2181,127.0.0.1:2181\"\n" +
                "        },\n" +
                "        \"hadoop.home.dir\": \"/opt/dtstack/hadoop-3.0.0\",\n" +
                "        \"sftpConf\": {\n" +
                "            \"path\": \"/data/sftp\",\n" +
                "            \"password\": \"abc123\",\n" +
                "            \"port\": \"22\",\n" +
                "            \"auth\": \"1\",\n" +
                "            \"host\": \"172.16.10.168\",\n" +
                "            \"username\": \"root\"\n" +
                "        },\n" +
                "        \"java.home\": \"/opt/dtstack/java\",\n" +
                "        \"queue\": \"c\"\n" +
                "    },\n" +
                "    \"engineType\": \"dtScript\",\n" +
                "    \"taskParams\": \"worker.memory=512m\\nworker.cores=1\\nexclusive=false\\nworker.num=1\\njob.priority=10\",\n" +
                "    \"maxRetryNum\": 3,\n" +
                "    \"taskType\": 3,\n" +
                "    \"groupName\": \"default_c\",\n" +
                "    \"clusterName\": \"default\",\n" +
                "    \"name\": \"cronJob_shixi_shell_20200824000000\",\n" +
                "    \"tenantId\": 1,\n" +
                "    \"taskId\": \"application_1605237729642_127145\"\n" +
                "}";
    }

}
