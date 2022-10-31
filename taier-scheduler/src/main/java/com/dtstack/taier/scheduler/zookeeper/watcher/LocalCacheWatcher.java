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

package com.dtstack.taier.scheduler.zookeeper.watcher;

import com.dtstack.taier.scheduler.utils.LocalCacheUtil;
import com.dtstack.taier.scheduler.utils.PathUtil;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * zk 回调事件：清理本地缓存
 *
 * @author qiuyun
 * @version 1.0
 * @date 2022-01-18 20:27
 */
@Component
public class LocalCacheWatcher implements CuratorWatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalCacheWatcher.class);

    @Autowired
    private LocalCacheUtil localCacheUtil;

    public static LocalCacheWatcher getInstance() {
        return LocalCacheWatcherInstance.LOCAL_CACHE_WATCHER;
    }

    /**
     * zk 发现 path 路径发生变化，会回调该方法
     * @param watchedEvent
     * @throws Exception
     */
    @Override
    public void process(WatchedEvent watchedEvent) throws Exception {
        LOGGER.info("receive event:【{}】", watchedEvent.toString());
        if(null == localCacheUtil){
            return;
        }
        // 空事件不处理，只做监控
        if (Watcher.Event.EventType.None.equals(watchedEvent.getType())) {
            if (Watcher.Event.KeeperState.Expired.equals(watchedEvent.getState())) {
                LOGGER.info("clear all local cache...");
                localCacheUtil.removeLocalAll();
            }
            return;
        }
        String path = watchedEvent.getPath();
        String[] pathSplit = PathUtil.splitPath(path);
        LOGGER.info("GROUP={},KEY={},EVENT={}", pathSplit[1], pathSplit[2], watchedEvent);
        localCacheUtil.removeLocal(pathSplit[1], pathSplit[2]);
    }

    private static class LocalCacheWatcherInstance {
        private static final LocalCacheWatcher LOCAL_CACHE_WATCHER = new LocalCacheWatcher();
    }
}
