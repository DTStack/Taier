package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.LineageDataSource;
import com.dtstack.engine.api.domain.LineageRealDataSource;
import com.dtstack.engine.dao.LineageColumnColumnDao;
import com.dtstack.engine.dao.LineageTableTableDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author chener
 * @Classname LineageDataSourceService
 * @Description 数据源service
 * @Date 2020/10/23 14:43
 * @Created chener@dtstack.com
 */
@Service
public class LineageDataSourceService {
    private static final Logger logger = LoggerFactory.getLogger(LineageDataSourceService.class);

    @Autowired
    private LineageTableTableDao lineageTableTableDao;

    @Autowired
    private LineageColumnColumnDao lineageColumnColumnDao;

    /**
     * 新增或删除数据源
     * @param dataSourceId 数据源id
     * @param dataJson 数据源配置json
     * @param kerberosConf kerberos配置，null则不开启kerberos
     * @param sourceType 数据源类型
     * @param appType 应用类型
     */
    public void addOrUpdateDataSource(Long dataSourceId,String dataJson,String kerberosConf,Integer sourceType,Integer appType){
        //TODO
    }

    /**
     * 删除数据源
     * @param sourceId 数据源id
     * @param appType 应用类型
     */
    public void deleteDataSource(Long sourceId,Integer appType){
        //TODO
    }

    /**
     * 添加或更新真实数据源
     * @param dataSource 引擎数据源
     */
    public void addOrUpdateRealDataSource(LineageDataSource dataSource){
        //TODO
    }

    /**
     * 查找真实数据源源
     * @param dataSource 引擎数据源
     * @return
     */
    LineageRealDataSource getRealDataSource(LineageDataSource dataSource){
        //TODO
        return null;
    }

    /**
     *
     * @param dataSourceType 数据源类型
     * @param dataJson 数据源配置json
     * @return
     */
    LineageRealDataSource getRealDataSourceByDataJsonAndType(Integer dataSourceType,String dataJson){
        //TODO
        return null;
    }

}
