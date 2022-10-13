package com.dtstack.taier.develop.service.template.mysql;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.develop.dto.devlop.ConnectionDTO;
import com.dtstack.taier.develop.service.template.PluginName;
import com.dtstack.taier.develop.service.template.rdbms.RdbmsPollReader;
import org.apache.commons.collections.CollectionUtils;

/**
 * Date: 2020/2/19
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public class MysqlPollReader extends RdbmsPollReader {

    @Override
    public String pluginName() {
        boolean isMultiTable = (CollectionUtils.isNotEmpty(getConnection()) && getConnection().size() > 1);

        if (isMultiTable) {
            return PluginName.MySQLD_R;
        } else {
            if (getConnection().size() == 1) {
                ConnectionDTO connectionDTO = getConnection().get(0);
                if (connectionDTO.getTable().size() > 1) {
                    return PluginName.MySQLD_R;
                }
            }
            return PluginName.MySQL_R;
        }
    }

    @Override
    public void checkFormat(JSONObject jsonObject) {

    }
}
