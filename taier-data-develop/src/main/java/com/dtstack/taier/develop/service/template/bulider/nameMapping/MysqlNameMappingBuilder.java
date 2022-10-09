package com.dtstack.taier.develop.service.template.bulider.nameMapping;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import org.springframework.stereotype.Component;


/**
 * @author zhiChen
 * @date 2022/1/12 11:46
 */
@Component
public class MysqlNameMappingBuilder implements NameMappingBuilder {
//    @Autowired
//    private DataSourceCenterService dataSourceCenterService;

    @Override
    public JSONObject daReaderBuild(TaskResourceParam param) throws Exception {
//        if (Objects.equals(param.getTaskType(), EScheduleJobType.DATA_ACQUISITION.getVal())
//                && Objects.equals(SyncContentEnum.DATA_STRUCTURE_SYNC.getType(), param.getSourceMap().get("syncContent"))) {
//            JSONObject nameMappingJson = new JSONObject();
//            Map<String, Object> sourceMap = param.getSourceMap();
//            String mapperStr = sourceMap.getOrDefault("mapper", "").toString();
//            if (StringUtils.isNotBlank(mapperStr)) {
//                Long sourceId = Long.parseLong(sourceMap.get("sourceId").toString());
//                List<String> table = JSONObject.parseArray(JSONObject.toJSONString(sourceMap.get("table")), String.class);
//                List<SchemaInfoList> schemaInfoLists = dataSourceCenterService.batchTableColumn(sourceId, table, false);
//                if (CollectionUtils.isEmpty(schemaInfoLists)) {
//                    throw new RdosDefineException("表不能为空");
//                }
//                JSONObject mapper = JSONObject.parseObject(mapperStr, Feature.OrderedField);
//                JSONObject schemaMappings = new JSONObject();
//                JSONObject databaseMapper = mapper.getJSONObject("databaseMapper");
//                DsServiceInfoDTO dataSource = (DsServiceInfoDTO) sourceMap.get("source");
//                JSONObject dataJson = JSONObject.parseObject(dataSource.getDataJson());
//                String jdbc = JsonUtil.getStringDefaultEmpty(dataJson, JDBC_URL);
//                String db = MysqlUtil.getDB(jdbc);
//                if (databaseMapper != null) {
//                    JSONObject customize = databaseMapper.getJSONObject("customize");
//                    String targetDB = db;
//                    if (customize != null) {
//                        for (String key : customize.keySet()) {
//                            targetDB = targetDB.replace(key, customize.getString(key));
//                        }
//                    }
//                    if (databaseMapper.getString("prefix") != null) {
//                        targetDB = databaseMapper.getString("prefix") + targetDB;
//                    }
//                    if (databaseMapper.getString("suffix") != null) {
//                        targetDB = targetDB + databaseMapper.getString("suffix");
//                    }
//                    schemaMappings.put(db, targetDB);
//                } else {
//                    schemaMappings.put(db, db);
//                }
//                JSONObject tableMappings = new JSONObject();
//                JSONObject tableAndDBAndFieldMappings = new JSONObject();
//                JSONObject tableAndDBMappings = new JSONObject();
//
//                JSONObject fieldMappings = new JSONObject();
//                for (int i = 0; i < schemaInfoLists.size(); i++) {
//                    SchemaInfoList schemaInfoList1 = schemaInfoLists.get(i);
//                    JSONObject tableMapper = mapper.getJSONObject("tableMapper");
//                    JSONObject fieldMapper = mapper.getJSONObject("fieldMapper");
//                    String originalTable = schemaInfoList1.getTableName();
//                    JSONObject tableAndFieldMappings = new JSONObject();
//
//                    if (tableMapper != null) {
//                        JSONObject customize = tableMapper.getJSONObject("customize");
//                        String targetTable = schemaInfoList1.getTableName();
//                        if (customize != null) {
//                            for (String key : customize.keySet()) {
//                                targetTable = targetTable.replace(key, customize.getString(key));
//                            }
//                        }
//                        if (tableMapper.getString("prefix") != null) {
//                            targetTable = tableMapper.getString("prefix") + targetTable;
//                        }
//                        if (tableMapper.getString("suffix") != null) {
//                            targetTable = targetTable + tableMapper.getString("suffix");
//                        }
//                        tableAndDBMappings.put(originalTable, targetTable);
//                    } else {
//                        tableAndDBMappings.put(originalTable, originalTable);
//                    }
//                    if (CollectionUtils.isEmpty(schemaInfoList1.getColumnList())) {
//                    } else {
//                        for (ColumnMetaDTO columnMetaDTO : schemaInfoList1.getColumnList()) {
//                            String targetKey = columnMetaDTO.getKey();
//                            String originalKey = columnMetaDTO.getKey();
//                            if (fieldMapper != null) {
//                                JSONObject customize = fieldMapper.getJSONObject("customize");
//                                if (customize != null) {
//                                    for (String key : customize.keySet()) {
//                                        targetKey = targetKey.replace(key, customize.getString(key));
//                                    }
//                                }
//                                if (fieldMapper.getString("prefix") != null) {
//                                    targetKey = fieldMapper.getString("prefix") + targetKey;
//                                }
//                                if (fieldMapper.getString("suffix") != null) {
//                                    targetKey = targetKey + fieldMapper.getString("suffix");
//                                }
//                                tableAndFieldMappings.put(originalKey, targetKey);
//                            } else {
//                                tableAndFieldMappings.put(originalKey, originalKey);
//                            }
//                        }
//                    }
//                    tableAndDBAndFieldMappings.put(originalTable, tableAndFieldMappings);
//                }
//                tableMappings.put(db, tableAndDBMappings);
//                fieldMappings.put(db, tableAndDBAndFieldMappings);
//
//                nameMappingJson.put("schemaMappings", schemaMappings);
//                nameMappingJson.put("tableMappings", tableMappings);
//                nameMappingJson.put("fieldMappings", fieldMappings);
//            }
//            return nameMappingJson;
//        }
        return null;
    }

    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.MySQL;
    }
}
