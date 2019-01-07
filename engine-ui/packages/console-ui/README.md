# DATA-STACK | [CHANGELOG](./CHANGELOG.md)

数栈 Web 前端。

# 开发
基于 webpack 的开发配置环境，可以使用 npm 管理项目

```bash
$ npm i
$ # 开发构建
$ npm start 或者 npm run dev
$ # 生产构建
$ npm run build
```
推荐使用 yarn 管理依赖

```bash
$ yarn
$ yarn add package.name
```

## 提交 commit 

```bash
$ git cz
```

## 合并并 Push dev

```bash
# 该命令会先切换到 开发分支（dev), pull 远程的dev 分支后，会合并当前的工作分支, 然后
# 执行 git push 操作，最后切回到工作分支，rebase 开发分支的内容
$ npm run push-dev
```

## 版本发布

```bash
# 默认分支为 master , 发布为此版本更新
$ npm run release

#【自定义】版本发布名称为 v1.0.0-test
$ npm run release -- -r v1.0.0-test

# 指定升级版本为【次】版本号
$ npm run release -- -r minor

# 指定升级版本为【主】版本号
$ npm run release -- -r major

# 指定升级版本为【修订】版本号
$ npm run release -- -r patch

# 指定发布分支
$ npm run release -- -b branchName

# 指定发布分支以及发布名称
$ npm run release -- -b branchName -r versionName

```

## 手动生成 CHANGELOG

```bash
$ npm run changelog
```

# 其他文档
- [部署文档](./docs/Deploy.md)
- [Git 协作工作流](http://git.dtstack.cn/ziv/data-stack-web/wikis/gitflow)
- [项目技术和架构设计说明
](http://git.dtstack.cn/ziv/data-stack-web/wikis/Development)
- [Redmine Issue 追踪](http://redmine.prod.dtstack.cn/projects/dtinsight200)
- [Confluence 文档](http://confluence.dev.dtstack.cn/display/RDOS/RD-OS)
- [RDOS API 接口文档](http://git.dtstack.cn/dtstack/rdos-docs)


