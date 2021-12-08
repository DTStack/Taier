package com.dtstack.engine.master.dto.fill;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/9/10 3:49 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class FillDataInfoDTO {

    private List<FillDataChooseProjectDTO> projectList;

    private List<FillDataChooseTaskDTO> taskChooseList;

    private FillDataChooseTaskDTO rootTaskId;

    private List<FillDataChooseTaskDTO> whiteList;

    private List<FillDataChooseTaskDTO> blackList;


    public FillDataInfoDTO(List<FillDataChooseProjectDTO> projectList,
                           List<FillDataChooseTaskDTO> taskChooseList,
                           FillDataChooseTaskDTO rootTaskId,
                           List<FillDataChooseTaskDTO> whiteList,
                           List<FillDataChooseTaskDTO> blackList) {
        this.projectList = projectList;
        this.taskChooseList = taskChooseList;
        this.whiteList = whiteList;
        this.blackList = blackList;
        this.rootTaskId = rootTaskId;
    }

    public List<FillDataChooseProjectDTO> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<FillDataChooseProjectDTO> projectList) {
        this.projectList = projectList;
    }

    public List<FillDataChooseTaskDTO> getTaskChooseList() {
        return taskChooseList;
    }

    public void setTaskChooseList(List<FillDataChooseTaskDTO> taskChooseList) {
        this.taskChooseList = taskChooseList;
    }

    public FillDataChooseTaskDTO getRootTaskId() {
        return rootTaskId;
    }

    public void setRootTaskId(FillDataChooseTaskDTO rootTaskId) {
        this.rootTaskId = rootTaskId;
    }

    public List<FillDataChooseTaskDTO> getWhiteList() {
        return whiteList;
    }

    public void setWhiteList(List<FillDataChooseTaskDTO> whiteList) {
        this.whiteList = whiteList;
    }

    public List<FillDataChooseTaskDTO> getBlackList() {
        return blackList;
    }

    public void setBlackList(List<FillDataChooseTaskDTO> blackList) {
        this.blackList = blackList;
    }
}
