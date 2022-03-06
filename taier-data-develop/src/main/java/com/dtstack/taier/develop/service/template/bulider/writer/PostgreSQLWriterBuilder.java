package com.dtstack.taier.develop.service.template.bulider.writer;

import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import com.dtstack.taier.develop.service.datasource.impl.DsInfoService;
import com.dtstack.taier.develop.service.template.postgresql.PostgreSQLWriter;
import com.dtstack.taier.develop.service.template.rdbms.RDBWriter;
import com.dtstack.taier.develop.service.template.rdbms.RDBWriterParam;
import com.dtstack.taier.develop.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author daojin
 * @program: dt-centet-datasync
 * @description: Postgre Writer Builder
 * @date 2021-11-03 16:36:39
 */
@Component
public class PostgreSQLWriterBuilder extends AbsRDBWriterBuilder{

    @Autowired
    DsInfoService dataSourceAPIClient;


    @Override
    public DsInfoService getDataSourceAPIClient() {
        return dataSourceAPIClient;
    }

    @Override
    public RDBWriter getRDBWriter() {
        return new PostgreSQLWriter();
    }

    @Override
    public RDBWriterParam getRDBWriterParam(TaskResourceParam param) {
        return JsonUtils.objectToObject(param.getTargetMap(), RDBWriterParam.class);
    }

    @Override
    public void preWriterJson(TaskResourceParam param) {

    }


    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.PostgreSQL;
    }
}
