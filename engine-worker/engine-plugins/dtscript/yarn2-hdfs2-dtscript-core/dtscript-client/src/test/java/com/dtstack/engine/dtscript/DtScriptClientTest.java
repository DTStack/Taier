package com.dtstack.engine.dtscript;

import com.dtstack.engine.dtscript.client.Client;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Properties;

import static org.powermock.api.mockito.PowerMockito.when;


/**
 * @description:
 * @program: engine-all
 * @author: lany
 * @create: 2020/11/19 09:52
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Client.class})
@PowerMockIgnore("javax.net.ssl.*")
public class DtScriptClientTest {

    @Test
    public void testInit() throws Exception {
        DtScriptClient dtScriptClient = PowerMockito.mock(DtScriptClient.class);
        PowerMockito.whenNew(DtScriptClient.class).withNoArguments().thenReturn(dtScriptClient);

//        PowerMockito.doNothing().when(dtScriptClient.init());
    }

    public void testProcessSubmitJobWithType() throws Exception {
        DtScriptClient dtScriptClient = PowerMockito.mock(DtScriptClient.class);

        PowerMockito.whenNew(DtScriptClient.class).withNoArguments().thenReturn(dtScriptClient);


    }

}
