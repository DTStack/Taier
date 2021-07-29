package com.dtstack.batch.sync.template;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.common.template.Reader;
import com.dtstack.batch.sync.job.PluginName;
import com.dtstack.batch.sync.util.ColumnUtil;
import org.apache.commons.lang.StringUtils;

public class EsReader extends EsBase implements Reader {

    private JSONObject query;

    private String username;
    private String password;

    @Override
    public JSONObject toReaderJson() {
        JSONObject parameter = new JSONObject(true);
        parameter.put("address", address);
        parameter.put("query", query);
        parameter.put("column", ColumnUtil.getColumns(this.getColumn(), PluginName.ES_R));
        parameter.put("sourceIds",getSourceIds());
        parameter.putAll(super.getExtralConfigMap());
        parameter.put("username", this.getUsername());
        parameter.put("password", this.getPassword());
        JSONObject writer = new JSONObject(true);
        writer.put("name", PluginName.ES_R);
        writer.put("parameter", parameter);

        return writer;
    }

    @Override
    public String toReaderJsonString() {
        return toReaderJson().toJSONString();
    }

    public JSONObject getQuery() {
        return query;
    }

    public void setQuery(JSONObject query) {
        this.query = query;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void checkFormat(JSONObject data) {

        if (StringUtils.isBlank(data.getString("name"))){
            throw new RdosDefineException("name 不能为空");
        }

        JSONObject parameter = data.getJSONObject("parameter");

        if (StringUtils.isBlank(parameter.getString("address"))){
            throw new RdosDefineException("address 不能为空");
        }
        checkArray(parameter, "column");
    }
}
