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

package com.dtstack.taier.datasource.plugin.odps.pool;

import com.aliyun.odps.Odps;
import com.aliyun.odps.Tables;
import com.dtstack.taier.datasource.plugin.odps.OdpsClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * @company: www.dtstack.com
 * @Author ：wangchuan
 * @Date ：Created in 下午3:15 2020/8/3
 * @Description：
 */
@Slf4j
public class OdpsPoolFactory implements PooledObjectFactory<Odps> {
    private String odpsServer;

    private String accessId;

    private String accessKey;

    private String project;

    private String packageAuthorizedProject;

    private String accountType;

    public OdpsPoolFactory(OdpsPoolConfig config) {
        this.odpsServer = config.getOdpsServer();
        this.accessId = config.getAccessId();
        this.accessKey = config.getAccessKey();
        this.project = config.getProject();
        this.packageAuthorizedProject = config.getPackageAuthorizedProject();
        this.accountType = config.getAccountType();
    }

    /**
     * 当对象池中没有多余的对象可以用的时候，调用此方法。
     * @return
     * @throws Exception
     */
    @Override
    public PooledObject<Odps> makeObject() throws Exception {
        Odps odps = OdpsClient.initOdps(odpsServer, accessId, accessKey, project, packageAuthorizedProject, accountType);
        // 连接池中的连接对象
        return new DefaultPooledObject<>(odps);
    }

    /**
     * 销毁
     * @param pooledObject
     * @throws Exception
     */
    @Override
    public void destroyObject(PooledObject<Odps> pooledObject) throws Exception {
        passivateObject(pooledObject);
        pooledObject.markAbandoned();
    }

    /**
     * 功能描述：判断连接对象是否有效，有效返回 true，无效返回 false
     * 什么时候会调用此方法
     * 1：从连接池中获取连接的时候，参数 testOnBorrow 或者 testOnCreate 中有一个 配置 为 true 时，
     * 则调用 factory.validateObject() 方法.
     * 2：将连接返还给连接池的时候，参数 testOnReturn，配置为 true 时，调用此方法.
     * 3：连接回收线程，回收连接的时候，参数 testWhileIdle，配置为 true 时，调用此方法.
     * @param pooledObject
     * @return
     */
    @Override
    public boolean validateObject(PooledObject<Odps> pooledObject) {
        Odps odps = pooledObject.getObject();
        try {
            Tables tables = odps.tables();
            tables.iterator().hasNext();
            return true;
        } catch (Exception e) {
            log.error("check odps connect error..{}", e.getMessage(), e);
        }
        return false;
    }

    /**
     * 功能描述：激活资源对象
     * 什么时候会调用此方法
     * 1：从连接池中获取连接的时候
     *  2：连接回收线程，连接资源的时候，根据配置的 testWhileIdle 参数，
     *  判断 是否执行 factory.activateObject()方法，true 执行，false 不执行
     * @param pooledObject
     * @throws Exception
     */
    @Override
    public void activateObject(PooledObject<Odps> pooledObject) throws Exception {
        Odps odps = pooledObject.getObject();
        //测试连接一下，使其没有空闲
        try {
            Tables tables = odps.tables();
            tables.iterator().hasNext();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 功能描述：钝化资源对象
     * 将连接返还给连接池时，调用此方法。
     * @param pooledObject
     * @throws Exception
     */
    @Override
    public void passivateObject(PooledObject<Odps> pooledObject) throws Exception {
        // nothing
    }
}
