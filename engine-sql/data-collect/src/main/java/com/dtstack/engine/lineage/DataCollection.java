package com.dtstack.engine.lineage;

import com.dtstack.sdk.core.common.DtInsightApi;

import javax.sql.DataSource;

/**
 * @author chener
 * @Classname DataCollection
 * @Description TODO
 * @Date 2020/11/28 11:01
 * @Created chener@dtstack.com
 */
public abstract class DataCollection {

    DataSource dataSource;

    DtInsightApi dtInsightApi;

    public DataCollection(DataSource dataSource, DtInsightApi dtInsightApi) {
        this.dataSource = dataSource;
        this.dtInsightApi = dtInsightApi;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public DtInsightApi getDtInsightApi() {
        return dtInsightApi;
    }

    public abstract CollectAppType getAppType();

    public abstract void collect();
}
