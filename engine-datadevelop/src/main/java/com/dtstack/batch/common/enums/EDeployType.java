package com.dtstack.batch.common.enums;

/**
 * Reason:
 * Date: 2017/11/10
 * Company: www.dtstack.com
 * @author xuchao
 */
public enum EDeployType {

    /**
     *
     */
    STANDALONE(0, "standlone"),

    /**
     *
     */
    YARN(1, "yarn"),

    /**
     *
     */
    MESOS(2, "mesos"),

    /**
     *
     */
    LIBRA(3, "libra");

    int type;

    String name;

    EDeployType(int type, String name){
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
