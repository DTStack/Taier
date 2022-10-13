package com.dtstack.taier.develop.service.template;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.develop.common.template.Writer;

import java.util.List;

import static com.dtstack.taier.develop.service.template.BaseReaderPlugin.EXTRAL_CONFIG;
import static com.dtstack.taier.develop.service.template.BaseReaderPlugin.dealExtralConfig;


/**
 * Date: 2020/2/26
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public abstract class BaseWriterPlugin implements Writer {
    private String extralConfig;

    protected List<Long> sourceIds;

    @Override
    public JSONObject toWriterJson() {
        JSONObject param = new JSONObject();
        try {
            param = JSON.parseObject(JSON.toJSONString(this));
        } catch (Exception e) {
            param = JSON.parseObject(JSON.toJSON(this).toString());
        }


        dealExtralConfig(param);
        param.remove(EXTRAL_CONFIG);
        JSONObject res = new JSONObject();
        res.put("name", pluginName());
        res.put("parameter", param);
        return res;
    }

    @Override
    public String toWriterJsonString() {
        return toWriterJson().toJSONString();
    }

    @Override
    public void checkFormat(JSONObject data) {

    }

    public List<Long> getSourceIds() {
        return sourceIds;
    }

    public void setSourceIds(List<Long> sourceIds) {
        this.sourceIds = sourceIds;
    }

    public String getExtralConfig() {
        return extralConfig;
    }

    public void setExtralConfig(String extralConfig) {
        this.extralConfig = extralConfig;
    }

    public abstract String pluginName();
}
