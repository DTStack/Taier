package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.LineageDataSetInfo;
import com.dtstack.engine.api.domain.LineageDataSource;
import com.dtstack.engine.api.dto.DataSourceDTO;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.pojo.lineage.Column;
import com.dtstack.engine.api.vo.ClusterVO;
import com.dtstack.engine.common.akka.config.AkkaConfig;
import com.dtstack.engine.common.client.ClientOperator;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.lineage.impl.LineageDataSetInfoService;
import com.dtstack.lineage.impl.LineageDataSourceService;
import com.dtstack.schedule.common.enums.Sort;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;


/**
 * @Author tengzhen
 * @Description:
 * @Date: Created in 9:41 上午 2020/11/9
 */
@PrepareForTest({AkkaConfig.class, ClientOperator.class})
public class LineageDataSourceServiceTest extends AbstractTest {

    private static  String testClusterName = "testcase";

    @Autowired
    private ComponentService componentService;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private LineageDataSetInfoService dataSetInfoService;

    @Autowired
    private LineageDataSourceService dataSourceService;




    private final static List<String> mockInfos = Lists.newArrayList("Mock Info");













    /**
     * @author zyd
     * @Description 根据appType分页查询逻辑数据源列表
     * @Date 2020/10/30 11:55 上午
     * @return: java.util.List<com.dtstack.engine.api.domain.LineageDataSource>
     **/
    @Test
    public void pageQueryDataSourceByAppType(){

        PageResult<List<LineageDataSource>> listPageResult = dataSourceService.pageQueryDataSourceByAppType(1, 1, 10);
        System.out.println(listPageResult);
    }







    @Test
    public void getTableColumns(){

        //创建集群
        componentService.addOrCheckClusterWithName(testClusterName);
        //创建

        LineageDataSetInfo dataSetInfo = new LineageDataSetInfo();
        dataSetInfo.setSourceId(1L);
        dataSetInfo.setTableName("test_01");
        dataSetInfo.setDbName("db_01");
        dataSetInfoService.getTableColumns(dataSetInfo, "");
    }


}
