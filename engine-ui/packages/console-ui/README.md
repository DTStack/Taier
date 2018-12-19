# DATA-STACK | [CHANGELOG](./CHAGNELOG.md)

数栈 Web 前端。

# 开发
基于webpack的开发配置环境，可以使用npm管理项目

```bash
$ npm i
$ npm run dev
$ npm run build
```
推荐使用yarn管理依赖

```bash
$ yarn
$ yarn add package.name
```

## 提交 commit 

```bash
$ git cz
```

## 版本发布

```bash
# 默认分支为 master , 发布为此版本更新
$ npm run release

# 指定版本发布名称为 v1.0.0-test
$ npm run release -- -r v1.0.0-test

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
- [Git协作工作流](http://git.dtstack.cn/ziv/data-stack-web/wikis/gitflow)
- [项目技术和架构设计说明
](http://git.dtstack.cn/ziv/data-stack-web/wikis/Development)
- [Redmine Issue追踪](http://redmine.prod.dtstack.cn/projects/dtinsight200)
- [Confluence文档](http://confluence.dev.dtstack.cn/display/RDOS/RD-OS)
- [部署说明](http://git.dtstack.cn/ziv/data-stack-web/wikis/deploy)
- [RDOS API接口文档](http://git.dtstack.cn/dtstack/rdos-docs)


