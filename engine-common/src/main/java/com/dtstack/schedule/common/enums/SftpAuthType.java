package com.dtstack.schedule.common.enums;

/**
 * sftp校验方式
 *
 * @author sanyue
 * @date 2019/12/9
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
