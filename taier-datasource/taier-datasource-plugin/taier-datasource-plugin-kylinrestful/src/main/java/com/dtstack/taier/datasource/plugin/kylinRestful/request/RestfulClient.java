package com.dtstack.taier.datasource.plugin.kylinRestful.request;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.datasource.api.dto.ColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.JobParam;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.KylinRestfulSourceDTO;
import com.dtstack.taier.datasource.api.enums.JobStatus;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.plugin.kylinRestful.http.HttpAPI;
import com.dtstack.taier.datasource.plugin.kylinRestful.http.HttpClient;
import com.dtstack.taier.datasource.plugin.kylinRestful.http.HttpClientFactory;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class RestfulClient implements Closeable {

    private static final String KEY_JOB_ID = "uuid";
    private static final String KEY_JOB_STATUS = "job_status";
    private static final String KEY_CMD_OUTPUT = "cmd_output";

    @Override
    public void close() {
    }

    public Boolean auth(KylinRestfulSourceDTO sourceDTO) {
        HttpClient httpClient = HttpClientFactory.createHttpClient(sourceDTO);
        try {
            String result = httpClient.post(HttpAPI.AUTH, null);
            JSONObject.parseObject(result);
            return true;
        } catch (Exception e) {
            log.error("auth error, msg:{}", e.getMessage(), e);
        }
        return false;
    }

    public List<ColumnMetaDTO> getHiveColumnMetaData(KylinRestfulSourceDTO sourceDTO, SqlQueryDTO sqlQueryDTO) {
        String project = sourceDTO.getProject();
        String tableName = sqlQueryDTO.getTableName();
        if (StringUtils.isEmpty(project)) {
            throw new SourceException("get tables exception, project not null");
        }
        if (StringUtils.isEmpty(tableName)) {
            throw new SourceException("get tables exception, tableName not null");
        }
        HttpClient httpClient = HttpClientFactory.createHttpClient(sourceDTO);
        String result = httpClient.get(String.format(HttpAPI.HIVE_A_TABLE, project, tableName), null);
        JSONObject jsonObject = JSONObject.parseObject(result);
        List<ColumnMetaDTO> list = new ArrayList<>();
        if (jsonObject.getString("columns") == null) {
            return list;
        }
        JSONArray jsonArray;
        try {
            jsonArray = JSONArray.parseArray(jsonObject.getString("columns"));
        } catch (Exception e) {
            throw new SourceException("get metadata exception");
        }

        for (Object object : jsonArray) {
            ColumnMetaDTO columnMetaDTO = new ColumnMetaDTO();
            JSONObject obj = (JSONObject) object;
            columnMetaDTO.setKey(MapUtils.getString(obj, "name"));
            columnMetaDTO.setType(MapUtils.getString(obj, "datatype"));
        }
        return list;
    }

    /**
     * 获取 hive schema
     *
     * @param sourceDTO
     * @return
     */
    public List<String> getAllHiveDbList(KylinRestfulSourceDTO sourceDTO) {
        String project = sourceDTO.getProject();
        if (StringUtils.isEmpty(project)) {
            throw new SourceException("get tables exception, project not null");
        }

        HttpClient httpClient = HttpClientFactory.createHttpClient(sourceDTO);
        String result = httpClient.get(String.format(HttpAPI.HIVE_LIST_ALL_DB, project), null);
        return JSONArray.parseArray(result, String.class);

    }


    public List<String> getAllHiveTableListBySchema(KylinRestfulSourceDTO sourceDTO, SqlQueryDTO sqlQueryDTO) {
        String project = sourceDTO.getProject();
        String schema = sqlQueryDTO.getSchema();
        if (StringUtils.isEmpty(project)) {
            throw new SourceException("get tables exception, project not null");
        }
        if (StringUtils.isEmpty(schema)) {
            throw new SourceException("get tables exception, schema not null");
        }

        HttpClient httpClient = HttpClientFactory.createHttpClient(sourceDTO);
        String result = httpClient.get(String.format(HttpAPI.HIVE_LIST_ALL_TABLES, schema, project), null);
        return JSONArray.parseArray(result, String.class);
    }


    /**
     * kylin 表
     *
     * @param sourceDTO
     * @param sqlQueryDTO
     * @return
     */
    public List<String> listAllTables(KylinRestfulSourceDTO sourceDTO, SqlQueryDTO sqlQueryDTO) {
        String project = sourceDTO.getProject();
        if (StringUtils.isEmpty(project)) {
            throw new SourceException("get tables exception, project not null");
        }
        HttpClient httpClient = HttpClientFactory.createHttpClient(sourceDTO);
        String result = httpClient.get(HttpAPI.PROJECT_LIST, null);
        JSONArray jsonArray;
        try {
            jsonArray = JSONArray.parseArray(result);
        } catch (Exception e) {
            throw new SourceException("get project list exception", e);
        }
        List<String> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (project.equals(jsonObject.getString("name"))) {
                return JSONObject.parseArray(jsonObject.getString("tables"), String.class);
            }
        }
        return list;
    }

    public List<JSONObject> getJobList(KylinRestfulSourceDTO source, JobParam jobParam, Integer limit) {
        HttpClient httpClient = HttpClientFactory.createHttpClientWithTimeout(source, jobParam.getRequestConfig());
        String url = String.format(HttpAPI.GET_JOB_LIST, jobParam.getCubeName(), limit, source.getProject());
        String response = httpClient.get(url, null);

        List<JSONObject> jobs;
        if (StringUtils.isBlank(response) ||
                CollectionUtils.isEmpty(jobs = JSONObject.parseArray(response, JSONObject.class))) {
            return null;
        }
        return jobs;
    }

    public void discardJob(KylinRestfulSourceDTO source, JobParam jobParam) {
        HttpClient httpClient = HttpClientFactory.createHttpClientWithTimeout(source, jobParam.getRequestConfig());
        httpClient.put(String.format(HttpAPI.DISCARD_JOB, jobParam.getJobId()));
    }

    public String buildCube(KylinRestfulSourceDTO source, JobParam jobParam) {
        Map<String, Object> bodyMap = Maps.newHashMap();
        bodyMap.put("buildType", jobParam.getBuildType());
        if (jobParam.getStartTime() != null) {
            bodyMap.put("startTime", jobParam.getStartTime());
        }
        if (jobParam.getEndTime() != null) {
            bodyMap.put("endTime", jobParam.getEndTime());
        }
        String bodyStr = JSONObject.toJSONString(bodyMap);

        String url = String.format(HttpAPI.BUILD_CUBE, jobParam.getCubeName());
        HttpClient httpClient = HttpClientFactory.createHttpClientWithTimeout(source, jobParam.getRequestConfig());
        String response = httpClient.put(url, bodyStr);

        return parseRes(response, KEY_JOB_ID);
    }

    public boolean resumeJob(KylinRestfulSourceDTO source, String jobId, JobParam jobParam) {
        HttpClient httpClient = HttpClientFactory.createHttpClientWithTimeout(source, jobParam.getRequestConfig());
        String response = httpClient.put(String.format(HttpAPI.RESUME_JOB, jobId));

        return JobStatus.PENDING.name().equals(parseRes(response, KEY_JOB_STATUS));
    }

    public String getJobStatus(KylinRestfulSourceDTO sourceDTO, String jobId, JobParam jobParam) {
        String response = getJobInfo(sourceDTO, jobId, jobParam);
        //jobStatus统一定义
        return JobStatus.getStatus(parseRes(response, KEY_JOB_STATUS));
    }

    public String getJobInfo(KylinRestfulSourceDTO sourceDTO, String jobId, JobParam jobParam) {
        HttpClient httpClient = HttpClientFactory.createHttpClientWithTimeout(sourceDTO, jobParam.getRequestConfig());
        return httpClient.get(String.format(HttpAPI.GET_STATUS, jobId));
    }

    private String parseRes(String res, String key) {
        return Optional.ofNullable(res)
                .map(re -> {
                    if (StringUtils.isBlank(re)) {
                        throw new SourceException("this job status can't find");
                    }
                    return JSONObject.parseObject(re);
                })
                .map(json -> json.getString(key))
                .orElseThrow(() -> new SourceException("parse response error"));
    }

    public String getErrorLog(KylinRestfulSourceDTO source, String jobId, JobParam jobParam, String stepId) {
        HttpClient httpClient = HttpClientFactory.createHttpClientWithTimeout(source, jobParam.getRequestConfig());
        String res = httpClient.get(String.format(HttpAPI.GET_STEP_OUTPUT, jobId, stepId));
        return parseRes(res, KEY_CMD_OUTPUT);
    }
}
