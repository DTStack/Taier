package com.dtstack.engine.dtscript.am;

import com.dtstack.engine.dtscript.api.ApplicationContainerProtocol;
import org.apache.hadoop.security.authorize.PolicyProvider;
import org.apache.hadoop.security.authorize.Service;

/**
 * Reason:
 * Date: 2019/1/7
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class DTPolicyProvider extends PolicyProvider {
    @Override
    public Service[] getServices() {
        return new Service[]{new Service("security.application.dtcontaioner.protocol.acl", ApplicationContainerProtocol.class)};
    }
}