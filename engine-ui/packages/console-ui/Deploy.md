# 部署文档


## 一、 Web Server 安装配置说明

前端目前统一使用 Nginx 或者Tengine 作为 Web 服务器容器，可根据实际情况选择。目前我们测试环境的版本配置如下：

> 系统: CentOS Linux release 7.3.1611 (Core)<br>
> Server: Nginx version: nginx/1.12.0

#### Nginx 服务器配置说明
目前在仓库源码的`scripts`目录下，有有个`dt-rdos.conf`的 Nginx 服务器示例配置文件。在
`dt-rdos.conf` 文件中，需要根据具体情况手动的`配置点`如下：

1. location ~ ^/api <br>
`header, pass` 属性需要根据具体情况手动配置, 
目前默认配置的Host为测试服务器地址，部署到生产环境中时，则需要更改到正式地址。

2. root <br>
目前 `root` 配置的根路径为前端静态文件的根目录地址，也就是我们打包后通常`dist`文件夹所在的目录。打包好dist文件后，需要放到 `root` 指定的目录地址。

<i style="color:red">* 注：可以通过`nginx_init.sh`脚本，帮你安装Nignx 和拷贝默认服务器配置文件</i>

## 二、获取生产代码

目前前端代码仓库中，默认的`master`分支为最新的稳定版本，如果需要其他历史版本，则可以通过查看历史打包`标签`记录的方式获得。

获取生产环境的代码通常有`2`种方式，第一种则是直接把前端gitlab仓库项目对于的版本 `clone` 到本地, 安装打包环境后，并手动打包发布文件；第二种则是直接从gitlab仓库中，找到对应Tab版本并下载，使用下载后代码中`dist`中打包好的文件即可，因为前端在发布时，会把打包好的dist上传。

接下来，说一下如何手动打包生成代码。

### 打包之前

在开始打包项目前，你需要清楚的是，DTinsight 前端是一个集合了`DTinsight.IDE、DTinsight.Valid、DTinsight.API、DTinsight.Tag、DTinsight.Console`若干应用的项目。考虑到不同客户的需求情况，故设计成根据具体应用需求生成对应的发布文件的方式，也就是说假如你只需要，`DTinsight.IDE` 这一个应用，只需要配置该一个应用即可，打包后生成的代码仅此包含改项目的运行代码。该配置项的文件为：`src/config/base.js` 与`src/config/defaultApps.js`，在 `src/config` 目录下找到这2个配置文件，并找到对应App的启用`（enable）`字段，`true` 表示启用，`false`关闭。

### 打包源码

1. clone 项目源码

    通常推荐`ssh`的方式.

    ```bash
    http://git.dtstack.cn/ziv/data-stack-web.git // https
    ssh://git@git.dtstack.cn:10022/ziv/data-stack-web.git // ssh
    ```

2. 安装 Node.js

    目前编译前端项目，需要依赖 `Node.js` 环境，所以如果你没有安装 `Node.js` 的情况下，建议您先安装一下 `8.0` 以上版本后的 `Node.js` 后，再继续操作。具体安装可参看[Node.js官网](https://nodejs.org/en/download/).

3. 安装依赖环境，并执行构建

    ```
    $ npm install // 安装打包所需要的依赖文件
    $ npm run build // 执行生成环境打包
    ```

    运行`build`命令后，会在根目录下生产一份用于成产环境的代码 `dist`,
    打包完成后，可以通过`git`提交到远程仓库，或者直接copy代码到部署服务器.

### 三、根据生产环境，替换相关配置

由于数栈的Web有依赖 `UIC`、 `API Server` 等相关服务，所以在生成打包文件后，需要根据实际情况进行配置。目前数栈产品是多个项目的集合，所以每个项目都有单独的相关配置`（config) `文件，而具体的 config 文件在`dist`中的分布如下：

```bash
| - dist
    | - index.html
    | - rdos.html
    | - dataQuality.html
    | - public
        | - main
            | - config
                | - config.js
        | - rdos
            | - config
                | - config.js
        | - dataQuality
            | - config
                | - config.js
        | - dataApi
            | - config
                | - config.js
        | - dataLabel
            | - config
                | - config.js
        | - console
            | - config
                | - config.js
    | - ...
```
上面的 `config`文件主要包含即是 UIC 相关的配置, `dataQuality` 项目中则需要独立配置下的 `API Server` 地址，该配置主要用来提供给第三方 API 调用`远程触发`服务。

> 3.1.0版本更新：新增common/config.js文件，所有的应用都会从该文件继承配置。

###  四、版本验证
安装成功后，打开首页，在页脚有对应版本号，表示升级文件是否生效。另外，想验证功能的话，可以打开 `dist` 目录中的[README](./README.md)文件，查看升级日志后，进行操作。