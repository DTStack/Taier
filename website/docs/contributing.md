---
title: 贡献指南
sidebar_label: 贡献指南
---

## 为 Taier 做贡献
**Taier** 使用了 Apache 的多个开源项目如`Flink`、`Spark` 作为计算组件实现数据同步和批处理计算    
得益于开源社区才有 **Taier**。取之社区， 回馈社区。也欢迎对 Taier 感兴趣的开源伙伴一起参与共建！提出你宝贵的`Issue` 与 `PR`

如果您想为 **Taier** 做贡献（即便是一些微小的），请不要犹豫，参考下面的指导方针

1. 查看标记为feature或question的标签的问题并解决    
2. 回答有疑问的问题   
3. 改善使用文档

### 报告问题
在报告任何关于 **Taier** 的问题时，请前往[issues](https://github.com/DTStack/Taier/issues/new) 

### 贡献流程
这是一个贡献者工作流程的大致说明：

1. 克隆 **Taier** 项目
2. 从希望贡献的分支上创新新的分支，通常是 `master` 分支。
3. 提交您的更改。
4. 确保提交消息的格式正确。
5. 将新分支推送到您克隆的代码库中。
6. 执行检查表 `pull request`模版。
7. 在提交 `pull request` 请求前, 请将您克隆的代码和远程代码库同步，这样您的 `pull request` 会简单清晰。
  
:::tip 
具体操作如下：

* git remote add upstream git@github.com:DTStack/Taier.git
* git fetch upstream
* git rebase upstream/master
* git checkout -b your_awesome_patch
* ... add some work
* git push origin your_awesome_patch
:::

### 代码约定
保持代码的一致性，提高代码的可读性来保证代码的高质量及高维护性。我们的代码风格和标准 `Java` 约定一致，并参考`《阿里巴巴Java开发手册》`。

:::tip
额外附加限制：
* 将ASF许可注释添加到所有新的 `.java` 文件（从项目中的现有文件复制）

* 对于新的特征或重要的修复程序，应该添加单元测试。

* 如果没有其他人使用您的分支，请将它与 `master`（或主项目中的其他目标分支）同步。
:::
  
### 代码风格
1. 点击`Browse repositories`–>再搜索`CheckStyle`–>找到`CheckStyle-IDEA`–>再点击`Install`–>自动安装完成后重启
2. 找到`Other Settings` –>点击`Checkstyle`–>再点击`Configuration File`的加号
   先填写规则描述名–>然后点击`Browse`导入规则文件–>点击`Next`–再点击`Finish`
3. 点击状态栏上的`CheckStyle`按钮，点击（红叉按钮下）旁边的`Check project` 或者`check Module`按钮，检查工程的不规则编码和习惯

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
            <property name="max" value="2"/>
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
1. 统一使用`mybatisPlus`、`springboot`
2. 使用`mapstruct`
3. 禁止使用`lombok`


### 命名规范
1. `dao`统一后缀为`mapper` 统一放入`engine-dao` 模块
2. `datadevelop`中 按照功能划分包为`console`、`datasource`、`develop`、`schedule`
3. `controller`对应的接口需要补充`swagger`，统一返回值为 `R<Boolean>`
如果`controller`未使用参数校验，禁止使用
```java
return new APITemplate<Boolean>() {
    @Override
    protected Boolean process() {
        return batchDataSourceService.canSetIncreConf(vo.getId());
    }
}.execute();
```

直接使用 
```java
R.ok(batchDataSourceService.canSetIncreConf(vo.getId()));
```
   
4. `id`、`tenantId`、`userId`等常见`id` 使用`long`类型
5. 组件枚举统一使用`EComponentType`
6. 任务枚举统一使用`EScheduleJobType`
7. 数据源枚举统一使用`DataSourceType`
8. 日志打印规范 统一使用`LOGGER`大写、`debug`日志需要判断是否开启了`debug`
```java
 if (LOG.isDebugEnabled()) {
     LOG.debug("using local user:"+user);
 }
```
9. 异常错误 统一使用`errorCode`

### Commitment 规范
 **Commit Message** 三段式格式要求，模板：`[${jira-issue-id}]``[${affected-component}]` `${jira-issue-title}`
* 根据`issue-id`，如: **[Taier-issueId][Taier-common] Translate "common module" page into Chinese**

:::tip 
无`issue-id`时，可以分支命名，如：**[feat_doc][Taier-common] Translate "common module" page into Chinese**
:::


### 联系我们
我们使用[钉钉](https://www.dingtalk.com/) 沟通交流，可以搜索群号[**30537511**]或者扫描下面的二维码进入钉钉群
<div align="center"> 
 <img src="/Taier/img/readme/ding.jpeg" width="300" />
</div>