package com.dtstack.engine.master.multiengine;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.vo.components.ComponentsConfigOfComponentsVO;
import com.dtstack.engine.common.enums.MultiEngineType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.worker.WorkerOperator;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.multiengine.factory.MultiEngineFactory;
import com.dtstack.engine.master.utils.Template;
import com.dtstack.schedule.common.enums.AppType;
import com.dtstack.schedule.common.enums.EScheduleJobType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.util.Assert;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * @author yuebai
 * @date 2020-12-24
 */
public class HadoopJobStartTriggerTest extends AbstractTest {


    @Autowired
    private ScheduleTaskShadeDao scheduleTaskShadeDao;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @MockBean
    private ComponentService componentService;

    @MockBean
    private WorkerOperator workerOperator;

    @MockBean
    private ClusterService clusterService;

    @Autowired
    private MultiEngineFactory multiEngineFactory;

    @Before
    public void init() {
        when(clusterService.pluginInfoJSON(any(),any(),any(),any(),any())).thenReturn(new JSONObject());
        when(componentService.formatHadoopVersion(any(), any())).thenReturn("yarn2-hdfs2-hadoop");
        try {
            when(workerOperator.uploadStringToHdfs(any(), any(), any(), any())).thenReturn("hdfs://ns1/dtInsight/test.sh");
            when(workerOperator.executeQuery(anyString(), anyString(), anyString(), anyString())).thenReturn(new ArrayList<>());
            String data ="[{\n" +
                    "            \"componentTypeCode\": 0,\n" +
                    "            \"componentConfig\": \"{\\\"deploymode\\\":[\\\"session\\\",\\\"perjob\\\"],\\\"perjob\\\":{\\\"historyserver.web.address\\\":\\\"172.16.100.105\\\",\\\"historyserver.web.port\\\":\\\"8082\\\",\\\"remotePluginRootDir\\\":\\\"/data/insight_plugin/flinkplugin\\\",\\\"state.checkpoints.num-retained\\\":\\\"11\\\",\\\"metrics.reporter.promgateway.port\\\":\\\"9091\\\",\\\"prometheusHost\\\":\\\"172.16.100.105\\\",\\\"flinkJarPath\\\":\\\"/opt/dtstack/flink-1.8.3/lib\\\",\\\"high-availability.cluster-id\\\":\\\"/default\\\",\\\"metrics.reporter.promgateway.jobName\\\":\\\"181job\\\",\\\"jarTmpDir\\\":\\\"./tmp180\\\",\\\"yarn.taskmanager.heap.mb\\\":\\\"1024\\\",\\\"monitorAcceptedApp\\\":\\\"false\\\",\\\"state.savepoints.dir\\\":\\\"hdfs://ns1/dtInsight/flink180_1/savepoints\\\",\\\"clusterMode\\\":\\\"perjob\\\",\\\"high-availability.zookeeper.path.root\\\":\\\"/flink180\\\",\\\"metrics.reporter.promgateway.class\\\":\\\"org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter\\\",\\\"yarn.jobmanager.heap.mb\\\":\\\"1024\\\",\\\"high-availability.storageDir\\\":\\\"hdfs://ns1/flink180/ha\\\",\\\"sessionStartAuto\\\":\\\"false\\\",\\\"flinkPluginRoot\\\":\\\"/opt/dtstack/180_flinkplugin/\\\",\\\"metrics.reporter.promgateway.randomJobNameSuffix\\\":\\\"true\\\",\\\"yarn.application-attempts\\\":\\\"0\\\",\\\"flink.env.java.opts\\\":\\\"-XX:MaxMetaspaceSize=500m\\\",\\\"yarn.taskmanager.numberOfTaskManager\\\":\\\"2\\\",\\\"prometheusClass\\\":\\\"com.dtstack.jlogstash.metrics.promethues.PrometheusPushGatewayReporter\\\",\\\"gatewayJobName\\\":\\\"pushgateway\\\",\\\"pluginLoadMode\\\":\\\"shipfile\\\",\\\"taskparams.taskmanager.heap.mb\\\":\\\"20\\\",\\\"jobmanager.archive.fs.dir\\\":\\\"hdfs://ns1/flink180/completed-jobs\\\",\\\"classloader.resolve-order\\\":\\\"parent-first\\\",\\\"state.backend.incremental\\\":\\\"true\\\",\\\"metrics.reporter.promgateway.deleteOnShutdown\\\":\\\"true\\\",\\\"flinkInterval\\\":\\\"5 SECONDS\\\",\\\"high-availability.zookeeper.quorum\\\":\\\"172.16.101.13:2181, 172.16.100.105:2181, 172.16.100.132:2181\\\",\\\"yarn.application-attempt-failures-validity-interval\\\":\\\"3600000\\\",\\\"high-availability\\\":\\\"ZOOKEEPER\\\",\\\"submitTimeout\\\":\\\"5\\\",\\\"state.backend\\\":\\\"RocksDB\\\",\\\"yarn.taskmanager.numberOfTaskSlots\\\":\\\"2\\\",\\\"metrics.reporter.promgateway.host\\\":\\\"172.16.100.105\\\",\\\"prometheusPort\\\":\\\"9090\\\",\\\"queue\\\":\\\"default\\\",\\\"state.checkpoints.dir\\\":\\\"hdfs://ns1/dtInsight/flink180_1/savepoints\\\"},\\\"session\\\":{\\\"historyserver.web.address\\\":\\\"172.16.100.105\\\",\\\"historyserver.web.port\\\":\\\"8082\\\",\\\"remotePluginRootDir\\\":\\\"/opt/dtstack/180_flinkplugin/\\\",\\\"state.checkpoints.num-retained\\\":\\\"11\\\",\\\"flinkSessionName\\\":\\\"hxb\\\",\\\"metrics.reporter.promgateway.port\\\":\\\"9091\\\",\\\"prometheusHost\\\":\\\"172.16.100.105\\\",\\\"flinkJarPath\\\":\\\"/opt/dtstack/flink-1.8.3/lib\\\",\\\"high-availability.cluster-id\\\":\\\"/default\\\",\\\"metrics.reporter.promgateway.jobName\\\":\\\"181job\\\",\\\"jarTmpDir\\\":\\\"./tmp180\\\",\\\"yarn.taskmanager.heap.mb\\\":\\\"1024\\\",\\\"monitorAcceptedApp\\\":\\\"false\\\",\\\"state.savepoints.dir\\\":\\\"hdfs://ns1/dtInsight/flink180_1/savepoints\\\",\\\"clusterMode\\\":\\\"session\\\",\\\"high-availability.zookeeper.path.root\\\":\\\"/flink180\\\",\\\"metrics.reporter.promgateway.class\\\":\\\"org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter\\\",\\\"yarn.jobmanager.heap.mb\\\":\\\"1024\\\",\\\"high-availability.storageDir\\\":\\\"hdfs://ns1/flink180/ha\\\",\\\"sessionStartAuto\\\":\\\"true\\\",\\\"flinkPluginRoot\\\":\\\"/opt/dtstack/180_flinkplugin/\\\",\\\"metrics.reporter.promgateway.randomJobNameSuffix\\\":\\\"true\\\",\\\"yarn.application-attempts\\\":\\\"0\\\",\\\"flink.env.java.opts\\\":\\\"-XX:MaxMetaspaceSize=500m\\\",\\\"yarn.taskmanager.numberOfTaskManager\\\":\\\"2\\\",\\\"prometheusClass\\\":\\\"com.dtstack.jlogstash.metrics.promethues.PrometheusPushGatewayReporter\\\",\\\"gatewayJobName\\\":\\\"pushgateway\\\",\\\"pluginLoadMode\\\":\\\"shipfile\\\",\\\"taskparams.taskmanager.heap.mb\\\":\\\"20\\\",\\\"jobmanager.archive.fs.dir\\\":\\\"hdfs://ns1/flink180/completed-jobs\\\",\\\"checkSubmitJobGraphInterval\\\":\\\"120\\\",\\\"classloader.resolve-order\\\":\\\"parent-first\\\",\\\"state.backend.incremental\\\":\\\"true\\\",\\\"flinkSessionSlotCount\\\":\\\"10\\\",\\\"metrics.reporter.promgateway.deleteOnShutdown\\\":\\\"true\\\",\\\"flinkInterval\\\":\\\"5 SECONDS\\\",\\\"high-availability.zookeeper.quorum\\\":\\\"172.16.101.13:2181,172.16.100.105:2181,172.16.100.132:2181\\\",\\\"yarn.application-attempt-failures-validity-interval\\\":\\\"3600000\\\",\\\"high-availability\\\":\\\"ZOOKEEPER\\\",\\\"submitTimeout\\\":\\\"5\\\",\\\"state.backend\\\":\\\"RocksDB\\\",\\\"yarn.taskmanager.numberOfTaskSlots\\\":\\\"2\\\",\\\"metrics.reporter.promgateway.host\\\":\\\"172.16.100.105\\\",\\\"prometheusPort\\\":\\\"9090\\\",\\\"queue\\\":\\\"default\\\",\\\"state.checkpoints.dir\\\":\\\"hdfs://ns1/flink180/checkpoints/metadata\\\"},\\\"typeName\\\":\\\"yarn3-hdfs3-flink180\\\"}\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"componentTypeCode\": 3,\n" +
                    "            \"componentConfig\": \"{\\\"pythonConf\\\":{\\\"python3.path\\\":\\\"/opt/dtstack/Hadoop/yarn_nodemanager/miniconda3/bin/python2\\\",\\\"python2.path\\\":\\\"/opt/dtstack/Hadoop/yarn_nodemanager/miniconda3/bin/python3\\\"},\\\"jupyterConf\\\":{\\\"c.NotebookApp.token\\\":\\\"''\\\",\\\"jupyter.path\\\":\\\"/data/anaconda3/bin/jupyter-lab\\\",\\\"c.NotebookApp.allow_remote_access\\\":\\\"true\\\",\\\"c.NotebookApp.default_url\\\":\\\"'/lab'\\\",\\\"c.NotebookApp.ip\\\":\\\"'*'\\\",\\\"c.NotebookApp.open_browser\\\":\\\"true\\\",\\\"jupyter.project.root\\\":\\\"/data/DTAiworks/Jupyter/\\\"},\\\"typeName\\\":\\\"yarn3-hdfs3-dtscript\\\",\\\"commonConf\\\":{\\\"shellPath\\\":\\\"/root/shellPath\\\",\\\"yarn.application.classpath\\\":\\\"/opt/dtstack/Hadoop/hadoop_pkg/etc/hadoop,/opt/dtstack/Hadoop/hadoop_pkg/lib/*,/opt/dtstack/Hadoop/hadoop_hdfs/lib/*,/opt/dtstack/Hadoop/hadoop_yarn/lib/*,/opt/dtstack/Hadoop/hadoop_yarn/*,/opt/dtstack/Hadoop/hadoop_mapreduce/*,$HADOOP_HDFS_HOME/share/hadoop/hdfs/lib/*,/opt/dtstack/Hadoop/hadoop_hdfs/*\\\",\\\"hadoop.home.dir\\\":\\\"/opt/dtstack/Hadoop/hadoop_pkg\\\",\\\"hadoop.username\\\":\\\"hxb\\\",\\\"java.home\\\":\\\"/opt/dtstack/java/bin\\\"}}\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"componentTypeCode\": 4,\n" +
                    "            \"componentConfig\": \"{\\\"hive.metastore.pre.event.listeners\\\":\\\"org.apache.sentry.binding.metastore.MetastoreAuthzBinding\\\",\\\"hive.server2.map.fair.scheduler.queue\\\":\\\"false\\\",\\\"hadoop.security.groups.cache.secs\\\":\\\"1\\\",\\\"hadoop.security.group.mapping.ldap.base\\\":\\\"dc=dtstack,dc=com\\\",\\\"javax.jdo.option.ConnectionDriverName\\\":\\\"com.mysql.jdbc.Driver\\\",\\\"dfs.replication\\\":\\\"3\\\",\\\"dfs.ha.fencing.ssh.private-key-files\\\":\\\"~/.ssh/id_rsa\\\",\\\"dfs.nameservices\\\":\\\"ns1\\\",\\\"dfs.safemode.threshold.pct\\\":\\\"0.5\\\",\\\"dfs.ha.namenodes.ns1\\\":\\\"nn1,nn2\\\",\\\"hive.server2.authentication\\\":\\\"LDAP\\\",\\\"dfs.journalnode.rpc-address\\\":\\\"0.0.0.0:8485\\\",\\\"fs.trash.checkpoint.interval\\\":\\\"0\\\",\\\"dfs.journalnode.http-address\\\":\\\"0.0.0.0:8480\\\",\\\"dfs.namenode.rpc-address.ns1.nn2\\\":\\\"172.16.100.132:9000\\\",\\\"hive.security.authorization.sqlstd.confwhitelist\\\":\\\".*\\\",\\\"dfs.namenode.rpc-address.ns1.nn1\\\":\\\"172.16.100.105:9000\\\",\\\"hive.metastore.warehouse.dir\\\":\\\"/dtInsight/hive/warehouse\\\",\\\"hive.server2.webui.host\\\":\\\"0.0.0.0\\\",\\\"hive.security.authorization.task.factory\\\":\\\"org.apache.sentry.binding.hive.SentryHiveAuthorizationTaskFactoryImpl\\\",\\\"hive.metastore.schema.verification\\\":\\\"false\\\",\\\"hive.exec.mode.local.auto\\\":\\\"true\\\",\\\"hive.auto.convert.join\\\":\\\"false\\\",\\\"hive.server2.support.dynamic.service.discovery\\\":\\\"true\\\",\\\"dfs.permissions\\\":\\\"true\\\",\\\"javax.jdo.option.ConnectionPassword\\\":\\\"DT@Stack#123\\\",\\\"hive.metastore.authorization.storage.checks\\\":\\\"true\\\",\\\"hadoop.security.group.mapping.ldap.bind.user\\\":\\\"cn=ldapmanager,dc=dtstack,dc=com\\\",\\\"hive.metastore.uris\\\":\\\"thrift://172.16.101.13:9083\\\",\\\"hive.exec.dynamic.partition.mode\\\":\\\"nonstrict\\\",\\\"dfs.namenode.acls.enabled\\\":\\\"true\\\",\\\"hive.metastore.execute.setugi\\\":\\\"true\\\",\\\"hadoop.proxyuser.hxb.groups\\\":\\\"*\\\",\\\"hadoop.security.group.mapping\\\":\\\"org.apache.hadoop.security.LdapGroupsMapping\\\",\\\"dfs.permissions.enabled\\\":\\\"true\\\",\\\"hive.zookeeper.quorum\\\":\\\"172.16.100.105:2181,172.16.100.132:2181,172.16.101.13:2181\\\",\\\"ha.zookeeper.quorum\\\":\\\"172.16.100.105:2181,172.16.100.132:2181,172.16.101.13:2181\\\",\\\"hive.server2.thrift.min.worker.threads\\\":\\\"200\\\",\\\"hive.server2.webui.port\\\":\\\"10002\\\",\\\"fs.defaultFS\\\":\\\"hdfs://ns1\\\",\\\"hadoop.security.group.mapping.ldap.search.attr.member\\\":\\\"memberUid\\\",\\\"hadoop.proxyuser.hxb.hosts\\\":\\\"*\\\",\\\"hive.server2.authentication.ldap.baseDN\\\":\\\"ou=People,dc=dtstack,dc=com\\\",\\\"hive.metastore.event.listeners\\\":\\\"org.apache.sentry.binding.metastore.SentrySyncHMSNotificationsPostEventListener,org.apache.hive.hcatalog.listener.DbNotificationListener\\\",\\\"dfs.client.file-block-storage-locations.timeout\\\":\\\"10000\\\",\\\"sentry.service.client.server.rpc-connection-timeout\\\":\\\"200000\\\",\\\"dfs.ha.fencing.methods\\\":\\\"sshfence\\\",\\\"dfs.client.failover.proxy.provider.ns1\\\":\\\"org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider\\\",\\\"typeName\\\":\\\"yarn3-hdfs3-hadoop3\\\",\\\"hadoop.security.group.mapping.ldap.bind.password\\\":\\\"dtstack2019\\\",\\\"dfs.domain.socket.path\\\":\\\"/data/hadoop/dfs/dn._PORT\\\",\\\"javax.jdo.option.ConnectionURL\\\":\\\"jdbc:mysql://172.16.101.13:3306/cdh_metastore?createDatabaseIfNotExist=true&useSSL=false\\\",\\\"dfs.client.read.shortcircuit\\\":\\\"true\\\",\\\"dfs.namenode.name.dir\\\":\\\"file:/data/hadoop/dfs/name\\\",\\\"dfs.datanode.hdfs-blocks-metadata.enabled\\\":\\\"true\\\",\\\"hive.security.authorization.enabled\\\":\\\"true\\\",\\\"hadoop.security.group.mapping.ldap.url\\\":\\\"ldap://hxb01\\\",\\\"hadoop.security.group.mapping.ldap.search.filter.group\\\":\\\"(objectClass=posixGroup)\\\",\\\"dfs.namenode.inode.attributes.provider.class\\\":\\\"org.apache.sentry.hdfs.SentryINodeAttributesProvider\\\",\\\"fs.trash.internal\\\":\\\"21600\\\",\\\"hive.server2.authentication.ldap.url\\\":\\\"ldap://hxb01\\\",\\\"dfs.datanode.data.dir\\\":\\\"file:/data/hadoop/dfs/data\\\",\\\"dfs.permissions.superusergroup\\\":\\\"hxb\\\",\\\"hadoop.security.group.mapping.ldap.search.filter.user\\\":\\\"(uid={0})\\\",\\\"dfs.namenode.shared.edits.dir\\\":\\\"qjournal://172.16.100.105:8485;172.16.100.132:8485;172.16.101.13:8485/namenode-ha-data\\\",\\\"sentry.service.client.server.rpc-addresses\\\":\\\"172.16.100.105:8038,172.16.100.132:8038,172.16.101.13:8038\\\",\\\"javax.jdo.option.ConnectionUserName\\\":\\\"drpeco\\\",\\\"hive.users.in.admin.role\\\":\\\"hxb\\\",\\\"ipc.client.connect.max.retries\\\":\\\"100\\\",\\\"hive.server2.thrift.port\\\":\\\"10000\\\",\\\"hadoop.tmp.dir\\\":\\\"/data/hadoop\\\",\\\"ha.zookeeper.session-timeout.ms\\\":\\\"5000\\\",\\\"ipc.client.connect.retry.interval\\\":\\\"10000\\\",\\\"dfs.journalnode.edits.dir\\\":\\\"/data/hadoop/dfs/journal\\\",\\\"hive.server2.zookeeper.namespace\\\":\\\"hiveserver2\\\",\\\"hive.server2.enable.doAs\\\":\\\"false\\\",\\\"dfs.namenode.http-address.ns1.nn2\\\":\\\"172.16.100.132:50070\\\",\\\"hive.sentry.conf.url\\\":\\\"file:///opt/dtstack/Hadoop/sentry_pkg/conf/sentry-site.xml\\\",\\\"hive.server2.session.hook\\\":\\\"org.apache.sentry.binding.hive.HiveAuthzBindingSessionHook\\\",\\\"dfs.namenode.http-address.ns1.nn1\\\":\\\"172.16.100.105:50070\\\",\\\"hive.reloadable.aux.jars.path\\\":\\\"/dtInsight/hive/udf\\\",\\\"md5zip\\\":\\\"dcf893253d663c99ad765fbbda807f85\\\",\\\"hive.exec.scratchdir\\\":\\\"/dtInsight/hive/warehouse\\\",\\\"hive.server2.webui.max.threads\\\":\\\"100\\\",\\\"hive.server2.thrift.bind.host\\\":\\\"0.0.0.0\\\",\\\"datanucleus.schema.autoCreateAll\\\":\\\"true\\\",\\\"hive.exec.dynamic.partition\\\":\\\"true\\\",\\\"dfs.ha.automatic-failover.enabled\\\":\\\"true\\\"}\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"componentTypeCode\": 5,\n" +
                    "            \"componentConfig\": \"{\\\"yarn.resourcemanager.zk-address\\\":\\\"172.16.100.105:2181,172.16.100.132:2181,172.16.101.13:2181\\\",\\\"yarn.resourcemanager.admin.address.rm1\\\":\\\"172.16.100.105:8033\\\",\\\"yarn.log.server.url\\\":\\\"http://172.16.100.105:19888/jobhistory/logs/\\\",\\\"hadoop.security.groups.cache.secs\\\":\\\"1\\\",\\\"yarn.resourcemanager.admin.address.rm2\\\":\\\"172.16.100.132:8033\\\",\\\"yarn.resourcemanager.ha.automatic-failover.zk-base-path\\\":\\\"/yarn-leader-election\\\",\\\"hadoop.security.group.mapping.ldap.base\\\":\\\"dc=dtstack,dc=com\\\",\\\"yarn.client.failover-proxy-provider\\\":\\\"org.apache.hadoop.yarn.client.ConfiguredRMFailoverProxyProvider\\\",\\\"yarn.resourcemanager.address.rm1\\\":\\\"172.16.100.105:8032\\\",\\\"yarn.resourcemanager.ha.enabled\\\":\\\"true\\\",\\\"yarn.resourcemanager.address.rm2\\\":\\\"172.16.100.132:8032\\\",\\\"yarn.resourcemanager.cluster-id\\\":\\\"yarn-rm-cluster\\\",\\\"yarn.nodemanager.aux-services\\\":\\\"mapreduce_shuffle\\\",\\\"yarn.nodemanager.resource.cpu-vcores\\\":\\\"8\\\",\\\"hadoop.security.group.mapping.ldap.bind.user\\\":\\\"cn=ldapmanager,dc=dtstack,dc=com\\\",\\\"yarn.resourcemanager.ha.automatic-failover.enabled\\\":\\\"true\\\",\\\"yarn.log-aggregation.retain-check-interval-seconds\\\":\\\"604800\\\",\\\"yarn.nodemanager.webapp.address\\\":\\\"0.0.0.0:8042\\\",\\\"yarn.nodemanager.aux-services.mapreduce_shuffle.class\\\":\\\"org.apache.hadoop.mapred.ShuffleHandler\\\",\\\"yarn.log-aggregation-enable\\\":\\\"true\\\",\\\"hadoop.proxyuser.hxb.groups\\\":\\\"*\\\",\\\"yarn.resourcemanager.store.class\\\":\\\"org.apache.hadoop.yarn.server.resourcemanager.recovery.ZKRMStateStore\\\",\\\"hadoop.security.group.mapping\\\":\\\"org.apache.hadoop.security.LdapGroupsMapping\\\",\\\"ha.zookeeper.quorum\\\":\\\"172.16.100.105:2181,172.16.100.132:2181,172.16.101.13:2181\\\",\\\"fs.defaultFS\\\":\\\"hdfs://ns1\\\",\\\"hadoop.security.group.mapping.ldap.search.attr.member\\\":\\\"memberUid\\\",\\\"hadoop.proxyuser.hxb.hosts\\\":\\\"*\\\",\\\"yarn.resourcemanager.webapp.address.rm2\\\":\\\"172.16.100.132:8088\\\",\\\"yarn.resourcemanager.webapp.address.rm1\\\":\\\"172.16.100.105:8088\\\",\\\"yarn.resourcemanager.ha.rm-ids\\\":\\\"rm1,rm2\\\",\\\"hadoop.security.group.mapping.ldap.bind.password\\\":\\\"dtstack2019\\\",\\\"yarn.resourcemanager.scheduler.address.rm1\\\":\\\"172.16.100.105:8030\\\",\\\"yarn.resourcemanager.scheduler.address.rm2\\\":\\\"172.16.100.132:8030\\\",\\\"yarn.nodemanager.delete.debug-delay-sec\\\":\\\"600\\\",\\\"yarn.log-aggregation.retain-seconds\\\":\\\"2592000\\\",\\\"yarn.nodemanager.resource.memory-mb\\\":\\\"12288\\\",\\\"hadoop.security.group.mapping.ldap.url\\\":\\\"ldap://hxb01\\\",\\\"hadoop.security.group.mapping.ldap.search.filter.group\\\":\\\"(objectClass=posixGroup)\\\",\\\"yarn.scheduler.minimum-allocation-mb\\\":\\\"512\\\",\\\"yarn.resourcemanager.scheduler.class\\\":\\\"org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler\\\",\\\"hadoop.security.group.mapping.ldap.search.filter.user\\\":\\\"(uid={0})\\\",\\\"yarn.resourcemanager.resource-tracker.address.rm1\\\":\\\"172.16.100.105:8031\\\",\\\"yarn.resourcemanager.resource-tracker.address.rm2\\\":\\\"172.16.100.132:8031\\\",\\\"yarn.nodemanager.pmem-check-enabled\\\":\\\"true\\\",\\\"yarn.nodemanager.remote-app-log-dir\\\":\\\"/data/hadoop/logs\\\",\\\"ipc.client.connect.max.retries\\\":\\\"100\\\",\\\"yarn.scheduler.maximum-allocation-mb\\\":\\\"12288\\\",\\\"yarn.nodemanager.vmem-check-enabled\\\":\\\"false\\\",\\\"ipc.client.connect.retry.interval\\\":\\\"10000\\\",\\\"yarn.resourcemanager.recovery.enabled\\\":\\\"true\\\",\\\"yarn.nodemanager.vmem-pmem-ratio\\\":\\\"4\\\",\\\"yarn.resourcemanager.zk-state-store.address\\\":\\\"172.16.100.105:2181,172.16.100.132:2181,172.16.101.13:2181\\\"}\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"componentTypeCode\": 9,\n" +
                    "            \"componentConfig\": \"{\\\"maxJobPoolSize\\\":\\\"\\\",\\\"password\\\":\\\"admin123\\\",\\\"minJobPoolSize\\\":\\\"\\\",\\\"jdbcUrl\\\":\\\"jdbc:hive2://172.16.100.132:2181,172.16.101.13:2181,172.16.100.105:2181/default;serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2\\\",\\\"queue\\\":\\\"\\\",\\\"username\\\":\\\"hxb\\\"}\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"componentTypeCode\": 10,\n" +
                    "            \"componentConfig\": \"{\\\"maxWaitMillis\\\":\\\"3600000\\\",\\\"minIdle\\\":\\\"16\\\",\\\"auth\\\":\\\"1\\\",\\\"isUsePool\\\":\\\"true\\\",\\\"timeout\\\":\\\"0\\\",\\\"path\\\":\\\"/data/sftp\\\",\\\"password\\\":\\\"dt@sz.com\\\",\\\"maxIdle\\\":\\\"16\\\",\\\"port\\\":\\\"22\\\",\\\"maxTotal\\\":\\\"16\\\",\\\"host\\\":\\\"172.16.101.184\\\",\\\"fileTimeout\\\":\\\"300000\\\",\\\"username\\\":\\\"admin\\\"}\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"componentTypeCode\": 11,\n" +
                    "            \"componentConfig\": \"{\\\"maxJobPoolSize\\\":\\\"\\\",\\\"password\\\":\\\"admin123\\\",\\\"minJobPoolSize\\\":\\\"\\\",\\\"jdbcUrl\\\":\\\"jdbc:impala://172.16.101.13:21050/%s;AuthMech=3\\\",\\\"username\\\":\\\"hxb\\\"}\"\n" +
                    "        }]";
            List<ComponentsConfigOfComponentsVO> finkCompoent = JSONObject.parseArray(data,ComponentsConfigOfComponentsVO.class);
           when(componentService.listConfigOfComponents(anyLong(),anyInt(),any())).thenReturn(finkCompoent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @Rollback
    public void testSQLHadoopJobTrigger() {
        try {
            ScheduleTaskShade sqlTaskShade = Template.getScheduleTaskShadeTemplate();
            ScheduleJob job = Template.getScheduleJobTemplate();
            initSQLData(sqlTaskShade, job);
            JSONObject jsonObject = JSONObject.parseObject(sqlTaskShade.getExtraInfo());
            JSONObject info = jsonObject.getJSONObject("info");
            Map<String, Object> actionParam = PublicUtil.strToMap(info.toJSONString());
            JobStartTriggerBase hadoopJobStartTrigger = multiEngineFactory.getJobTriggerService(MultiEngineType.HADOOP.getType());
            hadoopJobStartTrigger.readyForTaskStartTrigger(actionParam, sqlTaskShade, job);

            actionParam.put("sqlText","create table #{jobId}(id int)");
            JobStartTriggerBase jobTriggerService = multiEngineFactory.getJobTriggerService(null);
            jobTriggerService.readyForTaskStartTrigger(actionParam,sqlTaskShade,job);
        } catch (Exception e) {
            Assert.isNull(e);
        }
    }

    private ScheduleTaskShade initSQLData(ScheduleTaskShade sqlTaskShade, ScheduleJob job) {
        sqlTaskShade.setAppType(AppType.RDOS.getType());
        sqlTaskShade.setEngineType(1);
        sqlTaskShade.setTaskType(0);
        sqlTaskShade.setComputeType(1);
        sqlTaskShade.setTaskId(1487L);
        sqlTaskShade.setExtraInfo("{\"info\":\"{\\\"isFailRetry\\\":true,\\\"taskParamsToReplace\\\":\\\"[{\\\\\\\"gmtCreate\\\\\\\":1608882425000,\\\\\\\"gmtModified\\\\\\\":1608882425000,\\\\\\\"id\\\\\\\":157,\\\\\\\"isDeleted\\\\\\\":0,\\\\\\\"paramCommand\\\\\\\":\\\\\\\"yyyyMMddHHmmss\\\\\\\",\\\\\\\"paramName\\\\\\\":\\\\\\\"bdp.system.cyctime\\\\\\\",\\\\\\\"taskId\\\\\\\":1487,\\\\\\\"type\\\\\\\":0}]\\\",\\\"sqlText\\\":\\\"use beihai_test1;\\\\nset hive.default.fileformat=parquet;\\\\ninsert into table_aka_99_1 VALUES(1,${bdp.system.cyctime});\\\\n\\\",\\\"computeType\\\":1,\\\"engineType\\\":\\\"spark\\\",\\\"taskParams\\\":\\\"## Driver程序使用的CPU核数,默认为1\\\\r\\\\n# driver.cores=1\\\\r\\\\n\\\\n## Driver程序使用内存大小,默认512m\\\\r\\\\n# driver.memory=512m\\\\r\\\\n\\\\n## 对Spark每个action结果集大小的限制，最少是1M，若设为0则不限制大小。\\\\n## 若Job结果超过限制则会异常退出，若结果集限制过大也可能造成OOM问题，默认1g\\\\r\\\\n# driver.maxResultSize=1g\\\\r\\\\n\\\\n## SparkContext 启动时是否记录有效 SparkConf信息,默认false\\\\r\\\\n# logConf=false\\\\r\\\\n\\\\n## 启动的executor的数量，默认为1\\\\r\\\\nexecutor.instances=1\\\\r\\\\n\\\\n## 每个executor使用的CPU核数，默认为1\\\\r\\\\nexecutor.cores=1\\\\r\\\\n\\\\n## 每个executor内存大小,默认512m\\\\r\\\\n# executor.memory=512m\\\\r\\\\n\\\\n## 任务优先级, 值越小，优先级越高，范围:1-1000\\\\r\\\\njob.priority=10\\\\r\\\\n\\\\n## spark 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN\\\\r\\\\n# logLevel = INFO\\\\r\\\\n\\\\n## spark中所有网络交互的最大超时时间\\\\r\\\\n# spark.network.timeout=120s\\\\r\\\\n\\\\n## executor的OffHeap内存，和spark.executor.memory配置使用\\\\r\\\\n# spark.yarn.executor.memoryOverhead\\\",\\\"maxRetryNum\\\":3,\\\"userId\\\":1,\\\"dirtyDataSourceType\\\":7,\\\"taskType\\\":0,\\\"multiEngineType\\\":1,\\\"name\\\":\\\"1\\\",\\\"tenantId\\\":1,\\\"taskId\\\":1487}\"}");
        scheduleTaskShadeDao.insert(sqlTaskShade);
        scheduleTaskShadeDao.updateTaskExtInfo(sqlTaskShade.getTaskId(), sqlTaskShade.getAppType(), sqlTaskShade.getExtraInfo());

        job.setJobId("da951dd4");
        job.setJobKey("fillData_P_1_2020_12_25_00_51_35_20201225000000");
        job.setJobName("P_1_2020_12_25_00_51-1-20201225000000");
        job.setTaskId(1487L);
        job.setTaskType(0);
        job.setComputeType(1);
        scheduleJobDao.insert(job);
        return sqlTaskShade;
    }


    @Test
    @Rollback
    public void testDtScriptHadoopJobTrigger() {
        try {
            ScheduleTaskShade sqlTaskShade = Template.getScheduleTaskShadeTemplate();
            ScheduleJob job = Template.getScheduleJobTemplate();
            initDtScriptData(sqlTaskShade, job);
            JSONObject jsonObject = JSONObject.parseObject(sqlTaskShade.getExtraInfo());
            JSONObject info = jsonObject.getJSONObject("info");
            Map<String, Object> actionParam = PublicUtil.strToMap(info.toJSONString());
            JobStartTriggerBase hadoopJobStartTrigger = multiEngineFactory.getJobTriggerService(MultiEngineType.HADOOP.getType());
            hadoopJobStartTrigger.readyForTaskStartTrigger(actionParam, sqlTaskShade, job);
        } catch (Exception e) {
            Assert.isNull(e);
        }
    }

    private ScheduleTaskShade initDtScriptData(ScheduleTaskShade sqlTaskShade, ScheduleJob job) {
        sqlTaskShade.setAppType(AppType.RDOS.getType());
        sqlTaskShade.setEngineType(6);
        sqlTaskShade.setTaskType(EScheduleJobType.SHELL.getType());
        sqlTaskShade.setComputeType(1);
        sqlTaskShade.setTaskId(1487L);
        sqlTaskShade.setExtraInfo("{\"info\":\"{\\\"isFailRetry\\\":true,\\\"taskParamsToReplace\\\":\\\"[]\\\",\\\"sqlText\\\":\\\"#name s6\\\\n#type Shell\\\\n#author admin@dtstack.com\\\\n#create time 2020-11-24 14:38:05\\\\n#desc \\\\n\\\",\\\"computeType\\\":1,\\\"exeArgs\\\":\\\"--files ${uploadPath} --python-version 0 --app-type shell --app-name s6\\\",\\\"engineType\\\":\\\"dtScript\\\",\\\"taskParams\\\":\\\"worker.memory=512m\\\\nworker.cores=1\\\\nexclusive=false\\\\nworker.num=1\\\\njob.priority=10\\\",\\\"maxRetryNum\\\":3,\\\"userId\\\":1,\\\"dirtyDataSourceType\\\":7,\\\"taskType\\\":3,\\\"multiEngineType\\\":1,\\\"name\\\":\\\"s6\\\",\\\"tenantId\\\":1,\\\"taskId\\\":919}\"}");
        scheduleTaskShadeDao.insert(sqlTaskShade);
        scheduleTaskShadeDao.updateTaskExtInfo(sqlTaskShade.getTaskId(), sqlTaskShade.getAppType(), sqlTaskShade.getExtraInfo());

        job.setJobId("8e6646a7");
        job.setJobKey("cronTrigger_649_20201225000000");
        job.setJobName("cronJob_s6_20201225000000-1-20201225000000");
        job.setTaskId(919L);
        job.setTaskType(sqlTaskShade.getTaskType());
        job.setComputeType(1);
        scheduleJobDao.insert(job);
        return sqlTaskShade;
    }


    @Test
    @Rollback
    public void testSyncHadoopJobTrigger() {
        try {
            ScheduleTaskShade sqlTaskShade = Template.getScheduleTaskShadeTemplate();
            ScheduleJob job = Template.getScheduleJobTemplate();
            initSyncData(sqlTaskShade, job);
            JSONObject jsonObject = JSONObject.parseObject(sqlTaskShade.getExtraInfo());
            JSONObject info = jsonObject.getJSONObject("info");
            Map<String, Object> actionParam = PublicUtil.strToMap(info.toJSONString());
            JobStartTriggerBase hadoopJobStartTrigger = multiEngineFactory.getJobTriggerService(MultiEngineType.HADOOP.getType());
            hadoopJobStartTrigger.readyForTaskStartTrigger(actionParam, sqlTaskShade, job);
        } catch (Exception e) {
            Assert.isNull(e);
        }
    }

    private ScheduleTaskShade initSyncData(ScheduleTaskShade sqlTaskShade, ScheduleJob job) {
        sqlTaskShade.setAppType(AppType.RDOS.getType());
        sqlTaskShade.setEngineType(0);
        sqlTaskShade.setTaskType(EScheduleJobType.SYNC.getType());
        sqlTaskShade.setComputeType(1);
        sqlTaskShade.setTaskId(1561L);
        sqlTaskShade.setName("testSync1");
        sqlTaskShade.setExtraInfo("{\"info\":\"{\\\"isFailRetry\\\":true,\\\"taskParamsToReplace\\\":\\\"[]\\\",\\\"sqlText\\\":\\\"\\\",\\\"computeType\\\":1,\\\"engineIdentity\\\":\\\"dev\\\",\\\"engineType\\\":\\\"flink\\\",\\\"taskParams\\\":\\\"mr.job.parallelism = 1\\\\n\\\",\\\"maxRetryNum\\\":3,\\\"userId\\\":1,\\\"dirtyDataSourceType\\\":7,\\\"taskType\\\":2,\\\"multiEngineType\\\":1,\\\"name\\\":\\\"testInc\\\",\\\"tenantId\\\":1,\\\"job\\\":\\\"{\\\\\\\"job\\\\\\\":{\\\\\\\"content\\\\\\\":[{\\\\\\\"reader\\\\\\\":{\\\\\\\"parameter\\\\\\\":{\\\\\\\"password\\\\\\\":\\\\\\\"DT@Stack#123\\\\\\\",\\\\\\\"customSql\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"startLocation\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"increColumn\\\\\\\":\\\\\\\"id\\\\\\\",\\\\\\\"column\\\\\\\":[{\\\\\\\"name\\\\\\\":\\\\\\\"id\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"INT\\\\\\\",\\\\\\\"key\\\\\\\":\\\\\\\"id\\\\\\\"},{\\\\\\\"name\\\\\\\":\\\\\\\"task_id\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"INT\\\\\\\",\\\\\\\"key\\\\\\\":\\\\\\\"task_id\\\\\\\"},{\\\\\\\"name\\\\\\\":\\\\\\\"type\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"INT\\\\\\\",\\\\\\\"key\\\\\\\":\\\\\\\"type\\\\\\\"},{\\\\\\\"name\\\\\\\":\\\\\\\"param_name\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"VARCHAR\\\\\\\",\\\\\\\"key\\\\\\\":\\\\\\\"param_name\\\\\\\"},{\\\\\\\"name\\\\\\\":\\\\\\\"param_command\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"VARCHAR\\\\\\\",\\\\\\\"key\\\\\\\":\\\\\\\"param_command\\\\\\\"},{\\\\\\\"name\\\\\\\":\\\\\\\"gmt_create\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"DATETIME\\\\\\\",\\\\\\\"key\\\\\\\":\\\\\\\"gmt_create\\\\\\\"},{\\\\\\\"name\\\\\\\":\\\\\\\"gmt_modified\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"DATETIME\\\\\\\",\\\\\\\"key\\\\\\\":\\\\\\\"gmt_modified\\\\\\\"},{\\\\\\\"name\\\\\\\":\\\\\\\"is_deleted\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"TINYINT\\\\\\\",\\\\\\\"key\\\\\\\":\\\\\\\"is_deleted\\\\\\\"}],\\\\\\\"connection\\\\\\\":[{\\\\\\\"sourceId\\\\\\\":39,\\\\\\\"password\\\\\\\":\\\\\\\"DT@Stack#123\\\\\\\",\\\\\\\"jdbcUrl\\\\\\\":[\\\\\\\"jdbc:mysql://172.16.100.115:3306/ide\\\\\\\"],\\\\\\\"type\\\\\\\":1,\\\\\\\"table\\\\\\\":[\\\\\\\"rdos_batch_task_param\\\\\\\"],\\\\\\\"username\\\\\\\":\\\\\\\"drpeco\\\\\\\"}],\\\\\\\"sourceIds\\\\\\\":[39],\\\\\\\"username\\\\\\\":\\\\\\\"drpeco\\\\\\\"},\\\\\\\"name\\\\\\\":\\\\\\\"mysqlreader\\\\\\\"},\\\\\\\"writer\\\\\\\":{\\\\\\\"parameter\\\\\\\":{\\\\\\\"fileName\\\\\\\":\\\\\\\"pt=2020\\\\\\\",\\\\\\\"column\\\\\\\":[{\\\\\\\"name\\\\\\\":\\\\\\\"id\\\\\\\",\\\\\\\"index\\\\\\\":0,\\\\\\\"isPart\\\\\\\":false,\\\\\\\"type\\\\\\\":\\\\\\\"int\\\\\\\",\\\\\\\"key\\\\\\\":\\\\\\\"id\\\\\\\"},{\\\\\\\"name\\\\\\\":\\\\\\\"task_id\\\\\\\",\\\\\\\"index\\\\\\\":1,\\\\\\\"isPart\\\\\\\":false,\\\\\\\"type\\\\\\\":\\\\\\\"int\\\\\\\",\\\\\\\"key\\\\\\\":\\\\\\\"task_id\\\\\\\"},{\\\\\\\"name\\\\\\\":\\\\\\\"type\\\\\\\",\\\\\\\"index\\\\\\\":2,\\\\\\\"isPart\\\\\\\":false,\\\\\\\"type\\\\\\\":\\\\\\\"int\\\\\\\",\\\\\\\"key\\\\\\\":\\\\\\\"type\\\\\\\"},{\\\\\\\"name\\\\\\\":\\\\\\\"param_name\\\\\\\",\\\\\\\"index\\\\\\\":3,\\\\\\\"isPart\\\\\\\":false,\\\\\\\"type\\\\\\\":\\\\\\\"string\\\\\\\",\\\\\\\"key\\\\\\\":\\\\\\\"param_name\\\\\\\"},{\\\\\\\"name\\\\\\\":\\\\\\\"param_command\\\\\\\",\\\\\\\"index\\\\\\\":4,\\\\\\\"isPart\\\\\\\":false,\\\\\\\"type\\\\\\\":\\\\\\\"string\\\\\\\",\\\\\\\"key\\\\\\\":\\\\\\\"param_command\\\\\\\"},{\\\\\\\"name\\\\\\\":\\\\\\\"gmt_create\\\\\\\",\\\\\\\"index\\\\\\\":5,\\\\\\\"isPart\\\\\\\":false,\\\\\\\"type\\\\\\\":\\\\\\\"string\\\\\\\",\\\\\\\"key\\\\\\\":\\\\\\\"gmt_create\\\\\\\"},{\\\\\\\"name\\\\\\\":\\\\\\\"gmt_modified\\\\\\\",\\\\\\\"index\\\\\\\":6,\\\\\\\"isPart\\\\\\\":false,\\\\\\\"type\\\\\\\":\\\\\\\"string\\\\\\\",\\\\\\\"key\\\\\\\":\\\\\\\"gmt_modified\\\\\\\"},{\\\\\\\"name\\\\\\\":\\\\\\\"is_deleted\\\\\\\",\\\\\\\"index\\\\\\\":7,\\\\\\\"isPart\\\\\\\":false,\\\\\\\"type\\\\\\\":\\\\\\\"tinyint\\\\\\\",\\\\\\\"key\\\\\\\":\\\\\\\"is_deleted\\\\\\\"}],\\\\\\\"writeMode\\\\\\\":\\\\\\\"overwrite\\\\\\\",\\\\\\\"fieldDelimiter\\\\\\\":\\\\\\\"\\\\\\\\u0001\\\\\\\",\\\\\\\"encoding\\\\\\\":\\\\\\\"utf-8\\\\\\\",\\\\\\\"fullColumnName\\\\\\\":[\\\\\\\"id\\\\\\\",\\\\\\\"task_id\\\\\\\",\\\\\\\"type\\\\\\\",\\\\\\\"param_name\\\\\\\",\\\\\\\"param_command\\\\\\\",\\\\\\\"gmt_create\\\\\\\",\\\\\\\"gmt_modified\\\\\\\",\\\\\\\"is_deleted\\\\\\\"],\\\\\\\"path\\\\\\\":\\\\\\\"hdfs://ns1/user/hive/warehouse/dev.db/rdos_batch_task_param1\\\\\\\",\\\\\\\"partition\\\\\\\":\\\\\\\"pt=2020\\\\\\\",\\\\\\\"hadoopConfig\\\\\\\":{\\\\\\\"javax.jdo.option.ConnectionDriverName\\\\\\\":\\\\\\\"com.mysql.jdbc.Driver\\\\\\\",\\\\\\\"dfs.replication\\\\\\\":\\\\\\\"2\\\\\\\",\\\\\\\"dfs.ha.fencing.ssh.private-key-files\\\\\\\":\\\\\\\"~/.ssh/id_rsa\\\\\\\",\\\\\\\"dfs.nameservices\\\\\\\":\\\\\\\"ns1\\\\\\\",\\\\\\\"dfs.safemode.threshold.pct\\\\\\\":\\\\\\\"0.5\\\\\\\",\\\\\\\"dfs.ha.namenodes.ns1\\\\\\\":\\\\\\\"nn1,nn2\\\\\\\",\\\\\\\"dfs.journalnode.rpc-address\\\\\\\":\\\\\\\"0.0.0.0:8485\\\\\\\",\\\\\\\"dfs.journalnode.http-address\\\\\\\":\\\\\\\"0.0.0.0:8480\\\\\\\",\\\\\\\"dfs.namenode.rpc-address.ns1.nn2\\\\\\\":\\\\\\\"kudu2:9000\\\\\\\",\\\\\\\"dfs.namenode.rpc-address.ns1.nn1\\\\\\\":\\\\\\\"kudu1:9000\\\\\\\",\\\\\\\"hive.metastore.warehouse.dir\\\\\\\":\\\\\\\"/user/hive/warehouse\\\\\\\",\\\\\\\"hive.server2.webui.host\\\\\\\":\\\\\\\"172.16.10.34\\\\\\\",\\\\\\\"hive.metastore.schema.verification\\\\\\\":\\\\\\\"false\\\\\\\",\\\\\\\"hive.server2.support.dynamic.service.discovery\\\\\\\":\\\\\\\"true\\\\\\\",\\\\\\\"javax.jdo.option.ConnectionPassword\\\\\\\":\\\\\\\"abc123\\\\\\\",\\\\\\\"hive.metastore.uris\\\\\\\":\\\\\\\"thrift://kudu1:9083\\\\\\\",\\\\\\\"hive.exec.dynamic.partition.mode\\\\\\\":\\\\\\\"nonstrict\\\\\\\",\\\\\\\"hadoop.proxyuser.admin.hosts\\\\\\\":\\\\\\\"*\\\\\\\",\\\\\\\"hive.zookeeper.quorum\\\\\\\":\\\\\\\"kudu1:2181,kudu2:2181,kudu3:2181\\\\\\\",\\\\\\\"ha.zookeeper.quorum\\\\\\\":\\\\\\\"kudu1:2181,kudu2:2181,kudu3:2181\\\\\\\",\\\\\\\"hive.server2.thrift.min.worker.threads\\\\\\\":\\\\\\\"200\\\\\\\",\\\\\\\"hive.server2.webui.port\\\\\\\":\\\\\\\"10002\\\\\\\",\\\\\\\"fs.defaultFS\\\\\\\":\\\\\\\"hdfs://ns1\\\\\\\",\\\\\\\"hadoop.proxyuser.admin.groups\\\\\\\":\\\\\\\"*\\\\\\\",\\\\\\\"dfs.ha.fencing.methods\\\\\\\":\\\\\\\"sshfence\\\\\\\",\\\\\\\"dfs.client.failover.proxy.provider.ns1\\\\\\\":\\\\\\\"org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider\\\\\\\",\\\\\\\"typeName\\\\\\\":\\\\\\\"yarn2-hdfs2-hadoop2\\\\\\\",\\\\\\\"hadoop.proxyuser.root.groups\\\\\\\":\\\\\\\"*\\\\\\\",\\\\\\\"javax.jdo.option.ConnectionURL\\\\\\\":\\\\\\\"jdbc:mysql://kudu2:3306/ide?useSSL=false\\\\\\\",\\\\\\\"dfs.qjournal.write-txns.timeout.ms\\\\\\\":\\\\\\\"60000\\\\\\\",\\\\\\\"fs.trash.interval\\\\\\\":\\\\\\\"30\\\\\\\",\\\\\\\"hadoop.proxyuser.root.hosts\\\\\\\":\\\\\\\"*\\\\\\\",\\\\\\\"dfs.namenode.shared.edits.dir\\\\\\\":\\\\\\\"qjournal://kudu1:8485;kudu2:8485;kudu3:8485/namenode-ha-data\\\\\\\",\\\\\\\"javax.jdo.option.ConnectionUserName\\\\\\\":\\\\\\\"dtstack\\\\\\\",\\\\\\\"hive.server2.thrift.port\\\\\\\":\\\\\\\"10000\\\\\\\",\\\\\\\"ha.zookeeper.session-timeout.ms\\\\\\\":\\\\\\\"5000\\\\\\\",\\\\\\\"hadoop.tmp.dir\\\\\\\":\\\\\\\"/data/hadoop_${user.name}\\\\\\\",\\\\\\\"dfs.journalnode.edits.dir\\\\\\\":\\\\\\\"/data/dtstack/hadoop/journal\\\\\\\",\\\\\\\"hive.server2.zookeeper.namespace\\\\\\\":\\\\\\\"hiveserver2\\\\\\\",\\\\\\\"hive.server2.enable.doAs\\\\\\\":\\\\\\\"/false\\\\\\\",\\\\\\\"dfs.namenode.http-address.ns1.nn2\\\\\\\":\\\\\\\"kudu2:50070\\\\\\\",\\\\\\\"dfs.namenode.http-address.ns1.nn1\\\\\\\":\\\\\\\"kudu1:50070\\\\\\\",\\\\\\\"md5zip\\\\\\\":\\\\\\\"667e20cff7e9f2636fddc1440a9f5728\\\\\\\",\\\\\\\"hive.exec.scratchdir\\\\\\\":\\\\\\\"/user/hive/warehouse\\\\\\\",\\\\\\\"hive.server2.webui.max.threads\\\\\\\":\\\\\\\"100\\\\\\\",\\\\\\\"datanucleus.schema.autoCreateAll\\\\\\\":\\\\\\\"true\\\\\\\",\\\\\\\"hive.exec.dynamic.partition\\\\\\\":\\\\\\\"true\\\\\\\",\\\\\\\"hive.server2.thrift.bind.host\\\\\\\":\\\\\\\"kudu1\\\\\\\",\\\\\\\"dfs.ha.automatic-failover.enabled\\\\\\\":\\\\\\\"true\\\\\\\"},\\\\\\\"defaultFS\\\\\\\":\\\\\\\"hdfs://ns1\\\\\\\",\\\\\\\"connection\\\\\\\":[{\\\\\\\"jdbcUrl\\\\\\\":\\\\\\\"jdbc:hive2://172.16.8.107:10000/dev\\\\\\\",\\\\\\\"table\\\\\\\":[\\\\\\\"rdos_batch_task_param1\\\\\\\"]}],\\\\\\\"fileType\\\\\\\":\\\\\\\"parquet\\\\\\\",\\\\\\\"sourceIds\\\\\\\":[37],\\\\\\\"username\\\\\\\":\\\\\\\"admin\\\\\\\",\\\\\\\"fullColumnType\\\\\\\":[\\\\\\\"int\\\\\\\",\\\\\\\"int\\\\\\\",\\\\\\\"int\\\\\\\",\\\\\\\"string\\\\\\\",\\\\\\\"string\\\\\\\",\\\\\\\"string\\\\\\\",\\\\\\\"string\\\\\\\",\\\\\\\"tinyint\\\\\\\"]},\\\\\\\"name\\\\\\\":\\\\\\\"hdfswriter\\\\\\\"}}],\\\\\\\"setting\\\\\\\":{\\\\\\\"dirty\\\\\\\":{\\\\\\\"path\\\\\\\":\\\\\\\"null/task_name=testInc/time=1608962546892\\\\\\\",\\\\\\\"hadoopConfig\\\\\\\":{\\\\\\\"dfs.ha.namenodes.ns1\\\\\\\":\\\\\\\"nn1,nn2\\\\\\\",\\\\\\\"dfs.namenode.rpc-address.ns1.nn2\\\\\\\":\\\\\\\"kudu2:9000\\\\\\\",\\\\\\\"dfs.client.failover.proxy.provider.ns1\\\\\\\":\\\\\\\"org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider\\\\\\\",\\\\\\\"dfs.namenode.rpc-address.ns1.nn1\\\\\\\":\\\\\\\"kudu1:9000\\\\\\\",\\\\\\\"dfs.nameservices\\\\\\\":\\\\\\\"ns1\\\\\\\"},\\\\\\\"tableName\\\\\\\":\\\\\\\"dev.dirty_testInc\\\\\\\"},\\\\\\\"restore\\\\\\\":{\\\\\\\"maxRowNumForCheckpoint\\\\\\\":0,\\\\\\\"isRestore\\\\\\\":false,\\\\\\\"restoreColumnName\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"restoreColumnIndex\\\\\\\":0},\\\\\\\"errorLimit\\\\\\\":{\\\\\\\"record\\\\\\\":100},\\\\\\\"speed\\\\\\\":{\\\\\\\"bytes\\\\\\\":0,\\\\\\\"channel\\\\\\\":1}}}}\\\",\\\"dataSourceType\\\":7,\\\"taskId\\\":391}\"}");
        scheduleTaskShadeDao.insert(sqlTaskShade);
        scheduleTaskShadeDao.updateTaskExtInfo(sqlTaskShade.getTaskId(), sqlTaskShade.getAppType(), sqlTaskShade.getExtraInfo());

        job.setJobId("8c44f6fd");
        job.setJobKey("cronTrigger_1147_20201225000000");
        job.setJobName("cronJob_test_sync_1_20201225000000");
        job.setTaskId(sqlTaskShade.getTaskId());
        job.setTaskType(sqlTaskShade.getTaskType());
        job.setComputeType(1);
        scheduleJobDao.insert(job);

        ScheduleJob lastSuccessJob = new ScheduleJob();
        BeanUtils.copyProperties(job,lastSuccessJob);
        lastSuccessJob.setJobId("8c44f6fd1");
        lastSuccessJob.setJobKey("cronTrigger_1147_2020122-000000");
        lastSuccessJob.setJobName("cronJob_test_sync_1_20201220000000");
        lastSuccessJob.setCycTime("20201220000000");
        lastSuccessJob.setEngineJobId("739fa5259be35bd196c98d348a1c4940");
        lastSuccessJob.setStatus(RdosTaskStatus.FINISHED.getStatus());
        lastSuccessJob.setExecStartTime(new Timestamp(System.currentTimeMillis()));
        lastSuccessJob.setExecEndTime(new Timestamp(System.currentTimeMillis()));
        lastSuccessJob.setTaskId(sqlTaskShade.getTaskId());
        lastSuccessJob.setAppType(1);
        scheduleJobDao.insert(lastSuccessJob);
        scheduleJobDao.updateJobSubmitSuccess(lastSuccessJob.getJobId(),lastSuccessJob.getEngineJobId(),lastSuccessJob.getApplicationId(),"","");
        scheduleJobDao.updateStatusWithExecTime(lastSuccessJob);
        return sqlTaskShade;
    }
}
