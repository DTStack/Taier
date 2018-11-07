package com.dtstack.yarn.util;

import com.dtstack.yarn.container.DtContainer;
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

    private static final Log LOG = LogFactory.getLog(DtContainer.class);

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

    public static int getAvailablePortRange(int port) {
        while (true) {
            if (!checkPortUsed(port)) {
                LOG.warn("Container availablePort port:" + port);
                return port;
            }
            port++;
            if (port < 0 || port > 65535) {
                throw new IllegalArgumentException("Invalid port configuration. Port must be between 0" +
                        "and 65535, but was " + port + ".");
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(getAvailablePortRange(6767));
    }
}