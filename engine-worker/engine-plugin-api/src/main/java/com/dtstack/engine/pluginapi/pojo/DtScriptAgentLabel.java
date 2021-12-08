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

package com.dtstack.engine.pluginapi.pojo;

/**
 * @author xinge
 */
public class DtScriptAgentLabel {

    private Boolean autoDeploy;

    private Boolean autoUpdate;

    private String id;

    private String name;
    /**
     * 主机标签，多个,分割
     */
    private String label;

    private String os;

    private String version;

    /**
     * 主机ip
     */
    private String localIp;


    public Boolean getAutoDeploy() {
        return autoDeploy;
    }

    public void setAutoDeploy(Boolean autoDeploy) {
        this.autoDeploy = autoDeploy;
    }

    public Boolean getAutoUpdate() {
        return autoUpdate;
    }

    public void setAutoUpdate(Boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLocalIp() {
        return localIp;
    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }

    @Override
    public String toString() {
        return "DtScriptAgentLabel{" +
                "autoDeploy=" + autoDeploy +
                ", autoUpdate=" + autoUpdate +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", label='" + label + '\'' +
                ", os='" + os + '\'' +
                ", version='" + version + '\'' +
                ", localIp='" + localIp + '\'' +
                '}';
    }
}
