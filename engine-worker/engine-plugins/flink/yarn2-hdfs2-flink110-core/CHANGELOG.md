
flink
----------------------
org.apache.flink.yarn.YarnClusterClientFactory

```
更改项:
    1. 重载getClusterDescriptor方法，增加入参yarnConfiguration，yarnClient init使用传入的yarnConfiguration
    2. 重载createClusterDescriptor方法，增加入参yarnConfiguration，调用1重载的getClusterDescriptor方法
```


org.apache.flink.yarn.YarnClusterDescriptor

```
更改项:
    1. 更改获取日志配置文件的逻辑，在$engine_home/flinkconf目录下获取文件并且只拿log4j.properties或者logback.xml
    2. 将yarn-site.xml和hdfs-site.xml加到shipfile及classpath
    3. 变更判断是否开启kerberos逻辑，增加isSecurityEnabled方法，通过openKerberos参数判断是否开启kerberos
    4. 客户端获取jobGraph, 代码详情参照getJobGraph方法
    5. 初始化pluginsDir, 代码片段如下
        String pluginsDir = System.getenv().getOrDefault(
    	        ConfigConstants.ENV_FLINK_PLUGINS_DIR,
    	        ConfigConstants.DEFAULT_FLINK_PLUGINS_DIRS);
    	File pluginsFile = new File(pluginsDir);
    	if (!pluginsFile.exists()) {
    	    pluginsFile.mkdirs();
        }
```


org.apache.flink.optimizer.plantranslate.JobGraphGenerator

```
更改项:
    1. addUserArtifactEntries方法中在finally中清理flink-distributed-cache文件
```


org.apache.flink.client.program.PackagedProgram

```
更改项:
    1. PackagedProgram构造方法中添加以下代码，判断是否使用缓存的classLoader
        boolean cache;
        String dtstackCache = configuration.getString(ClassLoaderType.CLASSLOADER_DTSTACK_CACHE, ClassLoaderType.CLASSLOADER_DTSTACK_CACHE_FALSE);
        if (dtstackCache.equalsIgnoreCase(ClassLoaderType.CLASSLOADER_DTSTACK_CACHE_FALSE)){
        	cache = false;
        } else {
        	cache = true;
        }
```


org.apache.flink.client.ClientUtils

```
更改项:
    1. 重载buildUserCodeClassLoader方法，增加入参cache，表示是否使用缓存的classloader。添加以下代码片段
        if (cache) {
			Arrays.sort(urls, Comparator.comparing(URL::toString));
			String[] jarMd5s = new String[urls.length];
			for (int i = 0; i < urls.length; ++i) {
				try (FileInputStream inputStream = new FileInputStream(urls[i].getPath())){
					jarMd5s[i] = DigestUtils.md5Hex(inputStream);
				} catch (Exception e) {
					throw new RdosDefineException("Exceptions appears when read file:" + e);
				}
			}
			String md5KeyParent = StringUtils.join(jarMd5s, "_");
			return cacheClassLoader.computeIfAbsent(md5KeyParent, k -> classLoader);
		} else {
			return classLoader;
		}
    2. buildUserCodeClassLoader方法中增加解析childFirstLoaderPatterns
    3. 重载submitJob方法， 增加入参timeout和timeUnit，get方法设置超时时间
```


org.apache.flink.runtime.execution.librarycache.FlinkUserCodeClassLoaders

```
更改项:
    1. 重载create方法，增加入参childFirstPatterns
    2. 重载childFirst方法，增加入参childFirstPatterns
    3. 扩展ParentFirstClassLoader，增加根据childFirstPatterns加载类
```


org.apache.flink.util.ChildFirstClassLoader

```
更改项:
    1. 重载ChildFirstClassLoader构造方法，增加入参childFirstPatterns
    2. 更改loadClass方法中类加载逻辑，现判断是否由子加载器加载在判断是否父加载器加载
```

calcite
----------------------
org.apache.calcite.plan.volcano.VolcanoRuleCall
```
更改项:
    1. 去掉onMatch方法中以下代码，解决npe问题
        if (LOGGER.isDebugEnabled()) {
            this.generatedRelList = new ArrayList<>();
        }
        
        if (LOGGER.isDebugEnabled()) {
            if (generatedRelList.isEmpty()) {
              LOGGER.debug("call#{} generated 0 successors.", id);
            } else {
              LOGGER.debug(
                  "call#{} generated {} successors: {}",
                  id, generatedRelList.size(), generatedRelList);
            }
            this.generatedRelList = null;
        }
```