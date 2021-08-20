package com.dtstack.lineage.sourcekey;

import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.lineage.bo.RdbmsDataSourceConfig;

/**
 * @Author: ZYD
 * Date: 2021/2/3 17:56
 * Description: 普通数据源sourceKey生成器
 * @since 1.0.0
 */
public class NormalSourceKeyGenerator extends AbstractSourceKeyGenerator{

    @Override
    public String generateSourceKey(String jdbcUrl, Integer sourceType) {

        RdbmsDataSourceConfig sourceConfig = new RdbmsDataSourceConfig();
        if(jdbcUrl.contains("impala") && jdbcUrl.contains(ConfigConstant.SEMICOLON)){
            jdbcUrl = jdbcUrl.substring(0, jdbcUrl.indexOf(ConfigConstant.SEMICOLON));
        }
        if(jdbcUrl.contains("principal")){
            jdbcUrl = jdbcUrl.substring(0,jdbcUrl.indexOf("principal")-1);
            if(jdbcUrl.endsWith(ConfigConstant.BACKSLASH)){
                jdbcUrl = jdbcUrl.substring(0,jdbcUrl.length()-1);
            }
        }
        sourceConfig.setJdbc(jdbcUrl);
        return sourceConfig.generateRealSourceKey();
    }
}
