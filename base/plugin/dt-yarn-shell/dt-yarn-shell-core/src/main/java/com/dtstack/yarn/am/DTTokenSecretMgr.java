package com.dtstack.yarn.am;

import org.apache.hadoop.security.token.SecretManager;
import org.apache.hadoop.yarn.security.ContainerTokenIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reason:
 * Date: 2019/1/4
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class DTTokenSecretMgr extends SecretManager<DTTokenIdentifier> {

    private static final Logger LOG = LoggerFactory.getLogger(DTTokenSecretMgr.class);
    @Override
    public byte[] createPassword(DTTokenIdentifier id) {
        LOG.warn("---------create password");
        return id.getBytes();
    }

    @Override
    public byte[] retrievePassword(DTTokenIdentifier id)
            throws InvalidToken {
        LOG.warn("-----------retrievePassword");
        return id.getBytes();
    }

    @Override
    public DTTokenIdentifier createIdentifier() {
        LOG.warn("-------ContainerTokenIdentifier");
        return new DTTokenIdentifier();
    }
}
