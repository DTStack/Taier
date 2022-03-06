package com.dtstack.taier.develop.service.template.sqlserver;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.develop.service.template.PluginName;
import com.dtstack.taier.develop.service.template.rdbms.RdbmsPollReader;

/**
 * sqlServer 间隔轮询 reader
 *
 * @author ：wangchuan
 * date：Created in 上午10:40 2021/7/7
 * company: www.dtstack.com
 */
public class SqlServerPollReader extends RdbmsPollReader {

    @Override
    public String pluginName() {
        return PluginName.SQLSERVER_POLL_R;
    }

    @Override
    public void checkFormat(JSONObject jsonObject) {

    }
}
