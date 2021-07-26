package com.dtstack.engine.remote.exception;

import java.util.Arrays;

/**
 * @Auther: dazhi
 * @Date: 2020/9/3 8:06 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class RemoteException extends RuntimeException {

    private static final long serialVersionUID = -248560728348945046L;
    private String msg;

    private Exception exception;

    public RemoteException(String msg){
        super(msg);
        this.msg = msg;
    }

    public RemoteException(Exception exception) {
        this.exception = exception;
    }

    @Override
    public String toString() {
        StringBuilder toString = new StringBuilder("RemoteException{").append("msg='").append(msg) ;
        if (exception != null) {
            toString.append('\'' + ", exception=").append(Arrays.toString(exception.getStackTrace()));
        }
        toString.append('}') ;
        return toString.toString();
    }
}
