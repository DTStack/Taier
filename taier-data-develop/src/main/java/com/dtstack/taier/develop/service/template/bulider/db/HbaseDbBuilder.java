package com.dtstack.taier.develop.service.template.bulider.db;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.loader.dto.SqlQueryDTO;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HbaseDbBuilder {

    @Component
    public class HBASEDbBuilder extends AbsNoSqlDbBuilder {
        @Override
        public JSONObject pollPreview(String tableName, ISourceDTO sourceDTO) {
            JSONObject preview = new JSONObject(2);
            List<List<Object>> dataList = getClient().getPreview(sourceDTO, SqlQueryDTO.builder().tableName(tableName).previewNum(5).build());
            List<Map<String, String>> finalList = new ArrayList<>();
            dataList.stream().forEach(data -> {
                finalList.add((Map<String, String>) data.get(0));
            });
            preview.put("dataList", finalList);
            return preview;
        }

        @Override
        public String buildConnMsgForSA(JSONObject dataJson) {
            return null;
        }

        @Override
        public DataSourceType getDataSourceType() {
            return DataSourceType.HBASE;
        }

    }
}
