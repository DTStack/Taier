package com.dtstack.engine.datasource.facade.datasource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dtstack.dtcenter.common.pager.PageResult;
import com.dtstack.engine.datasource.dao.po.datasource.*;
import com.dtstack.engine.datasource.param.datasource.api.*;
import com.dtstack.engine.datasource.param.datasource.*;
import com.dtstack.engine.datasource.service.impl.datasource.*;
import com.dtstack.engine.datasource.vo.datasource.*;
import com.dtstack.engine.datasource.vo.datasource.api.*;
import com.dtstack.engine.datasource.common.constant.FormNames;
import com.dtstack.engine.datasource.common.enums.datasource.DataSourceTypeEnum;
import com.dtstack.engine.datasource.common.exception.ErrorCode;
import com.dtstack.engine.datasource.common.exception.PubSvcDefineException;
import com.dtstack.engine.datasource.common.utils.Collects;
import com.dtstack.engine.datasource.common.utils.CommonUtils;
import com.dtstack.engine.datasource.common.utils.DataSourceUtils;
import com.dtstack.engine.datasource.common.utils.Dozers;
import com.dtstack.engine.datasource.common.utils.datakit.Asserts;
import com.dtstack.engine.datasource.common.utils.PageUtil;
import com.google.common.collect.Lists;
import dt.insight.plat.lang.base.Strings;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Slf4j
@Service
public class ApiServiceFacade {

    private static String MARK_D = "//";

    private static String MARK_O = "/";

    private static String ORACLE_MARK_D = "@";

    private static String ORACLE_MARK_O = ":";


    @Autowired
    private DsInfoService dsInfoService;

    @Autowired
    private DsAppMappingService appMappingService;

    @Autowired
    private DsImportRefService importRefService;

    @Autowired
    private DsAuthRefService authRefService;

    @Autowired
    private DatasourceFacade datasourceFacade;


    /**
     * 外部-产品已被引入的数据源信息分页列表(需要做个性化处理)
     * @param listParam
     * @return
     */
    public PageResult<List<DsServiceListVO>> appDsPage(DsServiceListParam listParam) {
        Integer total = dsInfoService.countAppDsPage(listParam);
        if (total == 0) {
            return PageUtil.transfer(Collects.emptyList(), listParam, total);
        }
        List<DsServiceListVO> content = dsInfoService.queryAppDsPage(listParam);
        content.forEach(e -> {
            e.setLinkJson(DataSourceUtils.getDataSourceJsonStr(e.getLinkJson()));
        });
        return PageUtil.transfer(content, listParam, total);
    }

    /**
     * 外部引入数据源分页列表
     *
     * @param listParam
     * @return
     */
    public PageResult<List<DsServiceListVO>> importDsPage(DsServiceListParam listParam) {
        if (Collects.isEmpty(listParam.getDataTypeList())) {
            // 当参数中类型list为空时查询该产品下的所有数据库
            List<DsAppMapping> appMappingList = appMappingService.groupListByAppType(listParam.getAppType());
            List<String> dataTypeList = CommonUtils.contractField(appMappingList, "dataType", String.class);
            listParam.setDataTypeList(dataTypeList);
        }
        Long projectId =  listParam.getProjectId() == null ? -1L : listParam.getProjectId();
        listParam.setProjectId(projectId);
        Long dtuicTenantId =  listParam.getDsDtuicTenantId() == null ? 0L : listParam.getDsDtuicTenantId();
        listParam.setDsDtuicTenantId(dtuicTenantId);
        Integer total = dsInfoService.countImportDsPage(listParam);
        if (total == 0) {
            return PageUtil.transfer(Collects.emptyList(), listParam, total);
        }
        List<DsServiceListVO> content = dsInfoService.queryImportDsPage(listParam);
        content.forEach(e -> {
            e.setLinkJson(DataSourceUtils.getDataSourceJsonStr(e.getLinkJson()));
        });
        return PageUtil.transfer(content, listParam, total);
    }

    /**
     * 引入数据源下拉数据源类型列表
     *
     * @param appType
     * @return
     */
    public List<String> dsTypeListByProduct(Integer appType) {
        List<DsAppMapping> appMappingList = appMappingService.groupListByAppType(appType);
        return CommonUtils.contractField(appMappingList, "dataType", String.class);
    }

    /**
     * 外部-确认引入
     *
     * @param importParam
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean productImportDs(ProductImportParam importParam) {

        Long dtUicTenantId = null == importParam.getDtUicTenantId() ? 0 : importParam.getDtUicTenantId();
        Long projectId = null == importParam.getProjectId() ? -1 : importParam.getProjectId();
        List<DsImportRef> importRefList = importRefService.lambdaQuery()
                .eq(DsImportRef::getAppType, importParam.getAppType())
                .eq(DsImportRef::getDtUicTenantId,dtUicTenantId)
                .eq(DsImportRef::getProjectId,projectId)
                .in(DsImportRef::getDataInfoId, importParam.getDataInfoIdList()).list();
        if (Collects.isNotEmpty(importRefList)) {
            throw new PubSvcDefineException(ErrorCode.IMPORT_DATA_SOURCE_DUP_FAIL);
        }
        List<DsAuthRef> authRefList = authRefService.lambdaQuery()
                .eq(DsAuthRef::getAppType, importParam.getAppType())
                .eq(DsAuthRef::isDeleted,0)
                .in(DsAuthRef::getDataInfoId, importParam.getDataInfoIdList()).list();
        if(authRefList.size()< importParam.getDataInfoIdList().size()){
            throw new PubSvcDefineException(ErrorCode.IMPORT_DATA_SOURCE_AUTH_FAIL);
        }
        List<DsImportRef> dsImportRefs = new ArrayList<>();
        importParam.getDataInfoIdList().forEach(dataInfoId -> {
            validImportDs(dataInfoId, importParam.getAppType());
            DsImportRef dsImportRef = new DsImportRef();
            dsImportRef.setDataInfoId(dataInfoId);
            dsImportRef.setAppType(importParam.getAppType());
            dsImportRef.setProjectId(projectId);
            dsImportRef.setDtUicTenantId(dtUicTenantId);
            dsImportRef.setCreateUserId(importParam.getDtuicUserId());
            dsImportRefs.add(dsImportRef);
        });
        return importRefService.saveBatch(dsImportRefs);
    }

    /**
     * 校验导入数据源
     * @param dataInfoId
     * @param appType
     */
    private void validImportDs(Long dataInfoId, Integer appType) {
        DsInfo dsInfo = dsInfoService.getOneById(dataInfoId);
        DsAppMapping appMapping = appMappingService.lambdaQuery().eq(DsAppMapping::getDataType, dsInfo.getDataType())
                .eq(Strings.isNotBlank(dsInfo.getDataVersion()), DsAppMapping::getDataVersion, dsInfo.getDataVersion())
                .eq(DsAppMapping::getAppType, appType)
                .one();
        if (Objects.isNull(appMapping) || Objects.isNull(appMapping.getId())) {
            // 该数据源不属于该产品，无法授权
            throw new PubSvcDefineException(ErrorCode.IMPORT_DS_NOT_MATCH_APP);
        }
    }

    /**
     * 取消引入
     *
     * @param importParam
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean productCancelDs(ProductImportParam importParam) {
        return importRefService.cancelImport(importParam.getDataInfoIdList(), importParam.getAppType(),
                importParam.getDtUicTenantId(),importParam.getProjectId());
    }

    /**
     * 外部-创建Meta数据源
     *
     * @param createDsParam
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public DsShiftReturnVO createMetaDs(CreateDsParam createDsParam) {
        DataSourceVO dataSourceVO = convertParamToVO(createDsParam);
        //方法复用
        Long dataSourceId = datasourceFacade.addOrUpdateSource(dataSourceVO, dataSourceVO.getProjectId(), createDsParam.getUserId(), createDsParam.getDtuicTenantId());
        // 保存数据源和产品引入关系
        DsImportRef importRef = new DsImportRef();
        importRef.setDataInfoId(dataSourceId);
        importRef.setAppType(createDsParam.getAppType());
        importRefService.save(importRef);

        DsShiftReturnVO vo = new DsShiftReturnVO();
        vo.setDataInfoId(dataSourceId);
        vo.setDataName(createDsParam.getDataName());
        return vo;
    }

    /**
     * 外部-迁移外部产品数据源信息接口
     *
     * @param paramList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public List<DsShiftReturnVO> shiftAppDs(List<CreateDsParam> paramList) {
        // 数据迁移不需要校验连通性，并且直接通过sql插入数据
        List<DsAuthRef> dsAuthRefList = Lists.newArrayList();
        List<DsImportRef> dsImportRefList = Lists.newArrayList();
        List<DsInfo> insertDsInfoList = Lists.newArrayList();
        for (CreateDsParam createDsParam : paramList) {
            DsInfo dsInfo = dsInfoConvert(createDsParam);
            if (dsInfoService.checkDataNameDup(dsInfo)) {
                throw new PubSvcDefineException(ErrorCode.DATASOURCE_DUP_NAME);
            }
            DsInfo insertDsInfo = dsInfoService.shiftAppDs(dsInfo);
            DsAuthRef authRef = new DsAuthRef();
            authRef.setDataInfoId(dsInfo.getId());
            authRef.setAppType(createDsParam.getAppType());
            DsImportRef importRef = new DsImportRef();
            importRef.setDataInfoId(dsInfo.getId());
            importRef.setAppType(createDsParam.getAppType());
            dsAuthRefList.add(authRef);
            dsImportRefList.add(importRef);
            insertDsInfoList.add(insertDsInfo);
        }

        authRefService.saveBatch(dsAuthRefList);
        importRefService.saveBatch(dsImportRefList);

        return insertDsInfoList.stream().map(e -> {
            DsShiftReturnVO returnVO = new DsShiftReturnVO();
            returnVO.setDataName(e.getDataName());
            returnVO.setDataInfoId(e.getId());
            return returnVO;
        }).collect(Collectors.toList());
    }

    /**
     * 参数转换
     *
     * @param createDsParam
     * @return
     */
    private DsInfo dsInfoConvert(CreateDsParam createDsParam) {
        DsInfo dsInfo = new DsInfo();
        DataSourceTypeEnum typeEnum = DataSourceTypeEnum.valOf(createDsParam.getType());
        Asserts.notNull(typeEnum, ErrorCode.CAN_NOT_FITABLE_SOURCE_TYPE);
        dsInfo.setDataType(typeEnum.getDataType());
        dsInfo.setDataVersion(typeEnum.getDataVersion());
        dsInfo.setDataName(createDsParam.getDataName());
        dsInfo.setDataDesc(createDsParam.getDataDesc());
        if (Strings.isNotBlank(createDsParam.getDataJson())) {
            JSONObject dataJsonObject = DataSourceUtils.getDataSourceJson(createDsParam.getDataJson());
            String linkInfo = datasourceFacade.getDataSourceLinkInfo(typeEnum.getDataType(), typeEnum.getDataVersion(), dataJsonObject);
            dsInfo.setDataJson(DataSourceUtils.getEncodeDataSource(dataJsonObject, true));
            dsInfo.setLinkJson(DataSourceUtils.getEncodeDataSource(linkInfo, true));
        }
        dsInfo.setStatus(createDsParam.getStatus());
        dsInfo.setIsMeta(createDsParam.getIsMeta());
        dsInfo.setTenantId(createDsParam.getDsTenantId());
        dsInfo.setDtuicTenantId(createDsParam.getDsDtuicTenantId());
        dsInfo.setGmtCreate(createDsParam.getGmtCreate());
        dsInfo.setGmtModified(createDsParam.getGmtModified());
        dsInfo.setCreateUserId(createDsParam.getCreateUserId());
        dsInfo.setModifyUserId(createDsParam.getModifyUserId());
        return dsInfo;
    }


    /**
     * 外部-通过数据源实例Id获取全部信息
     *
     * @param dsInfoId
     * @return
     */
    public DsServiceInfoVO getDsInfoById(Long dsInfoId) {
        DsInfo dsInfo = dsInfoService.getOneById(dsInfoId);
        return Dozers.convert(dsInfo, DsServiceInfoVO.class, (t, s, c) -> {
            DataSourceTypeEnum typeEnum = DataSourceTypeEnum.typeVersionOf(s.getDataType(), s.getDataVersion());
            Asserts.notNull(typeEnum, ErrorCode.CAN_NOT_FITABLE_SOURCE_TYPE);
            t.setType(typeEnum.getVal());
            t.setDataInfoId(s.getId());
            t.setLinkJson(DataSourceUtils.getDataSourceJsonStr(s.getLinkJson()));
            t.setDataJson(DataSourceUtils.getDataSourceJsonStr(s.getDataJson()));
        });
    }



    /**
     * 外部-通过平台数据源实例Id和appType获取全部信息
     *
     * @param oldDataInfoId
     * @param appType
     * @return
     */
    public DsServiceInfoVO getDsInfoByOldDataInfoId(Long oldDataInfoId,Integer appType) {

        DsInfo dsInfo = dsInfoService.queryDsByAppTypeAndOldDataInfoId(oldDataInfoId,appType);
        return Dozers.convert(dsInfo, DsServiceInfoVO.class, (t, s, c) -> {
            DataSourceTypeEnum typeEnum = DataSourceTypeEnum.typeVersionOf(s.getDataType(), s.getDataVersion());
            Asserts.notNull(typeEnum, ErrorCode.CAN_NOT_FITABLE_SOURCE_TYPE);
            t.setType(typeEnum.getVal());
            t.setDataInfoId(s.getId());
            t.setLinkJson(DataSourceUtils.getDataSourceJsonStr(s.getLinkJson()));
            t.setDataJson(s.getDataJson());
        });
    }


    /**
     * 转换成datasourceVo
     * @param createDsParam
     * @return
     */
    private DataSourceVO convertParamToVO(CreateDsParam createDsParam) {
        DataSourceVO dataSourceVO = new DataSourceVO();
        dataSourceVO.setUserId(createDsParam.getUserId());
        dataSourceVO.setDtuicTenantId(createDsParam.getDsDtuicTenantId());
        dataSourceVO.setGmtCreate(createDsParam.getGmtCreate());
        dataSourceVO.setGmtModified(createDsParam.getGmtModified());
        dataSourceVO.setTenantId(createDsParam.getDsTenantId());
        dataSourceVO.setDataName(createDsParam.getDataName());
        dataSourceVO.setDataDesc(createDsParam.getDataDesc());
        dataSourceVO.setType(createDsParam.getType());

        DataSourceTypeEnum typeEnum = DataSourceTypeEnum.valOf(createDsParam.getType());
        Asserts.notNull(typeEnum, ErrorCode.CAN_NOT_FITABLE_SOURCE_TYPE);
        dataSourceVO.setDataType(typeEnum.getDataType());
        dataSourceVO.setDataVersion(typeEnum.getDataVersion());
        if (Strings.isNotBlank(createDsParam.getDataJson())) {
            dataSourceVO.setDataJson(DataSourceUtils.getDataSourceJson(createDsParam.getDataJson()));
        }
        dataSourceVO.setAppTypeList(Arrays.asList(createDsParam.getAppType()));
        dataSourceVO.setIsMeta(createDsParam.getIsMeta());
        return dataSourceVO;
    }

    /**
     * 修改控制台中心数据源集群
     * 控制台修改数据源信息参数 目前只做到修改JDBC相关信息，kerberos和SFTP信息需要后续技术方案确定后再进行开发
     * 包括jdbcUrl, username, password
     * @param consoleParam
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public List<DsShiftReturnVO> editConsoleDs(EditConsoleParam consoleParam) {
        DataSourceTypeEnum typeEnum = DataSourceTypeEnum.valOf(consoleParam.getType());
        boolean isOracle = Objects.equals(typeEnum.getVal(), DataSourceTypeEnum.Oracle.getVal());
        List<DsInfo> dsInfoList = dsInfoService.lambdaQuery()
                .in(DsInfo::getDtuicTenantId, consoleParam.getDsDtuicTenantIdList())
                .eq(DsInfo::getIsMeta, 1)
                .eq(DsInfo::getDataType, typeEnum.getDataType())
                .eq(Strings.isNotBlank(typeEnum.getDataVersion()), DsInfo::getDataVersion, typeEnum.getDataVersion())
                .list();
        if (Collects.isEmpty(dsInfoList)) {
            log.info("本次控制台修改数据源信息为空, 无法进行修改. dsDtuicTenantIdList: [{}], type: [{}]", JSON.toJSONString(consoleParam.getDsDtuicTenantIdList()), consoleParam.getType());
            return null;
        }
        List<Long> dsInfoIds = CommonUtils.contractField(dsInfoList, "id", Long.class);
        log.info("本次控制台修改涉及到的数据源为 dsInfoIds: [{}]", dsInfoIds);
        List<DsInfo> editDsInfoList = Lists.newArrayList();
        for (DsInfo dsInfo : dsInfoList) {
            if (Strings.isNotBlank(dsInfo.getDataJson())) {
                JSONObject dataJson = DataSourceUtils.getDataSourceJson(dsInfo.getDataJson());
                JSONObject linkDataJson = DataSourceUtils.getDataSourceJson(dsInfo.getLinkJson());
                String editJdbcUrl = editJdbcUrl(CommonUtils.getStrFromJson(dataJson, FormNames.JDBC_URL), consoleParam.getJdbcUrl(), isOracle);
                editJsonObjectField(dataJson, FormNames.JDBC_URL, editJdbcUrl, String.class);
                editJsonObjectField(dataJson, FormNames.USERNAME, consoleParam.getUsername(), String.class);
                editJsonObjectField(dataJson, FormNames.PASSWORD, consoleParam.getPassword(), String.class);

                editJsonObjectField(linkDataJson, FormNames.JDBC_URL, editJdbcUrl, String.class);
                editJsonObjectField(linkDataJson, FormNames.USERNAME, consoleParam.getUsername(), String.class);
                dsInfo.setDataJson(DataSourceUtils.getEncodeDataSource(dataJson, true));
                dsInfo.setLinkJson(DataSourceUtils.getEncodeDataSource(linkDataJson, true));
                // 测试联通性
                if (!datasourceFacade.checkConnectByDsInfo(dsInfo)) {
                    throw new PubSvcDefineException(ErrorCode.CONSOLE_EDIT_CAN_NOT_CONNECT);
                }
                editDsInfoList.add(dsInfo);
            }
        }
        dsInfoService.updateBatchById(editDsInfoList);

        return editDsInfoList.stream().map(e -> {
            DsShiftReturnVO vo = new DsShiftReturnVO();
            vo.setDataInfoId(e.getId());
            vo.setDataName(e.getDataName());
            return vo;
        }).collect(Collectors.toList());
    }


    private <T> void editJsonObjectField(JSONObject dataJson, String key, T data, Class<T> clazz) {
        if (Objects.isNull(data)) {
            return;
        }
        dataJson.put(key, data);
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean rollDsInfoById(RollDsParam rollDsParam) {
        DsInfo dsInfo = dsInfoService.getOneById(rollDsParam.getDataInfoId());
        if (Objects.equals(dsInfo.getIsMeta(), 0)) {
            log.info("API服务调用无法删除非默认数据源, dsId: [{}]", dsInfo.getId());
            throw new PubSvcDefineException(ErrorCode.API_CANT_DEL_NOT_META_DS);
        }
        if (!Objects.equals(dsInfo.getDtuicTenantId(), rollDsParam.getDsDtuicTenantId()) ||
                !Objects.equals(dsInfo.getTenantId(), rollDsParam.getDsTenantId())) {
            // 数据源的租户id无法对应上
            throw new PubSvcDefineException(ErrorCode.API_CANT_DEL_NOT_TENANT);
        }

        if (dsInfoService.removeById(rollDsParam.getDataInfoId())) {
            // 数据源信息删除成功, 继续删除引用信息
            importRefService.remove(Wrappers.<DsImportRef>update().eq("data_info_id", rollDsParam.getDataInfoId()));
            authRefService.remove(Wrappers.<DsAuthRef>update().eq("data_info_id", rollDsParam.getDataInfoId()));
            return true;
        }
        return false;
    }

    /**
     * 分割jdbcUrl原字符串，生成DTO
     * @param originStr
     * @return
     */
    public JdbcUrlDTO separateStrByMark(String originStr) {
        Objects.requireNonNull(originStr);
        JdbcUrlDTO dto = new JdbcUrlDTO();
        int sepIndex = originStr.indexOf(MARK_D);
        if (sepIndex < 0) {
            log.info("originStr: [{}] 寻找不到分隔符位置 mark: [{}]", originStr, MARK_D);
            return null;
        }
        String preString = originStr.substring(0, sepIndex);
        String afterString = originStr.substring(sepIndex + MARK_D.length());

        dto.setProtocol(preString);
        int oneIndex = afterString.indexOf(MARK_O);
        if (oneIndex < 0) {
            log.info("afterString: [{}] 寻找不到分隔符位置 markO: [{}], 可能不存在schema信息", originStr, MARK_O);
            dto.setUrl(afterString);
        } else {
            String urlString = afterString.substring(0, oneIndex);
            String schemaName = afterString.substring(oneIndex + MARK_O.length());
            dto.setUrl(urlString);
            dto.setSchema(schemaName);
        }
        return dto;
    }

    /**
     * 分割oracle jdbcUrl原字符串, 生成DTO
     * @param originStr
     * @return
     */
    public JdbcUrlDTO separateOracleByMark(String originStr) {
        Objects.requireNonNull(originStr);
        JdbcUrlDTO dto = new JdbcUrlDTO();
        int sepIndex = originStr.indexOf(ORACLE_MARK_D);
        if (sepIndex < 0) {
            log.info("oracle ==> originStr: [{}] 寻找不到分隔符位置 mark: [{}]", originStr, ORACLE_MARK_D);
            return null;
        }
        String preString = originStr.substring(0, sepIndex);
        String afterString = originStr.substring(sepIndex + ORACLE_MARK_D.length());
        dto.setProtocol(preString);
        String[] oracleSplit = afterString.split(ORACLE_MARK_O);

        if (oracleSplit.length == 1) {
            log.info("oracle ==> originStr: [{}] 地址不符合正常规则 mark: [{}]", originStr, ORACLE_MARK_O);
            return null;
        } else if (oracleSplit.length == 2) {
            log.info("oracle ==> originStr: [{}] 无schema信息 mark: [{}]", originStr, ORACLE_MARK_O);
            dto.setUrl(afterString);
        } else {
            log.info("oracle ==> originStr: [{}] 具有schema信息符合规则 mark: [{}]", originStr, ORACLE_MARK_O);
            dto.setUrl(oracleSplit[0] + ORACLE_MARK_O + oracleSplit[1]);
            dto.setSchema(oracleSplit[2]);
        }
        return dto;
    }

    /**
     * console修改后的jdbcUrl
     * @param originUrl
     * @param editUrl
     * @return
     */
    public String editJdbcUrl(String originUrl, String editUrl, Boolean isOracle) {
        JdbcUrlDTO originDto = isOracle ? separateOracleByMark(originUrl) : separateStrByMark(originUrl);
        JdbcUrlDTO editDto = isOracle ? separateOracleByMark(editUrl) : separateStrByMark(editUrl);
        if (Objects.isNull(originDto) || Objects.isNull(editDto)) {
            log.info("控制台修改jdbcUrl格式不正确, origin: [{}], editUrl: [{}]", originUrl, editUrl);
            throw new PubSvcDefineException(ErrorCode.CONSOLE_EDIT_JDBC_FORMAT_ERROR);
        }
        originDto.setUrl(editDto.getUrl());
        return isOracle ? originDto.getOracleUrl() : originDto.getJdbcUrl();
    }

    /**
     * 通过数据源实例IdList获取数据源列表
     * @param dataInfoIdList
     * @return
     */
    public List<DsServiceInfoVO> getDsInfoListByIdList(List<Long> dataInfoIdList) {

        List<DsInfo> dsInfoList = dsInfoService.getDsInfoListByIdList(dataInfoIdList);
        List<DsServiceInfoVO> dsServiceInfoVOS = new ArrayList<>();
        for (DsInfo dsInfo : dsInfoList) {
            DsServiceInfoVO infoVO = Dozers.convert(dsInfo, DsServiceInfoVO.class, (t, s, c) -> {
                DataSourceTypeEnum typeEnum = DataSourceTypeEnum.typeVersionOf(s.getDataType(), s.getDataVersion());
                Asserts.notNull(typeEnum, ErrorCode.CAN_NOT_FITABLE_SOURCE_TYPE);
                t.setType(typeEnum.getVal());
                t.setDataInfoId(s.getId());
                t.setLinkJson(DataSourceUtils.getDataSourceJsonStr(s.getLinkJson()));
                t.setDataJson(DataSourceUtils.getDataSourceJsonStr(s.getDataJson()));
            });
            dsServiceInfoVOS.add(infoVO);
        }
        return dsServiceInfoVOS;
    }


    @Data
    class JdbcUrlDTO {

        private String protocol;

        private String url;

        private String schema;

        public String getProtocol() {
            return protocol;
        }

        public String getJdbcUrl() {
            String jdbc = Optional.ofNullable(protocol).orElse("") + MARK_D + Optional.ofNullable(url).orElse("");
            if (Strings.isNotBlank(schema)) {
                jdbc = jdbc + MARK_O + schema;
            }
            return jdbc;
        }

        public String getOracleUrl() {
            String jdbc = Optional.ofNullable(protocol).orElse("") + ORACLE_MARK_D + Optional.ofNullable(url).orElse("");
            if (Strings.isNotBlank(schema)) {
                jdbc = jdbc + ORACLE_MARK_O + schema;
            }
            return jdbc;
        }
    }

    public static void main(String[] args) {

    }

}
