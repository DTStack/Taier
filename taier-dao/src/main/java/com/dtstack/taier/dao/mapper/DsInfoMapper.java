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

package com.dtstack.taier.dao.mapper;

import com.dtstack.taier.dao.domain.DsInfo;
import com.dtstack.taier.dao.domain.po.DsListBO;
import com.dtstack.taier.dao.domain.po.DsListQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @author 全阅
 * @Description:
 * @Date: 2021/3/10
 */
@Mapper
public interface DsInfoMapper extends com.baomidou.mybatisplus.core.mapper.BaseMapper<DsInfo> {



    /**
     * 获取数据源报表分页总数
     * @param dsListQuery
     * @return
     */
    Integer countDsPage(@Param("listQuery") DsListQuery dsListQuery);

    /**
     * 获取数据源报表分页数据
     * @param dsListQuery
     * @return
     */
    List<DsListBO> queryDsPage(@Param("listQuery")DsListQuery dsListQuery);



    /**
     * 根据产品type和平台数据源id查询数据源信息
     * @param appType
     * @param oldDataInfoId
     * @return
     */
    DsInfo queryDsByAppTypeAndOldDataInfoId(@Param("appType") Integer appType, @Param("oldDataInfoId") Long oldDataInfoId);



    /**
     * 通过数据源实例IdList获取数据源列表
     * @param dataInfoIdList
     * @return
     */
    List<DsInfo> getDsInfoListByIdList(List<Long> dataInfoIdList);

    void updateDsInfoStatus(@Param("dsInfoList") List<Long> dsInfoList,@Param("status")int status);

    List<DsInfo> queryByIds(@Param("idList") List<Long> notChecked);

    /**
     * 根据dataType和dataVersion查找数据源列表
     * @param dataType
     * @param dataVersion
     * @return
     */
    List<DsInfo> queryListByDataTypeAndVersion(@Param("dataType") String dataType,@Param("dataVersion") String dataVersion);

    /**
     * 根据dataTypeCode修改dataType和dataVersion
     * @param dataType
     * @param dataVersion
     * @param dataTypeCode
     */
    void updateDataTypeByDataTypeCode(@Param("dataType") String dataType,@Param("dataVersion") String dataVersion, @Param("dataTypeCode") Integer dataTypeCode);

}
