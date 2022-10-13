package com.dtstack.taier.datasource.api.manager.list;

import com.dtstack.taier.datasource.api.classloader.ChildFirstClassLoader;
import com.dtstack.taier.datasource.api.constant.ConfigConstants;
import com.dtstack.taier.datasource.api.exception.InitializeException;
import com.dtstack.taier.datasource.api.manager.AbstractManager;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 用于管理 classloader, 每个插件仅维护一份 classloader
 * 1. 提供清理逻辑, 清除某一个插件对应的 classloader
 * 2. 提供 refresh 逻辑, 重新加载某一个 classloader
 * <p>
 * 1. 当插件包目录清除后, 清除对应当 classloader
 * 2. 当新增插件包目录后, 自动创建对应的 classloader
 *
 * @author ：wangchuan
 * date：Created in 14:56 2022/9/23
 * company: www.dtstack.com
 */
@Slf4j
public class ClassloaderManager extends AbstractManager {

    /**
     * plugin name -> ClassLoader
     */
    private Map<String, ClassLoader> classLoaderMap = new ConcurrentHashMap<>();

    /**
     * ClassLoader -> datasource type name
     */
    private Map<ClassLoader, String> pluginNameMap = new ConcurrentHashMap<>();

    /**
     * 插件包路径
     */
    private String pluginDir = String.format("%s/datasource-plugins/", System.getProperty("user.dir"));

    @Override
    public void open() throws InitializeException {
        String configPluginDir = getRuntimeContext().getConfig().getConfig(ConfigConstants.PLUGIN_DIR, String.class);
        // set plugin dir
        if (configPluginDir != null && configPluginDir.length() != 0) {
            pluginDir = configPluginDir;
        }
        // 初始化一遍
        runScheduleJob();
    }

    @Override
    public void close() {
        // help gc.
        classLoaderMap.clear();
        classLoaderMap = null;
        pluginNameMap.clear();
        pluginNameMap = null;
    }

    /**
     * 重新加载 classloader
     *
     * @param pluginName 插件名称
     */
    public void refreshClassloader(String pluginName) {
        synchronized (pluginName.intern()) {
            removeClassloader(pluginName);
            getClassloaderByPluginName(pluginName);
        }
    }

    @Override
    public void runScheduleJob() {
        if (pluginDir == null) {
            throw new RuntimeException("plugin dir is null.");
        }
        File pluginDirFile = new File(pluginDir);
        if (!pluginDirFile.exists()) {
            throw new RuntimeException(String.format("plugin dir [%s] does not exists.", pluginDir));
        }
        File[] pluginFiles = pluginDirFile.listFiles();

        if (pluginFiles == null || pluginFiles.length == 0) {
            log.warn("plugin dir [{}] is empty.", pluginDir);
            // 清理所有插件
            classLoaderMap.keySet().forEach(this::removeClassloader);
            return;
        }

        // 现存的所有插件
        List<String> existsPlugins = Arrays.stream(pluginFiles).map(File::getName).collect(Collectors.toList());

        // 缓存的所有插件
        List<String> cachePlugins = new ArrayList<>(classLoaderMap.keySet());
        // 清理不存在的插件
        cachePlugins.removeAll(existsPlugins);
        cachePlugins.forEach(this::removeClassloader);

        for (String pluginName : existsPlugins) {
            getClassloaderByPluginName(pluginName);
        }
    }

    /**
     * 清理 classloader, 需要将 classloader 加载的所有 client 清理掉
     *
     * @param pluginName 插件名称
     */
    public void removeClassloader(String pluginName) {
        log.info("start clear plugin [{}]...", pluginName);
        ClassLoader rmClassLoader = classLoaderMap.remove(pluginName);
        if (null != rmClassLoader) {
            pluginNameMap.remove(rmClassLoader);
            // 关闭插件对应的所有 client
            ClientManager clientManager = getManagerFactory().getManager(ClientManager.class);
            clientManager.destroyByPluginName(pluginName);

            // 停止 proxyThread
            ProxyThreadPoolManager proxyThreadPoolManager = getManagerFactory().getManager(ProxyThreadPoolManager.class);
            proxyThreadPoolManager.destroyByClassloader(rmClassLoader);
        }
        log.info("clear plugin [{}] end.", pluginName);
    }

    @Override
    public boolean isSchedule() {
        return true;
    }

    @Override
    public int getSchedulePeriod() {
        return 20;
    }

    @Override
    public String getScheduleJobName() {
        return "classloader-refresh";
    }

    /**
     * 根据插件名称获取对应的 classloader
     *
     * @param pluginName 插件名称
     * @return classloader
     */
    public ClassLoader getClassloaderByPluginName(String pluginName) {
        return classLoaderMap.computeIfAbsent(pluginName, key -> {
            try {
                ClassLoader classLoad = getClassLoad(pluginName);
                pluginNameMap.put(classLoad, pluginName);
                return classLoad;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 根据 classloader 获取对应的插件名称
     *
     * @param classLoader classloader
     * @return 插件名称
     */
    public String getPluginNameByClassloader(ClassLoader classLoader) {
        return pluginNameMap.get(classLoader);
    }

    /**
     * 获取对应的 classloader
     *
     * @param pluginName 插件目录名称
     * @return classloader
     * @throws Exception 异常信息
     */
    private ClassLoader getClassLoad(String pluginName) throws Exception {
        synchronized (pluginName.intern()) {
            if (classLoaderMap.get(pluginName) != null) {
                return classLoaderMap.get(pluginName);
            }
            File file = getFileByPluginName(pluginName);
            File[] files = file.listFiles();
            if (files == null || files.length == 0) {
                throw new RuntimeException(String.format("The plugin [%s] folder setting is abnormal, please handle it again", pluginName));
            }

            List<URL> urlList = new ArrayList<>();
            for (File f : files) {
                String jarName = f.getName();
                if (f.isFile() && jarName.endsWith(".jar")) {
                    log.info("Data source plugin pulls Jar package, plugin name: {}, Jar package name: {}", pluginName, jarName);
                    urlList.add(f.toURI().toURL());
                }
            }
            return new ChildFirstClassLoader(urlList.toArray(new URL[0]), Thread.currentThread().getContextClassLoader());
        }
    }

    /**
     * 根据插件名称获取插件目录
     *
     * @param pluginName 插件名称
     * @return 插件目录
     */
    private File getFileByPluginName(String pluginName) {
        String plugin = String.format("%s%s%s", pluginDir, File.separator, pluginName).replaceAll("/+", "/");
        File finPut = new File(plugin);
        if (!finPut.exists()) {
            throw new RuntimeException(String.format("%s directory not found", plugin));
        }
        return finPut;
    }
}
