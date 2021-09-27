package com.dtstack.engine.master.controller;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/node")
public class StatusController {

    @RequestMapping(value = "/status")
    public String status(@RequestParam("dt_token") String dtToken) {
        return "SUCCESS";
    }

    @RequestMapping(value = "/value")
    public String value(@RequestParam("value") String value) {
        return value;
    }

}
