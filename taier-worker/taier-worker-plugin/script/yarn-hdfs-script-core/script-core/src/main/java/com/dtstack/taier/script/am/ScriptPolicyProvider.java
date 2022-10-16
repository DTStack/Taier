package com.dtstack.taier.script.am;

import com.dtstack.taier.script.api.ApplicationContainerProtocol;
import org.apache.hadoop.security.authorize.PolicyProvider;
import org.apache.hadoop.security.authorize.Service;

/**
 * Reason:
 * Date: 2019/1/7
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class ScriptPolicyProvider extends PolicyProvider {
    @Override
    public Service[] getServices() {
        return new Service[]{new Service("security.application.container.protocol.acl", ApplicationContainerProtocol.class)};
    }
}