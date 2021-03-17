import React, { useEffect, useState } from 'react';
import { Tabs, message as Message } from 'antd';
import HTable from './HTable';
import PaneTitle from '../components/PaneTitle';
import DataInfo from './DataInfo';
import { API } from '@/services';
import './style';
import { IModelDetail } from '../types';

const { TabPane } = Tabs;

interface IPropsDetail {
  modelId: number;
}

const Detail = (props: IPropsDetail) => {
  const { modelId } = props;
  const [modelDetail, setModelDetail] = useState<Partial<IModelDetail>>({
    joinList: [],
    metricColumns: [],
    dimensionColumns: [],
  });

  const getModelDetail = async (id: number) => {
    try {
      const { success, data, message } = await API.getModelDetail({ id });
      if(success) {
        setModelDetail(data as IModelDetail);
      } else {
        Message.error(message);
      }
    } catch(error) {
      Message.error(error.message);
    }
  }

  useEffect(() => {
    getModelDetail(modelId);
  }, [modelId])
  return (
    <div className="dm-detail">
      <div className="card-container">
        <Tabs type="card">
          <TabPane tab="基本信息" key="1">
            <div className="pane-container">
              <div className="inner-container">
                <div className="margin-bottom-20">
                  <PaneTitle title="模型信息" />
                  <HTable detail={modelDetail} />
                </div>

                <div className="margin-bottom-20">
                  <PaneTitle title="关联视图" />
                  <div className="releation-view" />
                </div>

                <div className="margin-bottom-20">
                  <PaneTitle title="数据信息" />
                  <DataInfo
                    relationTableList={modelDetail.joinList}
                    metricList={modelDetail.metricColumns}
                    dimensionList={modelDetail.dimensionColumns}
                  />
                </div>
              </div>
            </div>
          </TabPane>
          <TabPane tab="SQL信息" key="2">
            this is tab pane 2...
          </TabPane>
        </Tabs>
      </div>
    </div>
  )
}

export default Detail;
