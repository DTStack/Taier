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
@MapperScan(basePackages = {"com.dtstack.engine.dao"}, sqlSessionTemplateRef = "sqlSessionTemplate")
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
        dataSource.setPoolPreparedStatements(environmentContext.getPoolPreparedStatements());
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(environmentContext.getMaxPoolPreparedStatementPerConnectionSize());
        return dataSource;
    }

    @Primary
    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource());
        sqlSessionFactoryBean.setTypeAliasesPackage("com.dtstack.engine.api.domain,com.dtstack.engine.api.domain.po");
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
