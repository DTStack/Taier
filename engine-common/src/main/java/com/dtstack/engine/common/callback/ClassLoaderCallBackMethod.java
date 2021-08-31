package com.dtstack.engine.common.callback;

/**
 * Created by sishu.yss on 2017/8/28.
 */
public class ClassLoaderCallBackMethod {

    public static <M> M callbackAndReset(CallBack<M> callBack, ClassLoader toSetClassLoader, boolean reset) throws Exception {

        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(toSetClassLoader);
            return callBack.execute();
        }finally {
            if(reset){
                Thread.currentThread().setContextClassLoader(oldClassLoader);
            }
        }
    }

}
