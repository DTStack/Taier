FROM nginx

# 作者
MAINTAINER dazhi <dazhi@dtstack.com>

# 定义工作目录
ENV WORK_PATH /usr/taier
ENV NODE_HOME /usr/local/node/node-v16.15.0-linux-x64
ENV PATH=$NODE_HOME/bin:$PATH
ENV TAIER_IP localhost
ENV TAIER_PORT 8090
ENV TAIER_UI $WORK_PATH/ui

WORKDIR $WORK_PATH

RUN mkdir /usr/local/node && \
mkdir $TAIER_UI

# 添加node环境
ADD https://nodejs.org/dist/v16.15.0/node-v16.15.0-linux-x64.tar.xz /usr/local/node

# 拷贝nginx配置文件
COPY docker/nginx.template /etc/nginx/conf.d/

# 上传taier-ui相关文件
COPY dist $TAIER_UI/dist

EXPOSE 80

ENTRYPOINT envsubst '\${TAIER_IP} \${TAIER_PORT}' < /etc/nginx/conf.d/nginx.template > /etc/nginx/conf.d/default.conf && nginx -g 'daemon off;'

