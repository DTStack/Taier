//package com.dtstack.engine.master.client;
//
//import com.alibaba.fastjson.JSON;
//import com.dtstack.engine.api.dto.ScheduleTaskShadeDTO;
//import com.dtstack.engine.api.enums.DbType;
//import com.dtstack.engine.api.enums.EComponentApiType;
//import com.dtstack.engine.api.pager.PageResult;
//import com.dtstack.engine.api.param.SecurityLogParam;
//import com.dtstack.engine.api.pojo.ClusterResource;
//import com.dtstack.engine.api.service.*;
//import com.dtstack.engine.api.vo.ClusterVO;
//import com.dtstack.engine.api.vo.ScheduleFillDataJobPreViewVO;
//import com.dtstack.engine.api.vo.action.ActionJobStatusVO;
//import com.dtstack.engine.api.vo.action.ActionLogVO;
//import com.dtstack.engine.api.vo.action.ActionRetryLogVO;
//import com.dtstack.engine.api.vo.components.ComponentsConfigOfComponentsVO;
//import com.dtstack.engine.api.vo.components.ComponentsResultVO;
//import com.dtstack.engine.api.vo.console.ConsoleJobVO;
//import com.dtstack.engine.api.vo.engine.EngineSupportVO;
//import com.dtstack.engine.api.vo.schedule.job.ScheduleJobScienceJobStatusVO;
//import com.dtstack.engine.api.vo.schedule.task.shade.ScheduleTaskShadeCountTaskVO;
//import com.dtstack.engine.api.vo.schedule.task.shade.ScheduleTaskShadePageVO;
//import com.dtstack.engine.api.vo.template.TaskTemplateResultVO;
//import com.dtstack.engine.api.vo.template.TaskTemplateVO;
//import com.dtstack.engine.api.vo.tenant.UserTenantVO;
//import com.dtstack.sdk.core.common.ApiResponse;
//import com.dtstack.sdk.core.common.DtInsightApi;
//import org.assertj.core.util.Lists;
//import org.junit.Test;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static junit.framework.TestCase.fail;
//
///**
// * @Auther: dazhi
// * @Date: 2020/7/29 7:13 下午
// * @Email:dazhi@dtstack.com
// * @Description:
// */
//public class DAGScheduleXTest {
//
//    private String REMOTE_URL="http://172.16.100.251:8090";
//    private String LOCALHOST_URL="http://127.0.0.1:8099";
//
//    private DtInsightApi.ApiBuilder builder = new DtInsightApi.ApiBuilder()
//            .setEndpoint(LOCALHOST_URL)
//            .setToken("eyJzdWNjZXNzIjp0cnVlLCJtZXNzYWdlIjoi5omn6KGM5oiQ5YqfIiwiZGF0YSI6eyJ1c2VySWQiOjEsInVzZXJOYW1lIjoiYWRtaW5AZHRzdGFjay5jb20iLCJlbWFpbCI6ImFkbWluQGR0c3RhY2suY29tIiwicGhvbmUiOiIxMzUyNjkyNTI4NiIsInRlbmFudElkIjoxLCJ0ZW5hbnROYW1lIjoiRFRTdGFja+enn+aItyIsInRlbmFudE93bmVyIjpmYWxzZSwidGVuYW50T3duZXJJZCI6OH19");
//
//
//    @Test
//    public void testStart0() {
//        try {
//            DtInsightApi api = builder.buildApi();
//            ScheduleTaskShadeService apiClient = api.getApiClient(ScheduleTaskShadeService.class);
//            ArrayList<Long> taskIdList = Lists.newArrayList(1L, 2L);
//            ApiResponse<Void> response = apiClient.frozenTask(taskIdList,1,1L,1L,1);
//            System.out.println(JSON.toJSONString(response));
//        } catch (Exception e) {
//            fail("Have exception, message: " + e.getMessage());
//        }
//    }
//
//    @Test
//    public void testStart19() {
//        try {
//            DtInsightApi api = builder.buildApi();
//            TaskParamApiClient apiClient = api.getApiClient(TaskParamApiClient.class);
//            TaskTemplateVO taskTemplateVO = new TaskTemplateVO();
//            taskTemplateVO.setComputeType(1);
//            taskTemplateVO.setEngineType(5);
//            taskTemplateVO.setTaskType(0);
//            ApiResponse<TaskTemplateResultVO> computeType = apiClient.getEngineParamTmplByComputeType(taskTemplateVO);
//            System.out.println(JSON.toJSONString(computeType));
//        } catch (Exception e) {
//            fail("Have exception, message: " + e.getMessage());
//        }
//    }
//
//    @Test
//    public void testStart20() {
//        try {
//            DtInsightApi api = builder.buildApi();
//            SecurityLogApiClient apiClient = api.getApiClient(SecurityLogApiClient.class);
//            SecurityLogParam securityLogParam = new SecurityLogParam();
//            securityLogParam.setId(0L);
//            securityLogParam.setSign("B985723079A86B2DAB6DFA1A9F0CB92C455C0A40E4A7BC5AE4E7494C917979DBBF7BA51CAA379EDC51A4EDC94897D369CA440CA55ACB08164AD01AAEC3C70044BA090D1165E378776F2781392C3A588E944E53EF0165FE25DBFFFC7758E3E7C8B3E682302086CD28E8F32CBDEF58CD5F1198963CF9EA4D56570C114D829AA8EFD48448A09D94C2F6C77D1FD9A47158B9F12E1B275EF06B2DB78AC4AA8ECAA038");
//            securityLogParam.setIsDeleted(0);
//            ApiResponse<Void> voidApiResponse = apiClient.addSecurityLog(securityLogParam);
//
//        } catch (Exception e) {
//            fail("Have exception, message: " + e.getMessage());
//        }
//    }
//
//    @Test
//    public void testStart() {
//        try {
//            DtInsightApi api = builder.buildApi();
//            ActionService apiClient = api.getApiClient(ActionService.class);
//            List<String> jobIds = new ArrayList<>();
//            jobIds.add("80246d68");
//            ApiResponse<List<ActionJobStatusVO>> listApiResponse = apiClient.listJobStatusByJobIds(jobIds);
//            System.out.println(JSON.toJSONString(listApiResponse));
//        } catch (Exception e) {
//            fail("Have exception, message: " + e.getMessage());
//        }
//    }
//
//    @Test
//    public void testStartEntitys() {
//        try {
//            DtInsightApi api = builder.buildApi();
//            ActionService apiClient = api.getApiClient(ActionService.class);
//            List<String> jobIds = new ArrayList<>();
//            jobIds.add("51f08832");
//            jobIds.add("289154ff");
//            jobIds.add("5de68c11");
//            ApiResponse<Boolean> stop = apiClient.stop(jobIds);
//            System.out.println(JSON.toJSONString(stop));
//        } catch (Exception e) {
//            //fail("Have exception, message: " + e.getMessage());
//        }
//    }
//
//    @Test
//    public void testStartog() {
//        try {
//            DtInsightApi api = builder.buildApi();
//            ActionService apiClient = api.getApiClient(ActionService.class);
//
//            ApiResponse<ActionLogVO> log = apiClient.log("5de68c11", 0);
//            System.out.println(JSON.toJSONString(log));
//        } catch (Exception e) {
//            fail("Have exception, message: " + e.getMessage());
//        }
//    }
//
//    @Test
//    public void testStartRetryLog() {
//        try {
//            DtInsightApi api = builder.buildApi();
//            ActionService apiClient = api.getApiClient(ActionService.class);
//
//            ApiResponse<List<ActionRetryLogVO>> listApiResponse = apiClient.retryLog("5de68c11", 0);
//            System.out.println(JSON.toJSONString(listApiResponse));
//        } catch (Exception e) {
//            fail("Have exception, message: " + e.getMessage());
//        }
//    }
//
//    @Test
//    public void testStart1() {
//        try {
//            DtInsightApi api = builder.buildApi();
//            ScheduleTaskShadeService apiClient = api.getApiClient(ScheduleTaskShadeService.class);
//            ScheduleTaskShadeDTO batchTaskShadeDTO = new ScheduleTaskShadeDTO();
//
//            batchTaskShadeDTO.setSort("dece");
//            ApiResponse apiResponse = apiClient.addOrUpdate(batchTaskShadeDTO);
//            System.out.println(JSON.toJSONString(apiResponse));
//        } catch (Exception e) {
//            fail("Have exception, message: " + e.getMessage());
//        }
//    }
//
//
//    @Test
//    public void testStartClusterInfo() {
//        try {
//            DtInsightApi api = builder.buildApi();
//            ClusterService apiClient = api.getApiClient(ClusterService.class);
//            ApiResponse<String> stringApiResponse = apiClient.clusterInfo(1L);
//            System.out.println(JSON.toJSONString(stringApiResponse));
//        } catch (Exception e) {
//            fail("Have exception, message: " + e.getMessage());
//        }
//    }
//
//    @Test
//    public void testStartClusterExtInfo() {
//        try {
//            DtInsightApi api = builder.buildApi();
//            ClusterService apiClient = api.getApiClient(ClusterService.class);
//            ApiResponse<ClusterVO> clusterVOApiResponse = apiClient.clusterExtInfo(1L);
//            System.out.println(JSON.toJSONString(clusterVOApiResponse));
//        } catch (Exception e) {
//            fail("Have exception, message: " + e.getMessage());
//        }
//    }
//    @Test
//    public void testStartPluginInfoForType() {
//        try {
//            DtInsightApi api = builder.buildApi();
//            ClusterService apiClient = api.getApiClient(ClusterService.class);
//            ApiResponse<String> stringApiResponse = apiClient.pluginInfoForType(1L, Boolean.TRUE, EComponentApiType.CARBON_DATA);
//            System.out.println(JSON.toJSONString(stringApiResponse));
//        } catch (Exception e) {
//            fail("Have exception, message: " + e.getMessage());
//        }
//    }
//
//    @Test
//    public void testStartdbInfo() {
//        try {
//            DtInsightApi api = builder.buildApi();
//            ClusterService apiClient = api.getApiClient(ClusterService.class);
//            ApiResponse<String> stringApiResponse = apiClient.dbInfo(1L, 1L, DbType.Oracle);
//            System.out.println(JSON.toJSONString(stringApiResponse));
//        } catch (Exception e) {
//            fail("Have exception, message: " + e.getMessage());
//        }
//    }
//
//
//    @Test
//    public void testStartEngineSupportVOS() {
//        try {
//            DtInsightApi api = builder.buildApi();
//            EngineService apiClient = api.getApiClient(EngineService.class);
//            ApiResponse<List<EngineSupportVO>> listApiResponse = apiClient.listSupportEngine(1L);
//            System.out.println(JSON.toJSONString(listApiResponse));
//        } catch (Exception e) {
//            fail("Have exception, message: " + e.getMessage());
//        }
//    }
//
//    @Test
//    public void testStartListConfigOfComponents() {
//        try {
//            DtInsightApi api = builder.buildApi();
//            ComponentService apiClient = api.getApiClient(ComponentService.class);
//            ApiResponse<List<ComponentsConfigOfComponentsVO>> listApiResponse = apiClient.listConfigOfComponents(1L, 4);
//            System.out.println(JSON.toJSONString(listApiResponse));
//        } catch (Exception e) {
//            fail("Have exception, message: " + e.getMessage());
//        }
//    }
//
//    @Test
//    public void testStartAddOrCheckClusterWithName() {
//        try {
//            DtInsightApi api = builder.buildApi();
//            ComponentService apiClient = api.getApiClient(ComponentService.class);
//            ApiResponse<ComponentsResultVO> componentsResultVOApiResponse = apiClient.addOrCheckClusterWithName("123123");
//            System.out.println(JSON.toJSONString(componentsResultVOApiResponse));
//        } catch (Exception e) {
//            fail("Have exception, message: " + e.getMessage());
//        }
//    }
//
//    @Test
//    public void testStartListTenant() {
//        try {
//            DtInsightApi api = builder.buildApi();
//            TenantService apiClient = api.getApiClient(TenantService.class);
//            ApiResponse<List<UserTenantVO>> sdaisdjasdhiuahsidua = apiClient.listTenant("sdaisdjasdhiuahsidua");
//            System.out.println(sdaisdjasdhiuahsidua);
//        } catch (Exception e) {
//            fail("Have exception, message: " + e.getMessage());
//        }
//    }
//
//    @Test
//    public void testStartSearchJob() {
//        try {
//            DtInsightApi api = builder.buildApi();
//            ConsoleService apiClient = api.getApiClient(ConsoleService.class);
//            ApiResponse<ConsoleJobVO> consoleJobVOApiResponse = apiClient.searchJob("cronJob_mqTest01_20200729000000");
//            System.out.println(JSON.toJSONString(consoleJobVOApiResponse));
//        } catch (Exception e) {
//            fail("Have exception, message: " + e.getMessage());
//        }
//    }
//
//    @Test
//    public void testStartGroupDetail() {
//        try {
//            DtInsightApi api = builder.buildApi();
//            ConsoleService apiClient = api.getApiClient(ConsoleService.class);
//            ApiResponse<ClusterResource> aDefault = apiClient.clusterResources("default");
//            System.out.println(JSON.toJSONString(aDefault));
//        } catch (Exception e) {
//            fail("Have exception, message: " + e.getMessage());
//        }
//    }
//
//    @Test
//    public void testStartScheduleJobService() {
//        try {
//            DtInsightApi api = builder.buildApi();
//            ScheduleJobService apiClient = api.getApiClient(ScheduleJobService.class);
//            ApiResponse<PageResult<List<ScheduleFillDataJobPreViewVO>>> fillDataJobInfoPreview = apiClient.getFillDataJobInfoPreview("", null, null, null, null, 13L, null, 5, 1, 20, 3L);
//            System.out.println(JSON.toJSONString(fillDataJobInfoPreview));
//        } catch (Exception e) {
//            fail("Have exception, message: " + e.getMessage());
//        }
//    }
//
//    @Test
//    public void testStartCountScienceJobStatus() {
//        try {
//            DtInsightApi api = builder.buildApi();
//            ScheduleJobService apiClient = api.getApiClient(ScheduleJobService.class);
//            ApiResponse<ScheduleJobScienceJobStatusVO> scheduleJobScienceJobStatusVOApiResponse = apiClient.countScienceJobStatus(Lists.newArrayList(1L, 2L), 1L, 1, 1, "", "", "");
//            System.out.println(JSON.toJSONString(scheduleJobScienceJobStatusVOApiResponse));
//        } catch (Exception e) {
//            fail("Have exception, message: " + e.getMessage());
//        }
//    }
//
//    @Test
//    public void testStartQueryTasks() {
//        try {
//            DtInsightApi api = builder.buildApi();
//            ScheduleTaskShadeService apiClient = api.getApiClient(ScheduleTaskShadeService.class);
//            ApiResponse<ScheduleTaskShadePageVO> response = apiClient.queryTasks(1L, 1L, "", null, null, null, null, null, null, null, null, null, null);
//            System.out.println(JSON.toJSONString(response));
//        } catch (Exception e) {
//            fail("Have exception, message: " + e.getMessage());
//        }
//    }
//
//    @Test
//    public void testStartCountTaskByType() {
//        try {
//            DtInsightApi api = builder.buildApi();
//            ScheduleTaskShadeService apiClient = api.getApiClient(ScheduleTaskShadeService.class);
//            ApiResponse<ScheduleTaskShadeCountTaskVO> response = apiClient.countTaskByType(7L, 9L, 15L, 1, Lists.list(2,0));
//            System.out.println(JSON.toJSONString(response));
//        } catch (Exception e) {
//            fail("Have exception, message: " + e.getMessage());
//        }
//    }
//
//
//
//}
