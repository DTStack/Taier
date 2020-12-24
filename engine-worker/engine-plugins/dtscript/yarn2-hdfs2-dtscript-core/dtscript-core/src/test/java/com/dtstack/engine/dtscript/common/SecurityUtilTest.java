package com.dtstack.engine.dtscript.common;

import com.dtstack.engine.api.domain.User;
import com.dtstack.engine.dtscript.DtYarnConfiguration;
import com.dtstack.engine.dtscript.container.DtContainer;
import org.apache.commons.math3.analysis.function.Power;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.UserGroupInformation;


import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.yarn.api.records.Token;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.net.InetSocketAddress;

/**
 * @description:
 * @program: engine-all
 * @author: lany
 * @create: 2020/11/30 20:32
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({DtContainer.class, DtYarnConfiguration.class,ConverterUtils.class, RPC.class,FileSystem.class, UserGroupInformation.class})
@PowerMockIgnore({"javax.net.ssl.*","org.apache.hadoop.security.UserGroupInformation.*"})
public class SecurityUtilTest {

    @Test
    public void testGetDelegationTokens() throws Exception {

        PowerMockito.mockStatic(UserGroupInformation.class);
        PowerMockito.when(UserGroupInformation.isSecurityEnabled()).thenReturn(true);

        YarnConfiguration yarnConfiguration = PowerMockito.mock(YarnConfiguration.class);
        YarnClient yarnClient = PowerMockito.mock(YarnClient.class);


        Credentials credentials = PowerMockito.mock(Credentials.class);
        PowerMockito.whenNew(Credentials.class).withNoArguments().thenReturn(credentials);
        credentials.addToken(Mockito.any(Text.class), Mockito.any(org.apache.hadoop.security.token.Token.class));

        String tokenRenewer = "renewer";

        PowerMockito.when(yarnConfiguration.get(Mockito.anyString())).thenReturn(tokenRenewer);

        PowerMockito.mockStatic(FileSystem.class);
        FileSystem fileSystem = PowerMockito.mock(FileSystem.class);
        PowerMockito.when(FileSystem.get(yarnConfiguration)).thenReturn(fileSystem);


        org.apache.hadoop.security.token.Token token = PowerMockito.mock(org.apache.hadoop.security.token.Token.class);

        PowerMockito.whenNew(InetSocketAddress.class);


        PowerMockito.mockStatic(ConverterUtils.class);
        PowerMockito.when(ConverterUtils.convertFromYarn(Mockito.mock(Token.class),Mockito.mock(InetSocketAddress.class))).thenReturn(token);



        SecurityUtil.getDelegationTokens(yarnConfiguration, yarnClient);
    }

}
