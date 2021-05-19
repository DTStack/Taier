import React, { useEffect, useState } from 'react';
import { Tabs, Spin } from 'antd';
import CodeBlock from '../components/CodeBlock';
import { API } from '@/services';
import Message from 'pages/DataModel/components/Message';
import './style';
import { IModelDetail } from '../types';
import VersionHistory from './VersionHistory';
import ModelBasicInfo from './ModelBasicInfo';
const { TabPane } = Tabs;

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
            <ModelBasicInfo modelDetail={modelDetail} />
          </TabPane>
          <TabPane tab="SQL信息" key="2">
            <div className="pane-container">
              <div className="card-container">
                <div className="inner-container">
                  <CodeBlock code={code} />
                </div>
              </div>
            </div>
          </TabPane>
          <TabPane tab="版本变更" key="3">
            <div className="pane-container">
              <VersionHistory />
            </div>
          </TabPane>
        </Tabs>
      </div>
    </div>
  );
};

export default Detail;
