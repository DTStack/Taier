package com.dtstack.batch.sync.template;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.common.template.Reader;
import com.dtstack.batch.sync.job.PluginName;
import com.dtstack.batch.sync.util.ColumnUtil;

/**
 * @author jiangbo
 * @date 2018/7/3 13:35
 */
public class MongoDbReader extends MongoDbBase implements Reader {

    private String filter = "{}";

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    @Override
    public JSONObject toReaderJson() {
        JSONObject parameter = new JSONObject(true);
        parameter.put("hostPorts",this.getHostPorts());
        parameter.put("username",this.getUsername());
        parameter.put("password",this.getPassword());
        parameter.put("database",this.getDatabase());
        parameter.put("collectionName",this.getCollectionName());
        parameter.put("column", ColumnUtil.getColumns(this.getColumn(),PluginName.MongoDB_R));
        parameter.put("filter",this.getFilter());
        parameter.put("sourceIds",getSourceIds());
        parameter.putAll(super.getExtralConfigMap());

        JSONObject reader = new JSONObject(true);

        reader.put("name",PluginName.MongoDB_R);
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
