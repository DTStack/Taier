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

package com.dtstack.taier.script.api;

import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.util.Shell;
import org.apache.hadoop.yarn.api.ApplicationConstants;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public interface ScriptConstants {

  String YARN_CONFIGURATION = "core-site.xml";

  String SCRIPT_CONFIGURATION = "script-site.xml";

  String WORKER = "worker";

  String GPU = "yarn.io/gpu";

  String ENV_PRINCIPAL = "_PRINCIPAL";
  String ENV_GATEWAY_PORT = "_GATEWAY_PORT";

  String PYTHON_GATEWAY_PATH = "yarn3-hdfs3-gateway.jar";
  String LOCALIZED_GATEWAY_PATH = "gateway.jar";
  String LOCALIZED_KEYTAB_PATH = "krb5.keytab";
  String LOCALIZED_KR5B_PATH = "krb5.conf";
  String SCRIPT_ENV_PREFIX = "script.container.env.";

  enum Environment {
    HADOOP_USER_NAME("HADOOP_USER_NAME"),

    SCRIPT_SHIP_FILES("SCRIPT_SHIP_FILES"),

    APP_TYPE("APP_TYPE"),

    APP_ENV("APP_ENV"),
    
    PROJECT_TYPE("PROJECT_TYPE"),

    SCRIPT_CONTAINER_MAX_MEMORY("SCRIPT_MAX_MEMORY"),

    SCRIPT_CONTAIENR_GPU_NUM("GPU_NUM"),

    SCRIPT_TF_ROLE("TF_ROLE"),

    SCRIPT_TF_INDEX("TF_INDEX"),

    SCRIPT_STAGING_LOCATION("SCRIPT_STAGING_LOCATION"),

    APP_JAR_LOCATION("APP_JAR_LOCATION"),

    SCRIPT_FILES("FILES_LOCATION"),

    YARN_JOB_CONF_LOCATION("YARN_JOB_CONF_LOCATION"),

    LOG4J_JOB_CONF_LOCATION("LOG4J_JOB_CONF_LOCATION"),

    SCRIPT_JOB_CONF_LOCATION("SCRIPT_JOB_CONF_LOCATION"),

    CACHE_FILE_LOCATION("CACHE_FILE_LOCATION"),

    EXEC_CMD("EXEC_CMD"),

    USER_PATH("USER_PATH"),

    APPMASTER_HOST("APPMASTER_HOST"),

    APPMASTER_PORT("APPMASTER_PORT"),

    APP_ID("APP_ID"),

    APP_ATTEMPTID("APP_ATTEMPTID"),

    OUTPUTS("OUTPUTS"),

    INPUTS("INPUTS");

    private final String variable;

    Environment(String variable) {
      this.variable = variable;
    }

    public String key() {
      return variable;
    }

    @Override
    public String toString() {
      return variable;
    }

    public String dollar() {
      if (Shell.WINDOWS) {
        return "%" + variable + "%";
      } else {
        return "$" + variable;
      }
    }

    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public String doubleDollar() {
      return ApplicationConstants.PARAMETER_EXPANSION_LEFT +
          variable +
          ApplicationConstants.PARAMETER_EXPANSION_RIGHT;
    }
  }
}
