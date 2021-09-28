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

import com.dtstack.batch.mapstruct.vo.PermissionMapstructTransfer;
import com.dtstack.batch.service.auth.AuthCode;
import com.dtstack.batch.service.impl.PermissionService;
import com.dtstack.batch.vo.RoleVO;
import com.dtstack.batch.web.permission.vo.query.BatchPermissionVO;
import com.dtstack.batch.web.permission.vo.result.BatchTreeNodeResultVO;
import com.dtstack.batch.web.role.vo.result.BatchRoleResultVO;
import com.dtstack.dtcenter.common.tree.TreeNode;
import dt.insight.plat.autoconfigure.web.security.permissions.annotation.Security;
import dt.insight.plat.lang.coc.template.APITemplate;
import dt.insight.plat.lang.web.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "权限管理", tags = {"权限管理"})
@RestController
@RequestMapping(value = "/api/rdos/common/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @PostMapping(value = "getPermissionIdsByRoleId")
    @ApiOperation("根据角色id获取角色下所有的权限点Id")
    @Security(code = AuthCode.PROJECT_ROLE_QUERY)
    public R<BatchRoleResultVO> getPermissionIdsByRoleId(@RequestBody BatchPermissionVO vo) {
        return new APITemplate<BatchRoleResultVO>() {
            @Override
            protected BatchRoleResultVO process() {
                RoleVO permissionIdsByRoleId = permissionService.getPermissionIdsByRoleId(vo.getRoleId());
                return PermissionMapstructTransfer.INSTANCE.RoleVOToBatchRoleResultVO(permissionIdsByRoleId);
            }
        }.execute();
    }

    @PostMapping(value = "tree")
    @ApiOperation("权限树")
    @Security(code = AuthCode.PROJECT_ROLE_QUERY)
    public R<BatchTreeNodeResultVO> tree() {
        return new APITemplate<BatchTreeNodeResultVO>() {
            @Override
            protected BatchTreeNodeResultVO process() {
                TreeNode tree = permissionService.tree();
                return PermissionMapstructTransfer.INSTANCE.treeNodeToBatchTreeNodeResultVO(tree);
            }
        }.execute();
    }
}
