package com.dtstack.engine.master.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/")
public class StatusController {

    @RequestMapping(value = "/status", method = RequestMethod.POST)
    public String status() {
        return "SUCCESS";
    }
}
