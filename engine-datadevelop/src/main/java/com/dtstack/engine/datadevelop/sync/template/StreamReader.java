package com.dtstack.batch.sync.template;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.common.template.Reader;
import com.dtstack.batch.sync.job.PluginName;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class StreamReader extends ExtralConfig implements Reader {

    private long sliceRecordCount;

    private List<JSONObject> column;

    @Override
    public JSONObject toReaderJson() {
        JSONObject parameter = new JSONObject(true);
        parameter.put("sliceRecordCount",sliceRecordCount);
        parameter.put("column", CollectionUtils.isNotEmpty(this.getColumn()));
        parameter.putAll(super.getExtralConfigMap());

        JSONObject reader = new JSONObject(true);
        reader.put("name", PluginName.Stream_R);
        reader.put("parameter", parameter);
        return reader;
    }

    @Override
    public String toReaderJsonString() {
        return toReaderJson().toJSONString();
    }

    @Override
    public void checkFormat(JSONObject data) {

    }

    public long getSliceRecordCount() {
        return sliceRecordCount;
    }

    public void setSliceRecordCount(long sliceRecordCount) {
        this.sliceRecordCount = sliceRecordCount;
    }

    public List<JSONObject> getColumn() {
        return column;
    }

    public void setColumn(List<JSONObject> column) {
        this.column = column;
    }
}
