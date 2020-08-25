################################################################################
#  Licensed to the Apache Software Foundation (ASF) under one
#  or more contributor license agreements.  See the NOTICE file
#  distributed with this work for additional information
#  regarding copyright ownership.  The ASF licenses this file
#  to you under the Apache License, Version 2.0 (the
#  "License"); you may not use this file except in compliance
#  with the License.  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
# limitations under the License.
################################################################################

FROM openjdk:8u242-jdk

# build params
# flink-dist can point to a directory or a tarball on the local system
ARG flink_dist=dt-flink-1.10.tar.gz
ARG filebeat_path=filebeat


# Create flink user
RUN  groupadd -r flink && useradd -r -g flink flink

# Install requirements
ENV GOSU_VERSION 1.11
RUN set -ex; \
    \
    fetchDeps=" \
        libsnappy1v5 \
        gettext-base \
        sysstat \
        dnsutils \
        vim \
        python \
        python3 \
        ca-certificates \
        dirmngr \
        gnupg \
        wget \
        git \
        procps \
    "; \
    apt-get update; \
    apt-get install -y --no-install-recommends $fetchDeps; \
    rm -rf /var/lib/apt/lists/*; \
    \
    dpkgArch="$(dpkg --print-architecture | awk -F- '{ print $NF }')"; \
    wget -O /usr/local/bin/gosu "https://github.com/tianon/gosu/releases/download/$GOSU_VERSION/gosu-$dpkgArch"; \
    wget -O /usr/local/bin/gosu.asc "https://github.com/tianon/gosu/releases/download/$GOSU_VERSION/gosu-$dpkgArch.asc"; \
    export GNUPGHOME="$(mktemp -d)"; \
    gpg --batch --keyserver ha.pool.sks-keyservers.net --recv-keys B42F6819007F00F88E364FD4036A9C25BF357DD4; \
    gpg --batch --verify /usr/local/bin/gosu.asc /usr/local/bin/gosu; \
    gpgconf --kill all; \
    rm -r "$GNUPGHOME" /usr/local/bin/gosu.asc; \
    chmod +x /usr/local/bin/gosu; \
    gosu nobody true; \
    \
    /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone; \
    \
    wget -O /opt/flink-shaded-hadoop-2-uber-2.7.5-10.0.jar "https://repo.maven.apache.org/maven2/org/apache/flink/flink-shaded-hadoop-2-uber/2.7.5-10.0/flink-shaded-hadoop-2-uber-2.7.5-10.0.jar"; \
    mkdir -p /opt/hadoop/etc/hadoop; \
    echo "alias ll='ls -alF'" >> /etc/profile

# Add flinkStreamSql plugin and flinkx plugin
RUN mkdir -p /opt/dtstack/flinkplugin/sqlplugin && \
    mkdir -p /opt/dtstack/flinkplugin/syncplugin
ADD flinkStreamSQL/sqlplugins/ /opt/dtstack/flinkplugin/sqlplugin/
ADD flinkx/plugins/ /opt/dtstack/flinkplugin/syncplugin/

# Flink environment variables
ENV FLINK_INSTALL_PATH=/opt
ENV FLINK_HOME $FLINK_INSTALL_PATH/flink
ENV FLINK_LIB_DIR $FLINK_HOME/lib
ENV FLINK_PLUGINS_DIR $FLINK_HOME/plugins
ENV FLINK_OPT_DIR $FLINK_HOME/opt
ENV FLINK_JOB_ARTIFACTS_DIR $FLINK_INSTALL_PATH/artifacts
ENV PATH $PATH:$FLINK_HOME/bin
ENV HADOOP_CONF_DIR /opt/hadoop/etc/hadoop
ENV HADOOP_USER_NAME admin


# Install build dependencies and flink
ADD $flink_dist $FLINK_INSTALL_PATH/
# Install filebeat
ADD $filebeat_path /opt/

RUN set -x && \
  ln -s $FLINK_INSTALL_PATH/flink-[0-9]* $FLINK_HOME && \
  ln -s $FLINK_OPT_DIR/flink-cep*.jar $FLINK_LIB_DIR && \
  ln -s $FLINK_OPT_DIR/flink-metrics-prometheus*.jar $FLINK_LIB_DIR && \
  ln -s $FLINK_OPT_DIR/flink-python*.jar $FLINK_LIB_DIR && \
  if [ -f ${FLINK_INSTALL_PATH}/flink-shaded-hadoop* ]; then ln -s ${FLINK_INSTALL_PATH}/flink-shaded-hadoop* $FLINK_LIB_DIR; fi && \
  chown -R flink:flink ${FLINK_INSTALL_PATH}/flink-* && \
  chown -h flink:flink $FLINK_HOME && \
  chown -R flink:flink /opt/filebeat && \
  chown -R flink:flink /opt/dtstack

COPY kubernetes-entry.sh /opt/flink/bin/


WORKDIR /opt/flink
EXPOSE 8081 6123
ENTRYPOINT ["/opt/flink/bin/kubernetes-entry.sh"]
CMD ["--help"]
