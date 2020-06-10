## 插件说明
-   版本信息: CDH-6.2.0-1.cdh6.2.0.p0.967373

-   简介: 该插件主要由于焦点科技用户使用CDH自带spark提交任务，spark版本为2.4.0-cdh6.2.0
## 修改内容
任务提交到Yarn上以后找不到运行环境的类

Spark提交到yarn上后，需要下载上传到HDFS上的jar作为启动的classpath，cdh的spark版本读取spark.yarn.dist.files参数中的文件并下载作为classpath，没有单独写插件时配置项为空，classpath为空导致类找不到。