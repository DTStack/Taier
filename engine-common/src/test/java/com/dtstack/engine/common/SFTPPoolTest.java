package com.dtstack.engine.common;

import com.dtstack.engine.common.enums.SftpType;
import com.dtstack.engine.common.sftp.SftpConfig;
import com.dtstack.engine.common.sftp.SftpFileManage;
import com.jcraft.jsch.ChannelSftp;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class SFTPPoolTest {

    private final static String HOST = "172.16.100.251";
    private final static Integer PORT = 22;
    private final static String USERNAME = "root";
    private final static String PASSWORD = "abc123";

    private final static Integer COUNT = 100;

    private static SftpConfig config;

    {
        config = new SftpConfig();
        config.setHost(HOST);
        config.setPort(PORT);
        config.setUsername(USERNAME);
        config.setPassword(PASSWORD);
        config.setIsUsePool(true);
        config.setMinIdle(1);
        config.setMaxTotal(10);
        config.setTimeout(10000);
        config.setPath("/data/sftp");
        config.setAuth(SftpType.PASSWORD_AUTHENTICATION.getType());
    }

    @Test
    public void testGetInstance() throws Exception {
        SftpFileManage manage = new SftpFileManage(config);
        CountDownLatch countDownLatch = new CountDownLatch(COUNT);

        for (int i = 0; i < COUNT; ++i) {
            new Thread(
                    () -> {
                    ChannelSftp channelSftp = manage.getChannelSftp();
                    manage.close(channelSftp);
                    countDownLatch.countDown();
                }
            ).start();
            Thread.sleep(100);
        }
        countDownLatch.await();
        System.out.println("get count: " + manage.getCount.get());
        System.out.println("return count: " + manage.returnCount.get());
        System.out.println("finish sftp connection");
    }
}
