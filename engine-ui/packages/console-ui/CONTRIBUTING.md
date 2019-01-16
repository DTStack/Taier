# Contributing to DTinsight

如何开始参与贡献代码。 在正式编码前，你应该先阅读我们的[项目技术和架构设计说明
](http://git.dtstack.cn/dtstack/data-stack-web/wikis/Development), 和 [Git 协作工作流](http://git.dtstack.cn/dtstack/data-stack-web/wikis/gitflow)相关的内容。

# CodeReview 指南

## Code Review 常用工具

- Phabircator （Facebook)
- Gerrit (Google)
- Gitlab / Github
- ...

## Gitlab Code Review 基本流程

![codereview workflow](https://note.youdao.com/yws/api/personal/file/WEB8f0cb0cab85853c93fb4660713df45bd?method=download&shareKey=c8ae3328db2c9d855eec7bc315406d2e)

## Reviewer 的一些基本原则
- Reviewer 为模块最近的编辑者
- 团队资深的工程师
- 提前约定的 Reviewer
- 给跟你参与讨论和 Desgin 的人 Review
- MR 紧急，可以告知 Reviewers
- 单次 MR 的 commit 内容尽量不要过大（超过 10 个 commit ）
- ...


## checklist
CodeReview 时应该关注的点
- 错误的 API 调用，重复的 API 封装
- 代码质量（命名、冗余代码、可维护性、可读性等）
- 是否缺少应用的单月测试
- 配置、接口类的设计问题（合理性、友好性）
- 架构类问题（业务/技术）
- 缺陷类问题（功能，逻辑)
- 页面展示类的需要在浏览器 Review 成果
- 是否清理无用的注释代码
- ...


## 工具篇

- [Better Comments (VSCode - 插件)](https://marketplace.visualstudio.com/items?itemName=aaron-bond.better-comments)
- [ESLint (VSCode - 插件)](https://marketplace.visualstudio.com/items?itemName=dbaeumer.vscode-eslint)
- [GitLens (VSCode - 插件)](https://marketplace.visualstudio.com/items?itemName=eamodio.gitlens)

- [Gitlab MR (VSCode - 插件)](https://marketplace.visualstudio.com/items?itemName=jasonn-porch.gitlab-mr)
- [Redmine (VSCode - 插件)](https://marketplace.visualstudio.com/items?itemName=rozpuszczalny.vscode-redmine)

# 参考阅读

- https://www.zhihu.com/question/41089988
- https://sback.it/publications/icse2018seip.pdf
- https://coolshell.cn/articles/11432.html
- https://docs.gitlab.com/ee/integration/jenkins.html
https://gitlab.com/gitlab-org/gitlab-runner/merge_requests/1134/diffs
- https://gerrit-review.googlesource.com/q/status:open+project:gerrit
- https://docs.gitlab.com/ee/user/project/merge_requests/


(待完善)