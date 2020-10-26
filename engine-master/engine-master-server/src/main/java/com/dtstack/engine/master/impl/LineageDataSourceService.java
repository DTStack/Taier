package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.LineageDataSource;
import com.dtstack.engine.api.domain.LineageRealDataSource;
import com.dtstack.engine.dao.LineageColumnColumnDao;
import com.dtstack.engine.dao.LineageDataSourceDao;
import com.dtstack.engine.dao.LineageRealDataSourceDao;
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
    private LineageDataSourceDao lineageDataSourceDao;

    @Autowired
    private LineageRealDataSourceDao lineageRealDataSourceDao;

    /**
     * 新增或删除数据源
     * @param dataSourceId 数据源id
     * @param dataJson 数据源配置json
     * @param kerberosConf kerberos配置，null则不开启kerberos
     * @param sourceType 数据源类型
     * @param appType 应用类型
     */
    public void addOrUpdateDataSource(Long dataSourceId,String sourceName,String dataJson,String kerberosConf,Integer sourceType,Integer appType){
        //TODO
        //如果存在数据源则更新
        //更新后更新物理数据源
        //如果不存在数据源则添加
        //添加后添加物理数据源，如果物理数据源已经存在，检查配置是否更新。注意：如果两个应用使用了相同的物理数据源但是使用了不同的账号，依旧算同一个物理数据源
    }

    /**
     * 删除数据源
     * @param sourceId 数据源id
     * @param appType 应用类型
     */
    public void deleteDataSource(Long sourceId,Integer appType){
        //TODO
        //删除数据源时，不删除物理数据源
    }

    /**
     * 添加或更新真实数据源
     * @param dataSource 引擎数据源
     */
    public void addOrUpdateRealDataSource(LineageDataSource dataSource){
        //TODO
        //每次新增、修改逻辑数据源都要检查物理数据源是否需要对应修改。
//        物理数据源的dataJson作用不大，因为使用时，是用逻辑数据源，且不同逻辑数据源配置的用户可能不同
    }

    /**
     * 查找真实数据源源
     * @param dataSource 引擎数据源
     * @return
     */
    LineageRealDataSource getRealDataSource(LineageDataSource dataSource){
        //TODO
        //根据data source描述信息从lineage_real_data_source表中查询出真实数据源
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
