package com.dtstack.rdos.engine.execution.base.enumeration;

/**
 * Created by sishu.yss on 2017/3/9.
 */
public enum Restoration {

    NO(0),YES(1);

    private int type;

    Restoration(int type){
        this.type = type;
    }

    public static Restoration getRestoration(int type){
        Restoration[] restorations = Restoration.values();
        for(Restoration restoration:restorations){
            if(restoration.type == type){
                return restoration;
            }
        }
        return null;
    }

    public int getVal(){
        return type;
    }
}
