package com.dtstack.yarn.am;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.KerberosInfo;
import org.apache.hadoop.security.SecurityInfo;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.security.token.TokenInfo;
import org.apache.hadoop.security.token.TokenSelector;

import java.lang.annotation.Annotation;

/**
 * Reason:
 * Date: 2019/1/5
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class CustomerSecurityInfo extends SecurityInfo {

    @Override
    public KerberosInfo getKerberosInfo(Class<?> protocol, Configuration conf) {
        return new KerberosInfo() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }
            @Override
            public String serverPrincipal() {
                return "dt.ipc.server.principal";
            }
            @Override
            public String clientPrincipal() {
                return null;
            }
        };
    }

    @Override
    public TokenInfo getTokenInfo(Class<?> protocol, Configuration conf) {
        return new TokenInfo() {
            @Override
            public Class<? extends TokenSelector<? extends
                                TokenIdentifier>> value() {
                return DTTokenSelector.class;
            }
            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }
        };
    }
}
