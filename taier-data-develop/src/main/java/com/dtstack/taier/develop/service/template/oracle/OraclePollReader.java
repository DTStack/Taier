package com.dtstack.taier.develop.service.template.oracle;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.develop.service.template.PluginName;
import com.dtstack.taier.develop.service.template.rdbms.RdbmsPollReader;


/**
 * Date: 2020/1/7
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public class OraclePollReader extends RdbmsPollReader {

//    @Override
//    public JSONObject toReaderJson() {
//        JSONObject param = JSON.parseObject(JSON.toJSONString(this));
//        dealExtralConfig(param);
//        JSONObject res = new JSONObject();
//        res.put("name", PluginName.ORACLE_POLL_R);
//        res.put("type", DataSourceType.Oracle.getVal());
//        res.put("parameter", param);
//        return res;
//    }

    @Override
    public String pluginName() {
        return PluginName.ORACLE_POLL_R;
    }

    @Override
    public String toReaderJsonString() {
        return toReaderJson().toJSONString();
    }

    @Override
    public void checkFormat(JSONObject jsonObject) {
        //todo  联调的时候再准备增加检查
    }

}
