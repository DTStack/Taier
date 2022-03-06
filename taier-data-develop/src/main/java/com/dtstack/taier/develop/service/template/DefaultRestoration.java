//package com.dtstack.taier.develop.service.template;
//
//import com.alibaba.fastjson.JSONObject;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// *   "restoration":{
// *                     "skipDDL":false,
// *                     "monitor":{
// *                         "type":"mysql",
// *                         "properties":{
// *                             "jdbcUrl":"jdbc:mysql://k3:3306/test?useSSL=false",
// *                             "username":"root",
// *                             "password":"admin123",
// *                             "database":"test",
// *                             "table":"ddl_change_1"
// *                         }
// *                     }
// *                 }
// * @company: www.dtstack.com
// * @Author ：zhiChen
// * @Date ：Created in 17:49 2019-08-21
// */
//@Component
//public class DefaultRestoration implements Restoration {
//
//    @Autowired
//    private DataSourceCenterService dataSourceCenterService;
//
//    /**
//     * 跳过ddl，如果设置为true，就不处理ddl
//     */
//    private Boolean skipDDL = false;
//
//
//    @Override
//    public JSONObject toRestorationJson() {
//        JSONObject restoration = new JSONObject(true);
//        restoration.put("skipDDL",skipDDL);
//        JSONObject monitor = new JSONObject(true);
//        Map<String, Object> properties = new HashMap<>();
//        //得到project绑定的ddl
//        Project project = projectService.getOne(MetaObjectHolder.projectId());
//        dataSourceCenterService.checkAndCreateDDLTable(MetaObjectHolder.projectId());
//        DsServiceInfoDTO ddlDataSource = dataSourceCenterService.getById(project.getDdlDataSourceId());
//        JSONObject ddlDataSourceJson = DataSyncDataSourceUtils.getDataSourceJson(ddlDataSource.getDataJson());
//        String jdbcUrl = DataSyncDataSourceUtils.getJdbcUrl(ddlDataSourceJson);
//        properties.put("jdbcUrl",jdbcUrl);
//        properties.put("database", MysqlUtil.getDB(jdbcUrl));
//        properties.put("table", DDL_CHANGE);
//        properties.put("username", DataSyncDataSourceUtils.getJdbcUsername(ddlDataSourceJson));
//        properties.put("password", DataSyncDataSourceUtils.getJdbcPassword(ddlDataSourceJson) == null ? "" : DataSyncDataSourceUtils.getJdbcPassword(ddlDataSourceJson));
//        monitor.put("type", "mysql");  //目前只支持mysql
//        monitor.put("properties",properties);
//        restoration.put("monitor", monitor);
//        return restoration;
//    }
//
//    @Override
//    public String toRestorationJsonString() {
//        return toRestorationJson().toJSONString();
//    }
//
//
//
//}
