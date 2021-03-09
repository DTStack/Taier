package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.*;
import com.dtstack.engine.api.dto.Resource;
import com.dtstack.engine.api.pojo.ClientTemplate;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.api.vo.components.ComponentsConfigOfComponentsVO;
import com.dtstack.engine.api.vo.components.ComponentsResultVO;
import com.dtstack.engine.common.client.ClientOperator;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.MultiEngineType;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.sftp.SftpFileManage;
import com.dtstack.engine.dao.TestClusterDao;
import com.dtstack.engine.dao.TestComponentDao;
import com.dtstack.engine.dao.TestKerberosConfigDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.engine.master.utils.Template;
import com.dtstack.schedule.common.util.ZipUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.dtstack.engine.common.constrant.ConfigConstant.USER_DIR_UNZIP;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @author basion
 * @Classname ComponentServiceTest
 * @Description unit test for ComponentService
 * @Date 2020-11-25 20:00:08
 * @Created basion
 */
public class ComponentServiceTest extends AbstractTest {

    @Autowired
    private ComponentService componentService;

    @Autowired
    TestComponentDao componentDao;

    @Autowired
    TestKerberosConfigDao kerberosConfigDao;

    @Autowired
    TestClusterDao clusterDao;

    @MockBean
    private ClientOperator clientOperator;

    @MockBean
    private SftpFileManage sftpFileManage;

    /**
     * 存储临时创建的目录
     */
    private List<File> files = Lists.newArrayList();

    /**
     * do some mock before test
     */
    @Before
    public void setup() throws Exception {
        initMock();
    }

    @After
    public void cleanUp() {
        cleanDirs();
    }

    private void cleanDirs() {
        for (File file : files) {
            file.deleteOnExit();
        }
    }

    private void initMock() throws Exception {
        initMockClientOperator();
        initMockSftpFileManager();
    }

    private void initMockSftpFileManager() {
        SftpFileManage sftpFileManageBean = sftpFileManage;
        when(sftpFileManage.retrieveSftpManager(any())).thenReturn(sftpFileManageBean);
        when(sftpFileManageBean.downloadDir(anyString(), anyString())).thenAnswer((Answer<Boolean>) invocation -> {
            Object[] args = invocation.getArguments();
            String localDir = (String) args[1];
            File file = new File(localDir);
            if (!file.exists()) {
                boolean mkdirs = file.mkdirs();
                if (mkdirs) {
                    String hadoopConf = getClass().getClassLoader().getResource("hadoopConf").getFile();
                    FileUtils.copyDirectory(new File(hadoopConf),file);
                    files.add(file);
                    System.out.println("创建本地测试目录完成:" + localDir);
                }
            }
            return true;
        });
    }

    private void initMockClientOperator() {
        ComponentTestResult componentTestResult = new ComponentTestResult();
        componentTestResult.setResult(true);
        when(clientOperator.testConnect(any(), any())).thenReturn(componentTestResult);
        List<ClientTemplate> templates = new ArrayList<>();
        ClientTemplate clientTemplate = new ClientTemplate();
        templates.add(clientTemplate);
        when(clientOperator.getDefaultPluginConfig(any(), any())).thenReturn(templates);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testListConfigOfComponents() {
        Tenant defaultTenant = DataCollection.getData().getDefaultTenant();
        List<ComponentsConfigOfComponentsVO> listConfigOfComponents = componentService.listConfigOfComponents(defaultTenant.getDtUicTenantId(), MultiEngineType.HADOOP.getType());
        Assert.assertTrue(CollectionUtils.isNotEmpty(listConfigOfComponents));
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetOne() {
        Component one = componentDao.getOne();
        Component getOne = componentService.getOne(one.getId());
        Assert.assertNotNull(getOne);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetSftpClusterKey() {
        String getSftpClusterKey = componentService.getSftpClusterKey(1L);
        Assert.assertNotNull(getSftpClusterKey);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testUpdateCache() {
        Engine defaultHadoopEngine = DataCollection.getData().getDefaultHadoopEngine();
        componentService.updateCache(defaultHadoopEngine.getId(), EComponentType.SPARK.getTypeCode());
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testListComponent() {

        List<Component> listComponent = componentService.listComponent(1L);
        Assert.assertTrue(CollectionUtils.isNotEmpty(listComponent));
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetClusterLocalKerberosDir() {
        String getClusterLocalKerberosDir = componentService.getClusterLocalKerberosDir(1L);
        Assert.assertTrue(StringUtils.isNotEmpty(getClusterLocalKerberosDir));
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetKerberosConfig() {
        Cluster defaultCluster = DataCollection.getData().getDefaultCluster();
        KerberosConfig getKerberosConfig = componentService.getKerberosConfig(defaultCluster.getId(), EComponentType.YARN.getTypeCode());
        Assert.assertNotNull(getKerberosConfig);
    }

/*    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetSFTPConfig() {
        Component component = Template.getDefaultSftpComponentTemplate();
        Map<String, String> getSFTPConfig = componentService.getSFTPConfig(component.getClusterId());
        Assert.assertNotNull(getSFTPConfig);
        SftpConfig sftpConfig = componentService.getSFTPConfig(component, EComponentType.SFTP.getTypeCode(), "{\"path\":\"/data/sftp\",\"password\":\"abc123\",\"auth\":\"1\",\"port\":\"22\",\"host\":\"172.16.100.115\",\"username\":\"root\"}");
        Assert.assertNotNull(sftpConfig);
    }*/


    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testBuildConfRemoteDir() {
        String buildConfRemoteDir = componentService.buildConfRemoteDir(1L);
        Assert.assertNotNull(buildConfRemoteDir);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testCloseKerberos() {
        Component one = componentDao.getOne();
        componentService.closeKerberos(one.getId());
    }

    @Test(expected = RdosDefineException.class)
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testAddOrCheckClusterWithName() {
        Cluster one = clusterDao.getOne();
        ComponentsResultVO addOrCheckClusterWithName = componentService.addOrCheckClusterWithName(one.getClusterName());
        Assert.assertNotNull(addOrCheckClusterWithName);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testConfig() {
        Resource resource = new Resource();
        resource.setFileName("hadoopConf.zip");
        String hadoopConfZip = getClass().getClassLoader().getResource("zip/hadoopConf.zip").getFile();
        resource.setUploadedFileName(hadoopConfZip);
        List<Resource> resources = new ArrayList<>();
        resources.add(resource);
        List<Object> config = componentService.config(resources, EComponentType.YARN.getTypeCode(), false,null);
        Assert.assertNotNull(config);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testConfig2() {
        Resource resource = new Resource();
        resource.setFileName("a.json");
        String jsonFile = getClass().getClassLoader().getResource("json/a.json").getFile();
        resource.setUploadedFileName(jsonFile);
        List<Resource> resources = new ArrayList<>();
        resources.add(resource);
        try {
            List<Object> config = componentService.config(resources, EComponentType.FLINK.getTypeCode(), false,null);
        } catch (Exception e) {
            Assert.assertEquals("JSON文件格式错误",e.getMessage());
        }

    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testBuildSftpPath() {
        String buildSftpPath = componentService.buildSftpPath(1L, EComponentType.SPARK.getTypeCode());
        Assert.assertNotNull(buildSftpPath);
    }

/*    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testTestConnect() {
        Component one = DataCollection.getData().getDefaultK8sClusterHdfsComponent();
        Cluster cluster = DataCollection.getData().getDefaultK8sCluster();
        Map<String, String> getSFTPConfig = componentService.getSFTPConfig(cluster.getId());
        ComponentTestResult testConnect = componentService.testConnect(EComponentType.HDFS.getTypeCode(), one.getComponentConfig(), cluster.getClusterName(), one.getHadoopVersion(), one.getEngineId(), null, getSFTPConfig, 0);
        Assert.assertNotNull(testConnect);
    }*/

/*    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testWrapperConfig() {
        Component defaultSftpComponent = DataCollection.getData().getDefaultSftpComponent();
        Map<String, String> getSFTPConfig = componentService.getSFTPConfig(defaultSftpComponent.getClusterId());
        Component one = componentDao.getOne();
        Cluster cluster = clusterDao.getOne();
        KerberosConfig kerberosConfig = new KerberosConfig();
        kerberosConfig.setKrbName("tt.krb5");
        kerberosConfig.setRemotePath("console/1");
        kerberosConfig.setClusterId(cluster.getId());
        kerberosConfig.setOpenKerberos(1);
        kerberosConfig.setPrincipal("hive@host.com");
        String wrapperConfig = componentService.wrapperConfig(EComponentType.YARN.getTypeCode(), one.getComponentConfig(), getSFTPConfig, kerberosConfig, cluster.getClusterName());
        Assert.assertTrue(StringUtils.isNotEmpty(wrapperConfig));
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testWrapperConfigSql() {
        Component defaultSftpComponent = DataCollection.getData().getDefaultSftpComponent();
        Map<String, String> getSFTPConfig = componentService.getSFTPConfig(defaultSftpComponent.getClusterId());
        Component one = DataCollection.getData().getDefaultSparkSqlComponent();
        Cluster cluster = DataCollection.getData().getDefaultCluster();
        KerberosConfig kerberosConfig = new KerberosConfig();
        kerberosConfig.setKrbName("tt.krb5");
        kerberosConfig.setRemotePath("console/1");
        kerberosConfig.setClusterId(cluster.getId());
        kerberosConfig.setOpenKerberos(1);
        kerberosConfig.setPrincipal("hive@host.com");
        String wrapperConfig = componentService.wrapperConfig(EComponentType.SPARK_THRIFT.getTypeCode(), one.getComponentConfig(), getSFTPConfig, kerberosConfig, cluster.getClusterName());
        Assert.assertTrue(StringUtils.isNotEmpty(wrapperConfig));
    }*/

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetLocalKerberosPath() {
        Cluster defaultCluster = DataCollection.getData().getDefaultCluster();
        String getLocalKerberosPath = componentService.getLocalKerberosPath(defaultCluster.getId(), EComponentType.SPARK.getTypeCode());
        Assert.assertNotNull(getLocalKerberosPath);
    }


    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testLoadTemplate() {
        Cluster defaultCluster = DataCollection.getData().getDefaultCluster();
        List<ClientTemplate> loadTemplate = componentService.loadTemplate(EComponentType.YARN.getTypeCode(), defaultCluster.getClusterName(), "hadoop3", EComponentType.HDFS.getTypeCode());
        Assert.assertTrue(CollectionUtils.isEmpty(loadTemplate));
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testConvertComponentTypeToClient() {
        Cluster defaultCluster = DataCollection.getData().getDefaultCluster();
        String convertComponentTypeToClient = componentService.convertComponentTypeToClient(defaultCluster.getClusterName(), EComponentType.YARN.getTypeCode(), "hadoop3", EComponentType.HDFS.getTypeCode());
        Assert.assertTrue(StringUtils.isNotEmpty(convertComponentTypeToClient));
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testFormatHadoopVersion() {
        String formatHadoopVersion = componentService.formatHadoopVersion("hadoop3", EComponentType.YARN);
        Assert.assertTrue(StringUtils.isNotEmpty(formatHadoopVersion));
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testDelete() {
        Component defaultYarnComponentTemplate = Template.getDefaultYarnComponentTemplate();
        defaultYarnComponentTemplate.setComponentTypeCode(EComponentType.HIVE_SERVER.getTypeCode());
        componentDao.insert(defaultYarnComponentTemplate);
        componentService.delete(Lists.newArrayList(defaultYarnComponentTemplate.getId().intValue()));
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetComponentVersion() {
        Map getComponentVersion = componentService.getComponentVersion();
        Assert.assertNotNull(getComponentVersion);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetComponentByClusterId() {
        Cluster defaultCluster = DataCollection.getData().getDefaultCluster();
        Component getComponentByClusterId = componentService.getComponentByClusterId(defaultCluster.getId(), EComponentType.YARN.getTypeCode());
        Assert.assertNotNull(getComponentByClusterId);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testRefresh() {
        Cluster defaultCluster = DataCollection.getData().getDefaultCluster();
        List<ComponentTestResult> refresh = componentService.refresh(defaultCluster.getClusterName());
        Assert.assertTrue(CollectionUtils.isNotEmpty(refresh));
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testTestConnects() {
        Cluster defaultCluster = DataCollection.getData().getDefaultCluster();
        List<ComponentTestResult> testConnects = componentService.testConnects(defaultCluster.getClusterName());
        Assert.assertTrue(CollectionUtils.isNotEmpty(testConnects));
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetComponentStore() {
        Cluster defaultCluster = DataCollection.getData().getDefaultCluster();
        List<Component> getComponentStore = componentService.getComponentStore(defaultCluster.getClusterName(), EComponentType.YARN.getTypeCode());
        Assert.assertTrue(CollectionUtils.isNotEmpty(getComponentStore));
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testIsYarnSupportGpus() {
        Cluster defaultCluster = DataCollection.getData().getDefaultCluster();
        Boolean isYarnSupportGpus = componentService.isYarnSupportGpus(defaultCluster.getClusterName());
        Assert.assertTrue(!isYarnSupportGpus);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testParseKerberos() {
        List<Resource> resources = getResources();
        List<String> list = componentService.parseKerberos(resources);
        Assert.assertNotNull(list);
    }

    private List<Resource> getResources() {

        File file1 = new File(this.getClass().getResource("/kerberos/krb5.conf").getFile());
        File file2 = new File(this.getClass().getResource("/kerberos/hive-cdh03.keytab").getFile());
        List<File> files = Lists.newArrayList(file1, file2);
        ZipUtil.zipFile(USER_DIR_UNZIP + File.separator+"kerberos.zip",files);
        List<Resource> resources = Lists.newArrayList();
        Resource resource = new Resource();
        resource.setFileName("kerberos");
        resource.setKey("abcdefg");
        resource.setSize(20);
        resource.setUploadedFileName(USER_DIR_UNZIP + File.separator+"kerberos.zip");
        resource.setContentType("fajflajflajgljalgjalg");
        resources.add(resource);
        return resources;
    }


//    @Test
//    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
//    @Rollback
//    public void testUploadKerberos(){
//
//        Cluster defaultCluster = DataCollection.getData().getDefaultCluster();
//        List<Resource> resources = getResources();
//        Resource resource = resources.get(0);
//        resource.setFileName("kerberos.zip");
//        List<Resource> resourceList = Collections.singletonList(resource);
//        String kerberos = componentService.uploadKerberos(resourceList, defaultCluster.getId(), 10);
//        Assert.assertNotNull(kerberos);
//    }


    @Test
    public void testUpdateKrb5Conf(){

        componentService.updateKrb5Conf("[libdefaults]\n" +
                "default_realm = DTSTACK.COM\n" +
                "dns_lookup_kdc = false\n" +
                "dns_lookup_realm = false\n" +
                "ticket_lifetime = 600\n" +
                "renew_lifetime = 3600 \n" +
                "forwardable = true\n" +
                "default_tgs_enctypes = rc4-hmac aes256-cts\n" +
                "default_tkt_enctypes = rc4-hmac aes256-cts\n" +
                "permitted_enctypes = rc4-hmac aes256-cts\n" +
                "udp_preference_limit = 1\n" +
                "kdc_timeout = 3000\n" +
                "\n" +
                "[realms]\n" +
                "DTSTACK.COM = {\n" +
                "kdc = eng-cdh1\n" +
                "admin_server = eng-cdh1\n" +
                "default_domain = DTSTACK.COM\n" +
                "}\n" +
                "\n" +
                "[domain_realm]\n" +
                " .k.com = K.COM\n" +
                " k.com = K.COM\n" +
                " krb01.k.com = K.COM\n" +
                " eng-cdh1 = DTSTACK.COM\n" +
                " eng-cdh2 = DTSTACK.COM\n" +
                " eng-cdh3 = DTSTACK.COM");
    }
}
