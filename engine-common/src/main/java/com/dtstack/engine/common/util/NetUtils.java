package com.dtstack.engine.common.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetUtils {

    private static final Logger LOG = LoggerFactory.getLogger(NetUtils.class);

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