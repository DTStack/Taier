package com.dtstack.rdos.engine.execution.base.callback;

/**
 * Created by sishu.yss on 2017/8/28.
 */
public class ClassLoaderCallBackMethod<T> {

    public T callback(ClassLoaderCallBack<T> classLoaderCallBack, ClassLoader current, ClassLoader main, boolean isSet) throws Exception {
        Thread.currentThread().setContextClassLoader(current);
        T result = classLoaderCallBack.execute();
        if(isSet){
            if(main == null){
                Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            }else{
                Thread.currentThread().setContextClassLoader(main);
            }
        }
        return result;
    }

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
