package com.dtstack.taier.develop.service.template.mysql;

import com.dtstack.taier.develop.service.template.PluginName;
import com.dtstack.taier.develop.service.template.rdbms.RDBWriter;

/**
 * @author daojin
 * @program: dt-centet-datasync
 * @description:
 * @date 2021-11-03 16:52:22
 */
public class MySQLWriter extends RDBWriter {

    @Override
    public String pluginName() {
        return PluginName.MySQL_W;
    }
}
