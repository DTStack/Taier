package com.dtstack.engine.dtscript.execution.odps.util;

import com.aliyun.odps.Odps;
import com.aliyun.odps.account.Account;
import com.aliyun.odps.account.AliyunAccount;
import com.dtstack.engine.common.exception.RdosException;
import org.apache.commons.lang3.StringUtils;
import java.util.Map;

import static com.dtstack.engine.dtscript.execution.odps.constant.ConfigConstant.*;

public class OdpsUtil {
    public static Odps initOdps(Map<String,String> odpsConfig) {
        String odpsServer = odpsConfig.get(ODPS_SERVER);
        if(StringUtils.isBlank(odpsServer)) {
            odpsServer = DEFAULT_ODPS_SERVER;
        }

        String accessId = odpsConfig.get(ACCESS_ID);
        if(StringUtils.isBlank(accessId)) {
            throw new RdosException("accessId is required");
        }

        String accessKey = odpsConfig.get(ACCESS_KEY);
        if(StringUtils.isBlank(accessKey)) {
            throw new RdosException("accessKey is required");
        }

        String project = odpsConfig.get(PROJECT);
        if(StringUtils.isBlank(project)) {
            throw new RdosException("project is required");
        }

        String packageAuthorizedProject = odpsConfig.get(PACKAGE_AUTHORIZED_PROJECT);

        String defaultProject;
        if(StringUtils.isBlank(packageAuthorizedProject)) {
            defaultProject = project;
        } else {
            defaultProject = packageAuthorizedProject;
        }

        String accountType = odpsConfig.get(ACCOUNT_TYPE);
        if(StringUtils.isBlank(accountType)) {
            accountType = DEFAULT_ACCOUNT_TYPE;
        }

        Account account = null;
        if (accountType.equalsIgnoreCase(DEFAULT_ACCOUNT_TYPE)) {
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
