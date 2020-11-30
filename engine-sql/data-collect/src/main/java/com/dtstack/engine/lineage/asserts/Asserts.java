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

    private static String table_lineage_sql = "select is_manual,lineage_table_id,input_table_id from assets_table_lineage";

    private static String lineage_table_sql = "assets_lineage_table(is_manual,table_id,table_name,db_name,data_source_name)";

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
