---
title: 贡献指南
sidebar_label: 贡献指南
---

## 为 Taiga 做贡献
Taiga 使用了Apache的多个开源项目如Flink、Spark 作为计算组件实现数据同步和批处理计算，得益于开源社区才有 Taiga。取之社区，
回馈社区。Taiga 迈出 v1.0.0 版本的这一步代表着袋鼠云-研发中心对开源的决心，未来我们会整合内部资源
尽快推出后续版本，也欢迎对 Taiga 感兴趣的开源伙伴一起参与共建！提出你宝贵的Issue 与 PR！

如果您想为 Taiga 做贡献（即便是一些微小的），请不要犹豫，参考下面的指导方针。

### 联系我们
我们使用[钉钉](https://www.dingtalk.com/) 沟通交流，可以搜索群号[**30537511**]或者扫描下面的二维码进入钉钉群
<div align="center"> 
 <img src="./readme/ding.jpeg" width="300" />
</div>

### 报告问题
在报告任何关于 Taiga 的问题时，请前往[Issues](https://github.com/DTStack/Taiga/issues/new) 。

### 代码约定
保持代码的一致性，提高代码的可读性来保证代码的高质量及高维护性。我们的代码风格和标准 Java 约定一致，并参考《阿里巴巴Java开发手册》，额外附加限制：
* 将ASF许可注释添加到所有新的 .java 文件（从项目中的现有文件复制）

* 对于新的特征或重要的修复程序，应该添加单元测试。

* 如果没有其他人使用您的分支，请将它与 master（或主项目中的其他目标分支）同步。



### 贡献流程
这是一个贡献者工作流程的大致说明：

1. 克隆 Taiga 项目
2. 从希望贡献的分支上创新新的分支，通常是 master 分支。
3. 提交您的更改。
4. 确保提交消息的格式正确。
5. 将新分支推送到您克隆的代码库中。
6. 执行检查表 pull request模版。
7. 在提交 pull request 请求前, 请将您克隆的代码和远程代码库同步，这样您的 pull request 会简单清晰。具体操作如下

```shell
git remote add upstream git@github.com:DTStack/Taiga.git
git fetch upstream
git rebase upstream/master
git checkout -b your_awesome_patch
... add some work
git push origin your_awesome_patch

```


