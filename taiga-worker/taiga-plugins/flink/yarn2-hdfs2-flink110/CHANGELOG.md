
hadoop
----------------------
org.apache.hadoop.io.retry.RetryInvocationHandler

```
原因:
    开启kerberos环境下，在一些未知情况下导致连接yarn或者hdfs一直重试
更改项:
    1. 在invoke函数中限制重试次数(重试4次)
```