/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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