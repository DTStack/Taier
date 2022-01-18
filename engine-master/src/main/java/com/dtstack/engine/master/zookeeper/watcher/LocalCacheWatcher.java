package com.dtstack.engine.master.zookeeper.watcher;

import com.dtstack.engine.master.utils.PathUtil;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * zk 回调事件：清理本地缓存
 *
 * @author qiuyun
 * @version 1.0
 * @date 2022-01-18 20:27
 */
public class LocalCacheWatcher implements CuratorWatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalCacheWatcher.class);

    public static LocalCacheWatcher getInstance() {
        return LocalCacheWatcherInstance.LOCAL_CACHE_WATCHER;
    }

    @Override
    public void process(WatchedEvent watchedEvent) throws Exception {
        LOGGER.info("receive event:{}", watchedEvent.toString());
        // 空事件不处理，只做监控 todo test
        if (Watcher.Event.EventType.None.equals(watchedEvent.getType())) {
            if (Watcher.Event.KeeperState.Expired.equals(watchedEvent.getState())) {
                LOGGER.info("clear all local cache...");
                // fixme LocalCacheUtil.removeAll();
            }
            return;
        }

        String path = watchedEvent.getPath();
        // todo test
        String[] pathSplit = PathUtil.splitPath(path);
        LOGGER.info("GROUP = {}, KEY = {}", pathSplit[1], pathSplit[2]);
        // fixme  LocalCacheUtil.removeLocal(pathSplit[1],pathSplit[2]);
    }

    private static class LocalCacheWatcherInstance {
        private static final LocalCacheWatcher LOCAL_CACHE_WATCHER = new LocalCacheWatcher();
    }
}
