package com.dtstack.taier.develop.service.template.bulider.reader;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.util.PublicUtil;
import com.dtstack.taier.dao.domain.DsInfo;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.develop.common.template.Reader;
import com.dtstack.taier.develop.dto.devlop.ColumnDTO;
import com.dtstack.taier.develop.dto.devlop.ConnectionDTO;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import com.dtstack.taier.develop.service.datasource.impl.DsInfoService;
import com.dtstack.taier.develop.service.template.clickhouse.ClickHouseReader;
import com.dtstack.taier.develop.service.template.clickhouse.ClickHouseReaderParam;
import com.dtstack.taier.develop.utils.JsonUtils;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author leon
 * @date 2022-10-11 23:09
 **/
@Component
public class ClickHouseReaderBuilder implements DaReaderBuilder {

    private final DsInfoService dataSourceCenterService;

    private final static String SOURCE_ID_KEY = "sourceId";

    private final static String TABLE_KEY = "table";

    public ClickHouseReaderBuilder(DsInfoService dataSourceCenterService) {
        this.dataSourceCenterService = dataSourceCenterService;
    }

    @Override
    public Reader daReaderBuild(TaskResourceParam param) throws Exception {
        ClickHouseReader reader = new ClickHouseReader();

        Map<String, Object> sourceMap = param.getSourceMap();
        Long sourceId = Long.parseLong(sourceMap.get(SOURCE_ID_KEY).toString());

        ClickHouseReaderParam readerParam = PublicUtil.objectToObject(sourceMap, ClickHouseReaderParam.class);
        DsInfo dsInfo = getDsInfo(sourceId);
        JSONObject dataJson = JSON.parseObject(dsInfo.getDataJson());

        ConnectionDTO connection = getConnection(sourceMap, sourceId, dsInfo, dataJson);

        reader.setConnection(Collections.singletonList(connection));
        reader.setUsername(JsonUtils.getStringDefaultEmpty(dataJson, JDBC_USERNAME));
        reader.setPassword(JsonUtils.getStringDefaultEmpty(dataJson, JDBC_PASSWORD));
        Optional.ofNullable(readerParam).map(ClickHouseReaderParam::getSplit).ifPresent(reader::setSplitPk);
        Optional.ofNullable(readerParam).map(ClickHouseReaderParam::getWhere).ifPresent(reader::setWhere);
        Optional.ofNullable(readerParam).map(ClickHouseReaderParam::getColumn).ifPresent(reader::setColumn);
        reader.setSourceIds(Lists.newArrayList(sourceId));
        List<ColumnDTO> column = reader.getColumn();
        column.forEach(c -> c.setName(c.getKey()));

        return reader;
    }

    @Override
    public Map<String, Object> getParserSourceMap(Map<String, Object> sourceMap) {
        ClickHouseReaderParam param = JsonUtils.objectToObject(sourceMap, ClickHouseReaderParam.class);
        return JsonUtils.objectToMap(param);
    }

    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.Clickhouse;
    }

    @Override
    public void setReaderJson(TaskResourceParam param) {
    }

    private DsInfo getDsInfo(Long sourceId) {
        return dataSourceCenterService.getOneById(sourceId);
    }

    @NotNull
    private static ConnectionDTO getConnection(Map<String, Object> sourceMap, Long sourceId, DsInfo dsInfo, JSONObject dataJson) {
        ConnectionDTO connection = new ConnectionDTO();
        connection.setJdbcUrl(Collections.singletonList(JsonUtils.getStringDefaultEmpty(dataJson, JDBC_URL)));
        connection.setTable(Collections.singletonList(JsonUtils.getStringDefaultEmpty(new JSONObject(sourceMap), TABLE_KEY)));
        connection.setType(dsInfo.getDataTypeCode());
        connection.setSourceId(sourceId);
        return connection;
    }
}
