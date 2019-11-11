package com.dtstack.engine.common.callback;

/**
 * Created by sishu.yss on 2017/8/28.
 */
public class ClassLoaderCallBackMethod {

    public static <M> M callbackAndReset(ClassLoaderCallBack<M> classLoaderCallBack, ClassLoader toSetClassLoader, boolean reset) throws Exception {

        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(toSetClassLoader);
        M result = classLoaderCallBack.execute();
        if(reset){
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
        return result;
    }

}
