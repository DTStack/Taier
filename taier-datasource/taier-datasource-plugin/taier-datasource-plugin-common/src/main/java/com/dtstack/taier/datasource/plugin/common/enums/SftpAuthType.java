package com.dtstack.taier.datasource.plugin.common.enums;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 13:04 2020/8/22
 * @Description：TODO
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
