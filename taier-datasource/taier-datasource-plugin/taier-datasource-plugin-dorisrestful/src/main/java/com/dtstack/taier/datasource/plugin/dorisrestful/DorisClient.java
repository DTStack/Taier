package com.dtstack.taier.datasource.plugin.dorisrestful;

import com.dtstack.taier.datasource.api.dto.ColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.Table;
import com.dtstack.taier.datasource.api.dto.source.DorisRestfulSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.plugin.common.nosql.AbsNoSqlClient;
import com.dtstack.taier.datasource.plugin.common.utils.SearchUtil;
import com.dtstack.taier.datasource.plugin.dorisrestful.request.DorisRestfulClient;
import com.dtstack.taier.datasource.plugin.dorisrestful.request.DorisRestfulClientFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * doris 客户端
 *
 * @author ：qianyi
 * date：Created in 上午10:33 2021/7/14
 * company: www.dtstack.com
 */
@Slf4j
public class DorisClient extends AbsNoSqlClient {

    @Override
    public Boolean testCon(ISourceDTO source) {
        DorisRestfulSourceDTO sourceDTO = buildSourceDTO(source);
        return StringUtils.isNotBlank(getConnectedUrl(sourceDTO));
    }

    @Override
    public List<String> getAllDatabases(ISourceDTO source, SqlQueryDTO queryDTO) {
        DorisRestfulSourceDTO sourceDTO = buildSourceDTO(source);
        sourceDTO.setUrl(getConnectedUrl(sourceDTO));
        DorisRestfulClient restfulClient = DorisRestfulClientFactory.getRestfulClient();
        return restfulClient.getAllDatabases(sourceDTO);
    }

    @Override
    public List<String> getTableListBySchema(ISourceDTO source, SqlQueryDTO queryDTO) {
        DorisRestfulSourceDTO sourceDTO = buildSourceDTO(source);
        sourceDTO.setUrl(getConnectedUrl(sourceDTO));
        DorisRestfulClient restfulClient = DorisRestfulClientFactory.getRestfulClient();
        return SearchUtil.handleSearchAndLimit(
                restfulClient.getTableListBySchema(sourceDTO, queryDTO), queryDTO);
    }

    @Override
    public List<String> getTableList(ISourceDTO source, SqlQueryDTO queryDTO) {
        DorisRestfulSourceDTO sourceDTO = buildSourceDTO(source);
        sourceDTO.setUrl(getConnectedUrl(sourceDTO));
        DorisRestfulClient restfulClient = DorisRestfulClientFactory.getRestfulClient();
        return SearchUtil.handleSearchAndLimit(
                restfulClient.getTableListBySchema(sourceDTO, queryDTO), queryDTO);
    }

    @Override
    public Table getTable(ISourceDTO source, SqlQueryDTO queryDTO) {
        Table table = new Table();
        try {
            List<ColumnMetaDTO> columnMetaData = getColumnMetaData(source, queryDTO);
            String tableComment = getTableMetaComment(source, queryDTO);
            table.setColumns(columnMetaData);
            table.setName(queryDTO.getTableName());
            table.setComment(tableComment);
        } catch (Exception e) {
            throw new SourceException(String.format("SQL executed exception: %s", e.getMessage()), e);
        }
        return table;
    }


    @Override
    public String getTableMetaComment(ISourceDTO source, SqlQueryDTO queryDTO) {
        DorisRestfulSourceDTO sourceDTO = buildSourceDTO(source);
        sourceDTO.setUrl(getConnectedUrl(sourceDTO));
        DorisRestfulClient restfulClient = DorisRestfulClientFactory.getRestfulClient();
        return restfulClient.getTableMetaComment(sourceDTO, queryDTO);
    }

    @Override
    public List<ColumnMetaDTO> getColumnMetaData(ISourceDTO source, SqlQueryDTO queryDTO) {
        DorisRestfulSourceDTO sourceDTO = buildSourceDTO(source);
        sourceDTO.setUrl(getConnectedUrl(sourceDTO));
        DorisRestfulClient restfulClient = DorisRestfulClientFactory.getRestfulClient();
        return restfulClient.getColumnMetaData(sourceDTO, queryDTO);
    }

    @Override
    public List<List<Object>> getPreview(ISourceDTO source, SqlQueryDTO queryDTO) {
        DorisRestfulSourceDTO sourceDTO = buildSourceDTO(source);
        sourceDTO.setUrl(getConnectedUrl(sourceDTO));
        DorisRestfulClient restfulClient = DorisRestfulClientFactory.getRestfulClient();
        return restfulClient.getPreview(sourceDTO, queryDTO);
    }

    @Override
    public List<Map<String, Object>> executeQuery(ISourceDTO source, SqlQueryDTO queryDTO) {
        DorisRestfulSourceDTO sourceDTO = buildSourceDTO(source);
        sourceDTO.setUrl(getConnectedUrl(sourceDTO));
        DorisRestfulClient restfulClient = DorisRestfulClientFactory.getRestfulClient();
        return restfulClient.executeQuery(sourceDTO, queryDTO);
    }

    @Override
    public Boolean executeSqlWithoutResultSet(ISourceDTO source, SqlQueryDTO queryDTO) {
        DorisRestfulSourceDTO sourceDTO = buildSourceDTO(source);
        sourceDTO.setUrl(getConnectedUrl(sourceDTO));
        DorisRestfulClient restfulClient = DorisRestfulClientFactory.getRestfulClient();
        return restfulClient.executeSqlWithoutResultSet(sourceDTO, queryDTO);
    }

    @Override
    public Boolean isTableExistsInDatabase(ISourceDTO source, String tableName, String dbName) {
        DorisRestfulSourceDTO sourceDTO = buildSourceDTO(source);
        sourceDTO.setUrl(getConnectedUrl(sourceDTO));
        DorisRestfulClient restfulClient = DorisRestfulClientFactory.getRestfulClient();
        return restfulClient.isTableExistsInDatabase(sourceDTO, tableName, dbName);
    }

    /**
     * copy 对象
     *
     * @param source
     * @return
     */
    public DorisRestfulSourceDTO buildSourceDTO(ISourceDTO source) {
        DorisRestfulSourceDTO sourceDTO = (DorisRestfulSourceDTO) source;
        try {
            return DorisRestfulSourceDTO.builder()
                    .url(sourceDTO.getUrl())
                    .cluster(sourceDTO.getCluster())
                    .schema(sourceDTO.getSchema())
                    .userName(sourceDTO.getUsername())
                    .password(sourceDTO.getPassword())
                    .build();
        } catch (Exception e) {
            throw new SourceException(e.getMessage(), e);
        }
    }

    /**
     * 获取成功连接的url
     *
     * @param sourceDTO 数据源信息
     * @return connected url
     */
    private String getConnectedUrl(DorisRestfulSourceDTO sourceDTO) {
        if (StringUtils.isBlank(sourceDTO.getUrl())) {
            throw new SourceException("url can't be blank");
        }
        //url支持以逗号分隔的多节点，任一节点连接成功即可
        Exception lastException = null;
        for (String url : sourceDTO.getUrl().split(",")) {
            sourceDTO.setUrl(url);
            DorisRestfulClient restfulClient = DorisRestfulClientFactory.getRestfulClient();
            try {
                if (restfulClient.login(sourceDTO)) {
                    return url;
                }
            } catch (Exception e) {
                lastException = e;
                log.error("dorisRestful connect error.", e);
            }
        }

        //全部节点都连接失败则抛出最后一个节点的失败信息
        throw new SourceException("no url available ,last exception : ", lastException);
    }
}
