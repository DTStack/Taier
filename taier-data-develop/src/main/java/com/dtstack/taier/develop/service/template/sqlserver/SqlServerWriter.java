package com.dtstack.taier.develop.service.template.sqlserver;

import com.dtstack.taier.develop.service.template.PluginName;
import com.dtstack.taier.develop.service.template.rdbms.RDBWriter;

/**
 * @author daojin
 * @program: dt-centet-datasync
 * @description: SQLServer Writer
 * @date 2021-11-03 16:52:22
 */
public class SqlServerWriter extends RDBWriter {


    @Override
    public String pluginName() {
        return PluginName.SQLSERVER_W;
    }
}
