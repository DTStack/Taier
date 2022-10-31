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

package com.dtstack.taier.script.common;

import com.dtstack.taier.script.api.ScriptConstants;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeysPublic;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.SaslRpcServer;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.security.AMRMTokenIdentifier;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;

public class SecurityUtil {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityUtil.class);
    private static final String DT_SHELL_USER = "DT_SHELL_USER";

    public static ByteBuffer getDelegationTokens(YarnConfiguration yarnconf, YarnClient yarnClient)
            throws IOException, YarnException {
        if (!UserGroupInformation.isSecurityEnabled()) {
            return null;
        }
        // Set up security tokens for launching our ApplicationMaster container.
        Credentials credentials = new Credentials();
        String tokenRenewer = yarnconf.get(YarnConfiguration.RM_PRINCIPAL);
        if (tokenRenewer == null || tokenRenewer.length() == 0) {
            throw new IOException(
                    "Can't get Master kerberos principal for the RM to use as renewer");
        }
        FileSystem fs = FileSystem.get(yarnconf);
        // getting tokens for the default file-system.
        final Token<?> tokens[] =
                fs.addDelegationTokens(tokenRenewer, credentials);
        if (tokens != null) {
            for (Token<?> token : tokens) {
                LOG.info("Got dt for " + fs.getUri() + "; " + token);
            }
        }
        InetSocketAddress rmAddress = yarnconf.getSocketAddr(YarnConfiguration.RM_ADDRESS,
                YarnConfiguration.DEFAULT_RM_ADDRESS,
                YarnConfiguration.DEFAULT_RM_PORT);
        // getting yarn resource manager token
        Token<TokenIdentifier> token = ConverterUtils.convertFromYarn(
                yarnClient.getRMDelegationToken(new Text(tokenRenewer)),
                rmAddress);
        LOG.info("Added RM delegation token: " + token);
        credentials.addToken(token.getService(), token);

        DataOutputBuffer dob = new DataOutputBuffer();
        credentials.writeTokenStorageToStream(dob);
        return ByteBuffer.wrap(dob.getData(), 0, dob.getLength());
    }

    public static Configuration disableSecureRpc(Configuration conf) {
        conf = new Configuration(conf);
        conf.set(CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTHENTICATION,
                SaslRpcServer.AuthMethod.SIMPLE.toString());
        conf.setBoolean(CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTHORIZATION, false);
        return conf;
    }

    public static UserGroupInformation setupUserGroupInformation()
            throws IOException {
        YarnConfiguration conf = new YarnConfiguration();
        conf.addResource(new Path(ScriptConstants.YARN_CONFIGURATION));
        UserGroupInformation.setConfiguration(conf);
        if (!UserGroupInformation.isSecurityEnabled()) {
            return UserGroupInformation.getCurrentUser();
        }

        UserGroupInformation ugi = UserGroupInformation.createRemoteUser(DT_SHELL_USER);
        for (Token token : UserGroupInformation.getCurrentUser().getTokens()) {
            ugi.addToken(token);
        }

        LOG.info("UserGroupInformation: " + ugi);
        return ugi;
    }

    public static ByteBuffer copyUserToken() throws IOException {
        if (!UserGroupInformation.isSecurityEnabled()) {
            return null;
        }

        LOG.info("Setup container token for security hadoop cluster");
        Credentials credentials =
                UserGroupInformation.getCurrentUser().getCredentials();
        DataOutputBuffer dob = new DataOutputBuffer();
        credentials.writeTokenStorageToStream(dob);
        // Now remove the AM->RM token so that containers cannot access it.
        Iterator<Token<?>> iter = credentials.getAllTokens().iterator();
        while (iter.hasNext()) {
            Token<?> token = iter.next();
            if (token.getKind().equals(AMRMTokenIdentifier.KIND_NAME)) {
                iter.remove();
            }
        }

        return ByteBuffer.wrap(dob.getData(), 0, dob.getLength());
    }

    public static void setupUserEnv(Map<String, String> env) {
        if (!UserGroupInformation.isSecurityEnabled()) {
            return;
        }
        try {
            env.put(DT_SHELL_USER, UserGroupInformation.getCurrentUser().getShortUserName());
        } catch (IOException e) {
            LOG.warn("Failed to setup env: " + DT_SHELL_USER, e);
        }
    }
}