package com.dtstack.engine.dtscript.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/10/26
 */
public class NetUtils {

    private static final Log LOG = LogFactory.getLog(NetUtils.class);

    public static boolean checkPortUsed(int port) {
        try {
            ServerSocket socket = new ServerSocket(port);
            socket.close();
            return false;
        } catch (IOException e) {
            LOG.warn("Invalid port:" + port + " configuration");
            return true;
        }
    }

    public static int getAvailablePortRange(int portStart, int portEnd) {
        while (true) {
            if (!checkPortUsed(portStart)) {
                LOG.warn("Container availablePort port:" + portStart);
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