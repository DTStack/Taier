/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.engine.datasource.facade.datasource;

import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IKerberos;
import com.dtstack.dtcenter.loader.dto.ColumnMetaDTO;
import com.dtstack.dtcenter.loader.dto.SqlQueryDTO;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.engine.datasource.auth.MetaObjectHolder;
import com.dtstack.engine.datasource.common.enums.datasource.DataSourceTypeEnum;
import com.dtstack.engine.datasource.common.enums.datasource.SourceDTOType;
import com.dtstack.engine.datasource.common.exception.ErrorCode;
import com.dtstack.engine.datasource.common.exception.PubSvcDefineException;
import com.dtstack.engine.datasource.common.utils.CommonUtils;
import com.dtstack.engine.datasource.common.utils.DataSourceUtils;
import com.dtstack.engine.datasource.common.utils.datakit.Asserts;
import com.dtstack.engine.datasource.common.utils.datakit.Collections;
import com.dtstack.engine.datasource.dao.bo.datasource.DsListBO;
import com.dtstack.engine.datasource.dao.po.datasource.DsInfo;
import com.dtstack.engine.datasource.mapstruct.DsInfoStruct;
import com.dtstack.engine.datasource.service.impl.datasource.DsInfoService;
import com.dtstack.engine.datasource.service.impl.datasource.KerberosService;
import com.dtstack.engine.datasource.vo.datasource.api.DsServiceInfoVO;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
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
public class DtDataSourceFacade {

    @Autowired
    private DsInfoService dsInfoService;

    @Autowired
    private KerberosService kerberosService;

    @Autowired
    private DsInfoStruct dsInfoStruct;



    /**
     * 根据类型列表的查询数据源
     *
     * @return 返回类型对应的数据源列表
     */
    public DsServiceInfoVO queryDataSource(Long dsId) {
        Objects.requireNonNull(dsId);
        DsInfo dsInfo = dsInfoService.getOneById(dsId);

        DsServiceInfoVO dsServiceInfoVO = dsInfoStruct.toDsServiceInfoVO(dsInfo);
        dsServiceInfoVO.setDataInfoId(dsInfo.getId());
        DataSourceTypeEnum typeEnum = DataSourceTypeEnum.typeVersionOf(dsInfo.getDataType(), dsInfo.getDataVersion());
        dsServiceInfoVO.setType(typeEnum.getVal());
        return dsServiceInfoVO;
    }

    /**
     * 根据产品type查询当前租户下已经授权并且引入的数据源Id
     * @param appType
     * @param dtUicTenantId
     * @return
     */
    public List<DsListBO> queryDsByAppType(Integer appType, Long dtUicTenantId) {
        Objects.requireNonNull(appType);
        Objects.requireNonNull(dtUicTenantId);
        return dsInfoService.queryDsByAppType(appType, dtUicTenantId);
    }

    /**
     * 根据产品typeList查询当前租户下已经授权并且引入的数据源Id
     * @param appTypeList
     * @param dtUicTenantId
     * @return
     */
    public List<DsListBO> queryDsByAppTypeList(List<Integer> appTypeList, Long dtUicTenantId,Long datasourceId) {
        if (Collections.isEmpty(appTypeList)) {
            return Collections.emptyList();
        }
        Objects.requireNonNull(dtUicTenantId);
        return dsInfoService.queryDsByAppTypeList(appTypeList, dtUicTenantId, datasourceId);
    }

    public List<DsServiceInfoVO> queryDsByIds(List<Long> dsIds) {
        if (Collections.isEmpty(dsIds)) {
            return Collections.emptyList();
        }

        List<DsInfo> dsInfos = dsInfoService.listByIds(dsIds);
        return dsInfos.stream().map(t -> {
            DsServiceInfoVO dsServiceInfoVO = dsInfoStruct.toDsServiceInfoVO(t);
            dsServiceInfoVO.setDataInfoId(t.getId());
            DataSourceTypeEnum typeEnum = DataSourceTypeEnum.typeVersionOf(t.getDataType(), t.getDataVersion());
            dsServiceInfoVO.setType(typeEnum.getVal());
            return dsServiceInfoVO;
        }).collect(Collectors.toList());
    }

    /**
     * 根据类型列表的查询数据源
     *
     * @return 返回类型对应的数据源列表
     */
    public List<DsServiceInfoVO> queryDataSource(List<DataSourceTypeEnum> dataSourceEnums) {
        if (Collections.isEmpty(dataSourceEnums)) {
            return Collections.emptyList();
        }
        List<DsInfo> dsInfoList = dsInfoService.lambdaQuery()
                .in(DsInfo::getDataType, Collections.mapperList(dataSourceEnums, DataSourceTypeEnum::getDataType))
                .orderByDesc(DsInfo::getGmtModified, DsInfo::getDataType)
                .list();

        return dsInfoList.stream().map(t -> {
            DsServiceInfoVO dsServiceInfoVO = dsInfoStruct.toDsServiceInfoVO(t);
            dsServiceInfoVO.setDataInfoId(t.getId());
            DataSourceTypeEnum typeEnum = DataSourceTypeEnum.typeVersionOf(t.getDataType(), t.getDataVersion());
            dsServiceInfoVO.setType(typeEnum.getVal());
            return dsServiceInfoVO;
        }).collect(Collectors.toList());
    }

    /**
     * 默认查询Impala 和 Presto类型的数据源列表
     * @return
     */
    public List<DsServiceInfoVO> queryModelDsList(Long dtUicTenantId) {
        Objects.requireNonNull(dtUicTenantId);
        List<DsInfo> dsInfoList = dsInfoService.lambdaQuery()
                .eq(DsInfo::getDtuicTenantId, dtUicTenantId)

                .in(DsInfo::getDataType, DataSourceTypeEnum.IMPALA.getDataType(), DataSourceTypeEnum.Presto.getDataType()).list();

        return dsInfoList.stream().map(t -> {
            DsServiceInfoVO dsServiceInfoVO = dsInfoStruct.toDsServiceInfoVO(t);
            dsServiceInfoVO.setDataInfoId(t.getId());
            DataSourceTypeEnum typeEnum = DataSourceTypeEnum.typeVersionOf(t.getDataType(), t.getDataVersion());
            dsServiceInfoVO.setType(typeEnum.getVal());
            return dsServiceInfoVO;
        }).collect(Collectors.toList());
    }

    /**
     * 查询某个数据源实例下的所有schema列表
     * @param dataInfoId
     * @return
     */
    public List<String> querySchemaList(Long dataInfoId) {
        Objects.requireNonNull(dataInfoId);
        DsInfo dsInfo = dsInfoService.getOneById(dataInfoId);
        DataSourceTypeEnum typeEnum = DataSourceTypeEnum.typeVersionOf(dsInfo.getDataType(), dsInfo.getDataVersion());
        Asserts.notNull(typeEnum, ErrorCode.CAN_NOT_FITABLE_SOURCE_TYPE);
        // 构造kerberos参数
        Map<String, Object> tempConfMap = constructKerberos(dsInfo, typeEnum);
        ISourceDTO sourceDTO = SourceDTOType.getSourceDTO(dsInfo.getDataJson(), typeEnum.getVal(), tempConfMap);
        SqlQueryDTO queryDTO = SqlQueryDTO.builder().build();
        return ClientCache.getClient(typeEnum.getVal()).getAllDatabases(sourceDTO, queryDTO);
    }



    /**
     * 查询某个数据源实例特定schema下的table列表
     * @param dataInfoId
     * @param schemaName
     * @return
     */
    public List<String> queryTableList(Long dataInfoId, String schemaName) {
        Objects.requireNonNull(dataInfoId);
        DsInfo dsInfo = dsInfoService.getOneById(dataInfoId);
        DataSourceTypeEnum typeEnum = DataSourceTypeEnum.typeVersionOf(dsInfo.getDataType(), dsInfo.getDataVersion());
        Asserts.notNull(typeEnum, ErrorCode.CAN_NOT_FITABLE_SOURCE_TYPE);
        //构造kerberos参数
        Map<String, Object> tempConfMap = constructKerberos(dsInfo, typeEnum);
        ISourceDTO sourceDTO = SourceDTOType.getSourceDTO(dsInfo.getDataJson(), typeEnum.getVal(), tempConfMap, schemaName);
        SqlQueryDTO queryDTO = SqlQueryDTO.builder().schema(schemaName).build();
        return ClientCache.getClient(typeEnum.getVal()).getTableListBySchema(sourceDTO, queryDTO);
    }

    /**
     * 查询某个schema下表内的所有字段信息
     * @param dataInfoId
     * @param schemaName
     * @param tableName
     * @return
     */
    public List<ColumnMetaDTO> queryColumnList(Long dataInfoId, String schemaName, String tableName) {
        Objects.requireNonNull(dataInfoId);
        DsInfo dsInfo = dsInfoService.getOneById(dataInfoId);
        DataSourceTypeEnum typeEnum = DataSourceTypeEnum.typeVersionOf(dsInfo.getDataType(), dsInfo.getDataVersion());
        Asserts.notNull(typeEnum, ErrorCode.CAN_NOT_FITABLE_SOURCE_TYPE);
        //构造kerberos参数
        Map<String, Object> tempConfMap = constructKerberos(dsInfo, typeEnum);
        ISourceDTO sourceDTO = SourceDTOType.getSourceDTO(dsInfo.getDataJson(), typeEnum.getVal(), tempConfMap, schemaName);
        SqlQueryDTO queryDTO = SqlQueryDTO.builder().schema(schemaName).tableName(tableName).build();
        return ClientCache.getClient(typeEnum.getVal()).getColumnMetaData(sourceDTO, queryDTO);
    }

    /**
     * 查询某个schema下表内的分区字段
     * 若不存在分区则返回空数组
     * @param dataInfoId
     * @param schemaName
     * @param tableName
     * @return
     */
    public List<ColumnMetaDTO> queryPartColumnList(Long dataInfoId, String schemaName, String tableName) {
        Objects.requireNonNull(dataInfoId);
        DsInfo dsInfo = dsInfoService.getOneById(dataInfoId);
        DataSourceTypeEnum typeEnum = DataSourceTypeEnum.typeVersionOf(dsInfo.getDataType(), dsInfo.getDataVersion());
        Asserts.notNull(typeEnum, ErrorCode.CAN_NOT_FITABLE_SOURCE_TYPE);
        //构造kerberos参数
        Map<String, Object> tempConfMap = constructKerberos(dsInfo, typeEnum);
        ISourceDTO sourceDTO = SourceDTOType.getSourceDTO(dsInfo.getDataJson(), typeEnum.getVal(), tempConfMap, schemaName);
        SqlQueryDTO queryDTO = SqlQueryDTO.builder().schema(schemaName).tableName(tableName).build();
        return ClientCache.getClient(typeEnum.getVal()).getPartitionColumn(sourceDTO, queryDTO);
    }

    /**
     * 构造kerberos参数, 如果有kerberos配置则做好连接准备
     * @param dsInfo
     * @param typeEnum
     * @return
     */
    @Transactional
    public Map<String, Object> constructKerberos(DsInfo dsInfo, DataSourceTypeEnum typeEnum) {
        // 先判断当前数据源是否为kerberos认证
        Boolean openKerberos = DataSourceUtils.judgeOpenKerberos(dsInfo.getDataJson());
        String localKerberosPath;
        Map<String, Object> temConfMap = null;

        Long dtuicTenantId = MetaObjectHolder.uid();
        if (openKerberos) {
            Map<String, Object> kerberosConfig = null;
            localKerberosPath = kerberosService.getLocalKerberosPath(dsInfo.getId());
            try {
                if (!CommonUtils.filePathExist(localKerberosPath)) {
                    // 当前文件不存在则从SFTP下载
                    kerberosService.downloadKerberosFromSftp(dsInfo.getIsMeta(), dsInfo.getId(), DataSourceUtils.getDataSourceJson(dsInfo.getDataJson()), localKerberosPath, dtuicTenantId);
                }
                // 若当前文件存在则不从SFTP下载
                kerberosConfig = DataSourceUtils.getOriginKerberosConfig(dsInfo.getDataJson(), false);
            } catch (Exception e) {
                throw new PubSvcDefineException("获取kerberos认证文件失败", e);
            }

            if (MapUtils.isNotEmpty(kerberosConfig)) {
                temConfMap = new HashMap<>(kerberosConfig);
                IKerberos kerberos = ClientCache.getKerberos(typeEnum.getVal());
                kerberos.prepareKerberosForConnect(temConfMap, localKerberosPath);
            }
        }
        return temConfMap;
    }



}
