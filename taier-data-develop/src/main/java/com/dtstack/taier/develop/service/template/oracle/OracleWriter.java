package com.dtstack.taier.develop.service.template.oracle;

import com.dtstack.taier.develop.service.template.PluginName;
import com.dtstack.taier.develop.service.template.rdbms.RDBWriter;

/**
 * @author daojin
 * @program: dt-centet-datasync
 * @description:
 * @date 2021-11-03 16:52:22
 */
public class OracleWriter extends RDBWriter {

    @Override
    public String pluginName() {
        return PluginName.Oracle_W;
    }
}
