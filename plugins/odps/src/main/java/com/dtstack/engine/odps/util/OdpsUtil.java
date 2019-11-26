package com.dtstack.engine.odps.util;

import com.aliyun.odps.Odps;
import com.aliyun.odps.account.Account;
import com.aliyun.odps.account.AliyunAccount;
import com.dtstack.engine.common.exception.RdosException;
import com.dtstack.engine.odps.constant.ConfigConstant;
import org.apache.commons.lang3.StringUtils;
import java.util.Map;

public class OdpsUtil {
    public static Odps initOdps(Map<String,String> odpsConfig) {
        String odpsServer = odpsConfig.get(ConfigConstant.ODPS_SERVER);
        if(StringUtils.isBlank(odpsServer)) {
            odpsServer = ConfigConstant.DEFAULT_ODPS_SERVER;
        }

        String accessId = odpsConfig.get(ConfigConstant.ACCESS_ID);
        if(StringUtils.isBlank(accessId)) {
            throw new RdosException("accessId is required");
        }

        String accessKey = odpsConfig.get(ConfigConstant.ACCESS_KEY);
        if(StringUtils.isBlank(accessKey)) {
            throw new RdosException("accessKey is required");
        }

        String project = odpsConfig.get(ConfigConstant.PROJECT);
        if(StringUtils.isBlank(project)) {
            throw new RdosException("project is required");
        }

        String packageAuthorizedProject = odpsConfig.get(ConfigConstant.PACKAGE_AUTHORIZED_PROJECT);

        String defaultProject;
        if(StringUtils.isBlank(packageAuthorizedProject)) {
            defaultProject = project;
        } else {
            defaultProject = packageAuthorizedProject;
        }

        String accountType = odpsConfig.get(ConfigConstant.ACCOUNT_TYPE);
        if(StringUtils.isBlank(accountType)) {
            accountType = ConfigConstant.DEFAULT_ACCOUNT_TYPE;
        }

        Account account = null;
        if (accountType.equalsIgnoreCase(ConfigConstant.DEFAULT_ACCOUNT_TYPE)) {
            account = new AliyunAccount(accessId, accessKey);
        } else {
            throw new RdosException(String.format("unsupported account type:[%s]. Current only support aliyun, taobao.", accountType));
        }

        Odps odps = new Odps(account);
        odps.getRestClient().setConnectTimeout(3);
        odps.getRestClient().setReadTimeout(3);
        odps.getRestClient().setRetryTimes(2);
        odps.setDefaultProject(defaultProject);
        odps.setEndpoint(odpsServer);

        return odps;
    }

}
