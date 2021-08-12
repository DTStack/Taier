package com.dtstack.engine.lineage;

import com.dtstack.engine.lineage.util.SqlParserClientOperator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *类名称:BeanConfig
 *类描述:TODO
 *创建人:newman
 *创建时间:2021/4/17 11:57 上午
 *Version 1.0
 */
@Configuration
public class BeanConfig {

    @Bean
    public SqlParserClientOperator sqlParserClientOperator(){
        return SqlParserClientOperator.getInstance();
    }
}


