package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.ComponentConfig;
import com.dtstack.engine.api.pojo.ClientTemplate;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.util.ComponentConfigUtils;
import com.dtstack.engine.dao.ClusterDao;
import com.dtstack.engine.dao.ComponentConfigDao;
import com.dtstack.engine.dao.TestConsoleComponentTemplateDao;
import com.dtstack.engine.master.AbstractTest;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;

/**
 * @author yuebai
 * @date 2021-02-08
 */
public class TemplateTest extends AbstractTest {

    private final static Logger logger = LoggerFactory.getLogger(TemplateTest.class);

    Long testClusterId = -1L;
    Long testComponentId = -3L;
    String typeName = "yarn2-hdfs2-flink180";

    @Autowired
    private ComponentConfigDao componentConfigDao;

    @Autowired
    private ComponentConfigService componentConfigService;

    @Autowired
    private TestConsoleComponentTemplateDao testConsoleComponentTemplateDao;

    @SpyBean
    private ComponentService componentService;

    @Autowired
    private ClusterDao clusterDao;

    @Autowired
    private ComponentConfigOldCovertService componentConfigOldCovertService;

    @Before
    public void init() {
        doReturn(typeName).when(componentService).convertComponentTypeToClient(any(), anyInt(), any(),anyInt());
    }

    @Test
    public void testParseOldTemplate() {
        String json = "[{\"key\":\"deploymode\",\"values\":[{\"key\":\"perjob\",\"values\":null,\"type\":null,\"value\":\"perjob\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"session\",\"values\":null,\"type\":null,\"value\":\"session\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"standalone\",\"values\":null,\"type\":null,\"value\":\"standalone\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null}],\"type\":\"CHECKBOX\",\"value\":[\"perjob\",\"session\"],\"required\":true,\"dependencyKey\":\"\",\"dependencyValue\":\"\"},{\"key\":\"perjob\",\"values\":[{\"key\":\"classloader.resolve-order\",\"values\":null,\"type\":\"INPUT\",\"value\":\"parent-first\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"clusterMode\",\"values\":null,\"type\":\"INPUT\",\"value\":\"perjob\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"flink.env.java.opts\",\"values\":null,\"type\":\"INPUT\",\"value\":\"-XX:MaxMetaspaceSize=500m\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"flinkInterval\",\"values\":null,\"type\":\"INPUT\",\"value\":\"5 SECONDS\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"flinkJarPath\",\"values\":null,\"type\":\"INPUT\",\"value\":\"/opt/dtstack/flink-1.10.1/lib\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"flinkPluginRoot\",\"values\":null,\"type\":\"INPUT\",\"value\":\"/opt/dtstack/110_flinkplugin\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"gatewayJobName\",\"values\":null,\"type\":\"INPUT\",\"value\":\"pushgateway\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"high-availability\",\"values\":null,\"type\":\"INPUT\",\"value\":\"ZOOKEEPER\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"high-availability.cluster-id\",\"values\":null,\"type\":\"INPUT\",\"value\":\"/default\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"high-availability.storageDir\",\"values\":null,\"type\":\"INPUT\",\"value\":\"hdfs://ns1/flink110/ha\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"high-availability.zookeeper.path.root\",\"values\":null,\"type\":\"INPUT\",\"value\":\"/flink110\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"high-availability.zookeeper.quorum\",\"values\":null,\"type\":\"INPUT\",\"value\":\"172.16.8.107:2181,172.16.8.108:2181,172.16.8.109:2181\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"historyserver.web.address\",\"values\":null,\"type\":\"INPUT\",\"value\":\"172.16.100.168\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"historyserver.web.port\",\"values\":null,\"type\":\"INPUT\",\"value\":\"8082\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"jarTmpDir\",\"values\":null,\"type\":\"INPUT\",\"value\":\"./tmp110\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"jobmanager.archive.fs.dir\",\"values\":null,\"type\":\"INPUT\",\"value\":\"hdfs://ns1/dtInsight/flink110/completed-jobs\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"metrics.reporter.promgateway.class\",\"values\":null,\"type\":\"INPUT\",\"value\":\"org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"metrics.reporter.promgateway.deleteOnShutdown\",\"values\":[{\"key\":\"false\",\"values\":null,\"type\":null,\"value\":\"false\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"true\",\"values\":null,\"type\":null,\"value\":\"true\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null}],\"type\":\"SELECT\",\"value\":\"true\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"metrics.reporter.promgateway.host\",\"values\":null,\"type\":\"INPUT\",\"value\":\"172.16.100.216\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"metrics.reporter.promgateway.jobName\",\"values\":null,\"type\":\"INPUT\",\"value\":\"110job\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"metrics.reporter.promgateway.port\",\"values\":null,\"type\":\"INPUT\",\"value\":\"9091\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"metrics.reporter.promgateway.randomJobNameSuffix\",\"values\":[{\"key\":\"false\",\"values\":null,\"type\":null,\"value\":\"false\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"true\",\"values\":null,\"type\":null,\"value\":\"true\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null}],\"type\":\"SELECT\",\"value\":\"true\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"monitorAcceptedApp\",\"values\":null,\"type\":\"INPUT\",\"value\":\"false\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"pluginLoadMode\",\"values\":null,\"type\":\"INPUT\",\"value\":\"shipfile\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"prometheusClass\",\"values\":null,\"type\":\"INPUT\",\"value\":\"com.dtstack.jlogstash.metrics.promethues.PrometheusPushGatewayReporter\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"prometheusHost\",\"values\":null,\"type\":\"INPUT\",\"value\":\"172.16.20.16\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"prometheusPort\",\"values\":null,\"type\":\"INPUT\",\"value\":\"9090\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"queue\",\"values\":null,\"type\":\"INPUT\",\"value\":\"default\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"remotePluginRootDir\",\"values\":null,\"type\":\"INPUT\",\"value\":\"/opt/dtstack/110_flinkplugin\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"state.backend\",\"values\":null,\"type\":\"INPUT\",\"value\":\"RocksDB\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"state.backend.incremental\",\"values\":null,\"type\":\"INPUT\",\"value\":\"true\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"state.checkpoints.dir\",\"values\":null,\"type\":\"INPUT\",\"value\":\"hdfs://ns1/dtInsight/flink110/checkpoints\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"state.checkpoints.num-retained\",\"values\":null,\"type\":\"INPUT\",\"value\":\"11\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"state.savepoints.dir\",\"values\":null,\"type\":\"INPUT\",\"value\":\"hdfs://ns1/dtInsight/flink110/savepoints\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"submitTimeout\",\"values\":null,\"type\":\"INPUT\",\"value\":\"5\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"taskparams.taskmanager.heap.mb\",\"values\":null,\"type\":\"INPUT\",\"value\":\"20\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"yarn.application-attempt-failures-validity-interval\",\"values\":null,\"type\":\"INPUT\",\"value\":\"3600000\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"yarn.application-attempts\",\"values\":null,\"type\":\"INPUT\",\"value\":\"3\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"yarn.jobmanager.heap.mb\",\"values\":null,\"type\":\"INPUT\",\"value\":\"1024\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"yarn.taskmanager.heap.mb\",\"values\":null,\"type\":\"INPUT\",\"value\":\"1024\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"yarn.taskmanager.numberOfTaskManager\",\"values\":null,\"type\":\"INPUT\",\"value\":\"2\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"yarn.taskmanager.numberOfTaskSlots\",\"values\":null,\"type\":\"INPUT\",\"value\":\"2\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null}],\"type\":\"GROUP\",\"value\":null,\"required\":true,\"dependencyKey\":\"deploymode\",\"dependencyValue\":\"perjob\"},{\"key\":\"session\",\"values\":[{\"key\":\"checkSubmitJobGraphInterval\",\"values\":null,\"type\":\"INPUT\",\"value\":\"30\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"classloader.resolve-order\",\"values\":null,\"type\":\"INPUT\",\"value\":\"parent-first\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"clusterMode\",\"values\":null,\"type\":\"INPUT\",\"value\":\"session\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"flink.env.java.opts\",\"values\":null,\"type\":\"INPUT\",\"value\":\"-XX:MaxMetaspaceSize=500m\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"flinkInterval\",\"values\":null,\"type\":\"INPUT\",\"value\":\"5 SECONDS\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"flinkJarPath\",\"values\":null,\"type\":\"INPUT\",\"value\":\"/opt/dtstack/flink-1.10.1/lib\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"flinkPluginRoot\",\"values\":null,\"type\":\"INPUT\",\"value\":\"/opt/dtstack/110_flinkplugin\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"flinkSessionSlotCount\",\"values\":null,\"type\":\"INPUT\",\"value\":\"10\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"gatewayJobName\",\"values\":null,\"type\":\"INPUT\",\"value\":\"pushgateway\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"high-availability\",\"values\":null,\"type\":\"INPUT\",\"value\":\"ZOOKEEPER\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"high-availability.cluster-id\",\"values\":null,\"type\":\"INPUT\",\"value\":\"/default\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"high-availability.storageDir\",\"values\":null,\"type\":\"INPUT\",\"value\":\"hdfs://ns1/flink110/ha\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"high-availability.zookeeper.path.root\",\"values\":null,\"type\":\"INPUT\",\"value\":\"/flink110\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"high-availability.zookeeper.quorum\",\"values\":null,\"type\":\"INPUT\",\"value\":\"172.16.8.107:2181,172.16.8.108:2181,172.16.8.109:2181\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"historyserver.web.address\",\"values\":null,\"type\":\"INPUT\",\"value\":\"172.16.100.168\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"historyserver.web.port\",\"values\":null,\"type\":\"INPUT\",\"value\":\"8082\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"jarTmpDir\",\"values\":null,\"type\":\"INPUT\",\"value\":\"./tmp110\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"jobmanager.archive.fs.dir\",\"values\":null,\"type\":\"INPUT\",\"value\":\"hdfs://ns1/dtInsight/flink110/completed-jobs\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"metrics.reporter.promgateway.class\",\"values\":null,\"type\":\"INPUT\",\"value\":\"org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"metrics.reporter.promgateway.deleteOnShutdown\",\"values\":[{\"key\":\"false\",\"values\":null,\"type\":null,\"value\":\"false\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"true\",\"values\":null,\"type\":null,\"value\":\"true\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null}],\"type\":\"SELECT\",\"value\":\"true\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"metrics.reporter.promgateway.host\",\"values\":null,\"type\":\"INPUT\",\"value\":\"172.16.100.216\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"metrics.reporter.promgateway.jobName\",\"values\":null,\"type\":\"INPUT\",\"value\":\"110job\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"metrics.reporter.promgateway.port\",\"values\":null,\"type\":\"INPUT\",\"value\":\"9091\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"metrics.reporter.promgateway.randomJobNameSuffix\",\"values\":[{\"key\":\"false\",\"values\":null,\"type\":null,\"value\":\"false\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"true\",\"values\":null,\"type\":null,\"value\":\"true\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null}],\"type\":\"SELECT\",\"value\":\"true\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"monitorAcceptedApp\",\"values\":null,\"type\":\"INPUT\",\"value\":\"false\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"pluginLoadMode\",\"values\":null,\"type\":\"INPUT\",\"value\":\"shipfile\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"prometheusClass\",\"values\":null,\"type\":\"INPUT\",\"value\":\"com.dtstack.jlogstash.metrics.promethues.PrometheusPushGatewayReporter\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"prometheusHost\",\"values\":null,\"type\":\"INPUT\",\"value\":\"\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"prometheusPort\",\"values\":null,\"type\":\"INPUT\",\"value\":\"9090\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"queue\",\"values\":null,\"type\":\"INPUT\",\"value\":\"default\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"remotePluginRootDir\",\"values\":null,\"type\":\"INPUT\",\"value\":\"/opt/dtstack/110_flinkplugin\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"sessionStartAuto\",\"values\":null,\"type\":\"INPUT\",\"value\":\"true\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"state.backend\",\"values\":null,\"type\":\"INPUT\",\"value\":\"RocksDB\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"state.backend.incremental\",\"values\":null,\"type\":\"INPUT\",\"value\":\"true\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"state.checkpoints.dir\",\"values\":null,\"type\":\"INPUT\",\"value\":\"hdfs://ns1/dtInsight/flink110/checkpoints\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"state.checkpoints.num-retained\",\"values\":null,\"type\":\"INPUT\",\"value\":\"11\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"state.savepoints.dir\",\"values\":null,\"type\":\"INPUT\",\"value\":\"hdfs://ns1/dtInsight/flink110/savepoints\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"submitTimeout\",\"values\":null,\"type\":\"INPUT\",\"value\":\"5\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"taskparams.taskmanager.heap.mb\",\"values\":null,\"type\":\"INPUT\",\"value\":\"20\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"yarn.application-attempt-failures-validity-interval\",\"values\":null,\"type\":\"INPUT\",\"value\":\"3600000\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"yarn.application-attempts\",\"values\":null,\"type\":\"INPUT\",\"value\":\"1\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"yarn.jobmanager.heap.mb\",\"values\":null,\"type\":\"INPUT\",\"value\":\"1024\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"yarn.taskmanager.heap.mb\",\"values\":null,\"type\":\"INPUT\",\"value\":\"1024\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"yarn.taskmanager.numberOfTaskManager\",\"values\":null,\"type\":\"INPUT\",\"value\":\"2\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"yarn.taskmanager.numberOfTaskSlots\",\"values\":null,\"type\":\"INPUT\",\"value\":\"2\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"flinkSessionName\",\"value\":\"flinksession_4.0.x_flink110\",\"id\":\"161112966210796160\"}],\"type\":\"GROUP\",\"value\":null,\"required\":true,\"dependencyKey\":\"deploymode\",\"dependencyValue\":\"session\"},{\"key\":\"standalone\",\"values\":[{\"key\":\"clusterMode\",\"values\":null,\"type\":\"INPUT\",\"value\":\"standalone\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"flink.env.java.opts\",\"values\":null,\"type\":\"INPUT\",\"value\":\"-XX:MaxMetaspaceSize=500m\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"flinkJarPath\",\"values\":null,\"type\":\"INPUT\",\"value\":\"/opt/dtstack/DTCommon/Engine/flink/flink110_lib\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"flinkPluginRoot\",\"values\":null,\"type\":\"INPUT\",\"value\":\"/data/insight_plugin/flinkplugin\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"gatewayJobName\",\"values\":null,\"type\":\"INPUT\",\"value\":\"pushgateway\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"high-availability\",\"values\":null,\"type\":\"INPUT\",\"value\":\"zookeeper\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"high-availability.cluster-id\",\"values\":null,\"type\":\"INPUT\",\"value\":\"/default\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"high-availability.storageDir\",\"values\":null,\"type\":\"INPUT\",\"value\":\"hdfs://ns1/flink110/ha\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"high-availability.zookeeper.path.root\",\"values\":null,\"type\":\"INPUT\",\"value\":\"/flink110\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"high-availability.zookeeper.quorum\",\"values\":null,\"type\":\"INPUT\",\"value\":\"\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"historyserver.web.address\",\"values\":null,\"type\":\"INPUT\",\"value\":\"\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"historyserver.web.port\",\"values\":null,\"type\":\"INPUT\",\"value\":\"8082\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"jobmanager.archive.fs.dir\",\"values\":null,\"type\":\"INPUT\",\"value\":\"hdfs://ns1/dtInsight/flink110/completed-jobs\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"jobmanager.heap.mb\",\"values\":null,\"type\":\"INPUT\",\"value\":\"1024\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"metrics.reporter.promgateway.class\",\"values\":null,\"type\":\"INPUT\",\"value\":\"org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"metrics.reporter.promgateway.deleteOnShutdown\",\"values\":[{\"key\":\"false\",\"values\":null,\"type\":null,\"value\":\"false\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"true\",\"values\":null,\"type\":null,\"value\":\"true\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null}],\"type\":\"SELECT\",\"value\":\"true\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"metrics.reporter.promgateway.host\",\"values\":null,\"type\":\"INPUT\",\"value\":\"\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"metrics.reporter.promgateway.jobName\",\"values\":null,\"type\":\"INPUT\",\"value\":\"110job\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"metrics.reporter.promgateway.port\",\"values\":null,\"type\":\"INPUT\",\"value\":\"9091\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"metrics.reporter.promgateway.randomJobNameSuffix\",\"values\":[{\"key\":\"false\",\"values\":null,\"type\":null,\"value\":\"false\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"true\",\"values\":null,\"type\":null,\"value\":\"true\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null}],\"type\":\"SELECT\",\"value\":\"true\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"prometheusClass\",\"values\":null,\"type\":\"INPUT\",\"value\":\"com.dtstack.jlogstash.metrics.promethues.PrometheusPushGatewayReporter\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"prometheusHost\",\"values\":null,\"type\":\"INPUT\",\"value\":\"\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"prometheusPort\",\"values\":null,\"type\":\"INPUT\",\"value\":\"9090\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"remotePluginRootDir\",\"values\":null,\"type\":\"INPUT\",\"value\":\"/data/insight_plugin/flinkplugin\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"state.backend\",\"values\":null,\"type\":\"INPUT\",\"value\":\"RocksDB\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"state.backend.incremental\",\"values\":null,\"type\":\"INPUT\",\"value\":\"true\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"state.checkpoints.dir\",\"values\":null,\"type\":\"INPUT\",\"value\":\"hdfs://ns1/dtInsight/flink110/checkpoints\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"state.checkpoints.num-retained\",\"values\":null,\"type\":\"INPUT\",\"value\":\"11\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"state.savepoints.dir\",\"values\":null,\"type\":\"INPUT\",\"value\":\"hdfs://ns1/dtInsight/flink110/savepoints\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"taskmanager.heap.mb\",\"values\":null,\"type\":\"INPUT\",\"value\":\"1024\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"taskmanager.numberOfTaskManager\",\"values\":null,\"type\":\"INPUT\",\"value\":\"2\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"taskmanager.numberOfTaskSlots\",\"values\":null,\"type\":\"INPUT\",\"value\":\"2\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null}],\"type\":\"GROUP\",\"value\":null,\"required\":true,\"dependencyKey\":\"deploymode\",\"dependencyValue\":\"standalone\"}]";
        String config = "{\"deploymode\":[\"perjob\",\"session\"],\"typeName\":\"k8s-hdfs2-flink110\"}";
        componentConfigService.deepOldClientTemplate(config, json, testComponentId, testClusterId, EComponentType.FLINK.getTypeCode());
        List<ComponentConfig> componentConfigs = componentConfigDao.listByComponentId(testComponentId, true);
        Assert.assertNotNull(componentConfigs);
        List<ClientTemplate> dbClientTemplates = ComponentConfigUtils.buildDBDataToClientTemplate(componentConfigs);
        logger.info(JSONArray.toJSONString(dbClientTemplates));
        Assert.assertTrue(dbClientTemplates.size()>1);
        Map<String, Object> configToMap = componentConfigService.convertComponentConfigToMap(testComponentId, true);
        Assert.assertTrue(MapUtils.isNotEmpty(configToMap));
        componentConfigService.deleteComponentConfig(testComponentId);
    }

    @Test
    public void testParseOldKubernetes() {
        long testK8sComponentId = -4L;
        String json = "{\"kubernetes.context\":\"apiVersion: v1\\nkind: Config\\nclusters:\\n- name: \\\"dtstack\\\"\\n  cluster:\\n    server: \\\"https://172.16.8.88/k8s/clusters/c-vckqj\\\"\\n    certificate-authority-data: \\\"LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUZWRENDQ\\\\\\n      Xp3Q0NRQ3ZTQlZyUVQ1ZGpEQU5CZ2txaGtpRzl3MEJBUXNGQURCc01Rc3dDUVlEVlFRR0V3SkQKV\\\\\\n      GpFUk1BOEdBMVVFQ0F3SVdtaGxhbWxoYm1jeEVUQVBCZ05WQkFjTUNFaGhibWQ2YUc5MU1SQXdEZ\\\\\\n      1lEVlFRSwpEQWQ0WkdWMmIzQnpNUkF3RGdZRFZRUUxEQWQ0WkdWMmIzQnpNUk13RVFZRFZRUUREQ\\\\\\n      XBrZEhOMFlXTnJMbU51Ck1CNFhEVEl3TVRFd01USXhNVGd5TmxvWERUTXdNVEF6TURJeE1UZ3lOb\\\\\\n      G93YkRFTE1Ba0dBMVVFQmhNQ1EwNHgKRVRBUEJnTlZCQWdNQ0Zwb1pXcHBZVzVuTVJFd0R3WURWU\\\\\\n      VFIREFoSVlXNW5lbWh2ZFRFUU1BNEdBMVVFQ2d3SAplR1JsZG05d2N6RVFNQTRHQTFVRUN3d0hlR\\\\\\n      1JsZG05d2N6RVRNQkVHQTFVRUF3d0taSFJ6ZEdGamF5NWpiakNDCkFpSXdEUVlKS29aSWh2Y05BU\\\\\\n      UVCQlFBRGdnSVBBRENDQWdvQ2dnSUJBTHh3MWgzcXptTXE4YUErY01vWFQvM0UKRVVBdzRQNUhCZ\\\\\\n      GZYa2VVa1J1ZjV3VHUvdTBvRHFyUnVRNlcwMFJWS09BUFFQVDZpRVhCQkdyKzdTRGhISi8yTAo4V\\\\\\n      zBnNDRnaDB5N2lWREE1UmErV2dFS2Y4YmRvT3grUkMzME4wb3JyRmtZbDRCR3p2UWtDQXlOV1lZU\\\\\\n      1lheGxDCkFEeDdoQldhMzFQb0FaT3MxN0pBM3Y0a1lUVzhzTVBHQzFtdnhCSHJ4cGswK3NxeFFLQ\\\\\\n      jl2TzlaZ05zbXpQcXAKdUNsT0xQbkxjdkFrSjFMSXNIS3c4bXo3TlFnVnpVV3hzWEFTMVFnaTdXW\\\\\\n      lNwRUVINGsySmtQRTRLNU5Mc2g2Mgo0d3hPS0lRRS83TFFsaVlCRlpIZnFYTHZpU0F5MW94SklTb\\\\\\n      np4UmdmOFZVSmUrMUxKbGE0UHp6UDZuVUI2cGxUCnlKODQ2Z0QxQStxTmM1endlN2RtMytHSFVJN\\\\\\n      VVRNXhZejNZRDdQclRIWlJJd3p3SzVzeXNTM28rTEFOL1duYjgKSnlVK2haNG5hcEZUUzh0SVlLW\\\\\\n      GQ5V0pWUldYNWNzdkd2MXZSY1daK0tySGRtVi9Mc1oxbG04RFd1ODFLV0EyZQpKSmdvMlMrZ3R0R\\\\\\n      FBEenZqMHVnbUNEVFVRZGpsYjVQWnhFdWI4cVYvUXViMTBCQ3JJSkNZenJHMm91dld6TVJLCmJIU\\\\\\n      EE0OWtIVVYrR3BSdW8vNDZZYUw2T3haVjRQcnJjWFU1MVlQTVNZRnJiWU1iT2g1b1NxTnkyY1lrd\\\\\\n      jRhTkUKMDNzQUllclJHMzYvaktnelYzNFFkZGJvTEI5Yk1sZThEaGJQZ3JEV2VGcTdJRlpBVlYwO\\\\\\n      UcwdGx6R1hNaDE5YQpLZm8yVnNMRDZMT1dLTGwvOUNVRkFnTUJBQUV3RFFZSktvWklodmNOQVFFT\\\\\\n      EJRQURnZ0lCQUNvM3ZYcHBJTk9TCkxkSEpTRXRYT3haSi9kMEw4aXZQaEUwZzlRb0h0NUNNUUZWM\\\\\\n      GlKcVpzZzVLQURDckJSUG42ZnBVZDFhaDROZWoKWW5lUWxzbEpUOFFJN1RIQ1ZiTzBuOUtsbEVxW\\\\\\n      EJ5c2gxeDdkTGVXdFVaczRxLzB2QVlxbTc3aWYyaVpWTEFzNgprMk81NmhpelNwN2svZC90SXd3a\\\\\\n      1k3dTN4UkF2b2RWWEM3cnA1ZmViSTRWcDZLUFMwbjF4Yk8wSXBqUzd3KzhPClkxMjFXdmtxUUQyc\\\\\\n      khCMkUySFJSSWdGTEF2eVFqY01hak9KMzRSUnh4VDljUzFIWEUzVUdDVVl2bHc1NnMySmMKMWYvM\\\\\\n      XNXYTJNaHFiUUdzVDZJS3dVOXZ2SlArQktoWnBsWnpicUlnZHZyZHVHNWFCbEpnNlNCQTZsUThST\\\\\\n      DhnNQp6MmpscGFaTE5jUmYySkdTUm5TTzlDZzJTcU5va2wwaDRRZVRCZEFNeGhPVnk0MmxOc1grL\\\\\\n      zVOWVhuM0RXRWo1CmoybTV0SFpRU3ptY3BZZmNlY0ZpRXdud3BwOVdMbWlsb0N3SHVON2FNYU9Za\\\\\\n      kpSVnVPcWJoTXZMUjFLTnpWVm0KUFdVblZJa3FBY2p0TXVKVFh6clZGV081VXEvazF0WkIzdGhNN\\\\\\n      Ed4c1JZcElDbEJmS3c3emFIaGxsZmNvNE53eQprbWVDeENWMnVLZ3AxYkJlZ0hReTVEc3E4OFI5Q\\\\\\n      kh1b0g0aWpEbzlUTGRpU2NXT0o1TkFEOUJ5K2F3VGpsUk1qCmtrVHgyMFRZdm1VdEF6T2N5R0xCO\\\\\\n      ENEWERwS1B4b1ZBdVREMGFKMy9na3p0aHZQQ1dDamxINGI2cXVMS3lwVHgKOU85OVRZWGw3Y2VNS\\\\\\n      XZIc2I3L3l4L01KUG9RbUZjcUgKLS0tLS1FTkQgQ0VSVElGSUNBVEUtLS0tLQ==\\\"\\n- name: \\\"dtstack-172-16-8-160\\\"\\n  cluster:\\n    server: \\\"https://172.16.8.160:6443\\\"\\n    certificate-authority-data: \\\"LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUN3akNDQ\\\\\\n      WFxZ0F3SUJBZ0lCQURBTkJna3Foa2lHOXcwQkFRc0ZBREFTTVJBd0RnWURWUVFERXdkcmRXSmwKT\\\\\\n      FdOaE1CNFhEVEl3TVRFd05UQXlNekF3T0ZvWERUTXdNVEV3TXpBeU16QXdPRm93RWpFUU1BNEdBM\\\\\\n      VVFQXhNSAphM1ZpWlMxallUQ0NBU0l3RFFZSktvWklodmNOQVFFQkJRQURnZ0VQQURDQ0FRb0NnZ\\\\\\n      0VCQUsyQmpTMEdYVmo5ClNZQWpjTFhYWUR2bXo2OStHdWkvbHQ4WHp0aC9ZdG4rU1NxR2NXSms4N\\\\\\n      zhLaHI5RGdRQ1hNR3VtZXZtbUhFemIKYVVsY29oQXU0Q0NaS0VmYmxncFY2ZzFqNGtreVFxS2hTS\\\\\\n      G84VDBhU2VLOW53dDZZMzQ4MXVxTG9UWHhubFhPKwpZblJOWjVsQnlVTWRXb2JmSGhzRGJQM2lLc\\\\\\n      XdkU3NYckhxYWJlNmFDdk5tMlpIelNJbnc1eG0wakZHWXJvS0M5Cll3a3FVYmpWMDEzRXV1bzV1d\\\\\\n      zdKZ2l1eFdFcDhoSjJ1WEFKNkp3OGZEeml5aUxIay9qTGVyTFM2UnN5dWNtYXEKWUJPclIvS2pmT\\\\\\n      llGV1hlTWlqNzF3NXZqRTJFcG1DUnRLMFdtRjFpU3gyUFo2ZlBtYmVSd2lpRUVScXFFZGhYbQpqe\\\\\\n      GZNRnRWSzUzY0NBd0VBQWFNak1DRXdEZ1lEVlIwUEFRSC9CQVFEQWdLa01BOEdBMVVkRXdFQi93U\\\\\\n      UZNQU1CCkFmOHdEUVlKS29aSWh2Y05BUUVMQlFBRGdnRUJBSTRxcUxnVFRJa1VQbGtJUWRGdjlNN\\\\\\n      mRseHpJTlZmU0xXU2QKdEJuZGV6NUVmelBRaVBOdHQzbDlHNklrZmpYSGJUcU1rN0NsTWkvUnZtV\\\\\\n      DlDcjMwc093NHY4WVNOQTFyYjBHYwp2VURJdEl0ZGoyVW8wSUtzQ2dsN0psalhMcVNvd01jczMwQ\\\\\\n      y9IQXJGdU9pSTlsS2VBdlVIeFhFTG5ST1hxaGozCjhoTG5kS0dTak8rYXRFVSs1b0ZFa0pydS9hc\\\\\\n      DdjTGxWSWVQVmh3eHpuM2pSS2dmRDVXTmI4aHRpRzg1YjMrNVIKMGt4eGFjNlRyaDBKQkh5cVpRS\\\\\\n      lVWMUg2djk3NXdqNnZJNU5NcDNvbnlVZjJxaGJzSjVIWEJJRDh2NEtUQms3MQoxVnliMzFwY3NjZ\\\\\\n      EVibGNPTFlveTFyR1VDT1QxMzNmbFlyTldwc2VXcFJ6Y2NPUE1WYVE9Ci0tLS0tRU5EIENFUlRJR\\\\\\n      klDQVRFLS0tLS0K\\\"\\n- name: \\\"dtstack-172-16-8-166\\\"\\n  cluster:\\n    server: \\\"https://172.16.8.166:6443\\\"\\n    certificate-authority-data: \\\"LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUN3akNDQ\\\\\\n      WFxZ0F3SUJBZ0lCQURBTkJna3Foa2lHOXcwQkFRc0ZBREFTTVJBd0RnWURWUVFERXdkcmRXSmwKT\\\\\\n      FdOaE1CNFhEVEl3TVRFd05UQXlNekF3T0ZvWERUTXdNVEV3TXpBeU16QXdPRm93RWpFUU1BNEdBM\\\\\\n      VVFQXhNSAphM1ZpWlMxallUQ0NBU0l3RFFZSktvWklodmNOQVFFQkJRQURnZ0VQQURDQ0FRb0NnZ\\\\\\n      0VCQUsyQmpTMEdYVmo5ClNZQWpjTFhYWUR2bXo2OStHdWkvbHQ4WHp0aC9ZdG4rU1NxR2NXSms4N\\\\\\n      zhLaHI5RGdRQ1hNR3VtZXZtbUhFemIKYVVsY29oQXU0Q0NaS0VmYmxncFY2ZzFqNGtreVFxS2hTS\\\\\\n      G84VDBhU2VLOW53dDZZMzQ4MXVxTG9UWHhubFhPKwpZblJOWjVsQnlVTWRXb2JmSGhzRGJQM2lLc\\\\\\n      XdkU3NYckhxYWJlNmFDdk5tMlpIelNJbnc1eG0wakZHWXJvS0M5Cll3a3FVYmpWMDEzRXV1bzV1d\\\\\\n      zdKZ2l1eFdFcDhoSjJ1WEFKNkp3OGZEeml5aUxIay9qTGVyTFM2UnN5dWNtYXEKWUJPclIvS2pmT\\\\\\n      llGV1hlTWlqNzF3NXZqRTJFcG1DUnRLMFdtRjFpU3gyUFo2ZlBtYmVSd2lpRUVScXFFZGhYbQpqe\\\\\\n      GZNRnRWSzUzY0NBd0VBQWFNak1DRXdEZ1lEVlIwUEFRSC9CQVFEQWdLa01BOEdBMVVkRXdFQi93U\\\\\\n      UZNQU1CCkFmOHdEUVlKS29aSWh2Y05BUUVMQlFBRGdnRUJBSTRxcUxnVFRJa1VQbGtJUWRGdjlNN\\\\\\n      mRseHpJTlZmU0xXU2QKdEJuZGV6NUVmelBRaVBOdHQzbDlHNklrZmpYSGJUcU1rN0NsTWkvUnZtV\\\\\\n      DlDcjMwc093NHY4WVNOQTFyYjBHYwp2VURJdEl0ZGoyVW8wSUtzQ2dsN0psalhMcVNvd01jczMwQ\\\\\\n      y9IQXJGdU9pSTlsS2VBdlVIeFhFTG5ST1hxaGozCjhoTG5kS0dTak8rYXRFVSs1b0ZFa0pydS9hc\\\\\\n      DdjTGxWSWVQVmh3eHpuM2pSS2dmRDVXTmI4aHRpRzg1YjMrNVIKMGt4eGFjNlRyaDBKQkh5cVpRS\\\\\\n      lVWMUg2djk3NXdqNnZJNU5NcDNvbnlVZjJxaGJzSjVIWEJJRDh2NEtUQms3MQoxVnliMzFwY3NjZ\\\\\\n      EVibGNPTFlveTFyR1VDT1QxMzNmbFlyTldwc2VXcFJ6Y2NPUE1WYVE9Ci0tLS0tRU5EIENFUlRJR\\\\\\n      klDQVRFLS0tLS0K\\\"\\n- name: \\\"dtstack-172-16-8-169\\\"\\n  cluster:\\n    server: \\\"https://172.16.8.169:6443\\\"\\n    certificate-authority-data: \\\"LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUN3akNDQ\\\\\\n      WFxZ0F3SUJBZ0lCQURBTkJna3Foa2lHOXcwQkFRc0ZBREFTTVJBd0RnWURWUVFERXdkcmRXSmwKT\\\\\\n      FdOaE1CNFhEVEl3TVRFd05UQXlNekF3T0ZvWERUTXdNVEV3TXpBeU16QXdPRm93RWpFUU1BNEdBM\\\\\\n      VVFQXhNSAphM1ZpWlMxallUQ0NBU0l3RFFZSktvWklodmNOQVFFQkJRQURnZ0VQQURDQ0FRb0NnZ\\\\\\n      0VCQUsyQmpTMEdYVmo5ClNZQWpjTFhYWUR2bXo2OStHdWkvbHQ4WHp0aC9ZdG4rU1NxR2NXSms4N\\\\\\n      zhLaHI5RGdRQ1hNR3VtZXZtbUhFemIKYVVsY29oQXU0Q0NaS0VmYmxncFY2ZzFqNGtreVFxS2hTS\\\\\\n      G84VDBhU2VLOW53dDZZMzQ4MXVxTG9UWHhubFhPKwpZblJOWjVsQnlVTWRXb2JmSGhzRGJQM2lLc\\\\\\n      XdkU3NYckhxYWJlNmFDdk5tMlpIelNJbnc1eG0wakZHWXJvS0M5Cll3a3FVYmpWMDEzRXV1bzV1d\\\\\\n      zdKZ2l1eFdFcDhoSjJ1WEFKNkp3OGZEeml5aUxIay9qTGVyTFM2UnN5dWNtYXEKWUJPclIvS2pmT\\\\\\n      llGV1hlTWlqNzF3NXZqRTJFcG1DUnRLMFdtRjFpU3gyUFo2ZlBtYmVSd2lpRUVScXFFZGhYbQpqe\\\\\\n      GZNRnRWSzUzY0NBd0VBQWFNak1DRXdEZ1lEVlIwUEFRSC9CQVFEQWdLa01BOEdBMVVkRXdFQi93U\\\\\\n      UZNQU1CCkFmOHdEUVlKS29aSWh2Y05BUUVMQlFBRGdnRUJBSTRxcUxnVFRJa1VQbGtJUWRGdjlNN\\\\\\n      mRseHpJTlZmU0xXU2QKdEJuZGV6NUVmelBRaVBOdHQzbDlHNklrZmpYSGJUcU1rN0NsTWkvUnZtV\\\\\\n      DlDcjMwc093NHY4WVNOQTFyYjBHYwp2VURJdEl0ZGoyVW8wSUtzQ2dsN0psalhMcVNvd01jczMwQ\\\\\\n      y9IQXJGdU9pSTlsS2VBdlVIeFhFTG5ST1hxaGozCjhoTG5kS0dTak8rYXRFVSs1b0ZFa0pydS9hc\\\\\\n      DdjTGxWSWVQVmh3eHpuM2pSS2dmRDVXTmI4aHRpRzg1YjMrNVIKMGt4eGFjNlRyaDBKQkh5cVpRS\\\\\\n      lVWMUg2djk3NXdqNnZJNU5NcDNvbnlVZjJxaGJzSjVIWEJJRDh2NEtUQms3MQoxVnliMzFwY3NjZ\\\\\\n      EVibGNPTFlveTFyR1VDT1QxMzNmbFlyTldwc2VXcFJ6Y2NPUE1WYVE9Ci0tLS0tRU5EIENFUlRJR\\\\\\n      klDQVRFLS0tLS0K\\\"\\n\\nusers:\\n- name: \\\"dtstack\\\"\\n  user:\\n    token: \\\"kubeconfig-user-mtrk8.c-vckqj:jk8k6n72dpxtllmtd4p99kwkkxthxsqg6562b9r2t2v7bdkf2jft9n\\\"\\n\\n\\ncontexts:\\n- name: \\\"dtstack\\\"\\n  context:\\n    user: \\\"dtstack\\\"\\n    cluster: \\\"dtstack\\\"\\n- name: \\\"dtstack-172-16-8-160\\\"\\n  context:\\n    user: \\\"dtstack\\\"\\n    cluster: \\\"dtstack-172-16-8-160\\\"\\n- name: \\\"dtstack-172-16-8-166\\\"\\n  context:\\n    user: \\\"dtstack\\\"\\n    cluster: \\\"dtstack-172-16-8-166\\\"\\n- name: \\\"dtstack-172-16-8-169\\\"\\n  context:\\n    user: \\\"dtstack\\\"\\n    cluster: \\\"dtstack-172-16-8-169\\\"\\n\\ncurrent-context: \\\"dtstack-172-16-8-169\\\"\\n\\n\"}";
        componentConfigService.deepOldClientTemplate(json, "[]", testK8sComponentId, testClusterId, EComponentType.KUBERNETES.getTypeCode());
        Map<String, Object> configToMap = componentConfigService.convertComponentConfigToMap(testK8sComponentId, true);
        Assert.assertTrue(MapUtils.isNotEmpty(configToMap));
        Assert.assertTrue(StringUtils.isNotBlank((String) configToMap.get("kubernetes.context")));
    }

    @Test
    public void testParseOldSftp() {
        long testSftp = -5L;
        String json = "[{\"key\":\"auth\",\"values\":[{\"key\":\"password\",\"values\":null,\"type\":null,\"value\":\"1\",\"required\":null,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"rsaPath\",\"values\":null,\"type\":null,\"value\":\"2\",\"required\":null,\"dependencyKey\":null,\"dependencyValue\":null}],\"type\":\"RADIO\",\"value\":\"1\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"host\",\"values\":null,\"type\":\"INPUT\",\"value\":\"172.16.100.115\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"password\",\"values\":null,\"type\":\"INPUT\",\"value\":\"123\",\"required\":true,\"dependencyKey\":\"auth\",\"dependencyValue\":\"1\"},{\"key\":\"path\",\"values\":null,\"type\":\"INPUT\",\"value\":\"/data/sftp\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"port\",\"values\":null,\"type\":\"INPUT\",\"value\":\"22\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"rsaPath\",\"values\":null,\"type\":\"INPUT\",\"required\":true,\"dependencyKey\":\"auth\",\"dependencyValue\":\"2\"},{\"key\":\"username\",\"values\":null,\"type\":\"INPUT\",\"value\":\"root\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null}]";
        String config = "{\"path\":\"/data/sftp\",\"password\":\"abc123\",\"auth\":\"1\",\"port\":\"22\",\"host\":\"127.0.0.1\",\"username\":\"root\"}";
        componentConfigService.deepOldClientTemplate(config, json, testSftp, testClusterId, EComponentType.SFTP.getTypeCode());
        List<ComponentConfig> configs = componentConfigDao.listByComponentId(testSftp, false);
        List<ClientTemplate> clientTemplates = ComponentConfigUtils.buildDBDataToClientTemplate(configs);
        Assert.assertNotNull(clientTemplates);
        Map<String, Object> configToMap = componentConfigService.convertComponentConfigToMap(testSftp, true);
        Assert.assertTrue(MapUtils.isNotEmpty(configToMap));
    }

    @Test
    @Transactional
    @Rollback
    public void loadDBTemplate() {
        testConsoleComponentTemplateDao.insert(typeName, "deploymode:\n" +
                "  controls: checkbox\n" +
                "  value: [perjob,session]\n" +
                "  values:\n" +
                "    perjob:\n" +
                "      dependencyKey: deploymode\n" +
                "      dependencyValue: perjob\n" +
                "      controls: group\n" +
                "      required:\n" +
                "        flinkJarPath: /opt/dtstack/DTCommon/Engine/flink/flink110_lib\n" +
                "        jobmanager.archive.fs.dir: hdfs://ns1/dtInsight/flink110/completed-jobs\n" +
                "        flinkPluginRoot: /data/insight_plugin/flinkplugin\n" +
                "        remotePluginRootDir: /data/insight_plugin/flinkplugin\n" +
                "\n" +
                "        high-availability.cluster-id: /default\n" +
                "        high-availability.zookeeper.path.root: /flink110\n" +
                "        high-availability.zookeeper.quorum:\n" +
                "        high-availability.storageDir: hdfs://ns1/flink110/ha\n" +
                "\n" +
                "        historyserver.web.address:\n" +
                "        historyserver.web.port: 8082\n" +
                "\n" +
                "        state.checkpoints.dir: hdfs://ns1/dtInsight/flink110/checkpoints\n" +
                "        state.checkpoints.num-retained: 11\n" +
                "\n" +
                "        akka.ask.timeout: 60 s\n" +
                "        metrics.reporter.promgateway.class: org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter\n" +
                "        metrics.reporter.promgateway.host:\n" +
                "        metrics.reporter.promgateway.port: 9091\n" +
                "        metrics.reporter.promgateway.jobName: 110job\n" +
                "        metrics.reporter.promgateway.randomJobNameSuffix:\n" +
                "          controls: select\n" +
                "          values: [true,false]\n" +
                "        metrics.reporter.promgateway.deleteOnShutdown:\n" +
                "          controls: select\n" +
                "          values: [true,false]\n" +
                "        clusterMode: perjob\n" +
                "\n" +
                "      optional:\n" +
                "        jarTmpDir: ./tmp110\n" +
                "        queue: default\n" +
                "        pluginLoadMode: shipfile\n" +
                "        submitTimeout: 5\n" +
                "        yarnAccepterTaskNumber: 3\n" +
                "        flinkInterval: 5 SECONDS\n" +
                "        classloader.resolve-order: child-first\n" +
                "        classloader.dtstack-cache: true\n" +
                "\n" +
                "        state.backend: RocksDB\n" +
                "        state.savepoints.dir: hdfs://ns1/dtInsight/flink110/savepoints\n" +
                "        state.backend.incremental: true\n" +
                "\n" +
                "        prometheusHost:\n" +
                "        prometheusPort: 9090\n" +
                "\n" +
                "        yarn.taskmanager.heap.mb: 1024\n" +
                "        yarn.jobmanager.heap.mb: 1024\n" +
                "        yarn.taskmanager.numberOfTaskManager: 2\n" +
                "        yarn.taskmanager.numberOfTaskSlots: 2\n" +
                "\n" +
                "        flink.env.java.opts: -XX:MaxMetaspaceSize=500m\n" +
                "        prometheusClass: com.dtstack.jlogstash.metrics.promethues.PrometheusPushGatewayReporter\n" +
                "        gatewayJobName: pushgateway\n" +
                "        yarn.application-attempts: 0\n" +
                "        yarn.application-attempt-failures-validity-interval: 3600000\n" +
                "        high-availability: ZOOKEEPER\n" +
                "\n" +
                "        monitorAcceptedApp: false\n" +
                "\n" +
                "    session:\n" +
                "      dependencyKey: deploymode\n" +
                "      dependencyValue: session\n" +
                "      controls: group\n" +
                "      required:\n" +
                "        flinkSessionSlotCount: 10\n" +
                "        flinkJarPath: /opt/dtstack/DTCommon/Engine/flink/flink110_lib\n" +
                "        jobmanager.archive.fs.dir: hdfs://ns1/dtInsight/flink110/completed-jobs\n" +
                "        flinkPluginRoot: /data/insight_plugin/flinkplugin\n" +
                "        remotePluginRootDir: /data/insight_plugin/flinkplugin\n" +
                "\n" +
                "        high-availability.cluster-id: /default\n" +
                "        high-availability.zookeeper.path.root: /flink110\n" +
                "        high-availability.zookeeper.quorum:\n" +
                "        high-availability.storageDir: hdfs://ns1/flink110/ha\n" +
                "\n" +
                "        historyserver.web.address:\n" +
                "        historyserver.web.port: 8082\n" +
                "\n" +
                "        state.checkpoints.dir: hdfs://ns1/dtInsight/flink110/checkpoints\n" +
                "        state.checkpoints.num-retained: 11\n" +
                "\n" +
                "        metrics.reporter.promgateway.class: org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter\n" +
                "        metrics.reporter.promgateway.host:\n" +
                "        metrics.reporter.promgateway.port: 9091\n" +
                "        metrics.reporter.promgateway.jobName: 110job\n" +
                "        metrics.reporter.promgateway.randomJobNameSuffix:\n" +
                "          controls: select\n" +
                "          values: [true,false]\n" +
                "        metrics.reporter.promgateway.deleteOnShutdown:\n" +
                "          controls: select\n" +
                "          values: [true,false]\n" +
                "        clusterMode: session\n" +
                "        sessionStartAuto: false\n" +
                "        checkSubmitJobGraphInterval: 60\n" +
                "\n" +
                "      optional:\n" +
                "        jarTmpDir: ./tmp180\n" +
                "        web.timeout: 100000\n" +
                "        blob.service.cleanup.interval: 900\n" +
                "        jobstore.expiration-time: 900\n" +
                "        queue: default\n" +
                "        pluginLoadMode: shipfile\n" +
                "        submitTimeout: 5\n" +
                "        yarnAccepterTaskNumber: 3\n" +
                "        flinkInterval: 5 SECONDS\n" +
                "        classloader.resolve-order: parent-first\n" +
                "        classloader.dtstack-cache: true\n" +
                "\n" +
                "        state.backend: RocksDB\n" +
                "        state.savepoints.dir: hdfs://ns1/dtInsight/flink110/savepoints\n" +
                "        state.backend.incremental: true\n" +
                "\n" +
                "        prometheusHost:\n" +
                "        prometheusPort: 9090\n" +
                "\n" +
                "        yarn.taskmanager.heap.mb: 1024\n" +
                "        yarn.jobmanager.heap.mb: 1024\n" +
                "        yarn.taskmanager.numberOfTaskManager: 2\n" +
                "        yarn.taskmanager.numberOfTaskSlots: 2\n" +
                "\n" +
                "        flink.env.java.opts: -XX:MaxMetaspaceSize=500m\n" +
                "        prometheusClass: com.dtstack.jlogstash.metrics.promethues.PrometheusPushGatewayReporter\n" +
                "        gatewayJobName: pushgateway\n" +
                "        yarn.application-attempts: 0\n" +
                "        yarn.application-attempt-failures-validity-interval: 3600000\n" +
                "        high-availability: ZOOKEEPER\n" +
                "\n" +
                "        monitorAcceptedApp: false\n" +
                "\n" +
                "    standalone:\n" +
                "      dependencyKey: deploymode\n" +
                "      dependencyValue: standalone\n" +
                "      controls: group\n" +
                "      required:\n" +
                "        flinkJarPath: /opt/dtstack/DTCommon/Engine/flink/flink110_lib\n" +
                "        jobmanager.archive.fs.dir: hdfs://ns1/dtInsight/flink110/completed-jobs\n" +
                "        flinkPluginRoot: /data/insight_plugin/flinkplugin\n" +
                "        remotePluginRootDir: /data/insight_plugin/flinkplugin\n" +
                "\n" +
                "        high-availability: zookeeper\n" +
                "        high-availability.cluster-id: /default\n" +
                "        high-availability.zookeeper.path.root: /flink110\n" +
                "        high-availability.zookeeper.quorum:\n" +
                "        high-availability.storageDir: hdfs://ns1/flink110/ha\n" +
                "\n" +
                "        historyserver.web.address:\n" +
                "        historyserver.web.port: 8082\n" +
                "\n" +
                "        state.checkpoints.dir: hdfs://ns1/dtInsight/flink110/checkpoints\n" +
                "        state.checkpoints.num-retained: 11\n" +
                "\n" +
                "        metrics.reporter.promgateway.class: org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter\n" +
                "        metrics.reporter.promgateway.host:\n" +
                "        metrics.reporter.promgateway.port: 9091\n" +
                "        metrics.reporter.promgateway.jobName: 110job\n" +
                "        metrics.reporter.promgateway.randomJobNameSuffix:\n" +
                "          controls: select\n" +
                "          values: [true,false]\n" +
                "        metrics.reporter.promgateway.deleteOnShutdown:\n" +
                "          controls: select\n" +
                "          values: [true,false]\n" +
                "        clusterMode: standalone\n" +
                "\n" +
                "      optional:\n" +
                "        state.backend: RocksDB\n" +
                "        state.savepoints.dir: hdfs://ns1/dtInsight/flink110/savepoints\n" +
                "        state.backend.incremental: true\n" +
                "\n" +
                "        yarnAccepterTaskNumber: 3\n" +
                "        prometheusHost:\n" +
                "        prometheusPort: 9090\n" +
                "\n" +
                "        taskmanager.heap.mb: 1024\n" +
                "        jobmanager.heap.mb: 1024\n" +
                "        taskmanager.numberOfTaskManager: 2\n" +
                "        taskmanager.numberOfTaskSlots: 2\n" +
                "\n" +
                "        flink.env.java.opts: -XX:MaxMetaspaceSize=500m\n" +
                "        prometheusClass: com.dtstack.jlogstash.metrics.promethues.PrometheusPushGatewayReporter\n" +
                "        gatewayJobName: pushgateway\n");
        List<ClientTemplate> clientTemplates = componentService.loadTemplate(EComponentType.FLINK.getTypeCode(), "", "",null);
        Assert.assertNotNull(clientTemplates);
    }


    @Test
    public void addOrUpdateNewComponent() {
        String clusterName = "testNewClientTemplate";
        String templateString = "[{\"key\":\"auth\",\"required\":true,\"type\":\"RADIO_LINKAGE\",\"value\":\"1\",\"values\":[{\"dependencyKey\":\"auth\",\"dependencyValue\":\"1\",\"key\":\"password\",\"required\":true,\"type\":\"\",\"value\":\"1\",\"values\":[{\"dependencyKey\":\"auth$password\",\"dependencyValue\":\"\",\"key\":\"password\",\"required\":true,\"type\":\"input\",\"value\":\"\"}]},{\"dependencyKey\":\"auth\",\"dependencyValue\":\"2\",\"key\":\"rsaPath\",\"required\":true,\"type\":\"\",\"value\":\"2\",\"values\":[{\"dependencyKey\":\"auth$rsaPath\",\"dependencyValue\":\"\",\"key\":\"rsaPath\",\"required\":true,\"type\":\"input\",\"value\":\"\"}]}]},{\"key\":\"fileTimeout\",\"required\":true,\"type\":\"INPUT\",\"value\":\"300000\"},{\"key\":\"host\",\"required\":true,\"type\":\"INPUT\",\"value\":\"127.0.0.1\"},{\"key\":\"isUsePool\",\"required\":true,\"type\":\"INPUT\",\"value\":\"true\"},{\"key\":\"maxIdle\",\"required\":true,\"type\":\"INPUT\",\"value\":\"16\"},{\"key\":\"maxTotal\",\"required\":true,\"type\":\"INPUT\",\"value\":\"16\"},{\"key\":\"maxWaitMillis\",\"required\":true,\"type\":\"INPUT\",\"value\":\"3600000\"},{\"key\":\"minIdle\",\"required\":true,\"type\":\"INPUT\",\"value\":\"16\"},{\"key\":\"path\",\"required\":true,\"type\":\"INPUT\",\"value\":\"/data/sftp\"},{\"key\":\"port\",\"required\":true,\"type\":\"INPUT\",\"value\":\"22\"},{\"key\":\"timeout\",\"required\":true,\"type\":\"INPUT\",\"value\":\"0\"},{\"key\":\"username\",\"required\":true,\"type\":\"INPUT\",\"value\":\"admin\"}]\n";
        //创建集群
        Cluster cluster = new Cluster();
        cluster.setClusterName(clusterName);
        cluster.setHadoopVersion("hadoop2");
        clusterDao.insert(cluster);
        //添加组件 添加引擎
        componentService.addOrUpdateComponent(cluster.getId(), "", null, "hadoop2", "", templateString, EComponentType.SFTP.getTypeCode(),null,null,null);
        Component sftpComponent = componentService.getComponentByClusterId(cluster.getId(), EComponentType.SFTP.getTypeCode());
        Assert.assertNotNull(sftpComponent);
        Map<String, Object> sftpConfig = componentConfigService.convertComponentConfigToMap(sftpComponent.getId(), true);
        Assert.assertNotNull(sftpConfig);
        Map<String, Object> originMap = JSONObject.parseObject("{\"maxWaitMillis\":\"3600000\",\"path\":\"/data/sftp\",\"minIdle\":\"16\",\"maxIdle\":\"16\",\"auth\":\"1\",\"isUsePool\":\"true\",\"port\":\"22\",\"maxTotal\":\"16\",\"host\":\"127.0.0.1\",\"fileTimeout\":\"300000\",\"timeout\":\"0\",\"username\":\"admin\",\"password\":\"\",\"rsaPath\":\"\"}", Map.class);
        for (String key : originMap.keySet()) {
            Assert.assertEquals(originMap.get(key), sftpConfig.get(key));
        }
        for (int i = 0; i < 3; i++) {
            Map<String, Object> cacheComponentConfigMap = componentService.getCacheComponentConfigMap(cluster.getId(), EComponentType.SFTP.getTypeCode(), true);
            if (i == 0) {
                componentService.clearComponentCache();
            }
            Assert.assertNotNull(cacheComponentConfigMap);
        }
    }


    @Test
    public void convertOldData() {
        componentConfigOldCovertService.onApplicationEvent(null);
    }
}
