package com.dtstack.taier.develop.service.template;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.develop.common.template.Reader;
import com.dtstack.taier.develop.common.template.Setting;
import com.dtstack.taier.develop.common.template.Writer;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import com.dtstack.taier.develop.enums.develop.SyncContentEnum;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;

import java.util.Objects;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 17:14 2019-08-21
 * @Description：flinkx 模板
 */
public abstract class FlinkxJobTemplate extends BaseJobTemplate {
    public abstract Setting newSetting();

    public abstract JSONObject nameMapping();

    public abstract Restoration restoration();

    @Override
    public String refresh(TaskResourceParam param) {

        Reader reader = newReader();
        Writer writer = newWrite();
        Setting setting = newSetting();


        JSONObject content = new JSONObject(2);
        //必填
        JSONObject readerObject = reader.toReaderJson();
        JSONObject writerObject = writer.toWriterJson();
        JSONObject settingObject = setting.toSettingJson();
        // todo 向这种依赖其他字段、源端依赖目标端的字段后续观察如何优化
        if (Objects.nonNull(settingObject.getJSONObject("restore")) &&
                StringUtils.isNotBlank(settingObject.getJSONObject("restore").getString("restoreColumnName"))) {
            writerObject.put("semantic", "exactly-once");
        }
        JSONObject readerParameter = readerObject.getJSONObject("parameter");
        if (Objects.equals(writerObject.getString("name"), PluginName.DORIS_RESTFUL_W)
                && Objects.equals(SyncContentEnum.DATA_STRUCTURE_SYNC.getType(), param.getSourceMap().get("syncContent"))) {
            readerParameter.put("split", true);
            readerParameter.put("pavingData", true);
        }
        content.put("reader", null == reader ? null : readerObject);
        content.put("writer", null == writer ? null : writerObject);
        //选填内容
        JSONObject nameMapping = nameMapping();//reader映射writer相关的配置
        Restoration restoration = restoration();//ddl相关的配置
        if (nameMapping != null) {
            content.put("nameMapping", nameMapping);
        }
        if (restoration != null) {
            content.put("restoration", restoration.toRestorationJson());
        }


        JSONObject jobJson = new JSONObject(2);
        jobJson.put("content", Lists.newArrayList(content));
        jobJson.put("setting", null == setting ? null : setting.toSettingJson());

        StringBuilder job = new StringBuilder();
        job.append("{ \"job\":");
        job.append(JSONObject.toJSONString(jobJson).replace("\"lineDelimiter\":\"\\\\", "\"lineDelimiter\":\"\\").replace("\"fieldDelimiter\":\"\\\\", "\"fieldDelimiter\":\"\\"));
        job.append(" }");
        return job.toString();
    }
}
