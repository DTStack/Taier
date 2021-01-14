package com.dtstack.lineage.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.LineageDataSource;
import com.dtstack.engine.api.domain.LineageRealDataSource;
import com.dtstack.engine.api.dto.DataSourceDTO;
import com.dtstack.engine.api.enums.DataSourceTypeEnum;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.ComponentDao;
import com.dtstack.engine.dao.TenantDao;
import com.dtstack.lineage.bo.RdbmsDataSourceConfig;
import com.dtstack.lineage.dao.LineageDataSourceDao;
import com.dtstack.lineage.dao.LineageRealDataSourceDao;
import com.dtstack.schedule.common.enums.AppType;
import com.dtstack.schedule.common.enums.DataSourceType;
import com.dtstack.schedule.common.enums.Sort;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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

    @Resource
    private LineageDataSourceDao lineageDataSourceDao;

    @Resource
    private LineageRealDataSourceDao lineageRealDataSourceDao;

    @Resource
    private TenantDao tenantDao;

    @Resource
    private ComponentDao componentDao;

    /**
     * 新增或修改逻辑数据源
     * @param dataSourceDTO 数据源信息
     */
    @Transactional(rollbackFor = Exception.class)
    public Long addOrUpdateDataSource(DataSourceDTO dataSourceDTO){
        //如果存在数据源则更新
        //更新后更新物理数据源
        //如果不存在数据源则添加
        //添加后添加物理数据源，如果物理数据源已经存在，检查配置是否更新。注意：如果两个应用使用了相同的物理数据源但是使用了不同的账号，依旧算同一个物理数据源
        try {
            if (Objects.isNull(dataSourceDTO.getDataSourceId())){
                return addDataSource(dataSourceDTO);
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
                //更新手动添加的数据源信息时，需要修改数据源类型
                boolean changeSourceType = DataSourceType.UNKNOWN.getVal() == one.getSourceType() && Objects.nonNull(dataSourceDTO.getSourceType());
                updateDataSource(dataSourceDTO,sourceKey,one.getRealSourceId(),changeSourceType);
                return one.getId();
            }
        } catch (Exception e) {
            logger.error("新增或修改数据源异常,e:{}", ExceptionUtil.getErrorMessage(e));
            throw new RdosDefineException("新增或修改数据源异常");
        }
    }

    private void updateDataSource(DataSourceDTO dataSourceDTO,String sourceKey,Long realSourceId,boolean changeSourceType) {
        LineageDataSource dataSource = convertLineageDataSource(dataSourceDTO, sourceKey, realSourceId);
        if (changeSourceType){
            dataSource.setSourceType(dataSourceDTO.getSourceType());
        }
        lineageDataSourceDao.updateDataSource(dataSource);

    }

    private Long addDataSource(DataSourceDTO dataSourceDTO) {
        try {
            //首先根据数据源名称查询数据源，如果数据源已经存在，说明是修改手动添加的数据源的信息。
            LineageDataSource lineageDataSource = getDataSourceByParams(dataSourceDTO.getSourceType(), dataSourceDTO.getSourceName(), dataSourceDTO.getDtUicTenantId(), dataSourceDTO.getAppType());
            if (Objects.nonNull(lineageDataSource)){
                dataSourceDTO.setDataSourceId(lineageDataSource.getId());
                return addOrUpdateDataSource(dataSourceDTO);
            }
            //是否是手动添加的数据源。手动添加的数据源暂时不知道数据源类型。当然也可能一直不知道数据源类型
            boolean isCustom = dataSourceDTO.getSourceType().equals(DataSourceType.UNKNOWN.getVal());
            //生成sourceKey
            String sourceKey;
            if(!isCustom) {
                sourceKey = generateSourceKey(dataSourceDTO.getDataJson());
            }else{
                sourceKey = "custom_"+dataSourceDTO.getSourceName();
                dataSourceDTO.setDataJson("-1");
            }
            //根据sourceKey和appType查找数据源
            LineageDataSource dataSourceParam = new LineageDataSource();
            dataSourceParam.setSourceKey(sourceKey);
            dataSourceParam.setAppType(dataSourceDTO.getAppType());
            dataSourceParam.setDtUicTenantId(dataSourceDTO.getDtUicTenantId());
            dataSourceParam.setIsDeleted(0);
            List<LineageDataSource> dataSourceByParams = lineageDataSourceDao.getDataSourceByParams(dataSourceParam);
            if(CollectionUtils.isNotEmpty(dataSourceByParams)){
                //已经有了该数据源
                return dataSourceByParams.get(0).getId();
            }
            Long realSourceId = 0L;
            if(!isCustom) {
                //只有非自定义数据源才插入物理数据愿
                 realSourceId = addRealDataSource(dataSourceDTO, sourceKey);
            }
            //插入逻辑数据源
            //查询组件
            LineageDataSource dataSource = convertLineageDataSource(dataSourceDTO, sourceKey, realSourceId);
            lineageDataSourceDao.insertDataSource(dataSource);
            return dataSource.getId();
        } catch (Exception e) {
            logger.error("新增数据源异常,dataSource:{},e:{}",JSON.toJSONString(dataSourceDTO),ExceptionUtil.getTaskLogError(e));
            throw new RdosDefineException("新增数据源异常");
        }
    }

    private LineageDataSource convertLineageDataSource(DataSourceDTO dataSourceDTO, String sourceKey, Long realSourceId) {
        Long tenantId = tenantDao.getIdByDtUicTenantId(dataSourceDTO.getDtUicTenantId());
        Integer componentId =  componentDao.getIdByTenantIdComponentType(tenantId, dataSourceDTO.getSourceType());
        LineageDataSource dataSource = new LineageDataSource();
        BeanUtils.copyProperties(dataSourceDTO,dataSource);
        dataSource.setComponentId(null == componentId ? -1 : componentId);
        //有组件则为内部数据源1，否则为外部数据源0
        if(StringUtils.isBlank(dataSourceDTO.getKerberosConf())){
            dataSource.setKerberosConf("-1");
            dataSource.setOpenKerberos( 0 );
        }else{
            dataSource.setKerberosConf(dataSourceDTO.getKerberosConf());
            dataSource.setOpenKerberos( 1 );
        }
        dataSource.setInnerSource(null == componentId ? 1 : 0);
        dataSource.setSourceKey(sourceKey);
        dataSource.setRealSourceId(realSourceId);
        dataSource.setAppSourceId(-1);
        dataSource.setDtUicTenantId(dataSourceDTO.getDtUicTenantId());
        return dataSource;
    }

    private String generateSourceKey(String dataJson) {
        if(null == dataJson){
            throw new RdosDefineException("dataJson不能为空");
        }
        JSONObject jsonObject = JSON.parseObject(dataJson);
        RdbmsDataSourceConfig sourceConfig = new RdbmsDataSourceConfig();
        String jdbcUrl = jsonObject.getString("jdbcUrl");
        if(jdbcUrl.contains("impala") && jdbcUrl.contains(";")){
             jdbcUrl = jdbcUrl.substring(0, jdbcUrl.indexOf(";"));
        }
        sourceConfig.setJdbc(jdbcUrl);
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
            realDataSource.setKerberosConf(StringUtils.isBlank(dataSourceDTO.getKerberosConf())
                    ? "-1" : dataSourceDTO.getKerberosConf());
            realDataSource.setOpenKerberos( StringUtils.isBlank(dataSourceDTO.getKerberosConf()) ? 0 : 1);
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
    public PageResult<List<LineageDataSource>> pageQueryDataSourceByAppType(Integer appType,Integer currentPage,Integer pageSize){

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
    public LineageDataSource getDataSourceById(Long id){

        return lineageDataSourceDao.getOne(id);
    }


    /**
     * @author zyd
     * @Description 根据id列表批量查询逻辑数据源信息
     * @Date 2020/10/30 2:25 下午
     * @param ids:
     * @return: com.dtstack.engine.api.domain.LineageDataSource
     **/
    public List<LineageDataSource> getDataSourcesByIdList(List<Long> ids){

        if(CollectionUtils.isEmpty(ids)){
            throw new RdosDefineException("数据源id列表不能为空");
        }
        return lineageDataSourceDao.getDataSourcesByIdList(ids);
    }



    /**
     * @author zyd
     * @Description 根据dt租户id和数据源类型查找数据源
     * @Date 2020/11/11 4:32 下午
     * @param dtUicTenantId:
     * @param sourceType:
     * @return: com.dtstack.engine.api.domain.LineageDataSource
     **/
    public LineageDataSource getDataSourceByParams(Integer sourceType,String sourceName,Long dtUicTenantId,
                                                   Integer appType){

        LineageDataSource lineageDataSource = new LineageDataSource();
        lineageDataSource.setSourceName(sourceName);
        lineageDataSource.setSourceType(sourceType);
        lineageDataSource.setDtUicTenantId(dtUicTenantId);
        lineageDataSource.setAppType(appType);
        lineageDataSource.setIsDeleted(0);
        List<LineageDataSource> dataSourceByParams = lineageDataSourceDao.getDataSourceByParams(lineageDataSource);
        if(CollectionUtils.isNotEmpty(dataSourceByParams)){
            return dataSourceByParams.get(0);
        }else{
            //未知数据源（手动添加血缘时添加的数据源）需要插入
            if (DataSourceType.UNKNOWN.getVal() == sourceType){
                DataSourceDTO dataSourceDTO = new DataSourceDTO();
                dataSourceDTO.setAppType(appType);
                dataSourceDTO.setDataJson(null);
                dataSourceDTO.setSourceName(sourceName);
                dataSourceDTO.setKerberosConf(null);
                dataSourceDTO.setDtUicTenantId(dtUicTenantId);
                dataSourceDTO.setSourceType(DataSourceType.UNKNOWN.getVal());
                Long id = addOrUpdateDataSource(dataSourceDTO);
                return getDataSourceById(id);
            }
            return null;
        }
    }

    public void acquireOldDataSourceList(List<DataSourceDTO> dataSourceDTOs) {

        if(CollectionUtils.isEmpty(dataSourceDTOs)){
            throw new RdosDefineException("数据源列表不能为空");
        }
        if (dataSourceDTOs.size()>200){
            throw new RdosDefineException("请分批执行");
        }
        logger.info("appType:{}类型,租户:{}一共,{}个数据源",dataSourceDTOs.get(0).getAppType(),
                dataSourceDTOs.get(0).getDtUicTenantId(),dataSourceDTOs.size());
        for (DataSourceDTO dataSourceDTO : dataSourceDTOs) {
            addDataSource(dataSourceDTO);
        }
    }

    public void synIdeDataSourceList() {

        //遍历租户
        try {
            List<Long> dtUicTenantIds = tenantDao.listAllDtUicTenantIds();
            int total = 0;
            for (Long dtUicTenantId : dtUicTenantIds) {
                Long tenantId = tenantDao.getIdByDtUicTenantId(dtUicTenantId);
                List<Component> componentList = componentDao.listByTenantId(tenantId);
                for (Component component : componentList) {
                    //是数据源的组件才插入
                    if(DataSourceTypeEnum.getAllTypeCodes().contains(component.getComponentTypeCode())){
                        total++;
                        DataSourceDTO dataSourceDTO = new DataSourceDTO();
                        dataSourceDTO.setDtUicTenantId(dtUicTenantId);
                        dataSourceDTO.setDataJson(component.getComponentConfig());
                        dataSourceDTO.setSourceName("ideDataSource_"+component.getComponentName());
                        dataSourceDTO.setAppType(AppType.RDOS.getType());
                        //数据源类型code统一转换
                        int code = DataSourceTypeEnum.getByCode(component.getComponentTypeCode()).getTypeCode();
                        DataSourceType byName = DataSourceType.getSourceType(code);
                        if(byName==null){
                            throw new RdosDefineException("数据源类型不匹配");
                        }
                        dataSourceDTO.setSourceType(byName.getVal());
                        addDataSource(dataSourceDTO);
                    }
                }
            }
            logger.info("符合条件的离线数据源个数:{}",total);
        } catch (Exception e) {
            logger.error(this.getClass().getName()+"-synIdeDataSourceList-异常,e:{}",ExceptionUtil.stackTrack());
            throw new RdosDefineException("同步离线数据源数据异常");
        }


    }
}
