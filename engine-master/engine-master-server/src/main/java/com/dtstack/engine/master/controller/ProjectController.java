package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.annotation.Param;
import com.dtstack.engine.master.impl.ProjectService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/node/project")
@Api(value = "/node/project", tags = {"项目接口"})
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @RequestMapping(value="/updateSchedule", method = {RequestMethod.POST})
    public void updateSchedule(@RequestParam("projectId")Long projectId, @RequestParam("appType")Integer appType, @RequestParam("scheduleStatus")Integer scheduleStatus) {
        projectService.updateSchedule(projectId, appType, scheduleStatus);
    }
}
