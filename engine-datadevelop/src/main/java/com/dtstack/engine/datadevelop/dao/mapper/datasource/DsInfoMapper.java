package com.dtstack.pubsvc.dao.mapper.datasource;

import com.dtstack.pubsvc.dao.bo.datasource.DsListBO;
import com.dtstack.pubsvc.dao.bo.datasource.DsServiceListBO;
import com.dtstack.pubsvc.dao.bo.query.DsListQuery;
import com.dtstack.pubsvc.dao.bo.query.DsServiceListQuery;
import com.dtstack.pubsvc.dao.mapper.IMapper;
import com.dtstack.pubsvc.dao.po.datasource.DsInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @author 全阅
 * @Description:
 * @Date: 2021/3/10
 */
@Mapper
public interface DsInfoMapper extends IMapper<DsInfo> {
    /**
     * 获取外部引入数据源分页总数
     * @param listQuery
     * @return
     */
    Integer countImportDsPage(@Param("listQuery") DsServiceListQuery listQuery);

    /**
     * 获取外部引入数据源分页数据
     * @param listQuery
     * @return
     */
    List<DsServiceListBO> queryImportDsPage(@Param("listQuery") DsServiceListQuery listQuery);

    /**
     * 获取数据源报表分页总数
     * @param dsListQuery
     * @return
     */
    Integer countDsPage(@Param("listQuery")DsListQuery dsListQuery);

    /**
     * 获取数据源报表分页数据
     * @param dsListQuery
     * @return
     */
    List<DsListBO> queryDsPage(@Param("listQuery")DsListQuery dsListQuery);

    /**
     * 迁移数据源信息
     * @param dsInfo
     * @return
     */
    Integer shiftAppDs(@Param("dsInfo") DsInfo dsInfo);

    /**
     * 获取已被产品引入的数据源分页总数
     * @param listQuery
     * @return
     */
    Integer countAppDsPage(@Param("listQuery") DsServiceListQuery listQuery);

    /**
     * 获取已被产品引入的数据源分页数据
     * @param listQuery
     * @return
     */
    List<DsServiceListBO> queryAppDsPage(@Param("listQuery") DsServiceListQuery listQuery);

    /**
     * 根据产品type查询当前租户下已经授权并且引入的数据源Id
     * @param appType
     * @param dtUicTenantId
     * @return
     */
    List<DsListBO> queryDsByAppType(@Param("appType") Integer appType, @Param("dtUicTenantId") Long dtUicTenantId);

    /**
     * 根据产品type和平台数据源id查询数据源信息
     * @param appType
     * @param oldDataInfoId
     * @return
     */
    DsInfo queryDsByAppTypeAndOldDataInfoId(@Param("appType") Integer appType, @Param("oldDataInfoId") Long oldDataInfoId);

    /**
     * 根据产品typeList查询当前租户下已经授权并且引入的数据源Id
     * @param appTypeList
     * @param dtUicTenantId
     * @return
     */
    List<DsListBO> queryDsByAppTypeList(@Param("appTypeList") List<Integer> appTypeList, @Param("dtUicTenantId") Long dtUicTenantId);


    /**
     * 通过数据源实例IdList获取数据源列表
     * @param dataInfoIdList
     * @return
     */
    List<DsInfo> getDsInfoListByIdList(List<Long> dataInfoIdList);
}
