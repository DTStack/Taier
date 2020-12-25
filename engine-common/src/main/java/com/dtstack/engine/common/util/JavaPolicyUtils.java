package com.dtstack.engine.common.util;

import com.dtstack.schedule.common.util.Xml2JsonUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * @author yuebai
 * @date 2020-12-25
 */
public class JavaPolicyUtils {

    public static void checkJavaPolicy() {
        String policyPath = System.getProperty("java.security.policy");
        if (StringUtils.isBlank(policyPath)) {
            throw new RuntimeException("启动参数上请加上java.security.policy 命令");
        }
        File policyFile = new File(policyPath);
        if (!policyFile.exists()) {
            throw new RuntimeException(String.format("启动参数上java.security.policy 文件路径 %s 不正确", policyPath));
        }
        try {
            boolean isKrb5ConfRead = false;
            String policyConfig = Xml2JsonUtil.readFile(policyFile);
            if (!StringUtils.isBlank(policyConfig)) {
                String[] split = policyConfig.split(";");
                for (String conf : split) {
                    String trimConf = conf.replace("\n", "").trim();
                    if (trimConf.startsWith("permission") && trimConf.contains("java.security.krb5.conf")) {
                        isKrb5ConfRead = trimConf.contains("read");
                    }
                }
            }
            if (!isKrb5ConfRead) {
                throw new RuntimeException(policyPath + " java.security.policy 不为read");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
