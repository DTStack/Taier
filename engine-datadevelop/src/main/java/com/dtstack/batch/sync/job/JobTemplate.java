package com.dtstack.batch.sync.job;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.common.template.Reader;
import com.dtstack.batch.common.template.Setting;
import com.dtstack.batch.common.template.Writer;
import com.google.common.collect.Lists;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/15
 */
public abstract class JobTemplate {

    public abstract Reader newReader();

    public abstract Writer newWrite();

    public abstract Setting newSetting();

    public String toJobJsonString() {

        Reader reader = newReader();
        Writer writer = newWrite();
        Setting setting = newSetting();

        JSONObject content = new JSONObject(2);
        content.put("reader", reader.toReaderJson());
        content.put("writer", writer.toWriterJson());

        JSONObject jobJson = new JSONObject(2);
        jobJson.put("content", Lists.newArrayList(content));
        jobJson.put("setting", setting.toSettingJson());

        StringBuilder job = new StringBuilder();
        job.append("{ \"job\":");
        job.append(jobJson.toJSONString());
        job.append(" }");
        return job.toString();
    }

}
