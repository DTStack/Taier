package com.dtstack.engine.api.vo.task;

/**
 * @Auther: dazhi
 * @Date: 2021/7/21 1:37 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class TaskTypeVO {

    /**
     * 任务类型名称
     */
    private String name;

    /**
     * 枚举名称
     */
    private String enumName;

    /**
     * 任务类型对应的code
     */
    private Integer code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getEnumName() {
        return enumName;
    }

    public void setEnumName(String enumName) {
        this.enumName = enumName;
    }
}
