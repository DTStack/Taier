package com.dtstack.batch.sync.template;


import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.common.template.Reader;
import com.dtstack.batch.sync.job.PluginName;
import com.dtstack.batch.sync.util.ColumnUtil;

public class OdpsReader extends OdpsBase implements Reader {

    @Override
    public JSONObject toReaderJson() {
        JSONObject odpsConfig = new JSONObject(true);
        odpsConfig.put("accessId", accessId);
        odpsConfig.put("accessKey", accessKey);
        odpsConfig.put("project", project);
        odpsConfig.put("odpsServer", endPoint);

        JSONObject parameter = new JSONObject(true);
        parameter.put("odpsConfig", odpsConfig);
        parameter.put("table", table);
        parameter.put("partition", partition);
        parameter.put("column", ColumnUtil.getColumns(this.getColumn(),PluginName.ODPS_R));
        parameter.put("sourceIds",getSourceIds());
        parameter.putAll(super.getExtralConfigMap());

        JSONObject reader = new JSONObject(true);

        reader.put("name", PluginName.ODPS_R);
        parameter.put("sourceId",getSourceId());
        reader.put("parameter", parameter);

        return reader;
    }

    @Override
    public String toReaderJsonString() {
        return toReaderJson().toJSONString();
    }

    @Override
    public void checkFormat(JSONObject data) {
        super.checkFormat(data);
    }
}
