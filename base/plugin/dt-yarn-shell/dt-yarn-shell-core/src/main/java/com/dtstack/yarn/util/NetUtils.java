package com.dtstack.yarn.util;

import java.io.IOException;
import java.net.Socket;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/10/26
 */
public class NetUtils {

    public static boolean checkRemotePortUsed(String host, int port) {
        try {
            Socket socket = new Socket(host, port);
            socket.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static int getAvailablePortRange(String host, int port) {
        while (true) {
            if (!checkRemotePortUsed(host, port)) {
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
        System.out.println(getAvailablePortRange("0.0.0.0",6767));
    }
}
