package com.dtstack.taier.datasource.plugin.saphana;

import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;
import com.dtstack.taier.datasource.api.source.DataBaseType;

/**
 * sap hana 连接工厂
 *
 * @author ：wangchuan
 * date：Created in 上午10:13 2021/12/30
 * company: www.dtstack.com
 */
public class SapHanaConnFactory extends ConnFactory {
    public SapHanaConnFactory() {
        driverName = DataBaseType.sapHana1.getDriverClassName();
        this.errorPattern = new SapHanaErrorPattern();
    }
}
