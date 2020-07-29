package com.dtstack.engine.master.client;

import com.alibaba.fastjson.JSON;
import com.dtstack.engine.api.dto.ScheduleTaskShadeDTO;
import com.dtstack.engine.api.pojo.ParamActionExt;
import com.dtstack.engine.api.service.*;
import com.dtstack.engine.api.vo.ClusterVO;
import com.dtstack.engine.api.vo.action.ActionJobEntityVO;
import com.dtstack.engine.api.vo.action.ActionJobStatusVO;
import com.dtstack.engine.api.vo.action.ActionLogVO;
import com.dtstack.engine.api.vo.action.ActionRetryLogVO;
import com.dtstack.engine.api.vo.components.ComponentsConfigOfComponentsVO;
import com.dtstack.engine.api.vo.components.ComponentsResultVO;
import com.dtstack.engine.api.vo.engine.EngineSupportVO;
import com.dtstack.engine.api.vo.tenant.UserTenantVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightApi;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.fail;

/**
 * @Auther: dazhi
 * @Date: 2020/7/29 7:13 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class DAGScheduleXTestController {

    private DtInsightApi.ApiBuilder builder = new DtInsightApi.ApiBuilder()
            .setEndpoint("http://127.0.0.1:8099")
            .setToken("eyJzdWNjZXNzIjp0cnVlLCJtZXNzYWdlIjoi5omn6KGM5oiQ5YqfIiwiZGF0YSI6eyJ1c2VySWQiOjEsInVzZXJOYW1lIjoiYWRtaW5AZHRzdGFjay5jb20iLCJlbWFpbCI6ImFkbWluQGR0c3RhY2suY29tIiwicGhvbmUiOiIxMzUyNjkyNTI4NiIsInRlbmFudElkIjoxLCJ0ZW5hbnROYW1lIjoiRFRTdGFja+enn+aItyIsInRlbmFudE93bmVyIjpmYWxzZSwidGVuYW50T3duZXJJZCI6OH19");


    @Test
    public void testStart() {
        try {
            DtInsightApi api = builder.buildApi();
            ActionService apiClient = api.getApiClient(ActionService.class);
            List<String> jobIds = new ArrayList<>();
            jobIds.add("51f08832");
            jobIds.add("289154ff");
            jobIds.add("5de68c11");
            ApiResponse<List<ActionJobStatusVO>> listApiResponse = apiClient.listJobStatusByJobIds(jobIds);
            System.out.println(JSON.toJSONString(listApiResponse));
        } catch (Exception e) {
            fail("Have exception, message: " + e.getMessage());
        }
    }

    @Test
    public void testStartEntitys() {
        try {
            DtInsightApi api = builder.buildApi();
            ActionService apiClient = api.getApiClient(ActionService.class);
            List<String> jobIds = new ArrayList<>();
            jobIds.add("51f08832");
            jobIds.add("289154ff");
            jobIds.add("5de68c11");
            ApiResponse<List<ActionJobEntityVO>> entitys = apiClient.entitys(jobIds, 0);
            System.out.println(JSON.toJSONString(entitys));
        } catch (Exception e) {
            fail("Have exception, message: " + e.getMessage());
        }
    }

    @Test
    public void testStartog() {
        try {
            DtInsightApi api = builder.buildApi();
            ActionService apiClient = api.getApiClient(ActionService.class);

            ApiResponse<ActionLogVO> log = apiClient.log("5de68c11", 0);
            System.out.println(JSON.toJSONString(log));
        } catch (Exception e) {
            fail("Have exception, message: " + e.getMessage());
        }
    }

    @Test
    public void testStartRetryLog() {
        try {
            DtInsightApi api = builder.buildApi();
            ActionService apiClient = api.getApiClient(ActionService.class);

            ApiResponse<List<ActionRetryLogVO>> listApiResponse = apiClient.retryLog("5de68c11", 0);
            System.out.println(JSON.toJSONString(listApiResponse));
        } catch (Exception e) {
            fail("Have exception, message: " + e.getMessage());
        }
    }

    @Test
    public void testStart1() {
        try {
            DtInsightApi api = builder.buildApi();
            ScheduleTaskShadeService apiClient = api.getApiClient(ScheduleTaskShadeService.class);
            ApiResponse apiResponse = apiClient.addOrUpdate(new ScheduleTaskShadeDTO());

            System.out.println(apiResponse);
        } catch (Exception e) {
            fail("Have exception, message: " + e.getMessage());
        }
    }


    @Test
    public void testStartClusterInfo() {
        try {
            DtInsightApi api = builder.buildApi();
            ClusterService apiClient = api.getApiClient(ClusterService.class);
            ApiResponse<ClusterVO> clusterVOApiResponse = apiClient.clusterInfo(1L);
            System.out.println(JSON.toJSONString(clusterVOApiResponse));
        } catch (Exception e) {
            fail("Have exception, message: " + e.getMessage());
        }
    }

    @Test
    public void testStartClusterExtInfo() {
        try {
            DtInsightApi api = builder.buildApi();
            ClusterService apiClient = api.getApiClient(ClusterService.class);
            ApiResponse<ClusterVO> clusterVOApiResponse = apiClient.clusterExtInfo(1L);
            System.out.println(JSON.toJSONString(clusterVOApiResponse));
        } catch (Exception e) {
            fail("Have exception, message: " + e.getMessage());
        }
    }
    @Test
    public void testStartPluginInfoForType() {
        try {
            DtInsightApi api = builder.buildApi();
            ClusterService apiClient = api.getApiClient(ClusterService.class);
            ApiResponse<String> stringApiResponse = apiClient.pluginInfoForType(1L, Boolean.TRUE, 7);
            System.out.println(JSON.toJSONString(stringApiResponse));
        } catch (Exception e) {
            fail("Have exception, message: " + e.getMessage());
        }
    }

    @Test
    public void testStartdbInfo() {
        try {
            DtInsightApi api = builder.buildApi();
            ClusterService apiClient = api.getApiClient(ClusterService.class);
            ApiResponse<String> stringApiResponse = apiClient.dbInfo(1L, 1L, 2);
            System.out.println(JSON.toJSONString(stringApiResponse));
        } catch (Exception e) {
            fail("Have exception, message: " + e.getMessage());
        }
    }


    @Test
    public void testStartEngineSupportVOS() {
        try {
            DtInsightApi api = builder.buildApi();
            EngineService apiClient = api.getApiClient(EngineService.class);
            ApiResponse<List<EngineSupportVO>> listApiResponse = apiClient.listSupportEngine(1L);
            System.out.println(JSON.toJSONString(listApiResponse));
        } catch (Exception e) {
            fail("Have exception, message: " + e.getMessage());
        }
    }

    @Test
    public void testStartListConfigOfComponents() {
        try {
            DtInsightApi api = builder.buildApi();
            ComponentService apiClient = api.getApiClient(ComponentService.class);
            ApiResponse<List<ComponentsConfigOfComponentsVO>> listApiResponse = apiClient.listConfigOfComponents(1L, 4);
            System.out.println(JSON.toJSONString(listApiResponse));
        } catch (Exception e) {
            fail("Have exception, message: " + e.getMessage());
        }
    }

    @Test
    public void testStartAddOrCheckClusterWithName() {
        try {
            DtInsightApi api = builder.buildApi();
            ComponentService apiClient = api.getApiClient(ComponentService.class);
            ApiResponse<ComponentsResultVO> componentsResultVOApiResponse = apiClient.addOrCheckClusterWithName("123123");
            System.out.println(JSON.toJSONString(componentsResultVOApiResponse));
        } catch (Exception e) {
            fail("Have exception, message: " + e.getMessage());
        }
    }

    @Test
    public void testStartListTenant() {
        try {
            DtInsightApi api = builder.buildApi();
            TenantService apiClient = api.getApiClient(TenantService.class);
            ApiResponse<List<UserTenantVO>> sdaisdjasdhiuahsidua = apiClient.listTenant("sdaisdjasdhiuahsidua");
            System.out.println(sdaisdjasdhiuahsidua);
        } catch (Exception e) {
            fail("Have exception, message: " + e.getMessage());
        }
    }


}
