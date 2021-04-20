package com.dtstack.lineage.impl;

import com.dtstack.engine.api.domain.LineageDataSource;
import com.dtstack.engine.api.domain.Tenant;
import com.dtstack.engine.api.dto.DataSourceDTO;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.common.enums.SourceType;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.engine.master.utils.Template;
import com.dtstack.schedule.common.enums.AppType;
import com.dtstack.schedule.common.enums.DataSourceType;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * @Author: ZYD
 * Date: 2021/1/27 20:05
 * Description: 测试
 * @since 1.0.0
 */
public class LineageDataSourceServiceTest extends AbstractTest {


    @Autowired
    private LineageDataSourceService sourceService;

    @Autowired
    private LineageDataSourceService dataSourceService;

    @Test
    public void testGenerateSourceKey(){

        String sourceKey = sourceService.generateSourceKey("{\n" +
                " \"jdbcUrl\":\"jdbc:phoenix:172.16.8.107,172.16.8.108,172.16.8.109:2181\"\n" +
                "}", DataSourceType.Phoenix.getVal());
        Assert.assertEquals("172.16.8.107#2181_172.16.8.108#2181_172.16.8.109#2181",sourceKey);
        String sourceKey2 = sourceService.generateSourceKey("{\n" +
                " \"jdbcUrl\":\"jdbc:phoenix:172.16.8.109:2181\"\n" +
                "}", DataSourceType.Phoenix.getVal());
        Assert.assertEquals("172.16.8.109#2181",sourceKey2);
        String sourceKey3 = sourceService.generateSourceKey("{\n" +
                " \"jdbcUrl\":\"172.16.10.104,172.16.10.224,172.16.10.252:2181\"\n" +
                "}", DataSourceType.HBASE.getVal());
        Assert.assertEquals("172.16.10.104#2181_172.16.10.224#2181_172.16.10.252#2181",sourceKey3);

        String sourceKey4 = sourceService.generateSourceKey("{\n" +
                " \"jdbcUrl\":\"172.16.100.175:2181,172.16.101.196:2181,172.16.101.227:2181\"\n" +
                "}", DataSourceType.HBASE.getVal());
        Assert.assertEquals("172.16.100.175#2181_172.16.101.196#2181_172.16.101.227#2181",sourceKey4);

        String sourceKey5 = sourceService.generateSourceKey("{\n" +
                " \"jdbcUrl\":\"172.16.100.175,172.16.101.196,172.16.101.227:2181\"\n" +
                "}", DataSourceType.HBASE.getVal());
        Assert.assertEquals("172.16.100.175#2181_172.16.101.196#2181_172.16.101.227#2181",sourceKey5);

        String sourceKey6 = sourceService.generateSourceKey("{\n" +
        " \"jdbcUrl\":\"jdbc:impala://172.16.8.83:21050/dtstack;AuthMech=3\"\n" +
        "}", DataSourceType.IMPALA.getVal());
        Assert.assertEquals("172.16.8.83#21050",sourceKey6);

        String sourceKey7 = sourceService.generateSourceKey("{\n" +
                " \"jdbcUrl\":\"jdbc:hive2://krbt3:10000/default;principal=hdfs/krbt3@DTSTACK.COM\"\n" +
                "}", DataSourceType.HIVE.getVal());
        Assert.assertEquals("krbt3#10000",sourceKey7);

    }


    @Test
    public void testGenerateSourceKey2(){

        String sourceKey = sourceService.generateSourceKey("{\"password\":\"Abc12345\",\"jdbcUrl\":\"jdbc:sqlserver://172.16.101.246:1433;database=shihu\",\"username\":\"sa\"}"
                , DataSourceType.HIVE.getVal());
        Assert.assertEquals("172.16.101.246#1433",sourceKey);

    }

    @Test
    public void testGenerateSourceKey3(){

        String sourceKey = sourceService.generateSourceKey("{\"password\":\"\",\"jdbcUrl\":\"jdbc:impala://172.16.100.109:21050/\",\"username\":\"\"}"
                , DataSourceType.HIVE.getVal());
        Assert.assertEquals("172.16.100.109#21050",sourceKey);

    }


    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testAddOrUpdateDataSource(){

        Tenant tenant = DataCollection.getData().getTenant();
        DataSourceDTO dataSourceDTO = getDataSourceDTO(tenant.getDtUicTenantId());
        Long count = dataSourceService.addOrUpdateDataSource(dataSourceDTO);
        Assert.assertNotNull(count);

        //测试更新数据源
        dataSourceDTO.setDataSourceId(count);
        dataSourceDTO.setSourceName("测试逻辑数据源改");
        Long id = dataSourceService.addOrUpdateDataSource(dataSourceDTO);
        Assert.assertEquals(count.toString(),id.toString());

    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetDataSourceByIdAndAppType(){

        LineageDataSource dataSource = DataCollection.getData().getDefaultLineageDataSource();
        LineageDataSource dataSourceByIdAndAppType = dataSourceService.getDataSourceByIdAndAppType(dataSource.getId(), AppType.DATAASSETS.getType());
        Assert.assertNotNull(dataSourceByIdAndAppType);

    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testUpdateDataSourceBySourceIdAndAppType(){

        LineageDataSource dataSource = DataCollection.getData().getDefaultLineageDataSource();
        DataSourceDTO dataSourceDTO = new DataSourceDTO();
        BeanUtils.copyProperties(dataSource,dataSourceDTO);
        dataSourceDTO.setDataJson("{\"maxJobPoolSize\":\"\",\"password\":\"123\",\"minJobPoolSize\":\"\"," +
                "\"jdbcUrl\":\"jdbc:hive2://172.16.8.107:10000/default\"," +
                "\"username\":\"admin\",\"typeName\":\"hive2.1.1-cdh6.1.1\"}");
        dataSourceService.updateDataSourceBySourceIdAndAppType(dataSourceDTO);

    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testDeleteDataSource(){

        Tenant tenant = DataCollection.getData().getTenant();
        DataSourceDTO dataSourceDTO = getDataSourceDTO(tenant.getDtUicTenantId());
        Long sourceId = dataSourceService.addOrUpdateDataSource(dataSourceDTO);
        dataSourceService.deleteDataSource(sourceId,dataSourceDTO.getAppType());
    }


    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testPageQueryDataSourceByAppType(){

        Tenant tenant = DataCollection.getData().getTenant();
        DataSourceDTO dataSourceDTO = getDataSourceDTO(tenant.getDtUicTenantId());
        Long sourceId = dataSourceService.addOrUpdateDataSource(dataSourceDTO);
        PageResult<List<LineageDataSource>> listPageResult = dataSourceService.pageQueryDataSourceByAppType(dataSourceDTO.getAppType(), 1, 10);
        Assert.assertNotNull(listPageResult.getData());
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetDataSourcesByIdList(){

        Tenant tenant = DataCollection.getData().getTenant();
        DataSourceDTO dataSourceDTO = getDataSourceDTO(tenant.getDtUicTenantId());
        Long sourceId = dataSourceService.addOrUpdateDataSource(dataSourceDTO);
        List<Long> idList = Arrays.asList(sourceId);
        List<LineageDataSource> sourcesByIdList = dataSourceService.getDataSourcesByIdList(idList);
        Assert.assertNotNull(sourcesByIdList);
    }



    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetDataSourceByParams(){

        Tenant tenant = DataCollection.getData().getTenant();
        List<LineageDataSource> dataSourceByParams = dataSourceService.getDataSourceByParams(DataSourceType.UNKNOWN.getVal()
                , "手动数据源", tenant.getDtUicTenantId(), AppType.DATAASSETS.getType());
        Assert.assertNotNull(dataSourceByParams);
    }



    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testAcquireOldDataSourceList(){

        Tenant tenant = DataCollection.getData().getTenant();
        DataSourceDTO dataSourceDTO = getDataSourceDTO(tenant.getDtUicTenantId());

    }

    private DataSourceDTO getDataSourceDTO(Long tenantId) {

        String dataJson = "{\"maxJobPoolSize\":\"\",\"password\":\"123\",\"minJobPoolSize\":\"\"," +
                "\"jdbcUrl\":\"jdbc:hive2://172.16.8.107:10000/default\"," +
                "\"username\":\"admin\",\"typeName\":\"hive2.1.1-cdh6.1.1\"}";
//        String kerberosConf = "{\n" +
//                "\"principalFile\":\"hive_pure.keytab\",\n" +
//                "\"remoteDir\":\"/data/sftp_dev/CONSOLE_kerberos/SPARK_THRIFT/kerberos\",\n" +
//                "\"krbName\":\"krb5.conf\",\n" +
//                "\"openKerberos\":true\n" +
//                "}";
        String kerberosConf = "";
        DataSourceDTO dataSourceDTO = new DataSourceDTO();
        dataSourceDTO.setAppType(1);
        dataSourceDTO.setDataJson(dataJson);
        dataSourceDTO.setSourceName("测试逻辑数据源1");
        dataSourceDTO.setDtUicTenantId(tenantId);
        dataSourceDTO.setKerberosConf(kerberosConf);
        dataSourceDTO.setSourceType(27);
        dataSourceDTO.setSourceId(1121L);
        dataSourceDTO.setProjectId(1L);
        dataSourceDTO.setSchemaName("default");
        return dataSourceDTO;
    }

}
