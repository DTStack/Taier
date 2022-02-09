---
title: 贡献指南
sidebar_label: 贡献指南
---

## 为 Taiga 做贡献
Taiga 使用了Apache的多个开源项目如Flink、Spark 作为计算组件实现数据同步和批处理计算，得益于开源社区才有 Taiga。取之社区，
回馈社区。Taiga 迈出 v1.0.0 版本的这一步代表着 `袋鼠云技术研发团队` 对开源的决心，未来我们会
尽快推出 Taiga 后续版本，也欢迎对 Taiga 感兴趣的开源伙伴一起参与共建！提出你宝贵的Issue 与 PR！

如果您想为 Taiga 做贡献（即便是一些微小的），请不要犹豫，参考下面的指导方针。

### 联系我们
我们使用[钉钉](https://www.dingtalk.com/) 沟通交流，可以搜索群号[**30537511**]或者扫描下面的二维码进入钉钉群
<div align="center"> 
 <img src="/img/readme/ding.jpeg" width="300" />
</div>

### 报告问题
在报告任何关于 Taiga 的问题时，请前往[Issues](https://github.com/DTStack/Taiga/issues/new) 。

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

### 代码约定
保持代码的一致性，提高代码的可读性来保证代码的高质量及高维护性。我们的代码风格和标准 Java 约定一致，并参考《阿里巴巴Java开发手册》，额外附加限制：
* 将ASF许可注释添加到所有新的 .java 文件（从项目中的现有文件复制）

* 对于新的特征或重要的修复程序，应该添加单元测试。

* 如果没有其他人使用您的分支，请将它与 master（或主项目中的其他目标分支）同步。

### 代码风格
1. 点击Browse repositories–>再搜索CheckStyle–>找到CheckStyle-IDEA–>再点击Install–>自动安装完成后重启
2. 找到Other Settings –>点击Checkstyle–>再点击Configuration File的加号
   先填写规则描述名–>然后点击Browse导入规则文件–>点击Next–再点击Finish
3. 点击状态栏上的CheckStyle按钮，点击（红叉按钮下）旁边的Check project 或者check Module按钮，检查工程的不规则编码和习惯

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE module PUBLIC "-//Puppy Crawl//DTD Check Configuration 1.2//EN" "http://www.puppycrawl.com/dtds/configuration_1_2.dtd">

<module name="Checker">

    <property name="charset" value="UTF-8"/>
    <property name="severity" value="warning"/>
    <property name="fileExtensions" value="java"/>

    <module name="TreeWalker">

        <!-- Checks for imports    -->
        <!-- 必须导入类的完整路径，即不能使用*导入所需的类 -->
        <module name="AvoidStarImport"/>

        <!-- 检查是否从非法的包中导入了类 illegalPkgs: 定义非法的包名称-->
        <module name="IllegalImport"/>

        <!-- 检查是否导入了不必显示导入的类-->
        <module name="RedundantImport"/>

        <!-- 检查是否导入的包没有使用-->
        <module name="UnusedImports"/>

        <!-- Checks for whitespace
        <module name="EmptyForIteratorPad"/>
        <module name="MethodParamPad"/>
        <module name="NoWhitespaceAfter"/>
        <module name="NoWhitespaceBefore"/>
        <module name="OperatorWrap"/>
        <module name="ParenPad"/>
        <module name="TypecastParenPad"/>
        <module name="WhitespaceAfter"/>
        <module name="WhitespaceAround"/>
        -->

        <!--option: 定义左大括号'{'显示位置，eol在同一行显示，nl在下一行显示
          maxLineLength: 大括号'{'所在行行最多容纳的字符数
          tokens: 该属性适用的类型，例：CLASS_DEF,INTERFACE_DEF,METHOD_DEF,CTOR_DEF -->
        <module name="LeftCurly">
            <property name="option" value="eol"/>
        </module>

        <!-- NeedBraces 检查是否应该使用括号的地方没有加括号
          tokens: 定义检查的类型 -->
        <module name="NeedBraces"/>

        <!-- Checks the placement of right curly braces ('}') for  else, try, and catch tokens. The policy to verify is specified using property  option.
          option: 右大括号是否单独一行显示
          tokens: 定义检查的类型 
        <module name="RightCurly">
            <property name="option" value="alone"/>
        </module> -->

        <!-- 检查在重写了equals方法后是否重写了hashCode方法 -->
        <module name="EqualsHashCode"/>

        <!--  Checks for illegal instantiations where a factory method is preferred.
          Rationale: Depending on the project, for some classes it might be preferable to create instances through factory methods rather than calling the constructor.
          A simple example is the java.lang.Boolean class. In order to save memory and CPU cycles, it is preferable to use the predefined constants TRUE and FALSE. Constructor invocations should be replaced by calls to Boolean.valueOf().
          Some extremely performance sensitive projects may require the use of factory methods for other classes as well, to enforce the usage of number caches or object pools. -->
        <module name="IllegalInstantiation">
            <property name="classes" value="java.lang.Boolean"/>
        </module>

               <!-- 代码缩进 
        <module name="Indentation">
        </module>  -->

        <!-- Checks for redundant exceptions declared in throws clause such as duplicates, unchecked exceptions or subclasses of another declared exception.
          检查是否抛出了多余的异常
        <module name="RedundantThrows">
            <property name="logLoadErrors" value="true"/>
            <property name="suppressLoadErrors" value="true"/>
        </module>
        -->

        <!--  Checks for overly complicated boolean expressions. Currently finds code like  if (b == true), b || true, !false, etc.
          检查boolean值是否冗余的地方
          Rationale: Complex boolean logic makes code hard to understand and maintain. -->
        <module name="SimplifyBooleanExpression"/>

        <!--  Checks for overly complicated boolean return statements. For example the following code
           检查是否存在过度复杂的boolean返回值
           if (valid())
              return false;
           else
              return true;
           could be written as
              return !valid();
           The Idea for this Check has been shamelessly stolen from the equivalent PMD rule. -->
        <module name="SimplifyBooleanReturn"/>

        <!-- Checks that a class which has only private constructors is declared as final.只有私有构造器的类必须声明为final-->
        <module name="FinalClass"/>

        <!-- 每一行只能定义一个变量 -->
        <module name="MultipleVariableDeclarations">
        </module>

        <!-- Checks the style of array type definitions. Some like Java-style: public static void main(String[] args) and some like C-style: public static void main(String args[])
          检查再定义数组时，采用java风格还是c风格，例如：int[] num是java风格，int num[]是c风格。默认是java风格-->
        <module name="ArrayTypeStyle">
        </module>

        <!-- Checks that there are no "magic numbers", where a magic number is a numeric literal that is not defined as a constant. By default, -1, 0, 1, and 2 are not considered to be magic numbers.
        <module name="MagicNumber">
        </module>
        -->

        <!-- A check for TODO: comments. Actually it is a generic regular expression matcher on Java comments. To check for other patterns in Java comments, set property format.
           检查是否存在TODO（待处理） TODO是javaIDE自动生成的。一般代码写完后要去掉。
         -->
        <module name="TodoComment"/>

        <!--  Checks that long constants are defined with an upper ell. That is ' L' and not 'l'. This is in accordance to the Java Language Specification,  Section 3.10.1.
          检查是否在long类型是否定义了大写的L.字母小写l和数字1（一）很相似。
          looks a lot like 1. -->
        <module name="UpperEll"/>

        <!--  Checks that switch statement has "default" clause. 检查switch语句是否有‘default’从句
           Rationale: It's usually a good idea to introduce a default case in every switch statement.
           Even if the developer is sure that all currently possible cases are covered, this should be expressed in the default branch,
            e.g. by using an assertion. This way the code is protected aginst later changes, e.g. introduction of new types in an enumeration type. -->
        <module name="MissingSwitchDefault"/>

        <!--检查switch中case后是否加入了跳出语句，例如：return、break、throw、continue -->
        <module name="FallThrough"/>

        <!-- Checks the number of parameters of a method or constructor. max default 7个. -->
        <module name="ParameterNumber">
            <property name="max" value="5"/>
        </module>

        <!-- 每行字符数 -->
        <module name="LineLength">
            <property name="max" value="200"/>
        </module>

        <!-- Checks for long methods and constructors. max default 150行. max=300 设置长度300 -->
        <module name="MethodLength">
            <property name="max" value="300"/>
        </module>

        <!-- ModifierOrder 检查修饰符的顺序，默认是 public,protected,private,abstract,static,final,transient,volatile,synchronized,native -->
        <module name="ModifierOrder">
        </module>

        <!-- 检查是否有多余的修饰符，例如：接口中的方法不必使用public、abstract修饰  -->
        <module name="RedundantModifier">
        </module>

        <!--- 字符串比较必须使用 equals() -->
        <module name="StringLiteralEquality">
        </module>

        <!-- if-else嵌套语句个数 最多4层 -->
        <module name="NestedIfDepth">
            <property name="max" value="3"/>
        </module>

        <!-- try-catch 嵌套语句个数 最多2层 -->
        <module name="NestedTryDepth">
            <property name="max" value=""/>
        </module>

        <!-- 返回个数 -->
        <module name="ReturnCount">
            <property name="max" value="5"/>
            <property name="format" value="^$"/>
        </module>
    </module>
</module>

```

### 框架使用规范
1. 统一使用mybatisPlus、springboot
2. 使用mapstruct
3. 禁止使用lombok


### 命名规范
1. dao统一后缀为mapper 统一放入engine-dao 模块
2. datadevelop中 按照功能划分包为console、datasource、develop、schedule
3. controller对应的接口需要补充swagger、 统一返回值为 `R<Boolean>`
    * 如果controller未使用参数校验，禁止使用
    ```Java
       //        return new APITemplate<Boolean>() {
       //            @Override
       //            protected Boolean process() {
       //                return batchDataSourceService.canSetIncreConf(vo.getId());
       //            }
       //        }.execute();
    ```

    * 直接使用 `R.ok(batchDataSourceService.canSetIncreConf(vo.getId()));`

4. id、tenantId、userId等常见id 使用long类型
5. 组件枚举统一使用EComponentType
6. 任务枚举统一使用EScheduleJobType
7. 数据源枚举统一使用DataSourceType
8. 日志打印规范 统一使用LOGGER大写、debug日志需要判断是否开启了debug

    ```Java
       if (LOG.isDebugEnabled()) {
       LOG.debug("using local user:"+user);
       }
   ```
9. 异常错误 统一使用errorCode


### Commitment 规范
对 Commit Message 的格式有一定要求，三段式 commit 信息：[${jira-issue-id}][${affected-component}] ${jira-issue-title}
1. 根据 Issue-Id
    * 如: [Taiga-issueId][taiga-common] Translate "common module" page into Chinese

2. 无 Issue-Id 以分支名字命名
    * 如：[feat_doc][taiga-common] Translate "common module" page into Chinese


