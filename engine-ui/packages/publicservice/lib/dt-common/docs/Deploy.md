# 数栈前端部署文档

## 一、 应用使用环境

> 浏览器：Chrome
> 版本： 70 +

在开始使用前，我们建议您检查下浏览器的版本，当前我们推荐您默认使用 `Chrome 70 `以上的版本，而保证获得良好的体验，`过低`的版本可能会导致应用`无法正常运行`。另外，为了您获得最好的使用体验，我们强烈建议你开启 Chrome 的自动更新，从而获取更多更好的新特性和优化。

## 二、 Web Server 安装配置说明

前端目前统一使用 Nginx 或者 Tengine 作为 Web 服务器容器，可根据实际情况选择。目前我们测试环境的版本配置如下：

> 系统: CentOS Linux release 7.3.1611 (Core)
> Server: Nginx version: nginx/1.12.0

### 应用部署目录

```bash
| - dt-insight-front # 根目录
    | - portal # 门户首页
        | - index.html
    | - batch # 离线计算
        | - index.html
    | - stream # 流计算
        | - index.html
    | - dataApi # 数据 API
        | - index.html
    | - valid # 数据质量
        | - index.html
    | - science # 数据科学
        | - index.html
    | - analytic # 分析引擎
        | - index.html
    | - tag # 标签
        | - index.html
    | - console # 控制台
```

### Nginx 配置
目前在仓库源码的`scripts`目录下，有有个`dt-insight-front.conf`的 Nginx 服务器示例配置文件。`详细配置`如下：

```bash
#Nginx conf
#Full Example: https://www.nginx.com/resources/wiki/start/topics/examples/full/

upstream rdos_web {
   server 172.16.10.168:9020; # dev env
    #server 172.16.3.158:9020; # sanyue env
    #server 172.16.10.86:9020; # test env
}

upstream stream_web {
   server 172.16.10.251:9023; # dev env
   #server 172.16.0.83:9023; # test env
}
upstream analytics_engine {
    server 172.16.10.168:9022; # dev env
    #server 172.16.10.45:9022; # test env
}

upstream data_quality {
    server 172.16.10.251:8089; # dev env
}

upstream data_api {
    server 172.16.10.251:8087; # dev env
}

upstream console {
   server 172.16.10.168:8084; # dev env

}

upstream data_science {
    server 172.16.10.251:9029; # dev env
}

upstream dtuic {
    server 172.16.10.61; # dev env
    #server 172.16.10.34; # test env
}

server {
    listen 80;
    listen 443 ssl http2;
    ssl_certificate /etc/nginx/ssl/server.crt;
    ssl_certificate_key /etc/nginx/ssl/server.key;
    ssl_session_timeout  5m;
    ssl_protocols  SSLv2 SSLv3 TLSv1;
    ssl_ciphers  HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers   on;
    server_name dev.insight.dtstack.net;
    error_log   /var/log/nginx/dt-insight-front.error.log;
    access_log  /var/log/nginx/dt-insight-front.access.log  main;
    client_body_buffer_size 100m;
    client_max_body_size 1024m;
    index /portal;

    location /portal {
    	alias /home/admin/app/dt-insight-front/portal;
        index index.html;
        autoindex on;
    }

    location /batch {
        alias /home/admin/app/dt-insight-front/batch;
    	index index.html;
	    autoindex on;
    }

    location /stream {
        alias /home/admin/app/dt-insight-front/stream;
        index index.html;
        autoindex on;
    }

    location /science {
        alias /home/admin/app/dt-insight-front/science;
        index index.html;
        autoindex on;
    }

    location /dataApi {
        alias /home/admin/app/dt-insight-front/dataApi;
	    index index.html;
        autoindex on;
    }

    location /valid {
        alias /home/admin/app/dt-insight-front/valid;
        index index.html;
        autoindex on;
    }

    location /analytic {
        alias /home/admin/app/dt-insight-front/analytic;
        index index.html;
        autoindex on;
    }

    location /tag {
        alias /home/admin/app/dt-insight-front/tag;
        index index.html;
        autoindex on;
    }

    location /console {
        alias /home/admin/app/dt-insight-front/console;
        index index.html;
        autoindex on;
    }

    location ~ /dt-common {
        root /home/admin/app/dt-insight-front/portal;
        rewrite ^/dt-common/(.*)$ /$1 break;
    }

    # 代理设置, 根据实际状况配置替换
    location ~ ^/api/rdos {
        proxy_set_header Host rdos_web;
        proxy_pass http://rdos_web;
    }

    location ~ ^/api/streamapp {
        proxy_set_header Host stream_web;
        proxy_pass http://stream_web;
    }

    location ~ ^/api/analysis {
        proxy_set_header Host analytics_engine;
        proxy_pass http://analytics_engine;
    }

    location ~ ^/api/dq {
        proxy_set_header Host data_quality;
        proxy_pass http://data_quality;
    }

    location ~ ^/api/dataScience {
        proxy_set_header Host data_science;
        proxy_pass http://data_science;
    }

    location ~ ^/api/da {
        proxy_set_header Host data_api;
        proxy_pass http://data_api;
    }

    location ~ ^/api/gateway {
      proxy_set_header X-Real-IP       $remote_addr;
      proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
      proxy_set_header Host            $host;
      proxy_pass http://172.16.8.107:8086;
    }

    location ~ ^/api/console {
        proxy_set_header Host console;
        proxy_pass http://console;
    }

    # 帮助文档映射路径
    location ~ /public/helpSite/ {
        root /home/admin/app/dtinsight-help-docs/output;
        rewrite ^/public/helpSite/(.*)$ /$1 break;
    }

    # UIC代理设置
    location ~ /uic {
        proxy_set_header Host dtuic;
        proxy_pass http://dtuic;

    }
   # 标签引擎代理
   location ~ ^/api/v1  {
        proxy_set_header Host tagEngine;
        proxy_pass http://172.16.8.194:8084;
    }

    error_page  500 502 503 504  /50x.html;
    location = /50x.html {
        root   /usr/share/nginx/html;
    }

    # Specify a charset
    charset utf-8;

    # Enable gzip compression.
    # Default: off
    gzip on;

    # Compression level (1-9).
    # 5 is a perfect compromise between size and CPU usage, offering about
    # 75% reduction for most ASCII files (almost identical to level 9).
    # Default: 1
    gzip_comp_level    5;

    # Don't compress anything that's already small and unlikely to shrink much
    # if at all (the default is 20 bytes, which is bad as that usually leads to
    # larger files after gzipping).
    # Default: 20
    gzip_min_length    256;

    # Compress data even for clients that are connecting to us via proxies,
    # identified by the "Via" header (required for CloudFront).
    # Default: off
    gzip_proxied       any;

    # Tell proxies to cache both the gzipped and regular version of a resource
    # whenever the client's Accept-Encoding capabilities header varies;
    # Avoids the issue where a non-gzip capable client (which is extremely rare
    # today) would display gibberish if their proxy gave them the gzipped version.
    # Default: off
    gzip_vary          on;

    # Compress all output labeled with one of the following MIME-types.
    # text/html is always compressed by gzip module.
    # Default: text/html
    gzip_types
        application/atom+xml
        application/javascript
        application/json
        application/ld+json
        application/manifest+json
        application/rss+xml
        application/vnd.geo+json
        application/vnd.ms-fontobject
        application/x-font-ttf
        application/x-web-app-manifest+json
        application/xhtml+xml
        application/xml
        font/opentype
        image/bmp
        image/svg+xml
        image/x-icon
        text/cache-manifest
        text/css
        text/plain
        text/vcard
        text/vnd.rim.location.xloc
        text/vtt
        text/x-component
        text/x-cross-domain-policy;
}

```


## 三、获取生产代码

目前前端代码仓库中，默认的`master`分支为最新发布的版本，如果需要[历史版本](http://git.dtstack.cn/dtstack/data-stack-web/tags)或者其他稳定（stable）版本，则可以通过查看 commit 的 [Tags 记录](http://git.dtstack.cn/dtstack/data-stack-web/tags)来获得。有些特殊的定制化版本，我们会根据具体定制用户进行命名上的调整，例如`浙江大学`定制版，我们会是这样的命名：`v3.4.0-zju`。所有在选择`clone` 或者下载包的时候需要注意对应的 `Tag` 版本或者 `Branch`，如果不确定版本号或者更新内容，可以查看根目录下的`CHANGELOG`文件或者`package.json` 里面的 `version` 字段进行确认。

### 代码仓库

- 公共门户：[http://git.dtstack.cn/dt-insight-front/dt-common](http://git.dtstack.cn/dt-insight-front/dt-common)
- 离线计算：[http://git.dtstack.cn/dt-insight-front/dt-batch-works](http://git.dtstack.cn/dt-insight-front/dt-batch-works)
- 实时计算：[http://git.dtstack.cn/dt-insight-front/dt-stream-works](http://git.dtstack.cn/dt-insight-front/dt-stream-works)
- 数据科学：[http://git.dtstack.cn/dt-insight-front/dt-ai-works](http://git.dtstack.cn/dt-insight-front/dt-ai-works)
- 数据 API: [http://git.dtstack.cn/dt-insight-front/dt-data-api](http://git.dtstack.cn/dt-insight-front/dt-data-api)
- 数据质量：[http://git.dtstack.cn/dt-insight-front/dt-data-valid](http://git.dtstack.cn/dt-insight-front/dt-data-valid)
- 分析引擎：[http://git.dtstack.cn/dt-insight-front/dt-analytic-engine](http://git.dtstack.cn/dt-insight-front/dt-analytic-engine)
- 标签引擎：[http://git.dtstack.cn/dt-insight-front/dt-tag-engine](http://git.dtstack.cn/dt-insight-front/dt-tag-engine)
- 控制台：[http://git.dtstack.cn/dt-insight-front/dt-console](http://git.dtstack.cn/dt-insight-front/dt-console)

接下来，说一下如何手动打包`生产环境`代码。

## 四、源码编译

1. 安装 Node.js

    目前编译前端项目，需要依赖 `Node.js` 环境，所以如果你没有安装 `Node.js` 的情况下，建议您先安装一下 `8.0` 以上版本后的 `Node.js` 后，再继续操作。具体安装可参看[Node.js 官网](https://nodejs.org/en/download/).


2. clone 项目源码

    推荐`ssh`的方式，这里以`dt-common`举例：

    ```bash
    $ # http
    $ git clone http://git.dtstack.cn/dt-insight-front/dt-common
    $ # ssh
    $ git clone ssh://git@git.dtstack.cn:10022/dt-insight-front/dt-common.git

    ```

3. 安装依赖环境，并执行构建

    ```bash
    $ # 默认 master 分支，如果需要切换到其他版本Tag， 则需要 checkout
    $ git checkout v3.4.0
    $ npm install // 安装打包所需要的依赖文件
    $ npm run build // 执行生成环境打包
    ```

    运行`build`命令后，会在根目录下生产一份用于成产环境的代码 `dist`,
    打包完成后，可以通过`git`提交到远程仓库，或者直接 copy 代码到部署服务器.

    `注意`: 如果安装过程中出现任何 `not found package`相关的情况，可以反馈给我们。另外，你可以通过手动拷贝缺少的`package`名称，执行 `npm install packageName --save` 命令来进行安装。


## 五、 生产程序（dist 目录）结构说明

<color style="color:red;">注意： 除了 **公共门户（dt-common）** 的稍有差别以外，其他所有应用的配置项均类似。</color>

```bash
| - dist # 根目录
    | - batch
        | - index.html # 首页
        | - public
                | - config # 公共配置文件
                    | - config.js
                    | - defaultApps.json # 仅 dt-common 才有
                | - img # 图片
                | - ...
        | - docs # 部署文档/更新日志 
                | - Deploy 部署文档
                | - CHANGELOG.md #
        | - ... 其他 js, 静态资源
```

## 六、运行 **配置**

<color style="color:red;">注意： 除了**公共门户（dt-common）**自定义配置，控制应用展示（defaultApps.json ）文件， **数据质量** 多一个 **API_SERVER** 的运行配置稍有差别以外，其他所有应用的配置项均一样。</color>

### dt-common 配置项说明

- **public/config/config.js**

| 参数名  | 必填  | 类型  |  默认值 | 备注  |
| ------------ | ------------ | ------------ | ------------ | ------------ |
| indexTitle  |  是 | string  | 无  |  门户页面的大标题 |
| indexDesc  | 是  |  string | 无  | 门户页面大标题下面的小说明  |
| showCopyright | 否  |  boolean | false  | 是否显示版权信息  |
| showSummary | 否  | boolean  |  false | 是否显示功能说明  |
| summary | 否  |  { title: string, content: string } |  无 |  功能说明详细内容 |
|  UIC_URL |  是 | string  | 无  | UIC 中心地址  |
|  UIC_DOMAIN | 是  | string  |  无 |  UIC 域，注意，是**域**，例如 ```.dtstack.com``` ，**不是 url** |
| prefix  | 否  |  string | 无  |  应用的前缀，例如 `DTinsight.离线计算` 中的`DTinsight` |
| name  |  否 | string  | 无  | 应用名字，在左上角显示，例如 `DTinsight.离线计算` 中的 `离线计算` 。**注意：门户页面没有 name**  |
| titleName  | 否  |  string | 无  | 网页的 title 中显示的名字，例如`DTinsight-离线计算`中的 `离线计算`  |
| loadingTitle  |  否 |  string | 无  | loading 页面显示的名字，和 titleName 类似 |
| theme  |  否 | 'default' / 'aliyun'  | 'default'  | 应用的主题，将会改变门户页面的布局样式以及应用的导航栏。当前只有 `aliyun` 和 `default` 两种可选  |
| logo  | 否  |  string | 无  |  应用的图标路径 **（该配置只在 `default` 主题下生效）** |
| hideUserCenter |  否 | boolean  | false  | 是否隐藏右上角下拉框中的用户中心  |
| disableHelp  | 否  |  boolean | false  | 是否隐藏帮助文档  |
| macChrome | 否 | string | 有 | mac 版`Chrome浏览器`官方下载地址 |
| windowsChrome | 否 | string | 有 | windows 版`Chrome浏览器`官方下载地址 |

- **portal/public/config/defaultApps.json**

以下是离线计算示例：

```json
[
     {
        "id": "rdos", // 应用ID
        "name": "离线计算", // 应用名称
        "filename": "index.html", // 应用名称
        "link": "/batch", // 应用访问连接
        "target": "_self", // 应用访问打开方式
        "enable": true, // 应用是否启用
        "hasProject": true, // 应用是否拥有项目概念
        "default": true,  // 是否为默认应用
        "icon": "./public/img/icon_1.png", // 应用老版本图标
        "newIcon": "./public/img/icon_new1.png", // 应用新版本图标
        "description": "一站式大数据开发平台，帮助企业快速完成数据中台搭建", // 应用描述
        "className": "icon_dropdown_offlin", // 应用附件样式 class
        "hideLogo": false, // 是否隐藏导航栏Logo
        "hideMenuRight": false // 是否隐藏导航栏右边操作按钮
    }
]
```

### 其他应用配置项说明

- **public/config/config.js**

| 参数名  | 必填  | 类型  |  默认值 | 备注  |
| ------------ | ------------ | ------------ | ------------ | ------------ |
|  UIC_URL |  是 | string  | 无  | UIC 中心地址  |
|  UIC_DOMAIN | 是  | string  |  无 |  UIC 域，注意，是**域**，例如 ```.dtstack.com``` ，**不是 url** |
| prefix  | 否  |  string | 无  |  应用的前缀，例如 `DTinsight.离线计算` 中的`DTinsight` |
| name  |  否 | string  | 无  | 应用名字，在左上角显示，例如 `DTinsight.离线计算` 中的 `离线计算` 。**注意：门户页面没有 name**  |
| titleName  | 否  |  string | 无  | 网页的 title 中显示的名字，例如`DTinsight-离线计算`中的 `离线计算`  |
| loadingTitle  |  否 |  string | 无  | loading 页面显示的名字，和 titleName 类似 |
| theme  |  否 | 'default' / 'aliyun'  | 'default'  | 应用的主题，将会改变门户页面的布局样式以及应用的导航栏。当前只有 `aliyun` 和 `default` 两种可选  |
| logo  | 否  |  string | 无  |  应用的图标路径 **（该配置只在 `default` 主题下生效）** |
| hideUserCenter |  否 | boolean  | false  | 是否隐藏右上角下拉框中的用户中心  |
| disableHelp  | 否  |  boolean | false  | 是否隐藏帮助文档  |
| macChrome | 否 | string | 有 | mac 版`Chrome浏览器`官方下载地址 |
| windowsChrome | 否 | string | 有 | windows 版`Chrome浏览器`官方下载地址 |
| API_SERVER  |  是 | string  | 无  |  API 服务 Base 地址 | <color style="color:red;">注意：数据质量（DataValid)独有）</color>
| enableGraphEdit | 否 | boolean | false | 实时开发开启可视化编辑任务配置 |

##  七、版本验证与更新日志
安装成功后，打开浏览器**控制台**，会打印当前应用与仓库对应的版本信息。或者可以打开 `dist/docs` 目录中的[CHANGELOG](./CHANGELOG.md)文件。查看更新日志后，进行验证操作。

## 其他
默认 Bug 反馈 xiaowei@dtstack.com

- [Issue](http://redmine.prod.dtstack.cn/projects/dtinsight200/issues)
- [历史版本](http://git.dtstack.cn/dtstack/data-stack-web/tags)
