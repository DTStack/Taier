package com.dtstack.engine.common.util;


import com.dtstack.dtcenter.common.util.AddressUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.ServerSocket;

public class NetUtils {

    private static final Log LOG = LogFactory.getLog(NetUtils.class);

    public static int getAvailablePortRange(String hostname, int portStart, int portEnd) {
        while (true) {
            if (!AddressUtil.telnet(hostname, portStart)) {
                LOG.warn("Akka availablePort port:" + portStart);
                return portStart;
            }
            portStart++;
            if (portStart < 0 || portStart > portEnd) {
                throw new IllegalArgumentException("Invalid port configuration. Port must be between 0" +
                        "and " + portEnd + ", but was " + portStart + ".");
            }
        }
    }

}