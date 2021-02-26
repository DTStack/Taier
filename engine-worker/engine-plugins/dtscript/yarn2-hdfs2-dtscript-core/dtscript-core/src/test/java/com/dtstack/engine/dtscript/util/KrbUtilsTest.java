package com.dtstack.engine.dtscript.util;

import com.dtstack.engine.dtscript.api.DtYarnConstants;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;


public class KrbUtilsTest {

    @Test
    public void testHasKrb5() {
        Map<String, String> env = new HashMap<>();
        Assert.assertFalse(KrbUtils.hasKrb(env));
        env.put(DtYarnConstants.ENV_PRINCIPAL, "foo@foo.com");
        Assert.assertTrue(KrbUtils.hasKrb(env));
    }
}