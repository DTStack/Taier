package com.dtstack.engine.master.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.dtstack.engine.common.exception.RdosDefineException;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * 修改druid数据源的自动释放连接
 * @author xinge
 */
@Component
public class DruidDataSourceService {

    private final boolean isDruidDataSource;

    private final DruidDataSource druidDataSource;


    public DruidDataSourceService(DataSource dataSource){
        if (dataSource instanceof DruidDataSource){
            isDruidDataSource = true;
            druidDataSource = (DruidDataSource) dataSource;
            return;
        }
        isDruidDataSource = false;
        druidDataSource = null;
    }

    /**
     * 禁止druid数据源后台定时任务释放长期连接
     * isDruidDataSource 为 false 时 druidDataSource必然不能为空
     */
    public void forbidRemoveAbandoned(){
        if (!isDruidDataSource || !druidDataSource.isRemoveAbandoned()){
            return;
        }
        // 指定的状态
        druidDataSource.setRemoveAbandoned(false);
    }

    /**
     * 恢复数据库默认状态
     */
    public void releaseRemoveAbandoned(){
        if (!isDruidDataSource || ! druidDataSource.isRemoveAbandoned()){
            return;
        }
        druidDataSource.setRemoveAbandoned(true);
    }


}
