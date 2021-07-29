package com.dtstack.batch.sync.template;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.common.template.Writer;
import com.dtstack.batch.sync.job.PluginName;
import com.dtstack.batch.sync.util.ColumnUtil;

public class OdpsWriter extends OdpsBase implements Writer {

    private String writeMode;

    public String getWriteMode() {
        return writeMode;
    }

    public void setWriteMode(String writeMode) {
        this.writeMode = writeMode;
    }

    @Override
    public JSONObject toWriterJson() {
        JSONObject odpsConfig = new JSONObject(true);
        odpsConfig.put("accessId", accessId);
        odpsConfig.put("accessKey", accessKey);
        odpsConfig.put("project", project);
        odpsConfig.put("odpsServer", endPoint);

        JSONObject parameter = new JSONObject(true);
        parameter.put("odpsConfig", odpsConfig);
        parameter.put("table", table);
        parameter.put("partition", partition);
        parameter.put("writeMode", writeMode);
        parameter.put("column", ColumnUtil.getColumns(this.getColumn(), PluginName.ODPS_W));
        parameter.put("sourceIds",getSourceIds());
        parameter.putAll(super.getExtralConfigMap());


        JSONObject writer = new JSONObject(true);
        writer.put("name", PluginName.ODPS_W);
        parameter.put("sourceId",getSourceId());
        writer.put("parameter", parameter);

        return writer;
    }

    @Override
    public String toWriterJsonString() {
        return toWriterJson().toJSONString();
    }

    @Override
    public void checkFormat(JSONObject data) {
        super.checkFormat(data);
    }
}
