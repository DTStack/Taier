package com.dtstack.engine.common.enums;

/**
 * Reason:
 * Date: 2017/11/10
 * Company: www.dtstack.com
 * @author xuchao
 */
public enum EDeployType {

    STANDALONE(0, "standalone"), YARN(1, "yarn"), MESOS(2, "mesos");

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

    public static EDeployType getDeployType(String name){
        for(EDeployType eDeployType : EDeployType.values()){
            if(eDeployType.getName().equals(name.toLowerCase())){
                return eDeployType;
            }
        }

        return null;
    }

    public static EDeployType getDeployType(int type){
        for(EDeployType eType : EDeployType.values()){
            if(eType.getType() == type){
                return eType;
            }
        }

        return null;
    }
}
