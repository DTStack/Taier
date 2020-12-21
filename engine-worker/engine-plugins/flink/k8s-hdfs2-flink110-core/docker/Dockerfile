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

FROM 172.16.8.120:5443/dtstack-dev/flink_base:1.0.0

# build params
# flink-dist can point to a directory or a tarball on the local system
ARG flink_dist=flink-1.10-dt.tar.gz
ARG flinkplugin=/data/insight_plugin/flinkplugin

# Install requirements
ENV GOSU_VERSION 1.11

RUN yum -y install sudo expect openssh-clients && \
    chmod 755 /etc/sudoers && \
    echo "flink	ALL=(ALL) NOPASSWD:ALL" >> /etc/sudoers && \
    chmod 440 /etc/sudoers

# Install plugins
COPY --chown=flink:flink flinkStreamSQL/sqlplugins/ $flinkplugin/sqlplugin/
COPY --chown=flink:flink flinkx/syncplugins/ $flinkplugin/syncplugin/

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
COPY --chown=flink:flink $flink_dist $FLINK_INSTALL_PATH/

RUN set -x && \
 tar zxf /opt/${flink_dist} -C $FLINK_INSTALL_PATH && \
 chown -R flink:flink /opt/flink-*-dt* && \
 rm -f /opt/${flink_dist} && \
 ln -s $FLINK_INSTALL_PATH/flink-[0-9]* $FLINK_HOME && \
 ln -s $FLINK_OPT_DIR/flink-cep*.jar $FLINK_LIB_DIR && \
 ln -s $FLINK_OPT_DIR/flink-metrics-prometheus*.jar $FLINK_LIB_DIR && \
 ln -s $FLINK_OPT_DIR/flink-python*.jar $FLINK_LIB_DIR && \
 if [ -f ${FLINK_INSTALL_PATH}/flink-shaded-hadoop* ]; then ln -s ${FLINK_INSTALL_PATH}/flink-shaded-hadoop* $FLINK_LIB_DIR; fi && \
 chmod 755 -R /opt/filebeat

COPY kubernetes-entry.sh /opt/flink/bin/

USER flink
WORKDIR /opt/flink
EXPOSE 8081 6123
ENTRYPOINT ["/opt/flink/bin/kubernetes-entry.sh"]
CMD ["--help"]
