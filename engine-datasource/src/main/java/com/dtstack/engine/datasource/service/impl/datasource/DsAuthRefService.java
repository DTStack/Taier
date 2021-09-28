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

package com.dtstack.engine.datasource.service.impl.datasource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dtstack.engine.datasource.common.constant.SystemConst;
import com.dtstack.engine.datasource.common.exception.ErrorCode;
import com.dtstack.engine.datasource.common.exception.PubSvcDefineException;
import com.dtstack.engine.datasource.common.utils.Collects;
import com.dtstack.engine.datasource.dao.bo.datasource.DsAuthRefBO;
import com.dtstack.engine.datasource.dao.mapper.datasource.DsAuthRefMapper;
import com.dtstack.engine.datasource.dao.po.datasource.DsAppList;
import com.dtstack.engine.datasource.dao.po.datasource.DsAuthRef;
import com.dtstack.engine.datasource.dao.po.datasource.DsImportRef;
import com.dtstack.engine.datasource.mapstruct.DsAppListStruct;
import com.dtstack.engine.datasource.param.datasource.ProductAuthParam;
import com.dtstack.engine.datasource.service.impl.BaseService;
import com.dtstack.engine.datasource.vo.datasource.AuthProductListVO;
import com.google.common.collect.Lists;
import org.apache.commons.collections.ListUtils;
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
public class DsAuthRefService extends BaseService<DsAuthRefMapper, DsAuthRef> {

   @Autowired
    private DsAppListService dsAppListService;

   @Autowired
   private DsImportRefService dsImportRefService;

   @Autowired
   private DsAppListStruct dsAppListStruct;


   /**
    * 根据产品Id获取数据源Id集合
    *
    * @param appTypes
    * @return
    */
   public List<Long> getDataIdsByAppTypes(List<Integer> appTypes) {
      return getBaseMapper().getDataIdByAppTypes(appTypes);
   }

   /**
    * 根据数据源id获取对应的数据源名称关系
    *
    * @param dataInfoIds
    * @return
    */
   public Map<Long, String> mapDaIdName(List<Long> dataInfoIds) {
      return getBaseMapper().mapDaIdName(dataInfoIds).stream().collect(Collectors
              .toMap(DsAuthRefBO::getDataInfoId, DsAuthRefBO::getProductNames));
   }

   /**
    * 根据数据源Id删除对应的关联关系
    *
    * @param dataInfoId
    * @return
    */
   public Boolean delByDsId(Long dataInfoId) {
      Objects.requireNonNull(dataInfoId);
      LambdaQueryWrapper<DsAuthRef> qw = Wrappers.lambdaQuery();
      return getBaseMapper().delete(qw.eq(DsAuthRef::getDataInfoId, dataInfoId)) > 0;
   }

   /**
    * 编辑时，获取授权产品界面
    *
    * @param dataInfoId
    * @return
    */
   public List<AuthProductListVO> getAuthProductList(Long dataInfoId) {
      Objects.requireNonNull(dataInfoId);
      List<Integer> dsAuthRefs = getCodeByDsId(dataInfoId);

      List<DsAppList> productListVOS = dsAppListService.list();
      return productListVOS.stream().map(t -> {
         AuthProductListVO authProductListVO = dsAppListStruct.dsApp2AuthProductListVO(t);
         Integer isAuth = dsAuthRefs.contains(t.getAppType()) ? SystemConst.IS_PRODUCT_AUTH : SystemConst.NOT_IS_PRODUCT_AUTH;
         authProductListVO.setIsAuth(isAuth);
         return authProductListVO;
      }).collect(Collectors.toList());
   }

   /**
    * 根据数据源Id获取对应的编码
    *
    * @param dataInfoId
    * @return
    */
   public List<Integer> getCodeByDsId(Long dataInfoId) {
      Objects.requireNonNull(dataInfoId);
      return lambdaQuery().eq(DsAuthRef::getDataInfoId, dataInfoId).list()
              .stream().map(DsAuthRef::getAppType).collect(Collectors.toList());
   }



   /**
    * 产品授权操作
    *
    * @param productAuthParam
    * @return
    */
   @Transactional(rollbackFor = Exception.class)
   public Boolean productAuth(ProductAuthParam productAuthParam) {
      //查询该数据源给哪些产品授权了
      List<Integer> appTypeList = this.getCodeByDsId(productAuthParam.getDataInfoId())
              .stream().distinct().collect(Collectors.toList());
      //获取要取消授权的产品
      List subtract = ListUtils.subtract(appTypeList, productAuthParam.getAppTypes());
      //判断该数据源是否给这些被取消授权的产品所引用
      if(subtract.size()>0){
         List<DsImportRef> dsImportRefs = dsImportRefService.lambdaQuery().
                 eq(DsImportRef::getDataInfoId, productAuthParam.getDataInfoId())
                 .eq(DsImportRef::isDeleted, 0)
                 .in(DsImportRef::getAppType, subtract).list();
         if(dsImportRefs.size()>0){
            throw new PubSvcDefineException(ErrorCode.CANCEL_AUTH_DATA_SOURCE_FAIL);
         }
      }
      // 删除对应数据源id的所有授权状态
      boolean deleteResult = this.remove(Wrappers.<DsAuthRef>update().eq("data_info_id", productAuthParam.getDataInfoId()));
      if (Collects.isEmpty(productAuthParam.getAppTypes())) {
         // 无授权产品直接返回结果
         return true;
      }
      List<DsAuthRef> dsAuthRefList = Lists.newArrayList();
      for (Integer appType : productAuthParam.getAppTypes()) {
         DsAuthRef authRef = new DsAuthRef();
         authRef.setAppType(appType);
         authRef.setDataInfoId(productAuthParam.getDataInfoId());
//         authRef.setCreateUserId(productAuthParam.getDtuicUserId());
         dsAuthRefList.add(authRef);
      }
      return this.saveBatch(dsAuthRefList);
   }

}
