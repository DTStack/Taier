# 数栈前端部署文档


## 一、 Web Server 安装配置说明

前端目前统一使用 Nginx 或者 Tengine 作为 Web 服务器容器，可根据实际情况选择。目前我们测试环境的版本配置如下：

> 系统: CentOS Linux release 7.3.1611 (Core)<br>
> Server: Nginx version: nginx/1.12.0

#### Nginx 服务器配置说明
目前在仓库源码的`scripts`目录下，有有个`dt-rdos.conf`的 Nginx 服务器示例配置文件。在
`dt-rdos.conf` 文件中，需要根据具体情况手动的`配置点`如下：

1. location ~ ^/api <br>
`header, pass` 属性需要根据具体情况手动配置, 
目前默认配置的 Host 为测试服务器地址，部署到生产环境中时，则需要更改到正式地址。

2. root <br>
目前 `root` 配置的根路径为前端静态文件的根目录地址，也就是我们打包后通常`dist`文件夹所在的目录。打包好 dist 文件后，需要放到 `root` 指定的目录地址。

<i style="color:red">* 注：可以通过`nginx_init.sh`脚本，帮你安装 Nignx 和拷贝默认服务器配置文件</i>

## 二、获取生产代码

目前前端代码仓库中，默认的`master`分支为最新的稳定版本，如果需要[历史版本](http://git.dtstack.cn/dtstack/data-stack-web/tags)，则可以通过查看 commit 的 [Tags 记录](http://git.dtstack.cn/dtstack/data-stack-web/tags)。

获取生产环境的代码是直接把前端 gitlab 仓库项目对于的版本 `clone` 到本地，找到对应 Tag 版本或者 branch 代码 checkout，然后执行 build 相关的指令。

接下来，说一下如何手动打包`生产环境`代码。

### 打包之前

在开始打包项目前，你需要清楚的是，DTinsight 前端是一个集合了`DTinsight.IDE、DTinsight.Valid、DTinsight.API、DTinsight.Tag、DTinsight.Console、DTinsight.Analytics`若干应用的项目。考虑到不同客户的需求情况，故设计成根据具体应用需求生成对应的发布文件的方式，也就是说假如你只需要，`DTinsight.IDE` 这一个应用，只需要配置该一个应用即可，打包后生成的代码仅此包含改项目的运行代码。该配置项的文件为：`src/config/base.js` 与`src/config/defaultApps.js`，在 `src/config` 目录下找到这 2 个配置文件，并找到对应 App 的启用`（enable）`字段，`true` 表示启用，`false`关闭。

### 打包源码

1. clone 项目源码

    通常推荐`ssh`的方式.

    ```bash
    $ # http
    $ git clone http://git.dtstack.cn/dtstack/data-stack-web.git
    $ # ssh
    $ git clone ssh://git@git.dtstack.cn:10022/dtstack/data-stack-web.git // ssh
    ```

2. 安装 Node.js

    目前编译前端项目，需要依赖 `Node.js` 环境，所以如果你没有安装 `Node.js` 的情况下，建议您先安装一下 `8.0` 以上版本后的 `Node.js` 后，再继续操作。具体安装可参看[Node.js 官网](https://nodejs.org/en/download/).

3. 安装依赖环境，并执行构建

    ```plain
    $ npm install // 安装打包所需要的依赖文件
    $ npm run build // 执行生成环境打包
    ```

    运行`build`命令后，会在根目录下生产一份用于成产环境的代码 `dist`,
    打包完成后，可以通过`git`提交到远程仓库，或者直接 copy 代码到部署服务器.

## 三、生产环境的相关`配置`

#### 生成代码的目录结构说明

```bash
| - dist
    | - index.html
    | - batch.html
    | - stream.html
    | - dataQuality.html
    | - dataApi.html
    | - dataLabel.html
    | - console.html
    | - analytics.html
    | - public
        | - common # 公共目录
            | - config # 公共配置文件
        | - main # 首页入口应用
            | - config
                | - config.js
        | - rdos # 离线计算应用
            | - config
                | - config.js
        | - stream # 实时计算应用
            | - config
                | - config.js
        | - dataQuality # 数据质量应用
            | - config
                | - config.js
        | - dataApi # 数据API应用
            | - config
                | - config.js
        | - dataLabel # 数据标签应用
            | - config
                | - config.js
        | - console # 控制台应用
            | - config
                | - config.js
        | - analyticsEngine # 分析引擎应用
            | - config
                | - config.js
    | - ...
```

由于数栈 Web 有依赖 `UIC` 等相关服务，所以在生成打包文件后，需要根据实际情况进行配置。目前数栈产品是多个项目的集合，所以每个项目都保有独立的自定义配置`（config)`文件。

以上 common 目录下的 config 配置为全局配置文件，服务所有应用的功能配置（UIC, 默认配置）项。其他的的每个应用的`config`文件主要包含应用本身自定义的内容。比较特别的是`dataQuality` 项目中则需要单独配置远程触发功能的 `API Server` 地址，该功能主要用来给第三方 提供 API 调用服务。配置方法请看下面：


#### 生产环境必配 <color style="color:red;">`必配项`</color>
生成配置是部署应用后必须要更改的配置项，否则会造成应用<color style="color:red;">无法正常访问！</color>

- 数栈 UIC

    配置地址在 `common/config.js` 中，修改参数为`UIC_URL`, `UIC_DOMAIN`两项
- 数据质量远程调用

    配置地址在 `dataQuality/config/config.js` 中，修改参数为`API_SERVER`

#### 数栈应用的`自定义`
数据目前支持修改应用 Logo, Loading 动画中的应用名称（name)、

- 门户页面自定义

    配置地址在`common/config.js` 中，具体如下：

``` json
    prefix: 'DTinsight', // 应用前缀
    indexTitle: '袋鼠云·数栈V3.0', // 主页的大标题
    showCopyright: true, // 是否显示版权信息
    name: '数栈' // 网页的title
```

- 应用 Logo 名称, Loading、title 配置

    配置地址在各应用自身的`config.js` 中，修改参数为`name`


##  四、版本验证与更新日志
安装成功后，打开首页，在页脚有对应版本号，表示升级文件是否生效。另外，想验证功能的话，可以打开 `dist/docs` 目录中的[CHANGELOG](./CHANGELOG.md)文件。查看更新日志后，进行验证操作。

## 其他
默认 Bug 反馈 xiaowei@dtstack.com

- [Issue](http://redmine.prod.dtstack.cn/projects/dtinsight200/issues)
- [README](./README.md)
- [历史版本](http://git.dtstack.cn/dtstack/data-stack-web/tags)
