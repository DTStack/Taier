package com.dtstack.batch.engine.rdbms.hive.util;

/**
 * Date: 2020/3/23
 * Company: www.dtstack.com
 * @Description 默认计算引擎使用spark。查询表元数据信息使用spark thriftServer
 * @author xiaochen
 */
public class SparkThriftConnectionUtils {

    public enum HiveVersion{

        /**
         * hive_1.x
         */
        HIVE_1x("1.x"),

        /**
         * hive_1.x
         */
        HIVE_2x("2.x"),

        /**
         * hive_3.x
         */
        HIVE_3x("3.x");
        private String version;

        public String getVersion() {
            return version;
        }

        HiveVersion(String version) {
            this.version = version;
        }

        public static HiveVersion getByVersion(String versionStr){
            for (HiveVersion version:values()){
                if (version.getVersion().equalsIgnoreCase(versionStr)){
                    return version;
                }
            }
            return null;
        }
    }

}
