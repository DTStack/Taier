package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.KerberosConfig;
import com.dtstack.engine.api.pojo.ClientTemplate;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.api.vo.ComponentVO;
import com.dtstack.engine.api.vo.components.ComponentsConfigOfComponentsVO;
import com.dtstack.engine.api.vo.components.ComponentsResultVO;
import com.dtstack.engine.common.akka.config.AkkaConfig;
import com.dtstack.engine.common.client.ClientOperator;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.sftp.SftpConfig;
import com.dtstack.engine.dao.TestClusterDao;
import com.dtstack.engine.dao.TestComponentDao;
import com.dtstack.engine.dao.TestKerberosConfigDao;
import com.dtstack.engine.master.enums.EComponentType;
import com.dtstack.engine.master.enums.MultiEngineType;
import com.dtstack.engine.master.router.login.DtUicUserConnect;
import com.dtstack.engine.master.utils.Template;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.dtstack.engine.master.AbstractTest;
import org.springframework.test.annotation.Rollback;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author basion
 * @Classname ComponentServiceTest
 * @Description unit test for ComponentService
 * @Date 2020-11-25 20:00:08
 * @Created basion
 */
@PrepareForTest({AkkaConfig.class, ClientOperator.class})
public class ComponentServiceTest extends AbstractTest {

    @Autowired
    private ComponentService componentService;

    @Autowired
    TestComponentDao componentDao;

    @Autowired
    TestKerberosConfigDao kerberosConfigDao;

    @Autowired
    TestClusterDao clusterDao;

    @Mock
    private ClientOperator clientOperator;

    /**
     * do some mock before test
     */
    @Before
    public void setup() throws Exception {
        initMock();
    }

    private void initMock() {
        MockitoAnnotations.initMocks(this);
        initMockAkka();
        initMockClientOperator();
    }

    private void initMockClientOperator() {
        PowerMockito.mockStatic(ClientOperator.class);
        ComponentTestResult componentTestResult = new ComponentTestResult();
        componentTestResult.setResult(true);
        when(ClientOperator.getInstance()).thenReturn(clientOperator);
        when(clientOperator.testConnect(any(),any())).thenReturn(componentTestResult);
    }

    private void initMockAkka() {
        PowerMockito.mockStatic(AkkaConfig.class);
        when(AkkaConfig.isLocalMode()).thenReturn(true);
    }
    @Test
    @Rollback
    public void testListConfigOfComponents() {
        initYarnComponentForTest();
        initHdfsComponentForTest();
        List<ComponentsConfigOfComponentsVO> listConfigOfComponents = componentService.listConfigOfComponents(1L, MultiEngineType.HADOOP.getType());
        Assert.assertTrue(CollectionUtils.isNotEmpty(listConfigOfComponents));
    }

    private Component initYarnComponentForTest() {
        Component component = Template.getDefaultYarnComponentTemplate();
        componentDao.insert(component);
        return component;
    }

    private Component initHdfsComponentForTest() {
        Component component = Template.getDefaltHdfsComponentTemplate();
        componentDao.insert(component);
        return component;
    }

    @Test
    public void testGetOne() {
        Component one = componentDao.getOne();
        Component getOne = componentService.getOne(one.getId());
        Assert.assertNotNull(getOne);
    }

    @Test
    public void testGetSftpClusterKey() {
        String getSftpClusterKey = componentService.getSftpClusterKey(1L);
        Assert.assertNotNull(getSftpClusterKey);
    }

    @Test
    public void testUpdateCache() {
        componentService.updateCache(1L, EComponentType.SPARK.getTypeCode());
    }

    @Test
    @Rollback
    public void testListComponent() {
        initYarnComponentForTest();
        initHdfsComponentForTest();
        List<Component> listComponent = componentService.listComponent(1L);
        Assert.assertTrue(CollectionUtils.isNotEmpty(listComponent));
    }

    @Test
    public void testGetClusterLocalKerberosDir() {
        String getClusterLocalKerberosDir = componentService.getClusterLocalKerberosDir(1L);
        Assert.assertTrue(StringUtils.isNoneEmpty(getClusterLocalKerberosDir));
    }

    @Test
    @Rollback
    public void testGetKerberosConfig() {
        initYarnComponentForTest();
        initHdfsComponentForTest();
        KerberosConfig getKerberosConfig = componentService.getKerberosConfig(1L, EComponentType.YARN.getTypeCode());
        Assert.assertNotNull(getKerberosConfig);
    }

    @Test
    @Rollback
    public void testGetSFTPConfig() {
        Component component = initSftpComponentForTest();
        Map<String, String> getSFTPConfig = componentService.getSFTPConfig(1L);
        Assert.assertNotNull(getSFTPConfig);
        SftpConfig sftpConfig = componentService.getSFTPConfig(component, EComponentType.SFTP.getTypeCode(), "{\"path\":\"/data/sftp\",\"password\":\"abc123\",\"auth\":\"1\",\"port\":\"22\",\"host\":\"172.16.100.115\",\"username\":\"root\"}");
        Assert.assertNotNull(sftpConfig);
    }

    private Component initSftpComponentForTest() {
        Component defaultSftpComponentTemplate = Template.getDefaultSftpComponentTemplate();
        componentDao.insert(defaultSftpComponentTemplate);
        return defaultSftpComponentTemplate;
    }

    @Test
    public void testAddOrUpdateComponent() {
        ComponentVO addOrUpdateComponent = componentService.addOrUpdateComponent(1L, "", null, "", "", "", 0, 0);
        //TODO
    }

    @Test
    public void testBuildConfRemoteDir() {
        String buildConfRemoteDir = componentService.buildConfRemoteDir(1L);
        Assert.assertNotNull(buildConfRemoteDir);
    }

    @Test
    public void testCloseKerberos() {
        Component one = componentDao.getOne();
        componentService.closeKerberos(one.getId());
    }

    @Test(expected = RdosDefineException.class)
    public void testAddOrCheckClusterWithName() {
        Cluster one = clusterDao.getOne();
        ComponentsResultVO addOrCheckClusterWithName = componentService.addOrCheckClusterWithName(one.getClusterName());
    }

    @Test
    public void testConfig() {
        List<Object> config = componentService.config(null, 0, false);
        //TODO
    }

    @Test
    public void testBuildSftpPath() {
        String buildSftpPath = componentService.buildSftpPath(1L, EComponentType.SPARK.getTypeCode());
        Assert.assertNotNull(buildSftpPath);
    }

    @Test
    @Rollback
    public void testTestConnect() {
        Component one = componentDao.getOne();
        Cluster cluster = clusterDao.getOne();
        Component component = initSftpComponentForTest();
        Map<String, String> getSFTPConfig = componentService.getSFTPConfig(1L);
        ComponentTestResult testConnect = componentService.testConnect(EComponentType.YARN.getTypeCode(), one.getComponentConfig(), cluster.getClusterName(), one.getHadoopVersion(), one.getEngineId(), null, getSFTPConfig, 0);
        Assert.assertNotNull(testConnect);
    }

    @Test
    @Rollback
    public void testWrapperConfig() {
        Component component = initSftpComponentForTest();
        Map<String, String> getSFTPConfig = componentService.getSFTPConfig(1L);
        Component one = componentDao.getOne();
        Cluster cluster = clusterDao.getOne();
        String wrapperConfig = componentService.wrapperConfig(EComponentType.YARN.getTypeCode(), one.getComponentConfig(), getSFTPConfig, null, cluster.getClusterName());
        Assert.assertTrue(StringUtils.isNoneEmpty(wrapperConfig));
    }

    @Test
    public void testGetLocalKerberosPath() {
        String getLocalKerberosPath = componentService.getLocalKerberosPath(1L, 0);
        //TODO
    }

    @Test
    public void testDownloadFile() {
        File downloadFile = componentService.downloadFile(0L, 0, 0, "", "");
        //TODO
    }

    @Test
    public void testLoadTemplate() {
        List<ClientTemplate> loadTemplate = componentService.loadTemplate(0, "", "", 0);
        //TODO
    }

    @Test
    public void testConvertComponentTypeToClient() {
        String convertComponentTypeToClient = componentService.convertComponentTypeToClient("", 0, "", 0);
        //TODO
    }

    @Test
    public void testFormatHadoopVersion() {
        String formatHadoopVersion = componentService.formatHadoopVersion("", null);
        //TODO
    }

    @Test
    public void testDelete() {
        componentService.delete(null);
        //TODO
    }

    @Test
    public void testGetComponentVersion() {
        Map getComponentVersion = componentService.getComponentVersion();
        //TODO
    }

    @Test
    public void testGetComponentByClusterId() {
        Component getComponentByClusterId = componentService.getComponentByClusterId(0L, 0);
        //TODO
    }

    @Test
    public void testRefresh() {
        List<ComponentTestResult> refresh = componentService.refresh("");
        //TODO
    }

    @Test
    public void testTestConnects() {
        List<ComponentTestResult> testConnects = componentService.testConnects("");
        //TODO
    }

    @Test
    public void testGetPluginInfoWithComponentType() {
        JSONObject getPluginInfoWithComponentType = componentService.getPluginInfoWithComponentType(0L, null);
        //TODO
    }

    @Test
    public void testGetComponentStore() {
        List<Component> getComponentStore = componentService.getComponentStore("", 0);
        //TODO
    }

    @Test
    public void testAddOrUpdateNamespaces() {
        Long addOrUpdateNamespaces = componentService.addOrUpdateNamespaces(0L, "", 0L, 0L);
        //TODO
    }

    @Test
    public void testIsYarnSupportGpus() {
        Boolean isYarnSupportGpus = componentService.isYarnSupportGpus("");
        //TODO
    }
}
