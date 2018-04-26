# DATA-STACK

# 开发准备

基于webpack的开发配置环境，可以使用npm管理项目

```
npm i
npm run dev
npm run build
```
推荐使用yarn管理依赖

```
yarn
yarn add package.name
```

# 版本号规则：
- 修订版本号：日常 bugfix 更新。（如果有紧急的 bugfix，则任何时候都可发布）
- 次版本号：带有新特性的向下兼容的版本。
- 主版本号：含有破坏性更新和新特性。

# 更新日志

# 项目技术和结构设计

#### 主要技术库和框架

- React.js
- Webpack
- ES6
- Redux
- Sass
- Styled Component
- Lodash
- mxGraph (RD-OS项目) 后期考虑自己写，减小打包体积
- Codemirror (RD-OS项目)， 使用VScode 替换

技术架构中用到的技术库或者框架，需要秉承一些基本原则，具体如下：
- 项目允许情况下，能自己写的，尽量不用第三方的（确保完全的掌控权）
- 所选用的框架或者库需要成熟的社区支撑，或者自己有完全掌控的能力
- 引入新技术前，需要跟组员沟通交流，并做过充分学习

#### 需求目标

- 可自由打包个单独或多个项目
- 适宜多人协作开发
- 兼容包含原有RDOS项目
- 保留未来可能拆分成独立项目的可能
- 根据打包项目所在独立维护的状态树（保证性能）
- 每个应用独立的模板页和app.js
- 应用展示菜单可通过配置控制


#### 目录结构

```
| - data-stack
    | - node_modules npm安装模块
    | - build 配置文件目录，主要包含Webpack配置
        | - base
        | - dev
        | - prod
        | - config.js
    | - dist 分发目录
    | - document 文档
    | - scripts 辅助脚本，例如ssh登录到测试环境
    | - src
        | - funcs   零碎的公共方法
        | - widgets 全局UI小组件
        | - consts   全局常量
        | - theme   主题配置文件
        | - utils 工具文件
            | - index.js 默认工具模块
            | - asyncLoader.js 异步加载js模块
            | - localDb.js localStorage操作
            | - pureRender.js 纯渲染装饰器
            | - reduxUtils.js redux工具
        | - config
            | - appConf.js 配置应用数量
        | - public 静态公开资源
            | - iconfont
            | - main
                | - config
                | - img
                | - js
                | - index.html 模板页
            | - rdos
                | - ...
            | - dataQuality
                | - ...
            | - favicon.ico
        | - webapps 各应用部分
            | - main 主应用
                | - api
                | - constants
                | - actions
                | - reducers
                | - styles 样式文件
                | - components 应用组件
                | - views
                    | - layout
                    | - project
                    | - user
                | - app.js
                | - routers.js 路由
                | - interceptor.js 拦截器
            | - rdos RD-OS
            | - dataQuality 数据质量
            | - dataApi 数据API
    | - .babelrc babel配置
    | - eslintrc.json eslint配置，可以自定义规则
    | - config.js 配置文件，包括devServer的相关信息
    | - README.md 文档
```

#### 编译打包后的结果
```
| - dt-stack
    | - index.html
    | - rdos.html
    | - dataQuality.html
    | - public
        | - main
        | - rdos
        | - ...
    | - app.js
    | - vendor.js
    | - rdos.js
    | - dataQuality.js
    | - ...
```

#### 隐患问题
- 项目过大，造成打包速度非常慢


# 部署说明

### 第一步、打包源代码
```
$ npm run build
```
运行`build`命令后，会在根目录下生产一份用于成产环境的代码dist,
打包完成后，可以通过`git`提交到远程仓库，或者直接copy代码到部署服务器

### 第二步、server配置
测试服务器配置：
> 系统: CentOS Linux release 7.3.1611 (Core)<br>
> Server: Nginx version: nginx/1.12.0

### Nginx服务器配置说明
目前在`scripts`目录下，有有个`dt-rdos.conf`的nginx服务器配置文件, 该文件为前端的部署配置文件。在
`dt-rdos.conf`文件中，需要根据具体情况手动配置的点如下：

1. location ~ ^/api <br>
`header, pass`属性需要根据具体情况手动配置, 
目前默认配置的Host为测试服务器地址，部署到生产环境中时，则需要更改到正式地址。
2. root <br>
目前root配置的根路径为测试环境地址，生成环境根据具体情况调整

<i style="color:red">* 注：可以通过`nginx_init.sh`脚本，帮你安装nignx和拷贝默认服务器配置文件</i>

### UIC相关配置
由于目前数据中台是多个项目，所以每个项目都有单独的UIC相关配置文件，而具体的config文件分布如下：

```
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
    | - ...
```
主要需要主要的即是 UIC 相关的配置项了。

# 其他文档

[RedmineAPI文档地址](http://redmine.prod.dtstack.cn/projects/rdos1/wiki)

[GitLab文档地址](http://git.dtstack.cn/dtstack/rdos-docs)

项目状态0：初始化，1：正常,2:禁用,3:失败
项目角色0：租户所有者，1：项目所有者,2:项目管理员,3:普通成员

