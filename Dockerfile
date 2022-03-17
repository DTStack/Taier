FROM centos

# 作者
MAINTAINER dazhi <dazhi@dtstack.com>

# 创建目录
RUN mkdir /usr/local/java
RUN mkdir /usr/local/java/jre
# 添加jre
ADD docker/jre-8u202-linux-x64.tar.gz /usr/local/java/jre

RUN ls /usr/local/java/jre
ENV JAVA_HOME /usr/local/java/jre/jre1.8.0_202
ENV PATH $JAVA_HOME/bin:$PATH

# 定义工作目录
ENV WORK_PATH /usr/taier

WORKDIR $WORK_PATH
# 第二步 上传taier 相关jar

# 创建对应的文件夹
ENV TAIER_LIB $WORK_PATH/lib
ENV TAIER_PLUGINLIBS $WORK_PATH/pluginLibs
ENV TAIER_LOGS $WORK_PATH/logs
ENV TAIER_BIN $WORK_PATH/bin
ENV TAIER_CONF $WORK_PATH/conf
ENV TAIER_RUN $WORK_PATH/run

ENV MYSQL_ROOT root
ENV MYSQL_ROOT_PASSWORD 123456
ENV MYSQL_IP 127.0.0.1
ENV MYSQL_PORT 3306
ENV NODE_ZKADDRESS 127.0.0.1:2181/taier

RUN mkdir $TAIER_LIB && \
mkdir $TAIER_PLUGINLIBS   && \
mkdir $TAIER_LOGS   && \
mkdir $TAIER_BIN   && \
mkdir $TAIER_CONF && \
mkdir $TAIER_RUN && \
touch $TAIER_RUN/rdos.pid && \
touch $TAIER_LOGS/rdos.stdout

COPY lib/* $TAIER_LIB/
COPY pluginLibs/* $TAIER_PLUGINLIBS/
COPY bin/* $TAIER_BIN/
COPY conf/* $TAIER_CONF/

# 修改配置文件
CMD sed -i "s/jdbc.username=/jdbc.username=$MYSQL_ROOT/g" $WORK_PATH/conf/application.properties && \
sed -i "s/jdbc.password=/jdbc.password=$MYSQL_ROOT_PASSWORD/g" $WORK_PATH/conf/application.properties && \
sed -i "s/mysql:\/\/127.0.0.1:3306/mysql:\/\/$MYSQL_IP:$MYSQL_PORT/g" $WORK_PATH/conf/application.properties && \
sed -i "s/nodeZkAddress=/nodeZkAddress=$NODE_ZKADDRESS/g" $WORK_PATH/conf/application.properties && \
sed -i "s/server.port =/server.port =$PORT/g" $WORK_PATH/conf/application.properties && \
./bin/taier.sh start














