package com.dtstack.engine.base.util;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * @program: engine-all
 * @author: wuren
 * @create: 2021/02/25
 **/
public class NetUtils {

    // ------------------------------------------------------------------------
    //  Lookup of to free ports
    // ------------------------------------------------------------------------

    /**
     * Inspired by Flink.
     * Find a non-occupied port.
     *
     * @return A non-occupied port.
     */
    public static int getAvailablePort() {
        for (int i = 0; i < 50; i++) {
            try (ServerSocket serverSocket = new ServerSocket(0)) {
                int port = serverSocket.getLocalPort();
                if (port != 0) {
                    return port;
                }
            } catch (IOException ignored) {
            }
        }

        throw new RuntimeException("Could not find a free permitted port on the machine.");
    }
}
