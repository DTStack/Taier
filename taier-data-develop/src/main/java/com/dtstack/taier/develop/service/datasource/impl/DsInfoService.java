package com.dtstack.taier.develop.service.datasource.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dtstack.taier.common.constant.FormNames;
import com.dtstack.taier.common.enums.DataSourceTypeEnum;
import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.PubSvcDefineException;
import com.dtstack.taier.common.util.DataSourceUtils;
import com.dtstack.taier.dao.domain.DsInfo;
import com.dtstack.taier.dao.domain.po.DsListBO;
import com.dtstack.taier.dao.domain.po.DsListQuery;
import com.dtstack.taier.dao.mapper.DsInfoMapper;
import com.dtstack.taier.dao.pager.PageResult;
import com.dtstack.taier.develop.bo.datasource.DsListParam;
import com.dtstack.taier.develop.mapstruct.datasource.DsDetailTransfer;
import com.dtstack.taier.develop.mapstruct.datasource.DsListTransfer;
import com.dtstack.taier.develop.vo.datasource.DsDetailVO;
import com.dtstack.taier.develop.vo.datasource.DsInfoVO;
import com.dtstack.taier.develop.vo.datasource.DsListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author 全阅
 * @Description:
 * @Date: 2021/3/10
 */
@Service
@Slf4j
public class DsInfoService  extends BaseService<DsInfoMapper, DsInfo>{


    @Autowired
    private DsInfoMapper dsInfoMapper;

    @Autowired
    private EnvironmentContext env;

    @Autowired
    private DsTypeService dsTypeService;

    private static String KERBEROS_CONFIG = "kerberosConfig";

    private static String KERBEROS_DIR = "kerberosDir";

    /**
     * 数据源列表分页
     *
     * @param dsListParam
     * @return
     */
    public PageResult<List<DsListVO>> dsPage(DsListParam dsListParam) {

        DsListQuery listQuery = DsListTransfer.INSTANCE.toInfoQuery(dsListParam);
        listQuery.turn();
        Integer total = this.baseMapper.countDsPage(listQuery);
        if (total == 0) {
            return new PageResult<>(0,0,0,0,new ArrayList<>());
        }
        List<DsListBO> dsListBOList = baseMapper.queryDsPage(listQuery);
        if (CollectionUtils.isEmpty(dsListBOList)) {
            return new PageResult<>(0,0,0,0,new ArrayList<>());
        }
        List<DsListVO> dsListVOS = new ArrayList<>();
        for (DsListBO dsListBO : dsListBOList) {
            DsListVO dsListVO = DsListTransfer.INSTANCE.toInfoVO(dsListBO);
            String linkJson = dsListVO.getLinkJson();
            JSONObject linkData = DataSourceUtils.getDataSourceJson(linkJson);
            linkData.put("schemaName",dsListVO.getSchemaName());
            dsListVO.setLinkJson(DataSourceUtils.getEncodeDataSource(linkData,true));
            dsListVOS.add(dsListVO);
        }
        return new PageResult<>(dsListParam.getCurrentPage(),dsListParam.getPageSize(),total,dsListVOS);
    }

    /**
     * 根据数据源Id获取数据源详情
     *
     * @param dataInfoId
     * @return
     */
    public DsDetailVO dsInfoDetail(Long dataInfoId) {
        DsInfo dsInfo = lambdaQuery().eq(DsInfo::getId, dataInfoId).one();
        DsDetailVO dsDetailVO = DsDetailTransfer.INSTANCE.toInfoVO(dsInfo);
        String dataJson = dsInfo.getDataJson();
        JSONObject dataSourceJson = DataSourceUtils.getDataSourceJson(dataJson);
        if(DataSourceUtils.judgeOpenKerberos(dataJson) && null == dataSourceJson.getString(FormNames.PRINCIPAL)){
            JSONObject kerberosConfig = dataSourceJson.getJSONObject(FormNames.KERBEROS_CONFIG);
            dataSourceJson.put(FormNames.PRINCIPAL,kerberosConfig.getString(FormNames.PRINCIPAL));
        }
        if(DataSourceUtils.judgeOpenKerberos(dsInfo.getDataJson()) && dsInfo.getDataType().equals(DataSourceTypeEnum.KAFKA.getDataType())){
            //kafka开启了kerberos认证
            dataSourceJson.put(FormNames.AUTHENTICATION,FormNames.KERBROS);
        }
        return dsDetailVO;
    }

    /**
     * 删除一条数据源信息
     *
     * @param dataInfoId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean delDsInfo(Long dataInfoId) {
        DsInfo dsInfo = this.getOneById(dataInfoId);
        if (Objects.equals(dsInfo.getIsMeta(), 1)) {
            throw new PubSvcDefineException(ErrorCode.CAN_NOT_DEL_META_DS);
        }
        return this.getBaseMapper().deleteById(dataInfoId) > 0;
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
            dataSource.setDataJson(dataSourceJson.toJSONString());
            return dataSource;
        }).collect(Collectors.toList());
    }


    /**
     * 判断当前数据源新增或者编辑是否有重名
     * @param dsInfo
     * @return
     */
    public Boolean checkDataNameDup(DsInfo dsInfo) {
        List<DsInfo> dsInfoList = this.lambdaQuery().eq(DsInfo::getDataName, dsInfo.getDataName())
                .eq(DsInfo::getTenantId, dsInfo.getTenantId())
                .ne(Objects.nonNull(dsInfo.getId()), DsInfo::getId, dsInfo.getId()).list();
        return CollectionUtils.isNotEmpty(dsInfoList);
    }



    public List<DsInfo> queryByIds(List<Long> notChecked) {
        return dsInfoMapper.queryByIds(notChecked);
    }

    /**
     * 根据租户查询数据源列表
     * @param tenantId
     * @return
     */
    public List<DsInfoVO> queryByTenantId(Long tenantId) {
        List<DsInfo> dsInfos = dsInfoMapper.queryByTenantId(tenantId);
        return DsListTransfer.INSTANCE.toDsInfoVOS(dsInfos);
    }


}
