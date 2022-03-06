package com.dtstack.taier.develop.service.template.postgresql;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.develop.service.template.PluginName;
import com.dtstack.taier.develop.service.template.rdbms.RdbmsPollReader;

/**
 * @author huoyun
 * @date 2021/4/13 2:06 下午
 * @company: www.dtstack.com
 */
public class PostGreSqlPollReader extends RdbmsPollReader {

    @Override
    public String pluginName() {
        return PluginName.PostgreSQL_R;
    }

    @Override
    public void checkFormat(JSONObject jsonObject) {

    }
}
