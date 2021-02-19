package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.ComponentConfig;
import com.dtstack.engine.api.pojo.ClientTemplate;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.dao.ComponentConfigDao;
import com.dtstack.engine.dao.TestConsoleComponentTemplateDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.enums.EComponentType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * @author yuebai
 * @date 2021-02-08
 */
public class TemplateTest extends AbstractTest {

    private final static Logger logger = LoggerFactory.getLogger(TemplateTest.class);
    Long testClusterId = -1L;
    Long testEngineId = -2L;
    Long testComponentId = -3L;
    String typeName = "yarn2-hdfs2-flink180";
    @Autowired
    private ComponentConfigDao componentConfigDao;
    @Autowired
    private ComponentConfigService componentConfigService;
    @Autowired
    private TestConsoleComponentTemplateDao testConsoleComponentTemplateDao;
    @MockBean
    private ComponentService componentService;
    @Autowired
    private EnvironmentContext environmentContext;
    @Autowired
    private ComponentTemplateService componentTemplateService;

    @Before
    public void init(){
        when(componentService.convertComponentTypeToClient(any(),any(),any())).thenReturn(typeName);
        when(componentService.loadTemplate(anyInt(),anyString(),any())).thenCallRealMethod();
        ReflectionTestUtils.setField(componentService,"env",environmentContext);
        ReflectionTestUtils.setField(componentService,"componentTemplateService",componentTemplateService);
    }

    @Test
    public void testParseOldTemplate() {
        String json = "[{\"key\":\"deploymode\",\"values\":[{\"key\":\"perjob\",\"values\":null,\"type\":null,\"value\":\"perjob\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"session\",\"values\":null,\"type\":null,\"value\":\"session\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"standalone\",\"values\":null,\"type\":null,\"value\":\"standalone\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null}],\"type\":\"CHECKBOX\",\"value\":[\"perjob\",\"session\"],\"required\":true,\"dependencyKey\":\"\",\"dependencyValue\":\"\"},{\"key\":\"perjob\",\"values\":[{\"key\":\"classloader.resolve-order\",\"values\":null,\"type\":\"INPUT\",\"value\":\"parent-first\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"clusterMode\",\"values\":null,\"type\":\"INPUT\",\"value\":\"perjob\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"flink.env.java.opts\",\"values\":null,\"type\":\"INPUT\",\"value\":\"-XX:MaxMetaspaceSize=500m\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"flinkInterval\",\"values\":null,\"type\":\"INPUT\",\"value\":\"5 SECONDS\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"flinkJarPath\",\"values\":null,\"type\":\"INPUT\",\"value\":\"/opt/dtstack/flink-1.10.1/lib\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"flinkPluginRoot\",\"values\":null,\"type\":\"INPUT\",\"value\":\"/opt/dtstack/110_flinkplugin\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"gatewayJobName\",\"values\":null,\"type\":\"INPUT\",\"value\":\"pushgateway\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"high-availability\",\"values\":null,\"type\":\"INPUT\",\"value\":\"ZOOKEEPER\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"high-availability.cluster-id\",\"values\":null,\"type\":\"INPUT\",\"value\":\"/default\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"high-availability.storageDir\",\"values\":null,\"type\":\"INPUT\",\"value\":\"hdfs://ns1/flink110/ha\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"high-availability.zookeeper.path.root\",\"values\":null,\"type\":\"INPUT\",\"value\":\"/flink110\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"high-availability.zookeeper.quorum\",\"values\":null,\"type\":\"INPUT\",\"value\":\"172.16.8.107:2181,172.16.8.108:2181,172.16.8.109:2181\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"historyserver.web.address\",\"values\":null,\"type\":\"INPUT\",\"value\":\"172.16.100.168\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"historyserver.web.port\",\"values\":null,\"type\":\"INPUT\",\"value\":\"8082\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"jarTmpDir\",\"values\":null,\"type\":\"INPUT\",\"value\":\"./tmp110\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"jobmanager.archive.fs.dir\",\"values\":null,\"type\":\"INPUT\",\"value\":\"hdfs://ns1/dtInsight/flink110/completed-jobs\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"metrics.reporter.promgateway.class\",\"values\":null,\"type\":\"INPUT\",\"value\":\"org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"metrics.reporter.promgateway.deleteOnShutdown\",\"values\":[{\"key\":\"false\",\"values\":null,\"type\":null,\"value\":\"false\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"true\",\"values\":null,\"type\":null,\"value\":\"true\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null}],\"type\":\"SELECT\",\"value\":\"true\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"metrics.reporter.promgateway.host\",\"values\":null,\"type\":\"INPUT\",\"value\":\"172.16.100.216\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"metrics.reporter.promgateway.jobName\",\"values\":null,\"type\":\"INPUT\",\"value\":\"110job\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"metrics.reporter.promgateway.port\",\"values\":null,\"type\":\"INPUT\",\"value\":\"9091\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"metrics.reporter.promgateway.randomJobNameSuffix\",\"values\":[{\"key\":\"false\",\"values\":null,\"type\":null,\"value\":\"false\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"true\",\"values\":null,\"type\":null,\"value\":\"true\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null}],\"type\":\"SELECT\",\"value\":\"true\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"monitorAcceptedApp\",\"values\":null,\"type\":\"INPUT\",\"value\":\"false\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"pluginLoadMode\",\"values\":null,\"type\":\"INPUT\",\"value\":\"shipfile\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"prometheusClass\",\"values\":null,\"type\":\"INPUT\",\"value\":\"com.dtstack.jlogstash.metrics.promethues.PrometheusPushGatewayReporter\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"prometheusHost\",\"values\":null,\"type\":\"INPUT\",\"value\":\"172.16.20.16\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"prometheusPort\",\"values\":null,\"type\":\"INPUT\",\"value\":\"9090\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"queue\",\"values\":null,\"type\":\"INPUT\",\"value\":\"default\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"remotePluginRootDir\",\"values\":null,\"type\":\"INPUT\",\"value\":\"/opt/dtstack/110_flinkplugin\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"state.backend\",\"values\":null,\"type\":\"INPUT\",\"value\":\"RocksDB\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"state.backend.incremental\",\"values\":null,\"type\":\"INPUT\",\"value\":\"true\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"state.checkpoints.dir\",\"values\":null,\"type\":\"INPUT\",\"value\":\"hdfs://ns1/dtInsight/flink110/checkpoints\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"state.checkpoints.num-retained\",\"values\":null,\"type\":\"INPUT\",\"value\":\"11\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"state.savepoints.dir\",\"values\":null,\"type\":\"INPUT\",\"value\":\"hdfs://ns1/dtInsight/flink110/savepoints\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"submitTimeout\",\"values\":null,\"type\":\"INPUT\",\"value\":\"5\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"taskparams.taskmanager.heap.mb\",\"values\":null,\"type\":\"INPUT\",\"value\":\"20\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"yarn.application-attempt-failures-validity-interval\",\"values\":null,\"type\":\"INPUT\",\"value\":\"3600000\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"yarn.application-attempts\",\"values\":null,\"type\":\"INPUT\",\"value\":\"3\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"yarn.jobmanager.heap.mb\",\"values\":null,\"type\":\"INPUT\",\"value\":\"1024\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"yarn.taskmanager.heap.mb\",\"values\":null,\"type\":\"INPUT\",\"value\":\"1024\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"yarn.taskmanager.numberOfTaskManager\",\"values\":null,\"type\":\"INPUT\",\"value\":\"2\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"yarn.taskmanager.numberOfTaskSlots\",\"values\":null,\"type\":\"INPUT\",\"value\":\"2\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null}],\"type\":\"GROUP\",\"value\":null,\"required\":true,\"dependencyKey\":\"deploymode\",\"dependencyValue\":\"perjob\"},{\"key\":\"session\",\"values\":[{\"key\":\"checkSubmitJobGraphInterval\",\"values\":null,\"type\":\"INPUT\",\"value\":\"30\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"classloader.resolve-order\",\"values\":null,\"type\":\"INPUT\",\"value\":\"parent-first\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"clusterMode\",\"values\":null,\"type\":\"INPUT\",\"value\":\"session\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"flink.env.java.opts\",\"values\":null,\"type\":\"INPUT\",\"value\":\"-XX:MaxMetaspaceSize=500m\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"flinkInterval\",\"values\":null,\"type\":\"INPUT\",\"value\":\"5 SECONDS\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"flinkJarPath\",\"values\":null,\"type\":\"INPUT\",\"value\":\"/opt/dtstack/flink-1.10.1/lib\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"flinkPluginRoot\",\"values\":null,\"type\":\"INPUT\",\"value\":\"/opt/dtstack/110_flinkplugin\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"flinkSessionSlotCount\",\"values\":null,\"type\":\"INPUT\",\"value\":\"10\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"gatewayJobName\",\"values\":null,\"type\":\"INPUT\",\"value\":\"pushgateway\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"high-availability\",\"values\":null,\"type\":\"INPUT\",\"value\":\"ZOOKEEPER\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"high-availability.cluster-id\",\"values\":null,\"type\":\"INPUT\",\"value\":\"/default\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"high-availability.storageDir\",\"values\":null,\"type\":\"INPUT\",\"value\":\"hdfs://ns1/flink110/ha\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"high-availability.zookeeper.path.root\",\"values\":null,\"type\":\"INPUT\",\"value\":\"/flink110\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"high-availability.zookeeper.quorum\",\"values\":null,\"type\":\"INPUT\",\"value\":\"172.16.8.107:2181,172.16.8.108:2181,172.16.8.109:2181\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"historyserver.web.address\",\"values\":null,\"type\":\"INPUT\",\"value\":\"172.16.100.168\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"historyserver.web.port\",\"values\":null,\"type\":\"INPUT\",\"value\":\"8082\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"jarTmpDir\",\"values\":null,\"type\":\"INPUT\",\"value\":\"./tmp110\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"jobmanager.archive.fs.dir\",\"values\":null,\"type\":\"INPUT\",\"value\":\"hdfs://ns1/dtInsight/flink110/completed-jobs\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"metrics.reporter.promgateway.class\",\"values\":null,\"type\":\"INPUT\",\"value\":\"org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"metrics.reporter.promgateway.deleteOnShutdown\",\"values\":[{\"key\":\"false\",\"values\":null,\"type\":null,\"value\":\"false\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"true\",\"values\":null,\"type\":null,\"value\":\"true\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null}],\"type\":\"SELECT\",\"value\":\"true\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"metrics.reporter.promgateway.host\",\"values\":null,\"type\":\"INPUT\",\"value\":\"172.16.100.216\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"metrics.reporter.promgateway.jobName\",\"values\":null,\"type\":\"INPUT\",\"value\":\"110job\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"metrics.reporter.promgateway.port\",\"values\":null,\"type\":\"INPUT\",\"value\":\"9091\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"metrics.reporter.promgateway.randomJobNameSuffix\",\"values\":[{\"key\":\"false\",\"values\":null,\"type\":null,\"value\":\"false\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"true\",\"values\":null,\"type\":null,\"value\":\"true\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null}],\"type\":\"SELECT\",\"value\":\"true\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"monitorAcceptedApp\",\"values\":null,\"type\":\"INPUT\",\"value\":\"false\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"pluginLoadMode\",\"values\":null,\"type\":\"INPUT\",\"value\":\"shipfile\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"prometheusClass\",\"values\":null,\"type\":\"INPUT\",\"value\":\"com.dtstack.jlogstash.metrics.promethues.PrometheusPushGatewayReporter\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"prometheusHost\",\"values\":null,\"type\":\"INPUT\",\"value\":\"\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"prometheusPort\",\"values\":null,\"type\":\"INPUT\",\"value\":\"9090\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"queue\",\"values\":null,\"type\":\"INPUT\",\"value\":\"default\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"remotePluginRootDir\",\"values\":null,\"type\":\"INPUT\",\"value\":\"/opt/dtstack/110_flinkplugin\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"sessionStartAuto\",\"values\":null,\"type\":\"INPUT\",\"value\":\"true\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"state.backend\",\"values\":null,\"type\":\"INPUT\",\"value\":\"RocksDB\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"state.backend.incremental\",\"values\":null,\"type\":\"INPUT\",\"value\":\"true\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"state.checkpoints.dir\",\"values\":null,\"type\":\"INPUT\",\"value\":\"hdfs://ns1/dtInsight/flink110/checkpoints\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"state.checkpoints.num-retained\",\"values\":null,\"type\":\"INPUT\",\"value\":\"11\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"state.savepoints.dir\",\"values\":null,\"type\":\"INPUT\",\"value\":\"hdfs://ns1/dtInsight/flink110/savepoints\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"submitTimeout\",\"values\":null,\"type\":\"INPUT\",\"value\":\"5\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"taskparams.taskmanager.heap.mb\",\"values\":null,\"type\":\"INPUT\",\"value\":\"20\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"yarn.application-attempt-failures-validity-interval\",\"values\":null,\"type\":\"INPUT\",\"value\":\"3600000\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"yarn.application-attempts\",\"values\":null,\"type\":\"INPUT\",\"value\":\"1\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"yarn.jobmanager.heap.mb\",\"values\":null,\"type\":\"INPUT\",\"value\":\"1024\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"yarn.taskmanager.heap.mb\",\"values\":null,\"type\":\"INPUT\",\"value\":\"1024\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"yarn.taskmanager.numberOfTaskManager\",\"values\":null,\"type\":\"INPUT\",\"value\":\"2\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"yarn.taskmanager.numberOfTaskSlots\",\"values\":null,\"type\":\"INPUT\",\"value\":\"2\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"flinkSessionName\",\"value\":\"flinksession_4.0.x_flink110\",\"id\":\"161112966210796160\"}],\"type\":\"GROUP\",\"value\":null,\"required\":true,\"dependencyKey\":\"deploymode\",\"dependencyValue\":\"session\"},{\"key\":\"standalone\",\"values\":[{\"key\":\"clusterMode\",\"values\":null,\"type\":\"INPUT\",\"value\":\"standalone\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"flink.env.java.opts\",\"values\":null,\"type\":\"INPUT\",\"value\":\"-XX:MaxMetaspaceSize=500m\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"flinkJarPath\",\"values\":null,\"type\":\"INPUT\",\"value\":\"/opt/dtstack/DTCommon/Engine/flink/flink110_lib\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"flinkPluginRoot\",\"values\":null,\"type\":\"INPUT\",\"value\":\"/data/insight_plugin/flinkplugin\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"gatewayJobName\",\"values\":null,\"type\":\"INPUT\",\"value\":\"pushgateway\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"high-availability\",\"values\":null,\"type\":\"INPUT\",\"value\":\"zookeeper\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"high-availability.cluster-id\",\"values\":null,\"type\":\"INPUT\",\"value\":\"/default\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"high-availability.storageDir\",\"values\":null,\"type\":\"INPUT\",\"value\":\"hdfs://ns1/flink110/ha\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"high-availability.zookeeper.path.root\",\"values\":null,\"type\":\"INPUT\",\"value\":\"/flink110\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"high-availability.zookeeper.quorum\",\"values\":null,\"type\":\"INPUT\",\"value\":\"\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"historyserver.web.address\",\"values\":null,\"type\":\"INPUT\",\"value\":\"\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"historyserver.web.port\",\"values\":null,\"type\":\"INPUT\",\"value\":\"8082\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"jobmanager.archive.fs.dir\",\"values\":null,\"type\":\"INPUT\",\"value\":\"hdfs://ns1/dtInsight/flink110/completed-jobs\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"jobmanager.heap.mb\",\"values\":null,\"type\":\"INPUT\",\"value\":\"1024\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"metrics.reporter.promgateway.class\",\"values\":null,\"type\":\"INPUT\",\"value\":\"org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"metrics.reporter.promgateway.deleteOnShutdown\",\"values\":[{\"key\":\"false\",\"values\":null,\"type\":null,\"value\":\"false\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"true\",\"values\":null,\"type\":null,\"value\":\"true\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null}],\"type\":\"SELECT\",\"value\":\"true\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"metrics.reporter.promgateway.host\",\"values\":null,\"type\":\"INPUT\",\"value\":\"\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"metrics.reporter.promgateway.jobName\",\"values\":null,\"type\":\"INPUT\",\"value\":\"110job\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"metrics.reporter.promgateway.port\",\"values\":null,\"type\":\"INPUT\",\"value\":\"9091\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"metrics.reporter.promgateway.randomJobNameSuffix\",\"values\":[{\"key\":\"false\",\"values\":null,\"type\":null,\"value\":\"false\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"true\",\"values\":null,\"type\":null,\"value\":\"true\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null}],\"type\":\"SELECT\",\"value\":\"true\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"prometheusClass\",\"values\":null,\"type\":\"INPUT\",\"value\":\"com.dtstack.jlogstash.metrics.promethues.PrometheusPushGatewayReporter\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"prometheusHost\",\"values\":null,\"type\":\"INPUT\",\"value\":\"\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"prometheusPort\",\"values\":null,\"type\":\"INPUT\",\"value\":\"9090\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"remotePluginRootDir\",\"values\":null,\"type\":\"INPUT\",\"value\":\"/data/insight_plugin/flinkplugin\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"state.backend\",\"values\":null,\"type\":\"INPUT\",\"value\":\"RocksDB\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"state.backend.incremental\",\"values\":null,\"type\":\"INPUT\",\"value\":\"true\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"state.checkpoints.dir\",\"values\":null,\"type\":\"INPUT\",\"value\":\"hdfs://ns1/dtInsight/flink110/checkpoints\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"state.checkpoints.num-retained\",\"values\":null,\"type\":\"INPUT\",\"value\":\"11\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"state.savepoints.dir\",\"values\":null,\"type\":\"INPUT\",\"value\":\"hdfs://ns1/dtInsight/flink110/savepoints\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"taskmanager.heap.mb\",\"values\":null,\"type\":\"INPUT\",\"value\":\"1024\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"taskmanager.numberOfTaskManager\",\"values\":null,\"type\":\"INPUT\",\"value\":\"2\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"taskmanager.numberOfTaskSlots\",\"values\":null,\"type\":\"INPUT\",\"value\":\"2\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null}],\"type\":\"GROUP\",\"value\":null,\"required\":true,\"dependencyKey\":\"deploymode\",\"dependencyValue\":\"standalone\"}]";
        List<ClientTemplate> clientTemplates = JSONObject.parseArray(json, ClientTemplate.class);

        componentConfigService.deepOldClientTemplate(clientTemplates,testComponentId,testClusterId,testEngineId,EComponentType.FLINK.getTypeCode());
        logger.info(JSONObject.toJSONString(clientTemplates));

        List<ComponentConfig> componentConfigs = componentConfigDao.listByComponentId(testComponentId);
        Assert.assertNotNull(componentConfigs);
        List<ClientTemplate> dbClientTemplates = componentConfigService.buildDBDataToComponentConfig(testComponentId);
        logger.info(JSONArray.toJSONString(clientTemplates));
        Assert.assertEquals(1,dbClientTemplates.size());
        componentConfigService.deleteComponentConfig(testComponentId);
    }


    @Test
    @Transactional
    @Rollback
    public void loadDBTemplate(){
        testConsoleComponentTemplateDao.insert(typeName,"deploymode:\n" +
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
        List<ClientTemplate> clientTemplates = componentService.loadTemplate(EComponentType.FLINK.getTypeCode(), "", "");
        Assert.assertNotNull(clientTemplates);
    }

}
