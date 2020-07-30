package com.dtstack.engine.api.vo.console;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2020/7/30 2:40 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ConsoleClusterResourcesVO {

    private List<ConsoleNodeDescriptionVO> yarn;
    private List<ConsoleTaskManagerDescriptionVO> flink;

    public List<ConsoleNodeDescriptionVO> getYarn() {
        return yarn;
    }

    public void setYarn(List<ConsoleNodeDescriptionVO> yarn) {
        this.yarn = yarn;
    }

    public List<ConsoleTaskManagerDescriptionVO> getFlink() {
        return flink;
    }

    public void setFlink(List<ConsoleTaskManagerDescriptionVO> flink) {
        this.flink = flink;
    }
}
