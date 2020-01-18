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


package com.dtstack.engine.kylin;

import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.kylin.constraint.ConfigConstraint;
import com.dtstack.engine.kylin.enums.EBuildType;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jiangbo
 * @date 2019/7/2
 */
public class KylinConfig {

    public static final String HOSP_PORT_REGEX = "(http://)*(?<hostPort>[^:]+:\\d+)(/.*)*";

    public static final Pattern HOSP_PORT_PATTERN = Pattern.compile(HOSP_PORT_REGEX);

    private String hostPort;

    private String username;

    private String password;

    private String cubeName;

    private String buildType;

    private Long startTime;

    private Long endTime;

    private Map<String, Object> connectParams;

    public static KylinConfig buildWithProperties(Properties prop){
        return new KylinConfig().setHostPort(prop.getProperty(ConfigConstraint.KEY_HOST_PORT))
                .setUsername(prop.getProperty(ConfigConstraint.KEY_USERNAME))
                .setPassword(prop.getProperty(ConfigConstraint.KEY_PASSWORD))
                .setConnectParams((Map<String, Object>) prop.get(ConfigConstraint.KEY_CONNECT_PARAMS))
                .setBuildType(prop.getProperty(ConfigConstraint.KEY_BUILD_TYPE))
                .setCubeName(prop.getProperty(ConfigConstraint.KEY_CUBE_NAME))
                .setStartTime((Long)prop.get(ConfigConstraint.KEY_START_TIME))
                .setEndTime((Long)prop.get(ConfigConstraint.KEY_END_TIME));
    }

    public String getCubeName() {
        return cubeName;
    }

    public KylinConfig setCubeName(String cubeName) {
        if(StringUtils.isEmpty(cubeName)){
            throw new RdosDefineException("Must specify cubeName", ErrorCode.INVALID_PARAMETERS);
        }

        this.cubeName = cubeName;
        return this;
    }

    public Map<String, Object> getConnectParams() {
        return connectParams;
    }

    public KylinConfig setConnectParams(Map<String, Object> connectParams) {
        this.connectParams = connectParams;
        return this;
    }

    public String getHostPort() {
        return hostPort;
    }

    public KylinConfig setHostPort(String hostPort) {
        if(StringUtils.isEmpty(hostPort)){
            throw new RdosDefineException("Must specify hostPort", ErrorCode.INVALID_PARAMETERS);
        }

        this.hostPort = getFormatUrl(hostPort);
        return this;
    }

    public String getFormatUrl(String hostPort){
        if(!hostPort.matches(HOSP_PORT_REGEX)){
            throw new RdosDefineException("", ErrorCode.INVALID_PARAMETERS);
        }

        Matcher matcher = HOSP_PORT_PATTERN.matcher(hostPort);
        if (matcher.find()) {
            hostPort = "http://" + matcher.group("hostPort");
        }

        return hostPort;
    }

    public String getUsername() {
        return username;
    }

    public KylinConfig setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public KylinConfig setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getBuildType() {
        return buildType;
    }

    public KylinConfig setBuildType(String buildType) {
        EBuildType type = EBuildType.getType(buildType);
        this.buildType = type.name();
        return this;
    }

    public Long getStartTime() {
        return startTime;
    }

    public KylinConfig setStartTime(Long startTime) {
        this.startTime = startTime;
        return this;
    }

    public Long getEndTime() {
        return endTime;
    }

    public KylinConfig setEndTime(Long endTime) {
        this.endTime = endTime;
        return this;
    }
}
