import React from 'react';
import { Tabs, Table } from 'antd';
import HTable from './HTable';
import PaneTitle from '../components/PaneTitle';
import DataInfo from './DataInfo';
import './style';

const { TabPane } = Tabs;

interface IPropsDetail {
  modelId: number;
}

const Detail = (props: IPropsDetail) => {
  const { modelId } = props;
  console.log(modelId);
  return (
    <div className="dm-detail">
      <div className="card-container">
        <Tabs type="card">
          <TabPane tab="基本信息" key="1">
            <div className="pane-container">
              <div style={{ width: '100%', height: '100%', overflow: 'auto' }}>
                <div className="margin-bottom-20" style={{ marginBottom: '20px' }}>
                  <PaneTitle title="模型信息" />
                  <HTable />
                </div>

                <div className="margin-bottom-20" style={{ marginBottom: '20px' }}>
                  <PaneTitle title="关联视图" />
                  <div className="releation-view" />
                </div>

                <div className="margin-bottom-20" style={{ marginBottom: '20px' }}>
                  <PaneTitle title="数据信息" />
                  <DataInfo />
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
