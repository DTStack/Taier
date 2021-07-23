package com.dtstack.engine.datasource.service.impl.datasource;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.dtcenter.common.pager.PageResult;
import com.dtstack.engine.datasource.common.constant.FormNames;
import com.dtstack.engine.datasource.common.constant.SystemConst;
import com.dtstack.engine.datasource.common.enums.datasource.DataSourceTypeEnum;
import com.dtstack.engine.datasource.common.exception.ErrorCode;
import com.dtstack.engine.datasource.common.exception.PubSvcDefineException;
import com.dtstack.engine.datasource.common.utils.Collects;
import com.dtstack.engine.datasource.common.utils.CommonUtils;
import com.dtstack.engine.datasource.common.utils.DataSourceUtils;
import com.dtstack.engine.datasource.common.utils.Dozers;
import com.dtstack.engine.datasource.dao.bo.datasource.DsListBO;
import com.dtstack.engine.datasource.dao.bo.datasource.DsServiceListBO;
import com.dtstack.engine.datasource.dao.bo.query.DsListQuery;
import com.dtstack.engine.datasource.dao.bo.query.DsServiceListQuery;
import com.dtstack.engine.datasource.dao.mapper.datasource.DsInfoMapper;
import com.dtstack.engine.datasource.dao.po.datasource.DsImportRef;
import com.dtstack.engine.datasource.dao.po.datasource.DsInfo;
import com.dtstack.engine.datasource.facade.datasource.DatasourceFacade;
import com.dtstack.engine.datasource.param.datasource.DsListParam;
import com.dtstack.engine.datasource.param.datasource.api.DsServiceListParam;
import com.dtstack.engine.datasource.service.impl.BaseService;
import com.dtstack.engine.datasource.common.utils.PageUtil;
import com.dtstack.engine.datasource.vo.datasource.DsDetailVO;
import com.dtstack.engine.datasource.vo.datasource.DsListVO;
import com.dtstack.engine.datasource.vo.datasource.api.DsServiceListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Service
@Slf4j
public class DsInfoService extends BaseService<DsInfoMapper, DsInfo> {

    @Autowired
    private DsAuthRefService authRefService;

    @Autowired
    private DsImportRefService importRefService;

    @Autowired
    private DatasourceFacade datasourceFacade;

    @Autowired
    private DsInfoMapper dsInfoMapper;

    private static String KERBEROS_CONFIG = "kerberosConfig";

    private static String KERBEROS_DIR = "kerberosDir";

    /**
     * 数据源列表分页
     *
     * @param dsListParam
     * @return
     */
    public PageResult<List<DsListVO>> dsPage(DsListParam dsListParam) {
        DsListQuery listQuery = Dozers.convert(dsListParam, DsListQuery.class);
        listQuery.turn();
        Integer total = this.baseMapper.countDsPage(listQuery);
        if (total == 0) {
            return PageUtil.transfer(Collects.emptyList(), dsListParam, total);
        }
        List<DsListBO> dsListBOList = baseMapper.queryDsPage(listQuery);
        if (Collects.isEmpty(dsListBOList)) {
            return PageUtil.transfer(Collects.emptyList(), dsListParam, total);
        }
        List<Long> dataInfoIdList = CommonUtils.contractField(dsListBOList, "dataInfoId", Long.class);
        List<DsImportRef> importRefList = importRefService.lambdaQuery().in(DsImportRef::getDataInfoId, dataInfoIdList).list();
        Map<Long, List<DsImportRef>> importRefMap = importRefList.stream().collect(Collectors.groupingBy(DsImportRef::getDataInfoId));
        List<DsListVO> dsListVOs = Dozers.convertList(dsListBOList, DsListVO.class, (retList, target, source, destinationClass) -> {
            Integer isImport = Objects.nonNull(importRefMap.get(source.getDataInfoId())) ? SystemConst.IS_PRODUCT_AUTH
                    : SystemConst.NOT_IS_PRODUCT_AUTH;
            target.setIsImport(isImport);
            retList.add(target);
        });
        return PageUtil.transfer(dsListVOs, dsListParam, total);
    }

    /**
     * 根据数据源Id获取数据源详情
     *
     * @param dataInfoId
     * @return
     */
    public DsDetailVO dsInfoDetail(Long dataInfoId) {
        DsInfo dsInfo = lambdaQuery().eq(DsInfo::getId, dataInfoId).one();
        return Dozers.convert(dsInfo, DsDetailVO.class, (target, source, destinationClass) -> {
            String dataJson = source.getDataJson();
            JSONObject dataSourceJson = DataSourceUtils.getDataSourceJson(dataJson);
            if(DataSourceUtils.judgeOpenKerberos(dataJson) && null == dataSourceJson.getString(FormNames.PRINCIPAL)){
                JSONObject kerberosConfig = dataSourceJson.getJSONObject(FormNames.KERBEROS_CONFIG);
                dataSourceJson.put(FormNames.PRINCIPAL,kerberosConfig.getString(FormNames.PRINCIPAL));
            }
            if(DataSourceUtils.judgeOpenKerberos(source.getDataJson()) && source.getDataType().equals(DataSourceTypeEnum.KAFKA.getDataType())){
                //kafka开启了kerberos认证
                dataSourceJson.put(FormNames.AUTHENTICATION,FormNames.KERBROS);
            }
            target.setDataJson(DataSourceUtils.getEncodeDataSource(dataSourceJson,true));
            target.setDataInfoId(source.getId());
        });
    }

    /**
     * 删除一条数据源信息
     *
     * @param dataInfoId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean delDsInfo(Long dataInfoId) {
        List<Integer> list = authRefService.getCodeByDsId(dataInfoId);
        if (CollectionUtils.isNotEmpty(list)) {
            throw new PubSvcDefineException(ErrorCode.CAN_NOT_DEL_AUTH_DS);
        }
        DsInfo dsInfo = this.getOneById(dataInfoId);
        if (Objects.equals(dsInfo.getIsMeta(), 1)) {
            throw new PubSvcDefineException(ErrorCode.CAN_NOT_DEL_META_DS);
        }
        return this.getBaseMapper().deleteById(dataInfoId) > 0;
    }

    /**
     * 获取外部引入数据源分页总数
     * @param listParam
     * @return
     */
    public Integer countImportDsPage(DsServiceListParam listParam) {
        listParam.turn();
        DsServiceListQuery listQuery = new DsServiceListQuery();
        BeanUtils.copyProperties(listParam, listQuery);
        return this.baseMapper.countImportDsPage(listQuery);
    }

    /**
     * 获取外部引入数据源分页数据
     * @param listParam
     * @return
     */
    public List<DsServiceListVO> queryImportDsPage(DsServiceListParam listParam) {
        listParam.turn();
        DsServiceListQuery listQuery = new DsServiceListQuery();
        BeanUtils.copyProperties(listParam, listQuery);
        List<DsServiceListBO> serviceListBOS = this.baseMapper.queryImportDsPage(listQuery);
        return Dozers.convertList(serviceListBOS, DsServiceListVO.class, (retList, target, source, destinationClass) -> {
            DataSourceTypeEnum typeEnum = DataSourceTypeEnum.typeVersionOf(source.getDataType(), source.getDataVersion());
            target.setType(typeEnum.getVal());
            target.setOpenKerberos(DataSourceUtils.judgeOpenKerberos(source.getDataJson()));
            retList.add(target);
        });
    }

    /**
     * 获取已被产品引入的数据源分页总数
     * @param listParam
     * @return
     */
    public Integer countAppDsPage(DsServiceListParam listParam) {
        listParam.turn();
        DsServiceListQuery listQuery = new DsServiceListQuery();
        BeanUtils.copyProperties(listParam, listQuery);
        Long dtuicTenantId =  listQuery.getDsDtuicTenantId() == null ? 0L : listQuery.getDsDtuicTenantId();
        listQuery.setDsDtuicTenantId(dtuicTenantId);
        return this.baseMapper.countAppDsPage(listQuery);
    }

    /**
     * 获取已被产品引入的数据源分页数据
     * @param listParam
     * @return
     */
    public List<DsServiceListVO> queryAppDsPage(DsServiceListParam listParam) {
        listParam.turn();
        DsServiceListQuery listQuery = new DsServiceListQuery();
        BeanUtils.copyProperties(listParam, listQuery);
        Long dtuicTenantId =  listQuery.getDsDtuicTenantId() == null ? 0L : listQuery.getDsDtuicTenantId();
        listQuery.setDsDtuicTenantId(dtuicTenantId);
        List<DsServiceListBO> serviceListBOS = this.baseMapper.queryAppDsPage(listQuery);

        List<Long> dsInfoIdList = serviceListBOS.stream().filter(dataSource -> DataSourceUtils.judgeOpenKerberos(dataSource.getDataJson()))
                .map(DsServiceListBO::getDataInfoId).collect(Collectors.toList());
        Map<Long, List<DsImportRef>> kerberosCache = importRefService.getImportDsByInfoIdList(dsInfoIdList).stream()
                .collect(Collectors.groupingBy(DsImportRef::getDataInfoId));

        List<DsServiceListBO> listBOS = serviceListBOS.stream().peek(dataSource -> {
            if (kerberosCache.containsKey(dataSource.getDataInfoId())){
                JSONObject dataSourceJson = DataSourceUtils.getDataSourceJson(dataSource.getDataJson());
                List<DsImportRef> dsImportRefList = kerberosCache.get(dataSource.getDataInfoId());
                if (dsImportRefList.size() > 1) {
                    log.error("this is ditty data,dataInfoId:{}}", dataSource.getDataInfoId());
                }
                DsImportRef dsImportRef = dsImportRefList.size() == 1 ? dsImportRefList.get(0) : null;
                Long oldDataInfoId = null != dsImportRef ? dsImportRef.getOldDataInfoId() : null;
                Integer appType = null != dsImportRef ? dsImportRef.getAppType() : null;
                handleKerberosConf(dataSourceJson, oldDataInfoId, appType,dataSource.getIsMeta());
                dataSource.setDataJson(dataSourceJson.toJSONString());
            }
        }).collect(Collectors.toList());
        return Dozers.convertList(listBOS, DsServiceListVO.class, (retList, target, source, destinationClass) -> {
            DataSourceTypeEnum typeEnum = null;
            try {
                typeEnum = DataSourceTypeEnum.typeVersionOf(source.getDataType(), source.getDataVersion());
            } catch (Exception e) {
                handleOldFaultDataInfoDataType(source);
            }
            target.setType(typeEnum==null ? source.getDataTypeCode() : typeEnum.getVal());
            retList.add(target);
        });
    }

    private void handleOldFaultDataInfoDataType(DsServiceListBO source) {
        //dataType和dataVersion数据不对，需要同步修改
        DsInfo dsInfo = new DsInfo();
        dsInfo.setId(source.getDataInfoId());
        Integer dataTypeCode = source.getDataTypeCode();
        DataSourceTypeEnum dataSourceTypeEnum = DataSourceTypeEnum.valOf(dataTypeCode);
        dsInfo.setDataType(dataSourceTypeEnum.getDataType());
        dsInfo.setDataVersion(dataSourceTypeEnum.getDataVersion());
        dsInfoMapper.updateById(dsInfo);
    }

    /**
     * 通过数据源主键id获取特定数据源
     * @param dsInfoId
     * @return
     */
    public DsInfo getOneById(Long dsInfoId) {
        DsInfo dataSource = this.getById(dsInfoId);
        if (Objects.isNull(dataSource) || Objects.isNull(dataSource.getId())) {
            throw new PubSvcDefineException(ErrorCode.CAN_NOT_FIND_DATA_SOURCE);
        }
        //查询引入表判断是否是迁移的数据源
        JSONObject dataSourceJson = DataSourceUtils.getDataSourceJson(dataSource.getDataJson());
        if(DataSourceUtils.judgeOpenKerberos(dataSource.getDataJson())){
            List<DsImportRef> dsImportRefs = importRefService.getImportDsByInfoId(dsInfoId);
            if(dsImportRefs.size()>1){
                log.error("this is ditty data,dataInfoId:{}}",dataSource.getId());
            }
            DsImportRef dsImportRef = dsImportRefs.size()==1 ? dsImportRefs.get(0):null;
            if(DataSourceUtils.judgeOpenKerberos(dataSource.getDataJson())) {
                Long oldDataInfoId = null != dsImportRef ? dsImportRef.getOldDataInfoId() : null;
                Integer appType = null != dsImportRef ? dsImportRef.getAppType() : null;
                handleKerberosConf(dataSourceJson, oldDataInfoId, appType,dataSource.getIsMeta());
            }
        }
        dataSource.setDataJson(dataSourceJson.toJSONString());
        return dataSource;
    }

    /**
     * 通过数据源主键id获取特定数据源
     * @param dataInfoIdList
     * @return
     */
    public List<DsInfo> getDsInfoListByIdList(List<Long> dataInfoIdList) {
        List<DsInfo> dataSourceList = dsInfoMapper.getDsInfoListByIdList(dataInfoIdList);
        if (org.apache.commons.collections.CollectionUtils.isEmpty(dataSourceList)) {
            throw new PubSvcDefineException(ErrorCode.CAN_NOT_FIND_DATA_SOURCE);
        }
        return dataSourceList.stream().map(dataSource -> {
            JSONObject dataSourceJson = DataSourceUtils.getDataSourceJson(dataSource.getDataJson());
            if (DataSourceUtils.judgeOpenKerberos(dataSource.getDataJson())) {
                //查询引入表判断是否是迁移的数据源
                List<DsImportRef> dsImportRefs = importRefService.getImportDsByInfoId(dataSource.getId());
                if(dsImportRefs.size()>1){
                    log.error("this is ditty data,dataInfoId:{}}",dataSource.getId());
                }
                DsImportRef dsImportRef = dsImportRefs.size()==1 ? dsImportRefs.get(0):null;
                if (DataSourceUtils.judgeOpenKerberos(dataSource.getDataJson())) {
                    Long oldDataInfoId = null != dsImportRef ? dsImportRef.getOldDataInfoId() : null;
                    Integer appType = null != dsImportRef ? dsImportRef.getAppType() : null;
                    handleKerberosConf(dataSourceJson, oldDataInfoId, appType,dataSource.getIsMeta());
                }
            }
            dataSource.setDataJson(dataSourceJson.toJSONString());
            return dataSource;
        }).collect(Collectors.toList());
    }

    private void handleKerberosConf(JSONObject dataSourceJson, Long oldDataInfoId, Integer appType, Integer isMeta ) {

        JSONObject kerberosConfig = dataSourceJson.getJSONObject(KERBEROS_CONFIG);
        String principalFile = kerberosConfig.getString("principalFile");
        String principalFileStr = principalFile.substring(principalFile.lastIndexOf("/")+1);
        kerberosConfig.put("principalFile",principalFileStr);
        String krb5File = kerberosConfig.getString("java.security.krb5.conf");
        if(StringUtils.isNotBlank(krb5File)) {
            kerberosConfig.put("java.security.krb5.conf", krb5File.substring(krb5File.lastIndexOf("/") + 1));
        }
        if(null != oldDataInfoId){
            String kerberosDir;
            if(isMeta == 1){
                String remotePath = kerberosConfig.getString("remotePath");
                kerberosDir = remotePath.substring(remotePath.indexOf("CONSOLE"));
            }
            kerberosDir =  AppType.getValue(appType).name()+"_"+oldDataInfoId;
            kerberosConfig.put(KERBEROS_DIR,kerberosDir);
        }
    }

    /**
     * 通过平台数据源实例Id和appType获取全部信息
     * @param oldDataInfoId
     * @param appType
     * @return
     */
    public DsInfo queryDsByAppTypeAndOldDataInfoId(Long oldDataInfoId,Integer appType) {
        DsInfo dsInfo = this.baseMapper.queryDsByAppTypeAndOldDataInfoId(appType, oldDataInfoId);
        if ( null == dsInfo || null == dsInfo.getId()) {
            throw new PubSvcDefineException(ErrorCode.CAN_NOT_FIND_DATA_SOURCE);
        }
        JSONObject dataSourceJson = DataSourceUtils.getDataSourceJson(dsInfo.getDataJson());
        if(DataSourceUtils.judgeOpenKerberos(dsInfo.getDataJson())){
            //处理kerberosConfig
            handleKerberosConf(dataSourceJson, oldDataInfoId, appType,dsInfo.getIsMeta());
        }
        dsInfo.setDataJson(dataSourceJson.toJSONString());
        return dsInfo;
    }

    /**
     * 判断当前数据源新增或者编辑是否有重名
     * @param dsInfo
     * @return
     */
    public Boolean checkDataNameDup(DsInfo dsInfo) {
        List<DsInfo> dsInfoList = this.lambdaQuery().eq(DsInfo::getDataName, dsInfo.getDataName())
                .eq(DsInfo::getDtuicTenantId, dsInfo.getDtuicTenantId())
                .ne(Objects.nonNull(dsInfo.getId()), DsInfo::getId, dsInfo.getId()).list();
        return Collects.isNotEmpty(dsInfoList);
    }

    /**
     * 迁移数据源信息
     * @param dsInfo
     * @return
     */
    public DsInfo shiftAppDs(DsInfo dsInfo) {
        if (this.baseMapper.shiftAppDs(dsInfo) > 0) {
            return dsInfo;
        }
        throw new PubSvcDefineException(ErrorCode.SHIFT_DATASOURCE_ERROR);
    }

    /**
     * 根据产品type查询当前租户下已经授权并且引入的数据源Id
     * @param appType
     * @param dtUicTenantId
     * @return
     */
    public List<DsListBO> queryDsByAppType(Integer appType, Long dtUicTenantId) {
        return this.baseMapper.queryDsByAppType(appType, dtUicTenantId);
    }

    /**
     * 根据产品typeList查询当前租户下已经授权并且引入的数据源Id
     * @param appTypeList
     * @param dtUicTenantId
     * @return
     */
    public List<DsListBO> queryDsByAppTypeList(List<Integer> appTypeList, Long dtUicTenantId) {
        return this.baseMapper.queryDsByAppTypeList(appTypeList, dtUicTenantId);
    }
}
