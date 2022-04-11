FROM nginx

# 作者
MAINTAINER dazhi <dazhi@dtstack.com>

# 定义工作目录
ENV WORK_PATH /usr/taier
WORKDIR $WORK_PATH

RUN mkdir /usr/local/node

# 添加node环境
ADD docker/node-v16.14.0-linux-x64.tar.xz /usr/local/node

ENV NODE_HOME /usr/local/node/node-v16.14.0-linux-x64
ENV PATH=$NODE_HOME/bin:$PATH
ENV TAIER_IP localhost
ENV TAIER_PORT 8090

# 拷贝nginx配置文件
COPY docker/nginx.conf /etc/nginx/conf.d/default.conf

ENV TAIER_UI $WORK_PATH/ui
RUN mkdir $TAIER_UI

# 上传taier-ui相关文件
COPY dist $TAIER_UI/dist

EXPOSE 80

CMD sed -i "s!http://localhost:8090!http://$TAIER_IP:$TAIER_PORT!g" /etc/nginx/conf.d/default.conf && \
/bin/sh -c 'nginx -g "daemon off;"'
