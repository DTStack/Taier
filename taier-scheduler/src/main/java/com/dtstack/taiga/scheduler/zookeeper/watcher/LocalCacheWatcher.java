package com.dtstack.taiga.scheduler.zookeeper.watcher;

import com.dtstack.taiga.scheduler.utils.LocalCacheUtil;
import com.dtstack.taiga.scheduler.utils.PathUtil;
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

    /**
     * zk 发现 path 路径发生变化，会回调该方法
     * @param watchedEvent
     * @throws Exception
     */
    @Override
    public void process(WatchedEvent watchedEvent) throws Exception {
        LOGGER.info("receive event:【{}】", watchedEvent.toString());
        // 空事件不处理，只做监控
        if (Watcher.Event.EventType.None.equals(watchedEvent.getType())) {
            if (Watcher.Event.KeeperState.Expired.equals(watchedEvent.getState())) {
                LOGGER.info("clear all local cache...");
                LocalCacheUtil.removeLocalAll();
            }
            return;
        }
        String path = watchedEvent.getPath();
        String[] pathSplit = PathUtil.splitPath(path);
        LOGGER.info("GROUP={},KEY={},EVENT={}", pathSplit[1], pathSplit[2], watchedEvent);
        LocalCacheUtil.removeLocal(pathSplit[1], pathSplit[2]);
    }

    private static class LocalCacheWatcherInstance {
        private static final LocalCacheWatcher LOCAL_CACHE_WATCHER = new LocalCacheWatcher();
    }
}
