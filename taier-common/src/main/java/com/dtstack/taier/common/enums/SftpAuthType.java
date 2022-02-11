package com.dtstack.taier.common.enums;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : hanbeikai
 * Date: 2021/12/16 12:13 上午
 * Description: No Description
 */
public enum SftpAuthType {

    /**
     * 密码校验
     */
    PASSWORD(1),
    /**
     * 密钥登录校验
     */
    RSA(2);

    private Integer type;

    SftpAuthType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
