package com.dtstack.batch.sync.template;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.common.template.Writer;
import com.dtstack.batch.sync.job.PluginName;
import com.dtstack.batch.sync.util.ColumnUtil;

/**
 * @author jiangbo
 * @date 2018/7/3 13:41
 */
public class MongoDbWriter extends MongoDbBase implements Writer {

    private String writeMode = "insert";

    private String replaceKey = "_id";

    @Override
    public JSONObject toWriterJson() {
        JSONObject parameter = new JSONObject(true);
        parameter.put("hostPorts",this.getHostPorts());
        parameter.put("username",this.getUsername());
        parameter.put("password",this.getPassword());
        parameter.put("database",this.getDatabase());
        parameter.put("collectionName",this.getCollectionName());
        parameter.put("column", ColumnUtil.getColumns(this.getColumn(),PluginName.MongoDB_W));
        parameter.put("writeMode",this.getWriteMode());
        parameter.put("replaceKey",this.getReplaceKey());
        parameter.put("sourceIds",getSourceIds());
        parameter.putAll(super.getExtralConfigMap());

        JSONObject writer = new JSONObject(true);

        writer.put("name",PluginName.MongoDB_W);
        writer.put("parameter", parameter);
        return writer;
    }

    @Override
    public String toWriterJsonString() {
        return toWriterJson().toJSONString();
    }

    public String getWriteMode() {
        return writeMode;
    }

    public void setWriteMode(String writeMode) {
        this.writeMode = writeMode;
    }

    public String getReplaceKey() {
        return replaceKey;
    }

    public void setReplaceKey(String replaceKey) {
        this.replaceKey = replaceKey;
    }

    @Override
    public void checkFormat(JSONObject data) {
        super.checkFormat(data);
    }
}
