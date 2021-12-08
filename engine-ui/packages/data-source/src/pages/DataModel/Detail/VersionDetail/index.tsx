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
import { IModelDetail } from '@/pages/DataModel/types';
import ModelBasicInfo from '../ModelBasicInfo';
import SqlPreview from '../SqlPreview';
import PaneTitle from '@/pages/DataModel/components/PaneTitle';
import LoadingPage from '@/components/LoadingPage';
import { API } from '@/services';
import Message from '@/pages/DataModel/components/Message';
import { EnumSize } from '../types';

interface IPropsVersionDetail {
  modelId: number;
  version: string;
}

const parseDetailParams = (detail: IModelDetail) => {
  const _detail: any = { ...detail };
  _detail.columnList = detail.columns;
  delete _detail.columns;
  return _detail;
};

const VersionDetail = (props: IPropsVersionDetail) => {
  // 获取选中版本数据模型详情
  // 更具模型详情获取sql信息
  const [modelDetail, setModelDetail] = useState<Partial<IModelDetail>>(null);
  const [sql, setSql] = useState('');
  const [loading, setLoading] = useState(false);
  const { modelId, version } = props;

  const getVersionDetail = async (modelId, version) => {
    setLoading(true);
    try {
      const detailRes = await API.getVersionDetail({
        id: modelId,
        version,
      });
      if (!detailRes.success) return Message.error(detailRes.message);
      setModelDetail(detailRes.data);
      const sqlRes = await API.previewSql(parseDetailParams(detailRes.data));
      if (!sqlRes.success) return Message.error(sqlRes.message);
      setSql(sqlRes.data.result);
    } catch (error) {
      Message.error(error.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    getVersionDetail(modelId, version);
  }, []);

  return (
    <div className="pane-container">
      {loading ? (
        <LoadingPage style={{ height: '300px' }} />
      ) : (
        <>
          <ModelBasicInfo modelDetail={modelDetail} size={EnumSize.SMALL} />
          <div>
            <PaneTitle title="SQL信息" />
            <SqlPreview code={sql} overflowEnable={false} />
          </div>
        </>
      )}
    </div>
  );
};

export default VersionDetail;
