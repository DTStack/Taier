package com.dtstack.taier.develop.service.template.bulider.writer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.util.PublicUtil;
import com.dtstack.taier.dao.domain.DsInfo;
import com.dtstack.taier.datasource.api.base.ClientCache;
import com.dtstack.taier.datasource.api.client.IClient;
import com.dtstack.taier.datasource.api.dto.ColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.Table;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.develop.common.template.Writer;
import com.dtstack.taier.develop.datasource.convert.load.SourceLoaderService;
import com.dtstack.taier.develop.dto.devlop.ColumnDTO;
import com.dtstack.taier.develop.dto.devlop.ConnectionDTO;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import com.dtstack.taier.develop.service.datasource.impl.DsInfoService;
import com.dtstack.taier.develop.service.template.clickhouse.ClickHouseWriter;
import com.dtstack.taier.develop.service.template.clickhouse.ClickHouseWriterParam;
import com.dtstack.taier.develop.utils.JsonUtils;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.dtstack.taier.develop.service.template.bulider.reader.DaReaderBuilder.JDBC_PASSWORD;
import static com.dtstack.taier.develop.service.template.bulider.reader.DaReaderBuilder.JDBC_URL;
import static com.dtstack.taier.develop.service.template.bulider.reader.DaReaderBuilder.JDBC_USERNAME;

/**
 * @author leon
 * @date 2022-10-11 17:12
 **/
@Component
public class ClickHouseWriterBuilder implements DaWriterBuilder {

    private final DsInfoService dataSourceCenterService;

    private final SourceLoaderService sourceLoaderService;

    private final static String SOURCE_ID_KEY = "sourceId";

    private final static String TABLE_KEY = "table";

    public ClickHouseWriterBuilder(DsInfoService dataSourceCenterService, SourceLoaderService sourceLoaderService) {
        this.dataSourceCenterService = dataSourceCenterService;
        this.sourceLoaderService = sourceLoaderService;
    }

    @Override
    public Writer daWriterBuild(TaskResourceParam param) {
        ClickHouseWriter writer = new ClickHouseWriter();

        Map<String, Object> sourceMap = param.getTargetMap();
        Long sourceId = Long.parseLong(sourceMap.get(SOURCE_ID_KEY).toString());

        ClickHouseWriterParam writerParam = PublicUtil.objectToObject(sourceMap, ClickHouseWriterParam.class);
        if (Objects.isNull(writerParam)) {
            return writer;
        }

        DsInfo dsInfo = getDsInfo(sourceId);
        JSONObject dataJson = JSON.parseObject(dsInfo.getDataJson());

        ConnectionDTO connection = getConnection(sourceMap, sourceId, dsInfo, dataJson);
        writer.setConnection(Collections.singletonList(connection));
        writer.setUsername(JsonUtils.getStringDefaultEmpty(dataJson, JDBC_USERNAME));
        writer.setPassword(JsonUtils.getStringDefaultEmpty(dataJson, JDBC_PASSWORD));

        Optional.of(writerParam).map(ClickHouseWriterParam::getColumn).ifPresent(writer::setColumn);
        writer.setWriteMode(writer.getWriteMode());
        writer.setSourceIds(Lists.newArrayList(sourceId));


        List<ColumnMetaDTO> allColumns = getAllColumns(writerParam, dsInfo);

        List<String> fullColumnNames = new ArrayList<>();
        List<String> fullColumnTypes = new ArrayList<>();

        for (ColumnMetaDTO dto : allColumns) {
            if (!dto.getPart()) {
                fullColumnNames.add(dto.getKey());
                fullColumnTypes.add(dto.getType());
            }
        }

        writer.setFullColumnName(fullColumnNames);
        writer.setGetFullColumnType(fullColumnTypes);

        for (int i = 0; i < fullColumnNames.size(); i++) {
            for (Object col : writer.getColumn()) {
                if (fullColumnNames.get(i).equals(((ColumnDTO) col).getKey())) {
                    ((ColumnDTO) col).setIndex(i);
                    break;
                }
            }
        }

        return writer;
    }

    private List<ColumnMetaDTO> getAllColumns(ClickHouseWriterParam writerParam, DsInfo dsInfo) {
        IClient client = ClientCache.getClient(dsInfo.getDataTypeCode());
        ISourceDTO sourceDTO = sourceLoaderService.buildSourceDTO(dsInfo.getId());
        Table tableInfo = client.getTable(sourceDTO, SqlQueryDTO.builder().tableName(writerParam.getTable()).build());
        return tableInfo.getColumns();
    }

    @Override
    public Map<String, Object> getParserTargetMap(Map<String, Object> sourceMap) {
        ClickHouseWriterParam param = JsonUtils.objectToObject(sourceMap, ClickHouseWriterParam.class);
        return JsonUtils.objectToMap(param);
    }

    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.Clickhouse;
    }

    @Override
    public void setWriterJson(TaskResourceParam param) {
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

    private DsInfo getDsInfo(Long sourceId) {
        return dataSourceCenterService.getOneById(sourceId);
    }

}
