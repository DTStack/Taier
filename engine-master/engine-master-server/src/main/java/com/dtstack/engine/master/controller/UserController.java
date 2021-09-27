package com.dtstack.engine.master.controller;

import com.dtstack.engine.master.vo.user.UserVO;
import com.dtstack.engine.master.impl.UserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/5/11 7:51 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@RestController
@RequestMapping("/node/user")
@Api(value = "/node/user", tags = {"用户接口"})
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/queryUser", method = {RequestMethod.POST})
    public List<UserVO> findUser(){
        return userService.findAllUser();
    }



}
