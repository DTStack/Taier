package com.dtstack.engine.lineage.asserts;

import com.dtstack.engine.lineage.CollectAppType;
import com.dtstack.engine.lineage.DataCollection;
import com.dtstack.sdk.core.common.DtInsightApi;

import javax.sql.DataSource;

/**
 * @author chener
 * @Classname Asserts
 * @Description TODO
 * @Date 2020/11/28 11:00
 * @Created chener@dtstack.com
 */
public class Asserts extends DataCollection {

    public Asserts(DataSource dataSource, DtInsightApi dtInsightApi) {
        super(dataSource, dtInsightApi);
    }

    @Override
    public CollectAppType getAppType() {
        return CollectAppType.ASSERTS;
    }

    @Override
    public void collect() {

    }
}
