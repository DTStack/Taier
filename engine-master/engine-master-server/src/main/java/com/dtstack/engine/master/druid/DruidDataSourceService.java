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

    private static final ThreadLocal<Boolean> INIT_STATUS = new ThreadLocal<>();

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
     */
    public void forbidRemoveAbandoned(){
        if (!isDruidDataSource){
            return;
        }
        // 指定的状态
        INIT_STATUS.set(druidDataSource.isRemoveAbandoned());
        druidDataSource.setRemoveAbandoned(false);
    }

    /**
     * 恢复数据库默认状态
     */
    public void releaseRemoveAbandoned(){
        if (!isDruidDataSource){
            return;
        }
        Boolean status = INIT_STATUS.get();
        if (status == null){
            throw new RdosDefineException("illegal datasource status");
        }
        INIT_STATUS.remove();
        druidDataSource.setRemoveAbandoned(status);
    }


}
