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

package com.dtstack.batch.controller;

import com.dtstack.batch.domain.Role;
import com.dtstack.batch.mapstruct.vo.RoleMapstructTransfer;
import com.dtstack.batch.service.auth.AuthCode;
import com.dtstack.batch.service.impl.RoleService;
import com.dtstack.batch.vo.RoleVO;
import com.dtstack.batch.web.pager.PageResult;
import com.dtstack.batch.web.role.vo.query.BatchRoleAddVO;
import com.dtstack.batch.web.role.vo.query.BatchRoleDeleteVO;
import com.dtstack.batch.web.role.vo.query.BatchRolePageQueryVO;
import com.dtstack.batch.web.role.vo.result.BatchRoleUpdateResultVO;
import dt.insight.plat.autoconfigure.web.security.permissions.annotation.Security;
import dt.insight.plat.lang.coc.template.APITemplate;
import dt.insight.plat.lang.exception.biz.BizException;
import dt.insight.plat.lang.web.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "角色管理", tags = {"角色管理"})
@RestController
@RequestMapping(value = "/api/rdos/common/role")
public class RoleController {

    @Autowired
    private RoleService roleService;


    @ApiOperation(value = "新建或修改角色")
    @Security(code = AuthCode.PROJECT_ROLE_EDIT)
    @PostMapping(value = "addOrUpdateRole")
    public R<BatchRoleUpdateResultVO> addOrUpdateRole(@RequestBody BatchRoleAddVO vo) {
        return new APITemplate<BatchRoleUpdateResultVO>() {
            @Override
            protected BatchRoleUpdateResultVO process() throws BizException {
                Role role = roleService.addOrUpdateRole(RoleMapstructTransfer.INSTANCE.roleAddVOToRoleVO(vo), vo.getUserId());
                return RoleMapstructTransfer.INSTANCE.roleToBatchRoleUpdateResultVO(role);
            }
        }.execute();
    }


    @ApiOperation(value = "删除角色")
    @Security(code = AuthCode.PROJECT_ROLE_EDIT)
    @PostMapping(value = "deleteRole")
    public R<Integer> deleteRole(@RequestBody BatchRoleDeleteVO vo) {
        return new APITemplate<Integer>() {
            @Override
            protected Integer process() throws BizException {
                return roleService.deleteRole(vo.getRoleId(), vo.getProjectId(), vo.getTenantId(), vo.getUserId());
            }
        }.execute();
    }


    @ApiOperation(value = "分页查询角色列表")
    @Security(code = AuthCode.PROJECT_ROLE_QUERY)
    @PostMapping(value = "pageQuery")
    public R<PageResult<List<BatchRoleUpdateResultVO>>> pageQuery(@RequestBody BatchRolePageQueryVO vo) {
        return new APITemplate<PageResult<List<BatchRoleUpdateResultVO>>>() {
            @Override
            protected PageResult<List<BatchRoleUpdateResultVO>> process() throws BizException {
                PageResult<List<RoleVO>> listPageResult = roleService.pageQuery(vo.getTenantId(), vo.getProjectId(), vo.getCurrentPage(), vo.getPageSize(), vo.getName());
                return RoleMapstructTransfer.INSTANCE.batchRoleUpdateResultVOToRoleVO(listPageResult);
            }
        }.execute();
    }
}
