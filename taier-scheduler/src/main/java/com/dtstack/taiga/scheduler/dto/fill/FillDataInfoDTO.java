package com.dtstack.taiga.scheduler.dto.fill;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/9/10 3:49 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class FillDataInfoDTO {

    private List<FillDataChooseTaskDTO> taskChooseList;

    private FillDataChooseTaskDTO rootTaskId;

    public FillDataInfoDTO(List<FillDataChooseTaskDTO> taskChooseList,
                           FillDataChooseTaskDTO rootTaskId) {
        this.taskChooseList = taskChooseList;
        this.rootTaskId = rootTaskId;
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

}
