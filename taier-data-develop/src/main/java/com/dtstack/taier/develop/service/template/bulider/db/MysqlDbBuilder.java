package com.dtstack.taier.develop.service.template.bulider.db;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.Mysql5SourceDTO;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;


@Component
public class MysqlDbBuilder extends AbsRdbmsDbBuilder {
    private static final Logger logger = LoggerFactory.getLogger(MysqlDbBuilder.class);

    private static Pattern pollColumn = Pattern.compile("^VARCHAR.*|^DATE.*|^DATETIME.*|^INT.*|^BIGINT.*|^TIMESTAMP.*");
    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.MySQL;
    }

    @Override
    public List<JSONObject> listPollTableColumn(ISourceDTO sourceDTO, String tableName) {
        List<JSONObject> columns = super.listPollTableColumn(sourceDTO, tableName);
        return getByColumn(columns, pollColumn);
    }

    @Override
    public List<String> listSchemas(ISourceDTO sourceDTO, String db) {
        return Collections.singletonList(getClient().getCurrentDatabase(sourceDTO));
    }
//
//    public Long getTableSize(Long sourceId, String tableName) {
//        Mysql5SourceDTO sourceDTO = (Mysql5SourceDTO)dataSourceCenterService.getSourceDTO(sourceId);
//        ITable table = ClientCache.getTable(sourceDTO.getSourceType());
//        try {
//           return table.getTableSize(sourceDTO, MysqlUtil.getDB(sourceDTO.getUrl()), tableName);
//        }catch (Exception e){
//            logger.error("获取表大小出错",e);
//            return 0L;
//        }
//    }
}
