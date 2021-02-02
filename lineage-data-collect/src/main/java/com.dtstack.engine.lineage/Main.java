package com.dtstack.engine.lineage;

import com.dtstack.engine.lineage.batch.Batch;
import com.dtstack.sdk.core.common.DtInsightApi;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * @author chener
 * @Classname Main
 * @Description TODO
 * @Date 2020/11/28 10:54
 * @Created chener@dtstack.com
 */
public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    private static DtInsightApi dtInsightApi;

    public static void main(String[] args) {
        initLog4jProperties();
        initApi();
        collectBatch();
    }

    private static void initLog4jProperties() {
        InputStream is = Batch.class.getClassLoader().getResourceAsStream("log4j.properties");
        Properties p = new Properties();
        try {
            p.load(is);
            if(p!=null){
                is.close();
            }
            PropertyConfigurator.configure(p);
        } catch (IOException e) {
            logger.error("",e);
        }

        try {
            Properties prop = new Properties();
            InputStream in = Batch.class.getClassLoader().getResourceAsStream("config.properties");
            prop.load(in);
            Iterator<String> it = prop.stringPropertyNames().iterator();
            while (it.hasNext()) {
                String key = it.next();
                Conf.confMap.put(key, prop.getProperty(key));
            }
            in.close();
        } catch (IOException e) {
            logger.error("",e);
        }
    }

    private static void initApi(){
        DtInsightApi.ApiBuilder builder = new DtInsightApi.ApiBuilder()
                .setEndpoint(Conf.getConf(Conf.SERVER))
                .setServerUrls(Conf.getConf(Conf.NODES).split(","))
                .setSlb(true)
                .setToken(Conf.getConf(Conf.TOKEN));
        dtInsightApi = builder.buildApi();
    }

    private static void collectBatch(){
        CollectAppType appType = CollectAppType.BATCH;
        try {
            String url = Conf.getConf(getDataSourceKey(appType,Conf.URL_SUFFIX));
            String user = Conf.getConf(getDataSourceKey(appType,Conf.USER_SUFFIX));
            if (StringUtils.isEmpty(url) || StringUtils.isEmpty(user)){
                logger.info("未配置离线数据库，不同步离线");
                return;
            }
            DataSource dataSource = getDataSource(CollectAppType.BATCH);
            Batch batch = new Batch(dataSource,dtInsightApi);
            batch.doBatchJob();
        } catch (PropertyVetoException e) {
            logger.error("",e);
        }
    }

    public static DataSource getDataSource(CollectAppType appType) throws PropertyVetoException {
        return DataSourceHolder.getDataSource(appType);
    }

    private static class DataSourceHolder{
        private static Map<CollectAppType,DataSource> mDataSourceMap;
        static DataSource getDataSource(CollectAppType appType) throws PropertyVetoException {
            if (mDataSourceMap == null){
                mDataSourceMap = new HashMap<>(4);
            }
            DataSource dataSource = mDataSourceMap.get(appType);
            if (Objects.isNull(dataSource)){
                ComboPooledDataSource comboDataSource = new ComboPooledDataSource();
                comboDataSource.setDriverClass(Conf.getConf(Conf.CLASS_NAME));
                comboDataSource.setJdbcUrl(Conf.getConf(getDataSourceKey(appType,Conf.URL_SUFFIX)));
                comboDataSource.setUser(Conf.getConf(getDataSourceKey(appType,Conf.USER_SUFFIX)));
                comboDataSource.setPassword(Conf.getConf(getDataSourceKey(appType,Conf.PASSWORD_SUFFIX)));
                comboDataSource.setMaxPoolSize(5);
                comboDataSource.setMinPoolSize(1);
                comboDataSource.setInitialPoolSize(3);
                comboDataSource.setCheckoutTimeout(10000);
                comboDataSource.setTestConnectionOnCheckin(true);
                comboDataSource.setTestConnectionOnCheckout(true);
                mDataSourceMap.put(appType,comboDataSource);
                return comboDataSource;
            }
            return dataSource;
        }
    }

    private static String getDataSourceKey(CollectAppType appType,String keySuffix){
        return String.format("%s.%s",appType.name().toLowerCase(),keySuffix);
    }
}
