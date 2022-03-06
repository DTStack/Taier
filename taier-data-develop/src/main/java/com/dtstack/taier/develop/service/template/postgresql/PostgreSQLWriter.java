package com.dtstack.taier.develop.service.template.postgresql;

import com.dtstack.taier.develop.service.template.PluginName;
import com.dtstack.taier.develop.service.template.rdbms.RDBWriter;

/**
 * @author daojin
 * @program: dt-centet-datasync
 * @description: PostgreSQL
 * @date 2021-11-05 10:39:58
 */
public class PostgreSQLWriter extends RDBWriter {
    @Override
    public String pluginName() {
        return PluginName.POSTGRESQL_W;
    }
}
