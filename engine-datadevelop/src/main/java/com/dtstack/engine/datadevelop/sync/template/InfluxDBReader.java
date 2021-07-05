package com.dtstack.batch.sync.template;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.common.template.Reader;
import com.dtstack.batch.sync.job.PluginName;
import com.dtstack.batch.sync.util.ColumnUtil;
import com.google.common.collect.Lists;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

@Data
public class InfluxDBReader extends InfluxDBBase implements Reader {

    /**
     * 自定义的查询语句
     *
     */
    private String customSql;

    /**
     * 数据过滤
     */
    private String where;

    /**
     * 切分键
     */
    private String splitPK;

    /**
     * 响应格式
     */
    private String format = "msgpack";

    @Override
    public JSONObject toReaderJson() {
        //构建reader
        JSONObject result = new JSONObject();
        result.put("name", PluginName.InfluxDB_R);
        //构建parameter
        JSONObject parameter = new JSONObject();
        parameter.put("sourceIds", getSourceIds());
        parameter.put("username", this.getUsername());
        parameter.put("password", this.getPassword());
        parameter.put("customSql", this.getCustomSql());
        parameter.put("where", this.getWhere());
        parameter.put("splitPk", this.getSplitPK());
        parameter.put("format", this.getFormat());
        parameter.put("extralConfig", this.getExtralConfig());
        parameter.put("column", ColumnUtil.getColumns(this.column,PluginName.HDFS_R));
        JSONObject connection = new JSONObject();
        connection.put("url", Lists.newArrayList(this.getUrl()));
        connection.put("table", Lists.newArrayList(this.getTable()));
        connection.put("schema", StringUtils.isNotBlank(this.getSchema()) ? this.getSchema() : "");
        parameter.put("connection", Lists.newArrayList(connection));
        result.put("parameter", parameter);
        return result;
    }

    @Override
    public String toReaderJsonString() {
        return toReaderJson().toJSONString();
    }

    @Override
    public void checkFormat(JSONObject data) {
        if (data == null){
            throw new RdosDefineException("reader is not null");
        }
    }
}
