/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React, { useEffect, useState } from 'react';
import { Tabs, Spin } from 'antd';
import { API } from '@/services';
import Message from 'pages/DataModel/components/Message';
import './style';
import { IModelDetail } from '../types';
import VersionHistory from './VersionHistory';
import ModelBasicInfo from './ModelBasicInfo';
import SqlPreview from './SqlPreview';
const { TabPane } = Tabs;
import './style';

interface IPropsDetail {
  modelId: number;
}

const Detail = (props: IPropsDetail) => {
  const { modelId } = props;
  const [modelDetail, setModelDetail] = useState<Partial<IModelDetail>>({
    joinList: [],
    columns: [],
  });
  const [code, setCode] = useState<string>('');
  const [loading, setLoading] = useState(false);

  const getModelDetail = async (id: number) => {
    if (id === -1) return;
    setLoading(true);
    try {
      const { success, data, message } = await API.getModelDetail({
        id,
        version: '0',
      });
      if (success) {
        setModelDetail(data as IModelDetail);
        const params = {
          ...data,
        };
        params.columnList = params.columns;
        delete params.columns;
        getSql(params);
      } else {
        Message.error(message);
      }
    } catch (error) {
      Message.error(error.message);
    } finally {
      setLoading(false);
    }
  };

  const getSql = async (modelDetail) => {
    setLoading(true);
    try {
      const { success, data, message } = await API.previewSql(modelDetail);
      if (success) {
        setCode(data.result);
      } else {
        setCode('');
        Message.error(message);
      }
    } catch (error) {
      setCode('');
      Message.error(error.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    getModelDetail(modelId);
  }, [modelId]);

  return (
    <div className="dm-detail">
      <div className="card-container">
        {loading ? (
          <div className="dm-modal">
            <Spin className="center" />
          </div>
        ) : null}
        <div className="drawer-title">{modelDetail.modelName}</div>
        <Tabs type="card">
          <TabPane tab="基本信息" key="1">
            <div className="pane-container">
              <ModelBasicInfo modelDetail={modelDetail} />
            </div>
          </TabPane>
          <TabPane tab="SQL信息" key="2">
            <div className="pane-container">
              <SqlPreview code={code} />
            </div>
          </TabPane>
          <TabPane tab="版本变更" key="3">
            <div className="pane-container">
              <VersionHistory
                modelId={modelDetail.id}
                modelStatus={modelDetail.modelStatus}
                onRecover={() => {
                  getModelDetail(modelId);
                }}
              />
            </div>
          </TabPane>
        </Tabs>
      </div>
    </div>
  );
};

export default Detail;
