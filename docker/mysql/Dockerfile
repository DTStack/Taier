# 构建mysql 从5.7.37 版本开始
FROM mysql:5.7.37-oracle

# 作者
MAINTAINER dazhi <dazhi@dtstack.com>

# 定义会被容器自动执行的目录
ENV AUTO_RUN_DIR /docker-entrypoint-initdb.d

#### 执行sql ####
# 定义要执行的sql文件名
ENV CREATE_SQL_FILE sql/init.sql

# 配置mysql
ENV MYSQL_DATABASE=taier
ENV MYSQL_ROOT_PASSWORD 123456

# 设置mysql的编码
COPY utf8mb4.cnf /etc/mysql/conf.d/utf8mb4.cnf

# 把sql文件复制到工作目录下和初始化脚本
COPY $CREATE_SQL_FILE $AUTO_RUN_DIR/
COPY $INSERT_SQL_FILE $AUTO_RUN_DIR/

# 给执行文件增加可执行权限
RUN chmod a+x $AUTO_RUN_DIR/$CREATE_DATA_SHELL