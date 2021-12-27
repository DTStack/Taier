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

package com.dtstack.batch.mapstruct.vo;

import com.dtstack.batch.domain.RoleUser;
import com.dtstack.batch.vo.UserRolePermissionVO;
import com.dtstack.batch.web.role.vo.result.BatchRoleUserResultVO;
import com.dtstack.batch.web.security.vo.result.SecurityResultRoleUserAdd;
import com.dtstack.batch.web.security.vo.result.SecurityResultRoleUserRemove;
import com.dtstack.batch.web.security.vo.result.SecurityResultRoleUserUpdate;
import com.dtstack.batch.web.user.vo.result.BatchUserRolePermissionResultVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

@Mapper
public interface RoleUserMapstructTransfer {

    RoleUserMapstructTransfer INSTANCE = Mappers.getMapper(RoleUserMapstructTransfer.class);

    /**
     * com.dtstack.dtcenter.common.console.SecurityResult<Map<Long, List<RoleUser>>> -->  SecurityResult<Map<Long, List<BatchRoleUserResultVO>>>
     *
     * @param mapSecurityResult
     * @return
     */
    SecurityResultRoleUserAdd mapSecurityResultTOBatchRoleUserResultVO(com.dtstack.engine.common.console.SecurityResult<Map<Long, List<RoleUser>>> mapSecurityResult);


    /**
     * 前置转化:
     * Map<Long, List<RoleUser>> -->  Map<Long, List<BatchRoleUserResultVO>>
     *
     * @param roleUsers
     * @return
     */
    List<BatchRoleUserResultVO> roleUsersTOBatchRoleUserResultVOs(List<RoleUser> roleUsers);


    /**
     * Map<Long, List<RoleUser>> -->  Map<Long, List<BatchRoleUserResultVO>>
     *
     * @param roleUserMap
     * @return
     */
    Map<Long, List<BatchRoleUserResultVO>> roleUserMapTOBatchRoleUserResultVOMap(Map<Long, List<RoleUser>> roleUserMap);

    /**
     * com.dtstack.dtcenter.common.console.SecurityResult<Integer> -->  SecurityResult<Integer>
     *
     * @param integerSecurityResult
     * @return
     */
    SecurityResultRoleUserRemove integerSecurityResultTOMySecurityResult(com.dtstack.engine.common.console.SecurityResult<Integer> integerSecurityResult);


    /**
     * com.dtstack.dtcenter.common.console.SecurityResult<List<RoleUser>> -->  SecurityResult<List<BatchRoleUserResultVO>>
     *
     * @param listSecurityResult
     * @return
     */
    SecurityResultRoleUserUpdate listSecurityResultTOMySecurityResult(com.dtstack.engine.common.console.SecurityResult<List<RoleUser>> listSecurityResult);

    /**
     * List<UserRolePermissionVO>  -> List<BatchUserRolePermissionResultVO>
     *
     * @param userRolePermissionVOList
     * @return
     */
    List<BatchUserRolePermissionResultVO> userRolePermissionVOListToBatchUserRolePermissionResultVOList(List<UserRolePermissionVO> userRolePermissionVOList);

}
