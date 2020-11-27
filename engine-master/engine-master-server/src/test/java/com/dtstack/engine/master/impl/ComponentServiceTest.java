package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.*;
import com.dtstack.engine.api.dto.Resource;
import com.dtstack.engine.api.pojo.ClientTemplate;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.api.vo.ComponentVO;
import com.dtstack.engine.api.vo.components.ComponentsConfigOfComponentsVO;
import com.dtstack.engine.api.vo.components.ComponentsResultVO;
import com.dtstack.engine.common.client.ClientOperator;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.sftp.SftpConfig;
import com.dtstack.engine.common.sftp.SftpFileManage;
import com.dtstack.engine.dao.TestClusterDao;
import com.dtstack.engine.dao.TestComponentDao;
import com.dtstack.engine.dao.TestKerberosConfigDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.engine.master.enums.DownloadType;
import com.dtstack.engine.master.enums.EComponentType;
import com.dtstack.engine.master.enums.MultiEngineType;
import com.dtstack.engine.master.utils.Template;
import com.dtstack.engine.master.utils.XmlFileUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
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
    @InjectMocks
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
        MockitoAnnotations.initMocks(this);
        initMockClientOperator();
        initMockSftpFileManager();
        initMockXmlFileUtil();
    }

    private void initMockXmlFileUtil() {
        PowerMockito.mockStatic(XmlFileUtil.class);
        List<File> files = new ArrayList<>();
        String hive = getClass().getClassLoader().getResource("hadoopConf/hive-site.xml").getFile();
        files.add(new File(hive));
        String core = getClass().getClassLoader().getResource("hadoopConf/core-site.xml").getFile();
        files.add(new File(core));
        String yarn = getClass().getClassLoader().getResource("hadoopConf/yarn-site.xml").getFile();
        files.add(new File(yarn));
        String hdfs = getClass().getClassLoader().getResource("hadoopConf/hdfs-site.xml").getFile();
        files.add(new File(hdfs));
        when(XmlFileUtil.getFilesFromZip(any(), any(), any())).thenReturn(files);
    }

    private void initMockSftpFileManager() {
        PowerMockito.mock(SftpFileManage.class);
        when(SftpFileManage.getSftpManager(any())).thenReturn(sftpFileManage);
        when(sftpFileManage.downloadDir(any(), any())).thenAnswer((Answer<Boolean>) invocation -> {
            Object[] args = invocation.getArguments();
            String localDir = (String) args[1];
            File file = new File(localDir);
            if (!file.exists()) {
                boolean mkdirs = file.mkdirs();
                if (mkdirs) {
                    files.add(file);
                    System.out.println("创建本地测试目录完成:" + localDir);
                }
            }
            return true;
        });
    }

    private void initMockClientOperator() {
        PowerMockito.mock(ClientOperator.class);
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
        componentService.updateCache(1L, EComponentType.SPARK.getTypeCode());
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
        Assert.assertTrue(StringUtils.isNoneEmpty(getClusterLocalKerberosDir));
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetKerberosConfig() {
        Cluster defaultCluster = DataCollection.getData().getDefaultCluster();
        KerberosConfig getKerberosConfig = componentService.getKerberosConfig(defaultCluster.getId(), EComponentType.YARN.getTypeCode());
        Assert.assertNotNull(getKerberosConfig);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetSFTPConfig() {
        Component component = Template.getDefaultSftpComponentTemplate();
        Map<String, String> getSFTPConfig = componentService.getSFTPConfig(component.getClusterId());
        Assert.assertNotNull(getSFTPConfig);
        SftpConfig sftpConfig = componentService.getSFTPConfig(component, EComponentType.SFTP.getTypeCode(), "{\"path\":\"/data/sftp\",\"password\":\"abc123\",\"auth\":\"1\",\"port\":\"22\",\"host\":\"172.16.100.115\",\"username\":\"root\"}");
        Assert.assertNotNull(sftpConfig);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testAddOrUpdateComponent() {
        Resource resource = new Resource();
        resource.setFileName("test.zip");
        resource.setUploadedFileName("test.zip");
        List<Resource> resources = new ArrayList<>();
        resources.add(resource);
        Cluster defaultCluster = DataCollection.getData().getDefaultK8sCluster();
        ComponentVO addOrUpdateComponent = componentService.addOrUpdateComponent(defaultCluster.getId(),
                "{\"kubernetes.context\":\"{\\\"kubernetes.context\\\":\\\"apiVersion: v1\\\\nclusters:\\\\n- cluster:\\\\n    certificate-authority-data: LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUN5RENDQWJDZ0F3SUJBZ0lCQURBTkJna3Foa2lHOXcwQkFRc0ZBREFWTVJNd0VRWURWUVFERXdwcmRXSmwKY201bGRHVnpNQjRYRFRJd01ETXlOakF6TkRJeU1sb1hEVE13TURNeU5EQXpOREl5TWxvd0ZURVRNQkVHQTFVRQpBeE1LYTNWaVpYSnVaWFJsY3pDQ0FTSXdEUVlKS29aSWh2Y05BUUVCQlFBRGdnRVBBRENDQVFvQ2dnRUJBS1Q5CjB3bGhxdzVPdWM2ZHBkRUMxaUovNys1SUE3QmZ5c0o4QzExREVnT25PMlJIRFk1S010Z2pPVitYMDZJNmF2ck8KdjgrTVZ2dkxGOEppSndkN0p5UWYrUVEwTjJ0UXU3d3h5R0Vla0Z3OUJMaGpicldRL0s2R2lHcEFiUVllbE5ibwpwZ1dFYUxpU3VJbnhUWGtZU2ltNW15dThVWkY4cFlwcnNSL3VIbHZBOFFIc004TjNrT243THppTFhXd1BBZlhvCnorRXhxeVEzc3JJVFZWdHlGakl5djFIME50RWxQODV0R2JrdGh6S2k4UVJzcDRJZTRQM3dHN01mZnNGdFF3Rm8KRnA4WWVkekZtYU9RTzI4dzgxQTFkcS8zRjdYNWhHK054T3hFTkR4cGU0QkQvZmwwWUM4eXI1UUxoZ3RkV29tegpSanU0MldJeHNsVU9pc1hJOExrQ0F3RUFBYU1qTUNFd0RnWURWUjBQQVFIL0JBUURBZ0trTUE4R0ExVWRFd0VCCi93UUZNQU1CQWY4d0RRWUpLb1pJaHZjTkFRRUxCUUFEZ2dFQkFBVXYwTi9GRkxEeVBQMnpEaFdKSGVyS25VNHoKeGxiYVNFMjkrZk5jWml5Q0UvQXVkc0RFbk1vZ25rRDg0WndNS1pXT0xXRkI5aUo5Z1VFcTFIT0ZZMlIyZzhiMgpHVko5dmdTVm9nZHhlQ1c3KzBDZ2JWQlEva1hiaU1SRTdPLy90V2lxQTdXOFBaSnc2Q01reXhNS1FFYUR2RFoxCnlydGE1dnM4cGJlcUIvakUrRUVWL0hDdWIvK3VxdjFhVWtWVHZ2N1ZHYllXU0hMN1Z2eitSOUlGSlp5dTI0dGsKVnBPVGRTbFNyY2Fqb3l0eXdMZTF6VzR4bENNd3FRMkRHaDZFeGl2WHBnSHBXVEVvM3Z1Z3VnUTY2S2RUdXpKaApEWDdZKzg3TTFrV1BDZFJmbW02emp5Zk5sbEhQRWhPWGxsanliSzNxRW1qM2FHSkZaUG1lSXptbWVXOD0KLS0tLS1FTkQgQ0VSVElGSUNBVEUtLS0tLQo=\\\\n    server: https://172.16.101.208:6443\\\\n  name: kubernetes\\\\ncontexts:\\\\n- context:\\\\n    cluster: kubernetes\\\\n    user: kubernetes-admin\\\\n  name: kubernetes-admin@kubernetes\\\\ncurrent-context: kubernetes-admin@kubernetes\\\\nkind: Config\\\\npreferences: {}\\\\nusers:\\\\n- name: kubernetes-admin\\\\n  user:\\\\n    client-certificate-data: LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUM4akNDQWRxZ0F3SUJBZ0lJUzZ2VTI2eUxLSzh3RFFZSktvWklodmNOQVFFTEJRQXdGVEVUTUJFR0ExVUUKQXhNS2EzVmlaWEp1WlhSbGN6QWVGdzB5TURBek1qWXdNelF5TWpKYUZ3MHlNVEF6TWpZd016UXlNalZhTURReApGekFWQmdOVkJBb1REbk41YzNSbGJUcHRZWE4wWlhKek1Sa3dGd1lEVlFRREV4QnJkV0psY201bGRHVnpMV0ZrCmJXbHVNSUlCSWpBTkJna3Foa2lHOXcwQkFRRUZBQU9DQVE4QU1JSUJDZ0tDQVFFQXRuVTJZd1hFbFF6cEZZRTgKUGdzajA0azM3QVQ4alI0ajF4cDFEdjNYbHlMSHU5N0JSbURUaVpwTnZmK2lmRHQ2eEVWMVliREYwRHU1NTIrdAplcGt6OVMzK2ZTS1JCTGtpbXhHeHhQQ2xBVUFIMHU4dlRBWWFmeCs5WndUcHdWYU5oY0NWdlRpWnI1Vlp2WEhrCmRkbDgydHFsa29yWVdzeWp2eXdzalFGZEgwNkpyMEw0KzdMaVpPem40YzdDdnBkTWIwamlCRG9uWFFocHBCb1gKNmFFQVE3YUg2WUozaWFtd1lKSTJWdDdITDZ4THFJRU5nU0RNMzRIUm01N2xxNHNIVEYxeEIvTlVKRXRIa3VzbwpsclFYRmpITmtKTWR0bU9lK2JKUWVnL1ZCUjNueWgrVXQ3eGdqV1dMd1pLaUhwSnFKWFZwSFA4dkRudUh4UVA5CmJZYlVrd0lEQVFBQm95Y3dKVEFPQmdOVkhROEJBZjhFQkFNQ0JhQXdFd1lEVlIwbEJBd3dDZ1lJS3dZQkJRVUgKQXdJd0RRWUpLb1pJaHZjTkFRRUxCUUFEZ2dFQkFDRXFJWS9TRUd3RXlUUTZWSm03RDM1b0ZmVXljTnEzQTNISwpsTis1bXovTVUzUTZxSGQyenRKcTVZOUs5Tlh6OEk3Y0RDajNtbGFhVjZMeUp0MkcweHBiRDV6RUY2T09xcXg5Cnp1cUpacUtjVmxadmRva2FFNUdmdDA2dkxWN2pjQU5wVDVPditrTWRMSWQ1WkZlNU50REh5N01mMzQwNmJSSXMKamlHTWJiWjVVbXdCSThnZVdha3o0YXY4YVY0akZyZkxtbkRvUUhRMENYNFpQT1pPcmZwRC9wK21tUTBTQ1dFZgpyZ1hKTk02OUozY0xES0tUbGgyQ3FiYnNVcmt0UWhNcHdFNEFzL1A2RjIwUWk3eHpiTDhQa3B4OFB6cGhLSUdPCjR6TGE0akU5U2Rtd1Q0NGJwYUVWNEd0dlQxVGRMUXN1VWVTMm9WNjhpZ1RQWVNlSWpaYz0KLS0tLS1FTkQgQ0VSVElGSUNBVEUtLS0tLQo=\\\\n    client-key-data: LS0tLS1CRUdJTiBSU0EgUFJJVkFURSBLRVktLS0tLQpNSUlFcEFJQkFBS0NBUUVBdG5VMll3WEVsUXpwRllFOFBnc2owNGszN0FUOGpSNGoxeHAxRHYzWGx5TEh1OTdCClJtRFRpWnBOdmYraWZEdDZ4RVYxWWJERjBEdTU1Mit0ZXBrejlTMytmU0tSQkxraW14R3h4UENsQVVBSDB1OHYKVEFZYWZ4Kzlad1Rwd1ZhTmhjQ1Z2VGlacjVWWnZYSGtkZGw4MnRxbGtvcllXc3lqdnl3c2pRRmRIMDZKcjBMNAorN0xpWk96bjRjN0N2cGRNYjBqaUJEb25YUWhwcEJvWDZhRUFRN2FINllKM2lhbXdZSkkyVnQ3SEw2eExxSUVOCmdTRE0zNEhSbTU3bHE0c0hURjF4Qi9OVUpFdEhrdXNvbHJRWEZqSE5rSk1kdG1PZStiSlFlZy9WQlIzbnloK1UKdDd4Z2pXV0x3WktpSHBKcUpYVnBIUDh2RG51SHhRUDliWWJVa3dJREFRQUJBb0lCQUVmVXczR2Vqck1EWHl3SgpNZmRYR1dhcFNldlFWc0VUMFpaWW95Y2d4bVNJMjh0WnVndUVDU1BPTExjVlVobklyTjlpWFFEMXdCcm51SnVsCnVzMWVUVGRFUVVGd2YxazFyYXNRLzBTQ1hPT3VHcVp2WmRadlBMVTVnSzV6SDdmdTVFNTQ4RHFMY3UzT1JZTXcKdUhteEF0ZUNadGJYZEsvaWlzQ3ptbUowMi8xN21xNzlpOThBWUY2aHc1WlowMENkQXhMWUR5akJqMDZqL0pTNApUZmNKZjRYY1lhTzlTenNUWkRQQUxQbkx3UnZ5TTRVNzVzL0pTTSt6cnA3cHdvUFhNTzUzVVRkRGhNZnBFMlJNCmZVSlJHNk1JcTliVTBLWGVMR1crQzNlTkF1Q1FsWGM4MEZ5cmRSK1NESFRpZ0V2V1pDQWhHMTFrVW1qYmpCRUUKdnZUMGNSRUNnWUVBMElSbVhsaXF5T20zTjZDZXlhdFlyaUJDcWsxdDhyQmpIQXRoNDVyUGdSRFdUemZmTXBvZwo3TmpET0t0cnd1ZU8zb3o0bWloVUtqV25LOEg0VVhSeXUyb3MxcnJuaFc3cENiQ0RCREVITTdNY1pGaU9QeURMCllGeXRWVVRFMlhpQ3orU2VkQndSQUx0MlFTaDNVSlpFZG1COERPVzJDYkhnRTY5VEVYR2tMV2tDZ1lFQTRBR3MKUHNDM1VmQWNTN0ZzQzVYZ1ZQSTJNT0VxOWczVzgzb2hnREJOcFlFZldjMS82c291YVJIQWNCbitlUVNrUU1CZgo0UENFWllpWnFqTTVQajZGQVZLSkNCZldLSUpZamdWK0ZaWGRBc2QyWVlPb21XR0JacnF1b0pCYzhvRW1zbXBDClg4Unc3TjlPMmRCejNJbWpIU3RPNmQ1b0hXSHlIU0c5QnVwYjVwc0NnWUVBbmE4Q0d1YkNnQno5eUx0V1dQdVMKbkZzWkR1QnUvTkFXb3VhWXFCNHlQVkFXUU9Ibmo4U3VrVzE2ZENodDNYNXV0QzIyOGh6OVNNNDZGUVVpVzdiTAo3SjVtT2h3dGFPSnVxRDByVnNnY3dpUDRuSW03U0ZIc2VucWJPWmcvcEpWVmx4RTBJbW4zRWE2eHhxUnJWaTNNCnFCaGV0d0lmbjBVOFJxYVhFdUgxWGNFQ2dZRUEyWGJtUjdtQmZvdFNmTzFPVGVUL2RwZjVvZlJHWjc3QnlYYnMKWk96L3hFZVpMdTVBVzZoUjYvQ3UyR1Z6MVBwN2x0enJkNDBuaXdaVTM1V0E0ZnVCMWVuUlhFai93QzNpV0dYZQpwSWZybWxJWGk4MXI5Uk5pczE5U1BsQkgyNmtqN3hzWE9xK1RUWEh3czZZWmhLVWQ5Q2hpSU1xb1dyWUdmTitQCkNkS2t5emNDZ1lBeU1USEY1LzQrNkMwVlIzWFFOb0F6bmh4RmtBV1MyeXB2Rk9zMW15V3RXaExXd0xpVFVQdTIKZ3lUWUFWYlpGU2xpVkZtRThKcjNJWURIL1lKamtFOTJNdHFaeW8rTGM5bFllR0xTZC9HeTdxaEJ0WmJNeEtVMwpMWk1reE1yNkRyMktWNEJlTldnWHI0b2p2aXVTMEtxWk94MlkyalBxcnpvNnkxbENJS2cyWUE9PQotLS0tLUVORCBSU0EgUFJJVkFURSBLRVktLS0tLQo=\\\\n\\\"}\"}",
                resources, "hadoop3", "", "", EComponentType.KUBERNETES.getTypeCode(), EComponentType.HDFS.getTypeCode());
    }

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
        resource.setFileName("test.zip");
        resource.setUploadedFileName("test.zip");
        List<Resource> resources = new ArrayList<>();
        resources.add(resource);
        List<Object> config = componentService.config(resources, EComponentType.YARN.getTypeCode(), false);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testBuildSftpPath() {
        String buildSftpPath = componentService.buildSftpPath(1L, EComponentType.SPARK.getTypeCode());
        Assert.assertNotNull(buildSftpPath);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testTestConnect() {
        Component one = componentDao.getOne();
        Cluster cluster = clusterDao.getOne();
        Map<String, String> getSFTPConfig = componentService.getSFTPConfig(cluster.getId());
        ComponentTestResult testConnect = componentService.testConnect(EComponentType.YARN.getTypeCode(), one.getComponentConfig(), cluster.getClusterName(), one.getHadoopVersion(), one.getEngineId(), null, getSFTPConfig, 0);
        Assert.assertNotNull(testConnect);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testWrapperConfig() {
        Component defaultSftpComponent = DataCollection.getData().getDefaultSftpComponent();
        Map<String, String> getSFTPConfig = componentService.getSFTPConfig(defaultSftpComponent.getClusterId());
        Component one = componentDao.getOne();
        Cluster cluster = clusterDao.getOne();
        String wrapperConfig = componentService.wrapperConfig(EComponentType.YARN.getTypeCode(), one.getComponentConfig(), getSFTPConfig, null, cluster.getClusterName());
        Assert.assertTrue(StringUtils.isNoneEmpty(wrapperConfig));
    }

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
    public void testDownloadFile() {
        Component defaultHdfsComponent = DataCollection.getData().getDefaultHdfsComponent();
        Cluster defaultCluster = DataCollection.getData().getDefaultCluster();
        System.out.println("组件id=" + defaultHdfsComponent.getId());
        System.out.println("组件类型=" + defaultHdfsComponent.getComponentTypeCode());
        File downloadFile = componentService.downloadFile(defaultHdfsComponent.getId(), DownloadType.Kerberos.getCode(), defaultHdfsComponent.getComponentTypeCode(), "hadoop3", defaultCluster.getClusterName());
        Assert.assertNotNull(downloadFile);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testLoadTemplate() {
        Cluster defaultCluster = DataCollection.getData().getDefaultCluster();
        List<ClientTemplate> loadTemplate = componentService.loadTemplate(EComponentType.YARN.getTypeCode(), defaultCluster.getClusterName(), "hadoop3", EComponentType.HDFS.getTypeCode());
        Assert.assertTrue(CollectionUtils.isNotEmpty(loadTemplate));
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
        defaultYarnComponentTemplate.setComponentTypeCode(EComponentType.SPARK_THRIFT.getTypeCode());
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
    public void testGetPluginInfoWithComponentType() {
        Tenant defaultTenant = DataCollection.getData().getDefaultTenant();
        JSONObject getPluginInfoWithComponentType = componentService.getPluginInfoWithComponentType(defaultTenant.getDtUicTenantId(), EComponentType.YARN);
        Assert.assertNotNull(getPluginInfoWithComponentType);
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
    public void testAddOrUpdateNamespaces() {
        Cluster defaultCluster = DataCollection.getData().getDefaultCluster();
        Queue defaultQueue = DataCollection.getData().getDefaultQueue();
        Tenant defaultTenant = DataCollection.getData().getDefaultTenant();
        Long addOrUpdateNamespaces = componentService.addOrUpdateNamespaces(defaultCluster.getId(), "test", defaultQueue.getId(), defaultTenant.getDtUicTenantId());
        Assert.assertNotNull(addOrUpdateNamespaces);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testIsYarnSupportGpus() {
        Cluster defaultCluster = DataCollection.getData().getDefaultCluster();
        Boolean isYarnSupportGpus = componentService.isYarnSupportGpus(defaultCluster.getClusterName());
        Assert.assertTrue(!isYarnSupportGpus);
    }
}
