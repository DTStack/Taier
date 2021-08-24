package com.dtstack.batch.controller;

import com.dtstack.batch.service.impl.BatchUserService;
import com.dtstack.batch.web.user.vo.query.BatchUserGetVO;
import com.dtstack.batch.web.user.vo.result.BatchGetUserByIdResultVO;
import com.dtstack.batch.web.user.vo.result.BatchUserGetUsersInTenantVO;
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

@Api(value = "用户管理", tags = {"用户管理"})
@RestController("batchUserController")
@RequestMapping(value =  "/api/rdos/common/user")
public class UserController {

    @Autowired
    private BatchUserService batchUserService;

    @ApiOperation(value = "获取租户下的用户")
    @PostMapping(value = "getUsersInTenant")
    public R<List<BatchUserGetUsersInTenantVO>> getUsersInTenant(@RequestBody(required = false) BatchUserGetVO baseParam) {

        return new APITemplate<List<BatchUserGetUsersInTenantVO>>() {
            @Override
            protected List<BatchUserGetUsersInTenantVO> process() throws BizException {
                return batchUserService.getUsersInTenant(baseParam.getTenantId());
            }
        }.execute();
    }

    @ApiOperation(value = "根据Id获取用户")
    @PostMapping(value = "getUserById")
    public R<BatchGetUserByIdResultVO> getUserById(@RequestBody(required = false) BatchUserGetVO baseParam) {

        return new APITemplate<BatchGetUserByIdResultVO>() {
            @Override
            protected BatchGetUserByIdResultVO process() throws BizException {
                return batchUserService.getUserById(baseParam.getTenantId(), baseParam.getUserId(), baseParam.getDtToken());
            }
        }.execute();
    }
}
