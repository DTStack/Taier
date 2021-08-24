package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.param.ScheduleEngineProjectParam;
import com.dtstack.engine.api.vo.project.NotDeleteProjectVO;
import com.dtstack.engine.api.vo.project.ScheduleEngineProjectVO;
import com.dtstack.engine.master.impl.ProjectService;
import org.springframework.web.bind.annotation.RequestParam;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @RequestMapping(value = "/addProject", method = {RequestMethod.POST})
    public void addProject(@RequestBody ScheduleEngineProjectParam scheduleEngineProjectParam) {
        projectService.addProjectOrUpdate(scheduleEngineProjectParam);
    }

    @RequestMapping(value = "/findProject", method = {RequestMethod.POST})
    public ScheduleEngineProjectVO findProject(@RequestParam("projectId") Long projectId, @RequestParam("appType") Integer appType) {
       return projectService.findProject(projectId,appType);
    }

    @RequestMapping(value = "/getNotDeleteTaskByProjectId", method = {RequestMethod.POST})
    public List<NotDeleteProjectVO> getNotDeleteTaskByProjectId(@RequestParam("projectId") Long projectId, @RequestParam("appType") Integer appType) {
        return projectService.getNotDeleteTaskByProjectId(projectId,appType);
    }

    @RequestMapping(value = "/updateProject", method = {RequestMethod.POST})
    public void updateProject(@RequestBody ScheduleEngineProjectParam scheduleEngineProjectParam) {
        projectService.addProjectOrUpdate(scheduleEngineProjectParam);
    }

    @RequestMapping(value = "/deleteProject", method = {RequestMethod.POST})
    public void deleteProject(@RequestParam("projectId") Long projectId, @RequestParam("appType") Integer appType) {
        projectService.deleteProject(projectId,appType);
    }

    @RequestMapping(value = "/findFuzzyProjectByProjectAlias", method = {RequestMethod.POST})
    public List<ScheduleEngineProjectVO> findFuzzyProjectByProjectAlias(@RequestParam("name") String name,
                                                                        @RequestParam("appType") Integer appType,
                                                                        @RequestParam("dt_tenant_id") Long uicTenantId,
                                                                        @RequestParam("projectId") Long projectId ) {
        return projectService.findFuzzyProjectByProjectAlias(name, appType, uicTenantId,projectId);
    }

}
