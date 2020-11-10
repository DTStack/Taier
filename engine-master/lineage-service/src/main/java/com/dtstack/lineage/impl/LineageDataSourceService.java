package com.dtstack.lineage.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.LineageDataSource;
import com.dtstack.engine.api.domain.LineageRealDataSource;
import com.dtstack.engine.api.dto.DataSourceDTO;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.ComponentDao;
import com.dtstack.engine.dao.TenantDao;
import com.dtstack.lineage.bo.RdbmsDataSourceConfig;
import com.dtstack.lineage.dao.LineageDataSourceDao;
import com.dtstack.lineage.dao.LineageRealDataSourceDao;
import com.dtstack.schedule.common.enums.Sort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Autowired
    private TenantDao tenantDao;


    @Autowired
    private ComponentDao componentDao;

    /**
     * 新增或修改逻辑数据源
     * @param dataSourceDTO 数据源信息
     */
    public void addOrUpdateDataSource(DataSourceDTO dataSourceDTO){
        //如果存在数据源则更新
        //更新后更新物理数据源
        //如果不存在数据源则添加
        //添加后添加物理数据源，如果物理数据源已经存在，检查配置是否更新。注意：如果两个应用使用了相同的物理数据源但是使用了不同的账号，依旧算同一个物理数据源
        try {
            if (Objects.isNull(dataSourceDTO.getDataSourceId())){
                addDataSource(dataSourceDTO);
            }else {
                //更新数据源
                LineageDataSource one = lineageDataSourceDao.getOne(dataSourceDTO.getDataSourceId());
                if (Objects.isNull(one)){
                    throw new RdosDefineException("数据源不存在");
                }
                if (!one.getAppType().equals(dataSourceDTO.getAppType())){
                    throw new RdosDefineException("数据源不存在");
                }
                String sourceKey = generateSourceKey(dataSourceDTO.getDataJson());
                if(!one.getSourceKey().equals(sourceKey)){
                    throw new RdosDefineException("jdbc.url中ip和端口不能修改");
                }
                updateDataSource(dataSourceDTO,sourceKey,one.getRealSourceId());
            }
        } catch (Exception e) {
            logger.error("新增或修改数据源异常,e:{}", ExceptionUtil.getErrorMessage(e));
            throw new RdosDefineException("新增或修改数据愿异常");
        }
    }

    private void updateDataSource(DataSourceDTO dataSourceDTO,String sourceKey,Long realSourceId) {

        LineageDataSource dataSource = convertLineageDataSource(dataSourceDTO, sourceKey, realSourceId);
        lineageDataSourceDao.updateDataSource(dataSource);

    }

    private void addDataSource(DataSourceDTO dataSourceDTO) {
        //生成sourceKey
        String sourceKey = generateSourceKey(dataSourceDTO.getDataJson());
        //插入物理数据愿
        Long realSourceId =  addRealDataSource(dataSourceDTO,sourceKey);
        //插入逻辑数据源
        //查询组件
        LineageDataSource dataSource = convertLineageDataSource(dataSourceDTO, sourceKey, realSourceId);
        lineageDataSourceDao.insertDataSource(dataSource);
    }

    private LineageDataSource convertLineageDataSource(DataSourceDTO dataSourceDTO, String sourceKey, Long realSourceId) {
        Long tenantId = tenantDao.getIdByDtUicTenantId(dataSourceDTO.getDtUicTenantId());
        Integer componentId =  componentDao.getIdByTenantIdComponentType(tenantId, dataSourceDTO.getSourceType());
        LineageDataSource dataSource = new LineageDataSource();
        BeanUtils.copyProperties(dataSourceDTO,dataSource);
        dataSource.setComponentId(componentId);
        //有组件则为内部数据源1，否则为外部数据源0
        dataSource.setInnerSource(null == componentId ? 1 : 0);
        dataSource.setOpenKerberos(null == dataSourceDTO.getKerberosConf() ? 0:1);
        dataSource.setSourceKey(sourceKey);
        dataSource.setRealSourceId(realSourceId);
        dataSource.setDtUicTenantId(dataSourceDTO.getDtUicTenantId());
        return dataSource;
    }

    private String generateSourceKey(String dataJson) {
        if(null == dataJson){
            throw new RdosDefineException("dataJson不能为空");
        }
        JSONObject jsonObject = JSON.parseObject(dataJson);
        RdbmsDataSourceConfig sourceConfig = new RdbmsDataSourceConfig();
        sourceConfig.setJdbc(jsonObject.getString("jdbc.url"));
        String sourceKey = sourceConfig.generateRealSourceKey();
        if(null == sourceKey){
            throw new RdosDefineException("dataJson格式有误");
        }
        return sourceKey;
    }

    /**
     * @author zyd
     * @Description 新增真实数据源
     * @Date 2020/10/30 11:52 上午
     * @param dataSourceDTO:
     * @param sourceKey:
     * @return: java.lang.Long
     **/
    private Long addRealDataSource(DataSourceDTO dataSourceDTO,String sourceKey) {
        //先根据sourceKey查询物理数据源是否存在
        LineageRealDataSource oneBySourceKey = getRealDataSource(sourceKey);
        Long realSourceId;
        if(null == oneBySourceKey){
            //不存在则新增
            LineageRealDataSource realDataSource = new LineageRealDataSource();
            realDataSource.setDataJason(dataSourceDTO.getDataJson());
            realDataSource.setKerberosConf(dataSourceDTO.getKerberosConf());
            realDataSource.setOpenKerberos( null == dataSourceDTO.getKerberosConf() ? 0 : 1);
            realDataSource.setSourceKey(sourceKey);
            realDataSource.setSourceName(dataSourceDTO.getSourceName());
            realDataSource.setSourceType(dataSourceDTO.getSourceType());
            lineageRealDataSourceDao.addRealDataSource(realDataSource);
            realSourceId = realDataSource.getId();

        }else{
            realSourceId = oneBySourceKey.getId();
        }
        return realSourceId;
    }

    /**
     * 删除逻辑数据源
     * @param sourceId 数据源id
     * @param appType 应用类型
     */
    public void deleteDataSource(Long sourceId,Integer appType){

        //删除数据源时，不删除物理数据源
        LineageDataSource one = lineageDataSourceDao.getOne(sourceId);
        if(null == one) {
            throw new RdosDefineException("该数据源不存在或已经被删除了");
        }
        if(!appType.equals(one.getAppType())){
            throw new RdosDefineException("不可以删除其它应用的数据源");
        }
        lineageDataSourceDao.deleteDataSource(sourceId);
    }


    /**
     * @author zyd
     * @Description 根据sourceId和appType查询数据源信息
     * @Date 2020/10/30 4:03 下午
     * @param sourceId: 数据源id
     * @param appType: 应用类型
     * @return: com.dtstack.engine.api.domain.LineageDataSource
     **/
    public LineageDataSource getDataSourceByIdAndAppType(Long sourceId,Integer appType){

        return lineageDataSourceDao.getOneByAppTypeAndId(sourceId,appType);
    }

    /**
     * 查找真实数据源源
     * @param sourceKey sourceKey
     * @return
     */
    private LineageRealDataSource getRealDataSource(String sourceKey){
        //根据data source描述信息从lineage_real_data_source表中查询出真实数据源
        return lineageRealDataSourceDao.getOneBySourceKey(sourceKey);
    }



    /**
     * @author zyd
     * @Description 根据appType分页查询逻辑数据源列表
     * @Date 2020/10/30 11:55 上午
     * @param appType:
     * @return: java.util.List<com.dtstack.engine.api.domain.LineageDataSource>
     **/
    PageResult<List<LineageDataSource>> pageQueryDataSourceByAppType(Integer appType,Integer currentPage,Integer pageSize){

        PageQuery<LineageDataSource> pageQuery = new PageQuery<>(currentPage,pageSize,"gmt_modified", Sort.DESC.name());
        LineageDataSource dataSource = new LineageDataSource();
        dataSource.setAppType(appType);
        dataSource.setIsDeleted(0);
        Integer count = lineageDataSourceDao.generalCount(dataSource);
        List<LineageDataSource> dataSourceList = new ArrayList<>();
        if(count>0){
            pageQuery.setModel(dataSource);
            dataSourceList = lineageDataSourceDao.generalQuery(pageQuery);
        }
        return new PageResult<>(dataSourceList,count,pageQuery);
    }


    /**
     * @author zyd
     * @Description 根据id查询逻辑数据源信息
     * @Date 2020/10/30 2:25 下午
     * @param id:
     * @return: com.dtstack.engine.api.domain.LineageDataSource
     **/
    LineageDataSource getDataSourceById(Long id){

        return lineageDataSourceDao.getOne(id);
    }


}
