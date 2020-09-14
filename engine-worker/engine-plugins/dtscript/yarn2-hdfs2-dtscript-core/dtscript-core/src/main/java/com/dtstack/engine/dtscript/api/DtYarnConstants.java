package com.dtstack.engine.dtscript.api;

import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.util.Shell;
import org.apache.hadoop.yarn.api.ApplicationConstants;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public interface DtYarnConstants {

  String LEARNING_JOB_CONFIGURATION = "core-site.xml";

  String APP_MASTER_JAR = "AppMaster.jar";

  String WORKER = "worker";

  String PROXY_USER_NAME = "dtProxyUserName";

  enum Environment {
    HADOOP_USER_NAME("HADOOP_USER_NAME"),

    APP_TYPE("APP_TYPE"),

    XLEARNING_CONTAINER_MAX_MEMORY("XLEARNING_MAX_MEM"),

    XLEARNING_TF_ROLE("TF_ROLE"),

    XLEARNING_TF_INDEX("TF_INDEX"),

    XLEARNING_STAGING_LOCATION("XLEARNING_STAGING_LOCATION"),

    APP_JAR_LOCATION("APP_JAR_LOCATION"),

    FILES_LOCATION("FILES_LOCATION"),

    XLEARNING_JOB_CONF_LOCATION("XLEARNING_JOB_CONF_LOCATION"),

    CACHE_FILE_LOCATION("CACHE_FILE_LOCATION"),

    DT_EXEC_CMD("DT_EXEC_CMD"),

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
