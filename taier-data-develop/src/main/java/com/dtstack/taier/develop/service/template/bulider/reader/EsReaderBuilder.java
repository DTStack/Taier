package com.dtstack.taier.develop.service.template.bulider.reader;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.taier.dao.domain.DsInfo;
import com.dtstack.taier.develop.common.template.Reader;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import com.dtstack.taier.develop.service.datasource.impl.DatasourceService;
import com.dtstack.taier.develop.service.datasource.impl.DsInfoService;
import com.dtstack.taier.develop.service.template.PluginName;
import com.dtstack.taier.develop.service.template.es.Es7Reader;
import com.dtstack.taier.develop.service.template.es.EsReaderParam;
import com.dtstack.taier.develop.service.template.es.SslConfig;
import com.dtstack.taier.develop.utils.JsonUtils;
import com.dtstack.taier.develop.utils.develop.sync.util.ColumnUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author daojin
 */
@Component
public class EsReaderBuilder implements DaReaderBuilder {

    @Autowired
    DsInfoService dsInfoService;

    @Autowired
    DatasourceService datasourceService;

    @Override
    public void setReaderJson(TaskResourceParam param) {
        Long sourceId = Long.parseLong(param.getSourceMap().get("sourceId").toString());
        DsInfo data = dsInfoService.getOneById(sourceId);
        param.getSourceMap().put("source", data);
        param.getSourceMap().put("dataSourceType", DataSourceType.ES7.getVal());
        param.getSourceMap().putAll(JSON.parseObject(data.getDataJson()));
        List<Long> sourceIds = new ArrayList<>();
        sourceIds.add(sourceId);
        param.getSourceMap().put("sourceIds", sourceIds);
        // ssl配置
        if (StringUtils.isNotEmpty((String) param.getSourceMap().get("keyPath"))) {
            Map<String, String> sftpMap = datasourceService.getSftpMap(param.getTenantId());
            SslConfig sslConfig = SslConfig.setSslConfig(param.getSourceMap(),sftpMap);
            param.getSourceMap().put("sslConfig", sslConfig);
        }
    }

    @Override
    public Reader daReaderBuild(TaskResourceParam TaskResourceParam) throws Exception {
        setReaderJson(TaskResourceParam);
        EsReaderParam param = JSONObject.parseObject(JSONObject.toJSONString(TaskResourceParam.getSourceMap()), EsReaderParam.class);
        Es7Reader esReader = new Es7Reader();
        esReader.setColumn(ColumnUtil.getColumns(param.getColumn(), PluginName.ES7_R));
        esReader.setIndex(param.getIndex());
        esReader.setHosts(Arrays.asList(TaskResourceParam.getSourceMap().get("address").toString()));
        esReader.setUsername(param.getUsername());
        esReader.setPassword(param.getPassword());
        esReader.setQuery(param.getQuery());
        esReader.setSourceIds(param.getSourceIds());
        esReader.setExtralConfig(param.getExtralConfig());
        esReader.setSslConfig(param.getSslConfig());
        return esReader;
    }

    @Override
    public Map<String, Object> getParserSourceMap(Map<String, Object> sourceMap) {
        String query = (String) sourceMap.get("query");
        if (StringUtils.isNotEmpty(query)) {
            sourceMap.put("query", JSONObject.parseObject(String.format("{%s}", query)));
        }
        EsReaderParam param = JsonUtils.objectToObject(sourceMap, EsReaderParam.class);
        return JsonUtils.objectToMap(param);
    }

    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.ES7;
    }

}
