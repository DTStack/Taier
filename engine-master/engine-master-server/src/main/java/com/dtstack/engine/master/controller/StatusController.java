package com.dtstack.engine.master.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/")
public class StatusController {

    @GetMapping(value = "/status")
    public Object status() {
        return "Subbb";
    }
}
