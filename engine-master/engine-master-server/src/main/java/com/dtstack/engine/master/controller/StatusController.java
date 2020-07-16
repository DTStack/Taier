package com.dtstack.engine.master.controller;

import com.dtstack.engine.master.router.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/node")
public class StatusController {

    @RequestMapping(value = "/status")
    public String status() {
        return "SUCCESS";
    }


    @RequestMapping(value = "/value")
    public Integer value(@RequestParam("value") Integer value) {
        return value;
    }

}
