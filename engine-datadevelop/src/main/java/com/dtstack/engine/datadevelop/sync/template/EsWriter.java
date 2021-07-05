package com.dtstack.batch.sync.template;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.common.template.Writer;
import com.dtstack.batch.sync.job.PluginName;
import com.dtstack.batch.sync.util.ColumnUtil;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class EsWriter extends EsBase implements Writer {

    private String index;

    private String type;

    private int bulkAction;

    private String username;
    private String password;

    private List<JSONObject> idColumn = new ArrayList<>();

    @Override
    public JSONObject toWriterJson() {
        JSONObject parameter = new JSONObject(true);
        parameter.put("address", address);
        parameter.put("index", index);
        parameter.put("type", type);
        parameter.put("bulkAction", bulkAction);
        parameter.put("column", ColumnUtil.getColumns(this.getColumn(),PluginName.ES_W));
        parameter.put("idColumn", idColumn);
        parameter.put("sourceIds",getSourceIds());
        parameter.put("username", this.getUsername());
        parameter.put("password", this.getPassword());

        parameter.putAll(super.getExtralConfigMap());
        JSONObject writer = new JSONObject(true);
        writer.put("name", PluginName.ES_W);
        writer.put("parameter", parameter);
        return writer;
    }

    @Override
    public String toWriterJsonString() {
        return toWriterJson().toJSONString();
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getBulkAction() {
        return bulkAction;
    }

    public void setBulkAction(int bulkAction) {
        this.bulkAction = bulkAction;
    }

    public List<JSONObject> getIdColumn() {
        return idColumn;
    }

    public void setIdColumn(List<JSONObject> idColumn) {
        this.idColumn = idColumn;
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

        if (StringUtils.isBlank(parameter.getString("index"))){
            throw new RdosDefineException("index 不能为空");
        }

        if (StringUtils.isBlank(parameter.getString("type"))){
            throw new RdosDefineException("type 不能为空");
        }

        if (StringUtils.isBlank(parameter.getString("bulkAction"))){
            throw new RdosDefineException("bulkAction 不能为空");
        }

        if (StringUtils.isBlank(parameter.getString("username"))) {
            throw new RdosDefineException("username 不能为空");
        }

        if (StringUtils.isBlank(parameter.getString("password"))) {
            throw new RdosDefineException("password 不能为空");
        }

        checkArray(parameter, "column");

        /**
         * if test == '[ ]' ,it will return idColumn can't be empty
         */
        String idColumn = parameter.getString("idColumn");
        if(idColumn.replaceAll("\\s{2,}", " ").trim().equals("[]")) {
            idColumn = "";
        }

        if (!StringUtils.isBlank(idColumn)){
            checkArray(parameter, "idColumn");
        }
    }
}
