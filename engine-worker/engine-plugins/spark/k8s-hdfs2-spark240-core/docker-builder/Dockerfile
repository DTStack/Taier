#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

FROM openjdk:8-jdk

ARG spark_version=2.4.4
ARG filebeat_path=filebeat

# Before building the docker image, first build and make a Spark distribution following
# the instructions in http://spark.apache.org/docs/latest/building-spark.html.
# If this docker file is being used in the context of building your images from a Spark
# distribution, the docker build command should be invoked from the top level directory
# of the Spark distribution. E.g.:
# docker build -t spark:latest -f kubernetes/dockerfiles/spark/Dockerfile .

# Create flink user
RUN  groupadd -r spark && useradd -r -g spark spark

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
        expect \
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
    /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone; \
    mkdir -p /opt/dtstack


# Install spark
RUN wget -O /opt/dtstack/spark-$spark_version.tgz "https://archive.apache.org/dist/spark/spark-$spark_version/spark-$spark_version-bin-hadoop2.7.tgz" && \
    cd /opt/dtstack && tar -zxvf spark-$spark_version.tgz && \
    rm -rf spark-$spark_version.tgz

# set env
ENV SPARK_HOME /opt/spark
ENV PYTHONPATH /opt/spark/python/lib/pyspark.zip:${SPARK_HOME}/python/lib/py4j-*.zip

# Install filebeat
ADD $filebeat_path /opt/dtstack

COPY entrypoint.sh /opt/dtstack
COPY sftpdownload.sh /opt/dtstack
COPY spark-sql-proxy.jar /opt/dtstack/

RUN set -x && \
  ln -s /opt/dtstack/spark-[0-9]* $SPARK_HOME && \
  chown -h spark:spark $SPARK_HOME &&\
  chown -R spark:spark /opt/dtstack && \
  chmod 755 /opt/dtstack/entrypoint.sh && \
  chmod 755 /opt/dtstack/sftpdownload.sh && \
  mkdir /opt/spark/logs

# update kubernetes-client
RUN rm -rf /opt/spark/jars/kubernetes-* && \
    wget -O /opt/spark/jars/kubernetes-client-4.6.1.jar "https://repo1.maven.org/maven2/io/fabric8/kubernetes-client/4.6.1/kubernetes-client-4.6.1.jar" && \
    wget -O /opt/spark/jars/kubernetes-model-4.6.1.jar "https://repo1.maven.org/maven2/io/fabric8/kubernetes-model/4.6.1/kubernetes-model-4.6.1.jar" && \
    wget -O /opt/spark/jars/kubernetes-model-common-4.6.1.jar "https://repo1.maven.org/maven2/io/fabric8/kubernetes-model-common/4.6.1/kubernetes-model-common-4.6.1.jar"

WORKDIR /opt/spark
ENTRYPOINT [ "/opt/dtstack/entrypoint.sh" ]