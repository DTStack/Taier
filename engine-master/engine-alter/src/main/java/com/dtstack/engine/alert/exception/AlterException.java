package com.dtstack.engine.alert.exception;

/**
 * @Auther: dazhi
 * @Date: 2021/1/15 3:36 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AlterException extends Exception {

    private static final long serialVersionUID = -6624959466110637919L;
    private String massage;

    public AlterException(String message) {
        super(message);
    }

    public String getMassage() {
        return massage;
    }

    public void setMassage(String massage) {
        this.massage = massage;
    }
}
