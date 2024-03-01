---
title: 本地启动
sidebar_label: 本地启动
---

# 本地启动

## IDEA本地启动

- 下载代码

```shell
$ git clone https://github.com/DTStack/Taier
```

- 本地启动需要提前编译好Taier插件包，在根目录执行编译脚本，脚本编译完成后项目后会生成对应的插件目录

```shell
$ build/mvn-build-plugins.sh
```

- 插件目录

```shell 
|-- worker-plugins  
|-- datasource-plugins
```

- 构建前端项目文件  
  前端文件默认指向到data-develop模块下的/resource/static/ 下 需要构建出来才能正常访问页面
```java
   public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/static/");
        registry.addResourceHandler("/images/**").addResourceLocations("classpath:/static/images/");
        registry.addResourceHandler("/assets/**").addResourceLocations("classpath:/static/assets/");
        registry.addResourceHandler("swagger-ui.html").addResourceLocations(
                "classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations(
                "classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/taier/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        super.addResourceHandlers(registry);
    }
```

- 前端文件构建分为2种方式
  1. 通过datadevelop.sh脚本构建  
     datadevelop.sh会构建前端脚本文件并复制到data-develop模块下的/resource/static/的目录下
    ```shell
    $ build/mvn-build-datadevelop.sh
    ```

     static目录结构
    ```shell
    static/
    ├── assets
    ├── images
    ├── static
    ├── **.js
    ├── **.js
    ```
  :::tip  
  如果datadevelop.sh脚本构建失败 请检查是否是node代理仓库无法访问导致
  :::
  2. 手动通过npm构建  
     参考 [源码编译](./quickstart/build.md)  
     编译完成之后 将taier-ui下`dist`目录拷贝至data-develop模块下的`/resource/`下  
     将`dist`文件目录重命名为`static`即可
 
   


- 在conf/application.properties配置好正确的Zookeeper、MySQL信息
- IDEA选择TaierApplication启动项目
  ![idea-run](/img/readme/idea-run.png)


:::tip 
源码中包含部分scala的代码，通过idea源码启动需要添加scala的sdk 建议scala版本为`2.11.1`  
Module Setting > Global Libraries > scala sdk
:::

:::tip
![img.png](/img/readme/no_dist.png)
如果访问页面提示空白 原因为前端文件不存在 需要编译后在启动
:::