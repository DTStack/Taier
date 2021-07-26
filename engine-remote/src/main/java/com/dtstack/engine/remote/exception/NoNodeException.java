package com.dtstack.engine.remote.exception;

/**
 * @Auther: dazhi
 * @Date: 2020/9/8 4:53 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class NoNodeException extends RemoteException {
    private static final long serialVersionUID = -1535086411779812805L;

    public NoNodeException(String msg) {
        super(msg);
    }

}
