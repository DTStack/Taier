FROM suranagivinod/openjdk8

# 作者
MAINTAINER dazhi <dazhi@dtstack.com>

# 定义工作目录
ENV WORK_PATH /usr/taier

# 创建目录
# 创建对应的文件夹
ENV DATASOURCEX_PATH $WORK_PATH/datasourcex
ENV DB_ROOT root
ENV DB_PASSWORD 123456
ENV DB_HOST 127.0.0.1
ENV DB_PORT 3306
ENV ZK_HOST 127.0.0.1
ENV ZK_PORT 2181
WORKDIR $WORK_PATH

RUN mkdir $WORK_PATH/datasourcex && \
mkdir $WORK_PATH/lib && \
mkdir $WORK_PATH/pluginLibs   && \
mkdir $WORK_PATH/logs   && \
mkdir $WORK_PATH/bin   && \
mkdir $WORK_PATH/conf && \
mkdir $WORK_PATH/run && \
mkdir $WORK_PATH/finkconf && \
mkdir $WORK_PATH/sparkconf && \
touch $WORK_PATH/run/rdos.pid && \
touch $WORK_PATH/logs/rdos.stdout

COPY lib $WORK_PATH/lib/
COPY pluginLibs $WORK_PATH/pluginLibs/
COPY bin/ $WORK_PATH/bin/
COPY conf/ $WORK_PATH/conf/
COPY flinkconf/ $WORK_PATH/flinkconf/
COPY sparkconf/ $WORK_PATH/sparkconf/

# 修改配置文件
CMD ./bin/base.sh start