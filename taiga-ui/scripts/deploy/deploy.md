# DAGScheduleX 前端部署文档

## 一、 应用使用环境

> 浏览器：Chrome
> 版本： 70 +

在开始使用前，我们建议您检查下浏览器的版本，当前我们推荐您默认使用 `Chrome 70 `以上的版本，而保证获得良好的体验，`过低`的版本可能会导致应用`无法正常运行`。另外，为了您获得最好的使用体验，我们强烈建议你开启 Chrome 的自动更新，从而获取更多更好的新特性和优化。

## 二、 Web Server 安装配置说明

DagScheduleX 目前统一使用 [cup](https://github.com/wewoor/cup) 作为 Web 服务器容器，使用 [pm2](https://github.com/Unitech/pm2) 作为进程守卫，使用 [lerna](https://github.com/lerna/lerna) 进行 monorepo 管理。可根据实际情况选择。目前我们测试环境的版本配置如下：

> 系统: CentOS Linux release 7.3.1611 (Core)
> 系统环境依赖: Lerna v4.0.0; pm2 v5.1.0

<color style="color:#AA0000;" >[cup](https://github.com/wewoor/cup) 服务器仅用快速部署以展示 DAGScheduleX，在实际生产环境中我们推荐使用 `nginx / tengine` 作为代理服务器。</color>

### 前端应用部署目录

```bash
| - engine-ui/out # 数据开发
    | - datasource # 数据源
        | - index.html
    | - console # 控制台与运维中心
        | - index.html
    | - index.html
```

### Cup Service 配置

目前在仓库源码的`scripts`目录下，有个`cup.config.js`的 Cup 服务器示例配置文件。`详细配置`如下：

```bash
/**
 * GitHub: https://github.com/wewoor/cup/blob/HEAD/README_zh.md
 * 使用：
 * > npm install -g mini-cup
 * > cup config // 按配置文件运行
 */
const path = require('path');
const rootPath = path.resolve(__dirname, './');
const publicURL = require(path.join(rootPath,'./package.json')).microHost;

module.exports = {
    'name': 'DAGScheduleX',
    'listen': 8080,
    'root': './out',
    'location': {
        '/ide': `./out/`,
        '/console-ui': `./out/`,
        '/data-source': './out/',
    },
    'proxyTable': {
        '/node': {
            target: `${publicURL}:8090`, 
            changeOrigin: true,
            secure: false
        },
        '/api': {
            target: `${publicURL}:8090`, 
            changeOrigin: true,
            secure: false
        },
    }
}

```

## 三、获取生产代码

目前前端代码仓库中，默认的`master`分支为最新发布的版本，如果需要[历史版本](https://github.com/DTStack/DAGScheduleX/tags)或者其他稳定（stable）版本，则可以通过查看 commit 的 [Tags 记录](https://github.com/DTStack/DAGScheduleX/tags)来获得。所有在选择`clone` 或者下载包的时候需要注意对应的 `Tag` 版本或者 `Branch`，如果不确定版本号或者更新内容，可以查看根目录下的`package.json` 里面的 `version` 字段进行确认。

## 四、源码编译

1. 安装 Node.js

    目前编译前端项目，需要依赖 `Node.js` 环境，所以如果你没有安装 `Node.js` 的情况下，建议您先安装一下 `12.18.0` 及以上版本后的 `Node.js` 后，再继续操作，更多详情可查看 [Node 官网](http://nodejs.cn/download/)

2. 安装 lerna

    目前各个前端微应用采用 `monorepo` 方式进行管理，`DAGScheduleX` 采用 `lerna` 作为方案，请确保安装 `4.0.0` 及以上版本的 `lerna` 后，再继续操作，更多详情可查看 [Lerna](https://www.npmjs.com/package/lerna)

3. 安装依赖环境，并执行构建

    ```bash
    $ # 默认 master 分支，如果需要切换到其他版本Tag， 则需要 checkout
    $ git checkout v1.0.0
    $ lerna bootstrap # 安装打包所需要的依赖文件
    $ yarn build # 执行生成环境打包
    ```

    运行`build`命令后，会在根目录下生产一份用于生产环境的代码 `out`,
    打包完成后，可以通过`git`提交到远程仓库，或者直接 copy 代码到部署服务器.

    `注意`: 如果安装过程中出现任何 `not found package` 相关的情况，可以反馈给我们。另外，你可以通过手动拷贝缺少的 `package` 名称，执行 `lerna add package [--scope=Micro Application]` 命令来进行安装。

## 五、运行 **配置**

### package.json 配置项说明

| 参数名  | 必填  | 类型  |  默认值 | 备注  |
| ------------ | ------------ | ------------ | ------------ | ---------- |
| microHost  |  是 | string  | "http://schedule.dtstack.cn"  |  微前端静态资源如图片、`iconfont` 等，在微前端启动时会被转义为带有 `microHost` 的 `domain`；`cup` 代理服务器 `proxy` 的 `target` 目标 |
| microApp  |  是 | object  | 见 package.json | 微前端的打包配置位置，它将在微前端启动和部署的时候起作用。其中 bundlePath 表示各微应用的打包器配置路径；iconfontPath 表示各微应用的 iconfont css 文件路径 |

#### 为什么需要提供 microApp 配置

在微前端体系中，每个微应用可以单独启动，也可以以主应用的视角进行访问，此时若微应用需要访问自身 `/public/xx.png` 可以成功取出并展示，而在主应用的视角访问微应用那么在此路径下查无文件，会以 404 作为响应。
为了解决此问题，在以 DAGScheduleX 作为主应用启动时，我们会在 runtime 时动态插入 `microHost` 将访问路径变成 `http://schedule.dtstack.cn/microApp/public/xx.png` 形式以解决此问题，iconfont 同理。
如果您的静态资源已经存放在 `OSS` 或以域名的形式进行访问，则不需要设置 microApp。

## 其他

默认 Bug 反馈 xiaowei@dtstack.com

- [Issue](https://github.com/DTStack/DAGScheduleX/issues)
- [历史版本](https://github.com/DTStack/DAGScheduleX/tags)