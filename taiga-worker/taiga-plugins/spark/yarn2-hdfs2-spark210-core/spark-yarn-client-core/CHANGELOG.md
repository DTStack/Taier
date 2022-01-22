spark
----------------------
org.apache.spark.deploy.PythonRunner

```text
更改项：
   1. 为Spark拉起的PY4J进程设置任务所需的环境变量
    代码片段如下：
    // env requires by python process.
    sparkConf.getExecutorEnv.foreach( python_env => env.put(python_env._1, python_env._2))
```
