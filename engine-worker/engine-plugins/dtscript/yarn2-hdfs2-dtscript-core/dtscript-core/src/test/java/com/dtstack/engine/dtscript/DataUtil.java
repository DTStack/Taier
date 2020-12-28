package com.dtstack.engine.dtscript;

import com.alibaba.fastjson.JSON;
import com.dtstack.engine.api.pojo.ParamAction;
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

    public static JobClient getJobClient2() throws Exception{
        ParamAction paramAction = new ParamAction();
        paramAction.setExeArgs("--files /data/Algorithm/main_v2.py " +
                "--cmd-opts eyJvdXRwdXRzIjpbeyJoaXZlX3BvcnQiOiIxMDAwMCIsImRhdGFiYXNlIjoiZ3VjaGVuIiwibmFtZSI6ImhpdmUiLCJoZGZzX2hvc3QiOiIxNzIuMTYuMTAuMTE1Ojk4NzAsMTcyLjE2LjEwLjExNDo5ODcwIiwiaGFkb29wX3VzZXJuYW1lIjoiYWRtaW4iLCJoaXZlX2hvc3QiOiIxNzIuMTYuMTAuMTE1IiwidGFibGUiOiJzY2llbmNlX3RtcF84ODMzXzkxIn0seyJwYXRoIjoiL2R0SW5zaWdodC9zY2llbmNlL2NvbXBvbmVudF9tb2RlbC90bXAvODc1OS84ODMzLzEyIiwibmFtZSI6InB5bW9kZWwiLCJoZGZzX2hvc3QiOiIxNzIuMTYuMTAuMTE1Ojk4NzAsMTcyLjE2LjEwLjExNDo5ODcwIiwiaGFkb29wX3VzZXJuYW1lIjoiYWRtaW4ifSx7InBhdGgiOiIvZHRJbnNpZ2h0L3NjaWVuY2UvY29tcG9uZW50X21vZGVsL3RtcC84NzU5Lzg4MzMvIiwibmFtZSI6Imhpc3RvZ3JhbSIsImhkZnNfaG9zdCI6IjE3Mi4xNi4xMC4xMTU6OTg3MCwxNzIuMTYuMTAuMTE0Ojk4NzAiLCJoYWRvb3BfdXNlcm5hbWUiOiJhZG1pbiJ9XSwiY29sX3NldHRpbmdzIjp7ImNvbCI6WyJEYXRlIl0sImtleSI6WyJ1c2VyX2lkIl19LCJpbnB1dHMiOlt7InBhdGgiOiIvZHRJbnNpZ2h0L2hpdmUvd2FyZWhvdXNlL2d1Y2hlbi5kYi9icmVhZGJhc2tldCIsImNvbHVtbnMiOlsiRGF0ZSIsIlRpbWUiLCJUcmFuc2FjdGlvbiIsIkl0ZW0iLCJ1c2VyX2lkIiwicHJpY2UiXSwibmFtZSI6ImhkZnMiLCJoZGZzX2hvc3QiOiIxNzIuMTYuMTAuMTE1Ojk4NzAsMTcyLjE2LjEwLjExNDo5ODcwIiwiaGFkb29wX3VzZXJuYW1lIjoiYWRtaW4iLCJzZXAiOiIsIn1dLCJtb2RlbF9zZXR0aW5ncyI6eyJtZXRob2QiOltbIm1heCJdXSwidXNlY29weSI6ZmFsc2V9LCJjbGFzc19uYW1lIjoiQWdncmVnYXRpb25WMkFpIn0= " +
                "--app-type python3 " +
                "--python-version 3.x "  +
                "--nodes node1" +
                "--am-memory 412m" +
                "--am-cores 1" +
                "--worker-memory 2" +
                "--worker-cores 2" +
                "--app-memory 512m" +
                "--priority 1" +
                "--logLevel INFO" +
                "--queue a" +
                "--output dsds" +
                "--user-path vds2e" +
                "--worker-num 2" +
                "--worker-reserved-memory 512m" +
                "--maxAppAttempts 3" +
                "--app-name run_pYTHON");
        paramAction.setTaskParams("worker.cores=1\nworker.memory=512");
        paramAction.setTaskId("35e24e9f");
        paramAction.setName("run_Python_task_1606442812431");
        paramAction.setEngineType("dtscript");
        paramAction.setComputeType(1);
        paramAction.setTaskType(3);
        paramAction.setTenantId(395L);
        paramAction.setDtuicTenantId(395L);
//        paramAction.set();
        return  new JobClient(paramAction);
    }

}
