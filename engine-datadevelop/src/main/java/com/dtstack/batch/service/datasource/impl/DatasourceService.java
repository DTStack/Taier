package com.dtstack.batch.service.datasource.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.common.enums.DataSourceTypeEnum;
import com.dtstack.batch.common.exception.PubSvcDefineException;
import com.dtstack.batch.enums.SourceDTOType;
import com.dtstack.batch.vo.DataSourceVO;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IKerberos;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.kerberos.HadoopConfTool;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.engine.common.constrant.FormNames;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.DtCenterDefException;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.util.CommonUtils;
import com.dtstack.engine.common.util.DataSourceUtils;
import com.dtstack.engine.common.util.Strings;
import com.dtstack.engine.domain.datasource.DsFormField;
import com.dtstack.engine.domain.datasource.DsInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 有关数据源中心
 * @description:
 * @author: liuxx
 * @date: 2021/3/16
 */
@Slf4j
@Service
public class DatasourceService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private EnvironmentContext env;

    @Autowired
    private KerberosService kerberosService;

    @Autowired
    private DsInfoService dsInfoService;

    @Autowired
    private DsFormFieldService formFieldService;

    @Autowired
    private DsTypeService typeService;


    @Autowired
    private DsTypeService dsTypeService;

    public static final String DSC_INFO_CHANGE_CHANNEL = "dscInfoChangeChannel";




    /**
     * 解析kerberos文件获取principal列表
     * @param source
     * @param resource
     * @param dtuicTenantId
     * @param projectId
     * @param userId
     * @return
     */
    public List<String> getPrincipalsWithConf(DataSourceVO source, Pair<String, String> resource, Long dtuicTenantId, Long projectId, Long userId) {
        String localKerberosPath;
        Map<String, Object> kerberosConfig;
        // 获取数据源类型，这里要做type version的改造
        DataSourceTypeEnum typeEnum = DataSourceTypeEnum.typeVersionOf(source.getDataType(),source.getDataVersion());
        IKerberos kerberos = ClientCache.getKerberos(typeEnum.getVal());
        if (Objects.nonNull(resource)) {
            localKerberosPath = kerberosService.getTempLocalKerberosConf(userId, projectId);
            try {
                // 解析Zip文件获取配置对象
                kerberosConfig = kerberos.parseKerberosFromUpload(resource.getRight(), localKerberosPath);
            } catch (IOException e) {
                log.error("解析principals， kerberos config 解析异常,{}", e.getMessage(), e);
                throw new PubSvcDefineException(String.format("kerberos config 解析异常,Caused by: %s", e.getMessage()), e);
            }
            // 连接 Kerberos 前的准备工作
            kerberos.prepareKerberosForConnect(kerberosConfig, localKerberosPath);
        } else {
            kerberosConfig = kerberosConnectPrepare(source.getId());
        }
        return kerberos.getPrincipals(kerberosConfig);
    }

    /**
     * kerberos认证前预处理 ：对kerberos参数替换相对路径为绝对路径等操作
     * @param sourceId
     * @return
     */
    public Map<String, Object> kerberosConnectPrepare(Long sourceId) {
        DsInfo dataSource = dsInfoService.getOneById(sourceId);
        DataSourceTypeEnum typeEnum = DataSourceTypeEnum.typeVersionOf(dataSource.getDataType(), dataSource.getDataVersion());
        if (Objects.isNull(typeEnum)) {
            throw new PubSvcDefineException(ErrorCode.CAN_NOT_FITABLE_SOURCE_TYPE);
        }
        Map<String, Object> kerberosConfig = fillKerberosConfig(dataSource.getId());
        HashMap<String, Object> tmpKerberosConfig = new HashMap<>(kerberosConfig);
        // kerberos获取表操作预处理
        if (MapUtils.isNotEmpty(kerberosConfig)) {
            String localKerberosPath = kerberosService.getLocalKerberosPath(sourceId);
            IKerberos kerberos = ClientCache.getKerberos(typeEnum.getVal());
            try {
                kerberos.prepareKerberosForConnect(tmpKerberosConfig, localKerberosPath);
            } catch (Exception e) {
                log.error("kerberos连接预处理失败！{}", e.getMessage(), e);
                throw new DtCenterDefException(String.format("kerberos连接预处理失败,Caused by: %s", e.getMessage()), e);
            }
        }
        return tmpKerberosConfig;
    }

    /**
     * 根据已有数据源主键填充confMap
     * @param sourceId
     * @return
     */
    public Map<String, Object> fillKerberosConfig(Long sourceId) {
        DsInfo dataSource = dsInfoService.getOneById(sourceId);
        Long tenantId = dataSource.getTenantId();
        // 获取Kerberos客户端
        JSONObject kerberosConfig = DataSourceUtils.getOriginKerberosConfig(dataSource.getDataJson(), false);

        if (MapUtils.isEmpty(kerberosConfig)) {
            return Collections.emptyMap();
        }

        try {
            // 获取kerberos本地路径
            String localKerberosConf = kerberosService.getLocalKerberosPath(sourceId);
            kerberosService.downloadKerberosFromSftp(dataSource.getIsMeta(), sourceId, DataSourceUtils.getDataSourceJson(dataSource.getDataJson()), localKerberosConf, tenantId);
        } catch (SftpException e) {
            throw new DtCenterDefException(String.format("获取kerberos认证文件失败,Caused by: %s", e.getMessage()), e);
        }
        return kerberosConfig;
    }

    /**
     * 测试联通性
     * @param source
     * @return
     */
    public Boolean checkConnection(DataSourceVO source) {
        return checkConnectionWithConf(source, null, null);
    }
    /**
     * 检测kerberos认证的数据源连通性
     * @param source
     * @param resource
     * @param projectId
     * @param userId
     * @return
     */
    public Boolean checkConnectionWithKerberos(DataSourceVO source, Pair<String, String> resource, Long projectId, Long userId) {
        Map<String, Object> kerberosConfig;
        String localKerberosPath;
        DataSourceTypeEnum typeEnum = DataSourceTypeEnum.typeVersionOf(source.getDataType(), source.getDataVersion());
        IKerberos kerberos = ClientCache.getKerberos(typeEnum.getVal());
        if (Objects.nonNull(resource)) {
            localKerberosPath = kerberosService.getTempLocalKerberosConf(userId, projectId);
            try {
                kerberosConfig = kerberos.parseKerberosFromUpload(resource.getRight(), localKerberosPath);
            } catch (IOException e) {
                log.error("检测连通性， kerberos config 解析异常,{}", e.getMessage(), e);
                throw new PubSvcDefineException(String.format("kerberos config 解析异常,Caused by: %s", e.getMessage()), e);
            }
        } else {
            localKerberosPath = kerberosService.getLocalKerberosPath(source.getId());
            kerberosConfig = fillKerberosConfig(source.getId());
        }
        try {
            source.setDataJson(DataSourceUtils.getDataSourceJson(source.getDataJsonString()));
        } catch (Exception e) {
            log.error("检查数据源连接，DataJsonString 转化异常", e);
            throw new PubSvcDefineException("JSONObject 转化异常", e);
        }
        // 设置前台传入的principals
        setPrincipals(source.getDataJson(), kerberosConfig);
        return checkConnectionWithConf(source, kerberosConfig, localKerberosPath);
    }

    /**
     * 数据源连通性测试
     * @param source
     * @param confMap
     * @param localKerberosPath
     * @return
     */
    public Boolean checkConnectionWithConf(DataSourceVO source, Map<String, Object> confMap, String localKerberosPath) {
        DataSourceTypeEnum typeEnum = DataSourceTypeEnum.typeVersionOf(source.getDataType(), source.getDataVersion());
        if (MapUtils.isEmpty(confMap) && source.getId() > 0L) {
            confMap = fillKerberosConfig(source.getId());
            localKerberosPath = kerberosService.getLocalKerberosPath(source.getId());
        }
        if(DataSourceTypeEnum.ADB_PostgreSQL == typeEnum){
            typeEnum = DataSourceTypeEnum.PostgreSQL;
        }
        // 替换相对绝对路径
        Map<String, Object> tempConfMap = null;
        if (MapUtils.isNotEmpty(confMap)) {
            tempConfMap = Maps.newHashMap(confMap);
            IKerberos kerberos = ClientCache.getKerberos(typeEnum.getVal());
            kerberos.prepareKerberosForConnect(tempConfMap, localKerberosPath);
        }
        // 测试连通性
        ISourceDTO sourceDTO = SourceDTOType.getSourceDTO(source.getDataJson(), typeEnum.getVal(), tempConfMap);
        return ClientCache.getClient(typeEnum.getVal()).testCon(sourceDTO);
    }


    /**
     * 设置前台传入的principals
     * @param dataJson
     * @param confMap
     */
    public void setPrincipals(JSONObject dataJson, Map<String, Object> confMap) {
        //principal 键
        String principal = dataJson.getString(FormNames.PRINCIPAL);
        if (Strings.isNotBlank(principal)) {
            confMap.put(HadoopConfTool.PRINCIPAL, principal);
        }
        //Hbase master kerberos Principal
        String hbaseMasterPrincipal = dataJson.getString(FormNames.HBASE_MASTER_PRINCIPAL);
        if (Strings.isNotBlank(hbaseMasterPrincipal)) {
            confMap.put(HadoopConfTool.HBASE_MASTER_PRINCIPAL, hbaseMasterPrincipal);
        }
        //Hbase region kerberos Principal
        String hbaseRegionserverPrincipal = dataJson.getString(FormNames.HBASE_REGION_PRINCIPAL);
        if (Strings.isNotBlank(hbaseRegionserverPrincipal)) {
            confMap.put(HadoopConfTool.HBASE_REGION_PRINCIPAL, hbaseRegionserverPrincipal);
        }
    }

    /**
     * 上传Kerberos添加和修改数据源
     * @param dataSourceVO
     * @param resource
     * @param projectId
     * @param userId
     * @param dtuicTenantId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Long addOrUpdateSourceWithKerberos(DataSourceVO dataSourceVO, Pair<String, String> resource, Long projectId, Long userId, Long dtuicTenantId) {
        Map<String, Object> confMap;
        JSONObject dataJson = DataSourceUtils.getDataSourceJson(dataSourceVO.getDataJsonString());
        dataSourceVO.setDataJson(dataJson);
        List<Integer> list = JSON.parseObject(dataSourceVO.getAppTypeListString(), List.class);
        dataSourceVO.setAppTypeList(list);
        String localKerberosConf;
        DataSourceTypeEnum typeEnum = DataSourceTypeEnum.typeVersionOf(dataSourceVO.getDataType(), dataSourceVO.getDataVersion());
        if (Objects.nonNull(resource)) {
            //resource不为空表示本地上传文件
            localKerberosConf = kerberosService.getTempLocalKerberosConf(userId, projectId);
            try {
                confMap = ClientCache.getKerberos(typeEnum.getVal()).parseKerberosFromUpload(resource.getRight(), localKerberosConf);
            } catch (IOException e) {
                log.error("添加数据源， kerberos config 解析异常,{}", e.getMessage(), e);
                throw new PubSvcDefineException(String.format("kerberos config 解析异常,Caused by: %s", e.getMessage()), e);
            }
            //设置openKerberos变量表示开启kerberos认证
            DataSourceUtils.setOpenKerberos(dataJson, true);
            DataSourceUtils.setKerberosFile(dataJson, resource.getRight());
        } else {
            DsInfo originSource = dsInfoService.getOneById(dataSourceVO.getId());
            DataSourceUtils.getOriginKerberosConfig(dataJson, originSource.getDataJson());
            localKerberosConf = kerberosService.getLocalKerberosPath(dataSourceVO.getId());
            confMap = fillKerberosConfig(dataSourceVO.getId());
        }
        // 设置前台传入的principals
        setPrincipals(dataJson, confMap);
        //检查链接
        Boolean connValue = checkConnectionWithConf(dataSourceVO, confMap, localKerberosConf);
        if (!connValue) {
            throw new PubSvcDefineException("不能添加连接失败的数据源", ErrorCode.CONFIG_ERROR);
        }
        Long dataSourceId = dataSourceVO.getId();
        //有认证文件上传 进行上传至sftp操作
        Long id = null;
        try {
            if (Objects.nonNull(resource)) {
                if (dataSourceVO.getId() == 0L) {
                    // 没有保存过的数据源需要先保存, 获取自增id
                    dataSourceVO.setKerberosConfig(confMap);
                    dataSourceVO.setDataJson((JSONObject) dataJson.clone());
                    dataSourceVO.setLocalKerberosConf(localKerberosConf);
                    dataSourceId = addOrUpdate(dataSourceVO, userId);
                }
                Map<String, String> sftpMap = kerberosService.getSftpMap(dtuicTenantId);
                //目录转换 - 将临时目录根据数据源ID转移到新的kerberos文件目录
                File localKerberosConfDir = new File(localKerberosConf);
                File newConfDir = new File(kerberosService.getLocalKerberosPath(dataSourceId));
                //如果原来的目录存在 删除原来的文件
                try {
                    FileUtils.deleteDirectory(newConfDir);
                } catch (IOException e) {
                    log.error("删除历史的kerberos文件失败", e);
                }
                //目录转换, temp路径转换成新路径
                localKerberosConfDir.renameTo(newConfDir);
                //上传配置文件到sftp
                String dataSourceKey = kerberosService.getSourceKey(dataSourceId, null);
                KerberosService.uploadDirFinal(sftpMap, newConfDir.getPath(), dataSourceKey);
                confMap.put("kerberosDir", dataSourceKey);
            }
            dataSourceVO.setKerberosConfig(confMap);
            dataSourceVO.setLocalKerberosConf(localKerberosConf);
            dataSourceVO.setDataJson(dataJson);
            dataSourceVO.setId(dataSourceId);
            id = addOrUpdate(dataSourceVO, userId);
        } catch (Exception e) {
            log.error("addOrUpdateSourceWithKerberos error",e);
            throw new PubSvcDefineException(e.getMessage());
        }
        return id;
    }



    /**
     * 添加和修改数据源
     * @param dataSourceVO
     * @param userId
     * @return
     */
    public Long addOrUpdateSource(DataSourceVO dataSourceVO, Long userId) {
        if (!checkConnectionWithConf(dataSourceVO, null, null)) {
            throw new PubSvcDefineException("不能添加连接失败的数据源" + ErrorCode.CONFIG_ERROR);
        }
        return addOrUpdate(dataSourceVO, userId);
    }


    /**
     * 添加和修改数据源信息
     * @param dataSourceVO
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Long addOrUpdate(DataSourceVO dataSourceVO, Long userId) {
        DataSourceTypeEnum typeEnum = DataSourceTypeEnum.typeVersionOf(dataSourceVO.getDataType(), dataSourceVO.getDataVersion());
        JSONObject json = dataSourceVO.getDataJson();
        //字段转换
        colMap(json, typeEnum.getVal(), dataSourceVO.getKerberosConfig());
        dataSourceVO.setDataJson(json);
        dataSourceVO.setModifyUserId(userId);
        // 构造数据源元数据
        DsInfo dsInfo = buildDsInfo(dataSourceVO);
        if (dataSourceVO.getId() > 0) {
            // edit 不存在授权操作
            dsInfoService.getOneById(dataSourceVO.getId());
            dsInfo.setId(dataSourceVO.getId());
            dsInfo.setModifyUserId(dataSourceVO.getUserId());
            if (dsInfoService.checkDataNameDup(dsInfo)) {
                throw new PubSvcDefineException(ErrorCode.DATASOURCE_DUP_NAME);
            }
            dsInfoService.updateById(dsInfo);
        } else {
            // add 存在授权产品操作
            dsInfo.setCreateUserId(dataSourceVO.getUserId());
            dsInfo.setModifyUserId(dataSourceVO.getUserId());
            if (dsInfoService.checkDataNameDup(dsInfo)) {
                throw new PubSvcDefineException(ErrorCode.DATASOURCE_DUP_NAME);
            }
            dsInfoService.save(dsInfo);
            // 保存数据源类型权重
            typeService.plusDataTypeWeight(dsInfo.getDataType(), 1);
        }
        return dsInfo.getId();
    }


    /**
     * 构建数据源元数据对象
     * @param dataSourceVO
     * @return
     */
    private DsInfo buildDsInfo(DataSourceVO dataSourceVO) {
        DsInfo dsInfo = new DsInfo();
        dsInfo.setDataType(dataSourceVO.getDataType());
        dsInfo.setDataVersion(dataSourceVO.getDataVersion());
        dsInfo.setDataName(dataSourceVO.getDataName());
        dsInfo.setDataDesc(dataSourceVO.getDataDesc());
        dsInfo.setStatus(1);
        dsInfo.setIsMeta(dataSourceVO.getIsMeta());
        dsInfo.setTenantId(dataSourceVO.getTenantId());
        dsInfo.setTenantId(dataSourceVO.getTenantId());
        dsInfo.setSchemaName(dataSourceVO.getSchemaName());
        DataSourceTypeEnum typeEnum = DataSourceTypeEnum.typeVersionOf(dataSourceVO.getDataType(), dataSourceVO.getDataVersion());
        dsInfo.setDataTypeCode(typeEnum.getVal());
        // dataJson
        if (Objects.nonNull(dataSourceVO.getDataJson())) {
            JSONObject dataJson = dataSourceVO.getDataJson();
            if(dataSourceVO.getDataType().equals(DataSourceTypeEnum.HBASE2.getDataType())){
                //Hbase需要特殊处理
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(FormNames.HBASE_ZK_QUORUM,dataJson.get(FormNames.HBASE_QUORUM));
                jsonObject.put(FormNames.HBASE_ZK_PARENT,dataJson.get(FormNames.HBASE_PARENT));
                dataJson.put("hbaseConfig",jsonObject);
            }
            JSONObject kerberos;
            if ((kerberos=dataJson.getJSONObject(DataSourceUtils.KERBEROS_CONFIG))!=null){
                dataJson.put(DataSourceUtils.KERBEROS_FILE_TIMESTAMP,kerberos.getOrDefault(DataSourceUtils.KERBEROS_FILE_TIMESTAMP,System.currentTimeMillis()));
            }
            dsInfo.setDataJson(DataSourceUtils.getEncodeDataSource(dataJson, true));
            String linkInfo = getDataSourceLinkInfo(dataSourceVO.getDataType(), dataSourceVO.getDataVersion(), dataSourceVO.getDataJson());
            dsInfo.setLinkJson(DataSourceUtils.getEncodeDataSource(linkInfo, true));
        } else if(Strings.isNotBlank(dataSourceVO.getDataJsonString())) {
            JSONObject dataSourceJson = DataSourceUtils.getDataSourceJson(dataSourceVO.getDataJsonString());
            if(dataSourceVO.getDataType().equals(DataSourceTypeEnum.HBASE2.getDataType())){
                //Hbase需要特殊处理
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(FormNames.HBASE_QUORUM,dataSourceJson.get(FormNames.HBASE_QUORUM));
                jsonObject.put(FormNames.HBASE_ZK_PARENT,dataSourceJson.get(FormNames.HBASE_PARENT));
                dataSourceJson.put("hbaseConfig",jsonObject);
            }
            JSONObject kerberos;
            if ((kerberos=dataSourceJson.getJSONObject(DataSourceUtils.KERBEROS_CONFIG))!=null){
                dataSourceJson.put(DataSourceUtils.KERBEROS_FILE_TIMESTAMP,kerberos.getOrDefault(DataSourceUtils.KERBEROS_FILE_TIMESTAMP,System.currentTimeMillis()));
            }
            dsInfo.setDataJson(DataSourceUtils.getEncodeDataSource(dataSourceJson, true));
            //获取连接信息
            String linkInfo = getDataSourceLinkInfo(dataSourceVO.getDataType(), dataSourceVO.getDataVersion(), dataSourceJson);
            dsInfo.setLinkJson(DataSourceUtils.getEncodeDataSource(linkInfo, true));
        } else {
            throw new PubSvcDefineException(ErrorCode.DATASOURCE_CONF_ERROR);
        }
        return dsInfo;
    }


    /**
     * 根据数据源版本获取对应的连接信息
     * @param dataType
     * @param dataVersion
     * @param dataJson
     * @return
     */
    public String getDataSourceLinkInfo(String dataType, String dataVersion, JSONObject dataJson) {
        List<DsFormField> linkFieldList = formFieldService.findLinkFieldByTypeVersion(dataType, dataVersion);
        if (CollectionUtils.isEmpty(linkFieldList)) {
            return null;
        }
        JSONObject linkJson = new JSONObject();
        for (DsFormField dsFormField : linkFieldList) {
            String value = CommonUtils.getStrFromJson(dataJson, dsFormField.getName());
            if (Strings.isNotBlank(value)) {
                linkJson.put(dsFormField.getName(), value);
            }
        }
        return linkJson.toJSONString();
    }

    /**
     * 解析字段
     * @param json
     * @param type
     * @param kerberosConfig
     * @return
     */
    private void colMap(JSONObject json, Integer type, Map<String, Object> kerberosConfig) {
        if (DataSourceType.getKafkaS().contains(type)) {
            ISourceDTO sourceDTO = SourceDTOType.getSourceDTO(json, type);
            String brokersAddress = null;

            try {
                brokersAddress = ClientCache.getKafka(type).getAllBrokersAddress(sourceDTO);
            } catch (Exception e) {
                log.error("获取kafka brokersAddress 异常!", e);
                throw new PubSvcDefineException("获取kafka brokersAddress 异常!", e);
            }
            json.put("bootstrapServers", brokersAddress);
        }

        if (kerberosConfig != null) {
            json.put(FormNames.KERBEROS_CONFIG, kerberosConfig);
        }
    }


    /**
     * 通过数据源信息判断数据源的联通性
     * @param dsInfo
     * @return
     */
    public Boolean checkConnectByDsInfo(DsInfo dsInfo) {
        DataSourceTypeEnum typeEnum = DataSourceTypeEnum.typeVersionOf(dsInfo.getDataType(), dsInfo.getDataVersion());
        // 测试连通性
        ISourceDTO sourceDTO = SourceDTOType.getSourceDTO(dsInfo.getDataJson(), typeEnum.getVal(), null);
        return ClientCache.getClient(typeEnum.getVal()).testCon(sourceDTO);
    }

}
