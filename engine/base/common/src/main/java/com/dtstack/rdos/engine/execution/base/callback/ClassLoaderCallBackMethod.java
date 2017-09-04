package com.dtstack.rdos.engine.execution.base.callback;

/**
 * Created by sishu.yss on 2017/8/28.
 */
public class ClassLoaderCallBackMethod {

    public Object callback(ClassLoaderCallBack classLoaderCallBack,ClassLoader current,ClassLoader main,boolean isSet) throws Exception {
        Thread.currentThread().setContextClassLoader(current);
        Object result = classLoaderCallBack.execute();
        if(isSet){
            if(main == null){
                Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            }else{
                Thread.currentThread().setContextClassLoader(main);
            }
        }
        return result;
    }

}
