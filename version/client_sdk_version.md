## engine-client版本记录

engine-client 版本说明：
   1. 只要有接口修改，切记一定要修改sdk，对应的接口。 
   2. 每次修改sdk后，测试环境调试时deploy带有SNAPSHOT版本，正式发布上线必须去掉SNAPSHOT，原因是正版版本不会被覆盖，带有SNAPSHOT版本有很多人
   有权限去覆盖，很容易出问题。
   3. 版本说明: 发布正式版,版本需要和引擎分支的版本对应上,例如: 这次上线需求是在引擎4.0上开发的 engine-client的正式版本: 
   <version>4.0.x</version>(如果是4.1的需求 就是<version>4.1.x</version>，x 表示当前发布数)
   4. 每一次deploy正式版本的时候必须标明版本号，时间，原因等，SNAPSHOT版本不需要记录。
   
   **注意： 线上依赖必须是正式版本，不能是SNAPSHOT**
版本记录
  
  4.0
--------------------------------------------------------

版本：4.0.1
时间：2020-10-17 11:20:10
原因：sdk上线
添加人: 大智
pom: 
<dependency>
  <groupId>com.dtstack.engine</groupId>
  <artifactId>sdk-engine-client</artifactId>
  <version>4.0.1</version>
</dependency>



  4.1 
--------------------------------------------------------





   