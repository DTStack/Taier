# 前端部署

## 准备前端部署环境

1. 首先，通过命令行安装 `Node.js` 的二进制文件。

```shell
$ wget https://nodejs.org/dist/v16.14.0/node-v16.14.0-linux-x64.tar.xz
```

:::tip 其他版本安装
这里安装的 `Node.js` 的版本为 `v16.14.0`, 如果需要下载不同的版本或其他平台的 Node.js 安装包，可以通过 https://nodejs.org/zh-cn/download/ 查询其他版本。

我们建议 `Node.js` 的版本在 `12.18.0` 及以上。
:::

1. 下载完成后，解压源码文件

```shell
$ xz -d node-v16.14.0-linux-x64.tar.xz
$ tar xvf node-v16.14.0-linux-x64.tar
```

2. 安装完成后，编辑环境变量

```shell
$ vim /etc/profile
```

打开 vim 编辑器后，在文件最下面粘贴如下内容:

```shell
#set for nodejs
export NODE_HOME=/usr/local/node/16.14.0
export PATH=$NODE_HOME/bin:$PATH
```

通过 `:wq` 保存并退出，退出后执行如下命令使之生效:

```shell
$ source /etc/profile
```

3. 验证安装

通过执行 `node -v` 是否输出 Node.js 的版本信息判断是否安装成功。

```shell
$ node -v
v16.14.0
```

:::note
**我们推荐使用 `yarn` 来管理前端项目依赖**
:::

4. 安装 `yarn`

安装 `Node.js` 的同时，会安装 `npm`，我们通过 `npm` 来安装 `yarn`.

```shell
$ npm -v
6.14.13
$ npm install -g yarn
```

安装完成后，通过以下命令确认是否安装成功

```shell
$ yarn -v
1.22.10
```

## 安装项目依赖

将当前路径切换到 `taier-ui` 文件夹下，然后执行 `yarn` 开始安装项目依赖

```shell
$ pwd
~/Your-Project-Path/Taier/taier-ui
$ yarn
```

:::tip 切换淘宝源
国内用户在安装依赖的时候会比较慢，可以在安装之前将 `yarn` 的源换成淘宝源.

```shell
$ yarn config set registry https://registry.npm.taobao.org/
```

::::
安装完成后，当前路径下会新增 `node_modules` 文件夹 和 `yarn.lock` 文件，前者是保存当前项目的依赖，后者是记录当前项目的依赖的版本信息。

安装依赖成功后，执行 `yarn build` 对项目进行编译。

```shell
$ yarn build
```

项目编译完成后，会将编译后的结果存放在当前目录的 dist 文件夹下，

```shell
./
├── README.md
├── cup.config.js
├── dist # 编译结果文件
├── node_modules # 项目依赖
├── package.json
├── pom.xml
├── public
├── scripts
├── src
├── tailwind.config.js
├── tsconfig.json
├── typings.d.ts
└── yarn.lock
```

然后这里我们借助 `mini-cup` 和 `pm2` 的能力来启动服务器，首先先全局安装 `mini-cup` 和 `pm2`.

```shell
$ yarn global add pm2 mini-cup
```

:::info
[`PM2`](https://www.npmjs.com/package/pm2) is a production process manager for Node.js applications with a built-in load balancer.

[`mini-cup`](https://github.com/wewoor/cup) is a lightweight web server for web applications.
:::

安装完成，就可以通过在 `taier-ui`目录下执行以下命令来启动服务:

```shell
$ pwd
/Your-Project-Path/Taier/taier-ui
$ pm2 start cup
```

执行命令后，打开浏览器输入 http://localhost:8080/ 即可看到页面。

:::caution
建议开发人员通过修改 `hosts` 进行开发
:::

该命令会查找当前目录下的 `cup.config.js` 文件，并将该文件作为配置文件启动服务器，该文件内容如下：

```js title="cup.config.js"
const publicURL = "http://taier.dtstack.cn"; // 跳转到后端部署的目录

module.exports = {
  name: "taier",
  listen: 8080, // 服务启动端口
  root: "./dist", // 服务启动的根目录
  proxyTable: {
    // 服务启动后的请求代理转发
    "/taier": {
      target: `${publicURL}:8090`,
      changeOrigin: true,
      secure: false,
    },
  },
};
```

:::caution
`mini-cup` 服务器仅用作本地快速部署，在实际生产环境中，我们推荐使用 `nginx` 作为代理服务器。
:::

如果您想要使用 `nginx` 作为代理服务，这里提供一份配置文件**仅供参考**。

```nginx title="taier.conf"
upstream taier{
  server Your-Server-IP:Your-Server-PORT;
}


server {
  listen *:80;
  listen [::]:80;
  # The host name to respond to
  server_name .taier.dtstack.com .taier.dtstack.cn;
  client_max_body_size  100m;

  proxy_set_header   cache-control no-cache;
  proxy_ignore_headers Cache-Control Expires;
  proxy_set_header   Referer $http_referer;
  proxy_set_header   Host   $host;
  proxy_set_header   Cookie $http_cookie;
  proxy_set_header   X-Real-IP  $remote_addr;
  proxy_set_header X-Forwarded-Host $host;
  proxy_set_header X-Forwarded-Server $host;
  proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;


  location / {
    proxy_set_header X-Real-IP  $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header Host $host;
    proxy_pass http://Your-address-ip:8080;
  }

  location /taier {
    proxy_set_header X-Real-IP  $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header Host $host;
    proxy_pass http://taier;
  }
}
```
