package com.dtstack.engine.common.sftp;

import com.jcraft.jsch.ChannelSftp;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * company: www.dtstack.com
 * author: yuemo
 * create: 2020-01-19
 */
public class SftpPoolConfig extends GenericObjectPoolConfig<ChannelSftp> {

    private int maxTotal = DEFAULT_MAX_TOTAL;
    private int maxIdle = DEFAULT_MAX_IDLE;
    private int minIdle = DEFAULT_MIN_IDLE;

    public SftpPoolConfig() {
        super();
    }

    public SftpPoolConfig(int maxTotal, int maxIdle, int minIdle) {
        super();
        this.maxTotal = maxTotal;
        this.maxIdle = maxIdle;
        this.minIdle = minIdle;
    }

    @Override
    public int getMaxTotal() {
        return maxTotal;
    }
    @Override
    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }
    @Override
    public int getMaxIdle() {
        return maxIdle;
    }
    @Override
    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }
    @Override
    public int getMinIdle() {
        return minIdle;
    }
    @Override
    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

}
