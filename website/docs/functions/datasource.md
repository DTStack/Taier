---
title: 数据源管理
sidebar_label: 数据源管理
---

# 创建数据源

## 创建Hive数据源

1、点击按钮，进入数据源中心页面

![img.png](/img/readme/img.png)

2、点击新增数据源

![img_1.png](/img/readme/img_1.png)

3、在搜索拦搜索Hive或者点击大数据存储找到Hive类型数据源

![img_2.png](/img/readme/img_2.png)

![img_3.png](/img/readme/img_3.png)

4、选择版本，这里我选择的是3.x，然后点击下一步进入数据源信息配置页面

![img_4.png](/img/readme/img_4.png)


5、数据源名称和jdbc url、defaultFs是必填的，如果defaultFs填的是域名形式的，则高可用配置一定要填。

![img_5.png](/img/readme/img_5.png)

6、如果需要开启高可用配置，则需要点击开启kerberos认证按钮，需要上传kerberos证书，
该文件是一个zip压缩文件，里面需要包含.keytab文件和krb5.conf文件。

7、填完必须要填的信息后，可以点击测试连通性按钮来检测该数据源是否可用，如果成功，再点击确定即可新增成功一个数据源。


其他数据源的创建过程基本和Hive数据源的创建一致。

