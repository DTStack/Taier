import React, { useEffect, useState } from 'react';
import { Tabs, Spin } from 'antd';
import { API } from '@/services';
import Message from 'pages/DataModel/components/Message';
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
      const { success, data, message } = await API.getModelDetail({ id });
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
              <VersionHistory modelId={modelDetail.id} />
            </div>
          </TabPane>
        </Tabs>
      </div>
    </div>
  );
};

export default Detail;
