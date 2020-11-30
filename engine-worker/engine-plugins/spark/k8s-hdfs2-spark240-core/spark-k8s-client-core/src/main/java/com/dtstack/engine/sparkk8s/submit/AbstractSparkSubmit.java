/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.engine.sparkk8s.submit;

import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.sftp.SftpConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.k8s.submit.ClientArguments;
import org.apache.spark.deploy.k8s.submit.DtKubernetesClientApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Date: 2020/7/9
 * Company: www.dtstack.com
 * @author maqi
 */
public abstract class AbstractSparkSubmit implements SparkSubmit {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractSparkSubmit.class);

    // 用户jar存储到镜像中的默认文件夹
    public static final String DEFAULT_USERJAR_LOCATION = "/opt/dtstack/userjar";
    public static final String SFTP_PREFIX = "sftp://";
    public static final String SFTP_FLAG = "sftp_";
    public static final String SFTP_REMOTE_PATH_KEY = "sftp_remotePath";
    public static final String SFTP_LOCAL_PATH_KEY = "sftp_localPath";
    public static final String LOCAL_PREFIX = "local://";

    /**
     * 通过将sftp配置绑定到环境变量，来选择是否下载user jar
     * @param sftpDir
     * @param sparkConf
     */
    public void fillSftpConfig(String sftpDir, SparkConf sparkConf, SftpConfig sftpConfig) {
        try {
            Field[] fields = SftpConfig.class.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.get(sftpConfig) != null) {
                    sparkConf.set(SFTP_FLAG + field.getName(), field.get(sftpConfig).toString());
                }
            }
        } catch (Exception e){
            throw new RdosDefineException(e);
        }

//        sftpConf.forEach((k, v) -> sparkConf.set(SFTP_FLAG + k, v));
        sparkConf.set(SFTP_REMOTE_PATH_KEY, sftpDir);
        sparkConf.set(SFTP_LOCAL_PATH_KEY, DEFAULT_USERJAR_LOCATION);
    }

    /**
     *  sftp文件会下载到镜像的指定路径下，重命名为镜像内部的地址。
     *
     * @param jarPath
     * @return
     */
    public String getJarImagePath(String jarPath) {
        if (!jarPath.startsWith(SFTP_PREFIX)) {
            throw new RdosDefineException("spark jar path protocol must be " + SFTP_PREFIX);
        }
        String jarName = StringUtils.substring(jarPath, jarPath.lastIndexOf("/"));
        String jarUrl = LOCAL_PREFIX + DEFAULT_USERJAR_LOCATION + jarName;
        LOG.info("the storage location of user jar packages in the image is :{} ", jarUrl);
        return jarUrl;
    }

    public JobResult runJobReturnResult(List<String> argList, SparkConf sparkConf) {
        try {
            DtKubernetesClientApplication k8sClientApp = new DtKubernetesClientApplication();
            ClientArguments clientArguments = ClientArguments.fromCommandLineArgs(argList.toArray(new String[argList.size()]));
            String appId = k8sClientApp.run(clientArguments, sparkConf);
            return JobResult.createSuccessResult(appId.toString());
        } catch (Exception ex) {
            return JobResult.createErrorResult("submit job get unknown error\n" + ExceptionUtil.getErrorMessage(ex));
        }
    }
}
