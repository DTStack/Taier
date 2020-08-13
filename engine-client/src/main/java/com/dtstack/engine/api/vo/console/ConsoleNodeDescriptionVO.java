package com.dtstack.engine.api.vo.console;

/**
 * @Auther: dazhi
 * @Date: 2020/7/30 2:43 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ConsoleNodeDescriptionVO {

    private int memory;
    private int virtualCores;
    private int usedMemory;
    private int usedVirtualCores;

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public int getVirtualCores() {
        return virtualCores;
    }

    public void setVirtualCores(int virtualCores) {
        this.virtualCores = virtualCores;
    }

    public int getUsedMemory() {
        return usedMemory;
    }

    public void setUsedMemory(int usedMemory) {
        this.usedMemory = usedMemory;
    }

    public int getUsedVirtualCores() {
        return usedVirtualCores;
    }

    public void setUsedVirtualCores(int usedVirtualCores) {
        this.usedVirtualCores = usedVirtualCores;
    }
}
