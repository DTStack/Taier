package com.dtstack.taier.datasource.plugin.common;

import com.dtstack.taier.datasource.api.exception.SourceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * @company: www.dtstack.com
 * @Author ：wangchuan
 * @Date ：Created in 下午3:11 2020/8/3
 * @Description：自定义连接池
 */
@Slf4j
public class Pool<T> implements Cloneable {

    /**
     * 连接池
     */
    protected GenericObjectPool<T> internalPool;

    public Pool(final GenericObjectPoolConfig poolConfig, PooledObjectFactory<T> factory) {
        initPool(poolConfig, factory);
    }

    private void initPool(final GenericObjectPoolConfig poolConfig, PooledObjectFactory<T> factory) {

        if (this.internalPool != null) {
            try {
                closeInternalPool();
            } catch (Exception e) {
                throw new SourceException(String.format("init pool error:%s", e.getMessage()), e);
            }
        }

        this.internalPool = new GenericObjectPool<T>(factory, poolConfig);
    }

    private void closeInternalPool() {
        try {
            internalPool.close();
        } catch (Exception e) {
            throw new SourceException(String.format("Could not destroy the pool:%s", e.getMessage()), e);
        }
    }

    /**
     * 从对象池中获取一个对象
     *
     * @return
     */
    public T getResource() {
        try {
            return internalPool.borrowObject();
        } catch (Exception e) {
            throw new SourceException(String.format("Could not get a resource from the pool:%s", e.getMessage()), e);
        }
    }

    /**
     * 对象使用完之后，归还到对象池
     *
     * @param resource
     */
    public void returnResource(final T resource) {
        if (resource != null) {
            returnResourceObject(resource);
        }
    }

    private void returnResourceObject(final T resource) {
        if (resource == null) {
            return;
        }
        try {
            internalPool.returnObject(resource);
        } catch (Exception e) {
            throw new SourceException(String.format("Could not return the resource to the pool,%s", e.getMessage()), e);
        }
    }

    /**
     * 销毁对象
     *
     * @param resource
     */
    public void returnBrokenResource(final T resource) {
        if (resource != null) {
            returnBrokenResourceObject(resource);
        }
    }

    private void returnBrokenResourceObject(T resource) {
        try {
            internalPool.invalidateObject(resource);
        } catch (Exception e) {
            throw new SourceException(String.format("Could not return the resource to the pool, %s", e.getMessage()), e);
        }
    }

    public void destroy() {
        closeInternalPool();
    }

    /**
     * @return 正在使用的数量
     */
    public int getNumActive() {
        if (poolInactive()) {
            return -1;
        }
        return this.internalPool.getNumActive();
    }

    /**
     * @return 闲置的数量
     */
    public int getNumIdle() {
        if (poolInactive()) {
            return -1;
        }
        return this.internalPool.getNumIdle();
    }

    public int getNumWaiters() {
        if (poolInactive()) {
            return -1;
        }
        return this.internalPool.getNumWaiters();
    }

    public long getMeanBorrowWaitTimeMillis() {
        if (poolInactive()) {
            return -1;
        }
        return this.internalPool.getMeanBorrowWaitTimeMillis();
    }

    public long getMaxBorrowWaitTimeMillis() {
        if (poolInactive()) {
            return -1;
        }
        return this.internalPool.getMaxBorrowWaitTimeMillis();
    }

    private boolean poolInactive() {
        return this.internalPool == null || this.internalPool.isClosed();
    }

    /**
     * 添加多少个连接给连接池
     *
     * @param count
     * @throws Exception
     */
    public void addObjects(int count) {
        try {
            for (int i = 0; i < count; i++) {
                this.internalPool.addObject();
            }
        } catch (Exception e) {
            throw new SourceException(String.format("Error trying to add idle objects,%s", e.getMessage()), e);
        }
    }
}
