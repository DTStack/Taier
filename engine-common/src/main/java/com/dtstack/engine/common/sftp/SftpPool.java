package com.dtstack.engine.common.sftp;

import com.jcraft.jsch.ChannelSftp;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.LoggerFactory;

/**
 * company: www.dtstack.com
 * author: yuemo
 * create: 2020-01-19
 */
public class SftpPool {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SftpPool.class);

    private GenericObjectPool<ChannelSftp> pool;

    public SftpPool(SftpFactory factory) {
        this.pool = new GenericObjectPool<>(factory);
    }

    public SftpPool(SftpFactory factory, SftpPoolConfig sftpPoolConfig) {
        this.pool = new GenericObjectPool<>(factory, sftpPoolConfig);
    }

    public SftpPool(SftpFactory factory, int maxTotal, int maxIdle, int minIdle) {
        this.pool = new GenericObjectPool<>(factory, new SftpPoolConfig(maxTotal, maxIdle, minIdle));
    }

    /**
     * 获取一个sftp连接对象
     */
    public ChannelSftp borrowObject() {
        try {
            ChannelSftp channelSftp = pool.borrowObject();
            logger.info("从Sfpt连接池中获取一个连接channelSftp : " + channelSftp);
            return channelSftp;
        } catch (Exception e) {
            throw new RuntimeException("从Sfpt连接池中获取连接失败", e);
        }
    }

    /**
     * 归还一个sftp连接对象
     */
    public void returnObject(ChannelSftp channelSftp) {
        if (channelSftp != null) {
            pool.returnObject(channelSftp);
            logger.info("归还channelSftp到Sfpt连接池中 : " + channelSftp);
        }
    }

}
