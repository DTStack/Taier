package com.dtstack.engine.dtscript.container;

import com.dtstack.engine.dtscript.DtYarnConfiguration;
import com.dtstack.engine.dtscript.am.AppArguments;
import com.dtstack.engine.dtscript.am.ApplicationContainerListener;
import com.dtstack.engine.dtscript.am.ApplicationMaster;
import com.dtstack.engine.dtscript.am.ApplicationMessageService;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.security.authorize.ServiceAuthorizationManager;
import org.apache.hadoop.yarn.client.api.async.AMRMClientAsync;
import org.apache.hadoop.yarn.client.api.async.NMClientAsync;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Map;

/**
 * @description:
 * @program: engine-all
 * @author: lany
 * @create: 2020/11/26 19:31
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({AppArguments.class, AMRMClientAsync.class,
        NMClientAsync.class, ApplicationMaster.class, ApplicationMessageService.class,
        System.class,
        NetUtils.class, ApplicationContainerListener.class})
@PowerMockIgnore({"javax.net.ssl.*"})
public class ContainerTest {




    @Test
    public void testInit() throws Exception{

    }

}
