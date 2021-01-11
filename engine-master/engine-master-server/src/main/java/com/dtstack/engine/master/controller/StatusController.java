package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.dto.UserDTO;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.router.DtHeader;
import com.dtstack.engine.master.router.DtRequestParam;
import com.dtstack.engine.master.router.login.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/node")
public class StatusController {

    @Autowired
    private SessionUtil sessionUtil;

    @RequestMapping(value = "/status")
    public String status(@DtHeader(value = "cookie", cookie = "dt_token") String dtToken) {
        UserDTO user = sessionUtil.getUser(dtToken, UserDTO.class);
        if (null == user) {
            throw new RdosDefineException(ErrorCode.USER_IS_NULL);
        }
        //root 用户才能进控制台
        if (null != user.getRootUser() && 1 == user.getRootUser()) {
            return "SUCCESS";
        }

        throw new RdosDefineException(ErrorCode.PERMISSION_LIMIT);
    }

    @RequestMapping(value = "/value")
    public String value(@DtRequestParam("value") String value) {
        return value;
    }

}
