package com.dtstack.batch.sync.template;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.common.template.Writer;
import com.dtstack.batch.sync.job.PluginName;

public class StreamWriter extends ExtralConfig implements Writer {

    @Override
    public JSONObject toWriterJson() {
        JSONObject writer = new JSONObject();
        writer.put("name", PluginName.Stream_W);
        JSONObject parameter = new JSONObject(true);
        parameter.putAll(super.getExtralConfigMap());

        writer.put("parameter", parameter);
        return writer;
    }

    @Override
    public String toWriterJsonString() {
        return toWriterJson().toJSONString();
    }

    @Override
    public void checkFormat(JSONObject data) {

    }
}
