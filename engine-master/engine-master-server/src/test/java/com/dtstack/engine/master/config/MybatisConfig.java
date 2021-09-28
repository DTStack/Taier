/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.engine.master.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.dtstack.engine.common.env.EnvironmentContext;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/8/14
 */
@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = {"com.dtstack.engine.*"})
@MapperScan(basePackages = {"com.dtstack.engine.dao","com.dtstack.engine.datasource.dao","com.dtstack.batch.dao"}, sqlSessionTemplateRef = "sqlSessionTemplate")
public class MybatisConfig {

    @Autowired
    private EnvironmentContext environmentContext;

    @Primary
    @Bean(name = "dataSource")
    public DataSource dataSource() throws PropertyVetoException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(environmentContext.getJdbcUrl());
        dataSource.setDriverClassName(environmentContext.getJdbcDriverClassName());
        dataSource.setUsername(environmentContext.getJdbcUser());
        dataSource.setPassword(environmentContext.getJdbcPassword());
        dataSource.setMaxActive(environmentContext.getMaxPoolSize());
        dataSource.setMinIdle(environmentContext.getMinPoolSize());
        dataSource.setInitialSize(environmentContext.getInitialPoolSize());
        dataSource.setKeepAlive(environmentContext.getKeepAlive());
        dataSource.setMinEvictableIdleTimeMillis(environmentContext.getMinEvictableIdleTimeMillis());
        dataSource.setTimeBetweenConnectErrorMillis(environmentContext.getTimeBetweenEvictionRunsMillis());
        dataSource.setRemoveAbandoned(environmentContext.getRemoveAbandoned());
        dataSource.setLogAbandoned(environmentContext.getRemoveAbandoned());
        dataSource.setRemoveAbandonedTimeout(environmentContext.getRemoveAbandonedTimeout());
        dataSource.setTestWhileIdle(environmentContext.getTestWhileIdle());
        dataSource.setTestOnBorrow(environmentContext.getTestOnBorrow());
        dataSource.setTestOnReturn(environmentContext.getTestOnReturn());
        dataSource.setValidationQuery("select 1");
        dataSource.setPoolPreparedStatements(environmentContext.getPoolPreparedStatements());
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(environmentContext.getMaxPoolPreparedStatementPerConnectionSize());
        return dataSource;
    }

    @Primary
    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource());
        sqlSessionFactoryBean.setTypeAliasesPackage("com.dtstack.engine.domain,com.dtstack.engine.domain.po");
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resourceDash = resolver.getResources(environmentContext.getMybatisMapperLocations());
        Resource[] resources = (Resource[]) ArrayUtils.addAll(resourceDash);
        sqlSessionFactoryBean.setMapperLocations(resources);
        Resource resource = resolver.getResource(environmentContext.getMybatisConfigLocation());
        sqlSessionFactoryBean.setConfigLocation(resource);
        sqlSessionFactoryBean.getObject().getConfiguration().setMapUnderscoreToCamelCase(true);
        return sqlSessionFactoryBean.getObject();
    }

    /**
     * 配置事务管理器
     */
    @Bean(name = "transactionManager")
    @Primary
    public DataSourceTransactionManager transactionManager() throws Exception {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean(name = "sqlSessionTemplate")
    @Primary
    public SqlSessionTemplate sqlSessionTemplate() throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory());
    }

}
