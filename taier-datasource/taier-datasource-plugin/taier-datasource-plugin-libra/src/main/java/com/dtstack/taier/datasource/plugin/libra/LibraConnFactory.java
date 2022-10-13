package com.dtstack.taier.datasource.plugin.libra;

import com.dtstack.taier.datasource.api.source.DataBaseType;
import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.postgresql.Driver;

import java.sql.DriverPropertyInfo;
import java.util.Properties;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 11:53 2020/2/29
 * @Description：Libra 连接工厂
 */
public class LibraConnFactory extends ConnFactory {
    /**
     * Libra 驱动
     */
    private static Driver LIBRA_DRIVER = new Driver();

    public LibraConnFactory() {
        this.driverName = DataBaseType.PostgreSQL.getDriverClassName();
        this.errorPattern = new LibraErrorPattern();
    }

    /**
     * 获取 URL 属性
     *
     * @param url
     * @param info
     * @param key
     * @return
     */
    public static String getDriverPropertyInfo(String url, Properties info, String key) {
        if (StringUtils.isBlank(key)) {
            return StringUtils.EMPTY;
        }

        // 读取 Libra 所有属性
        DriverPropertyInfo[] propertyInfo = LIBRA_DRIVER.getPropertyInfo(url, info);
        if (ArrayUtils.isEmpty(propertyInfo)) {
            return StringUtils.EMPTY;
        }

        for (DriverPropertyInfo driverPropertyInfo : propertyInfo) {
            // 根据 KEY 判断
            if (driverPropertyInfo != null && key.equalsIgnoreCase(driverPropertyInfo.name)) {
                return driverPropertyInfo.value;
            }
        }

        return StringUtils.EMPTY;
    }
}
