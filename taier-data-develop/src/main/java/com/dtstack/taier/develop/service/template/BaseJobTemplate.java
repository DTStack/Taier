package com.dtstack.taier.develop.service.template;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dtstack.taier.develop.common.template.Reader;
import com.dtstack.taier.develop.common.template.Writer;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import com.google.common.collect.Lists;

/**
 * @author sanyue
 * @date 2018/9/12
 */
public abstract class BaseJobTemplate {

    public abstract Reader newReader();

    public abstract Writer newWrite();

    public String toJobJsonString(TaskResourceParam param) {
        return refresh(param);
    }

    public String refresh(TaskResourceParam param) {
        Reader reader = newReader();
        Writer writer = newWrite();

        JSONObject content = new JSONObject();
        if (reader != null) {
            content.put("inputs", new JSONArray(Lists.newArrayList(reader.toReaderJson())));
        }
        if (writer != null) {
            content.put("outputs", new JSONArray(Lists.newArrayList(writer.toWriterJson())));
        }
        return JSONObject.toJSONString(content, SerializerFeature.MapSortField);
    }
}
