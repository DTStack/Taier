import React from 'react';
import { Tabs } from 'antd';
import HTable from '../components/HTable';
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
          <TabPane tab="Tab Title 1" key="1">
            {modelId}
            <HTable />
          </TabPane>
          <TabPane tab="Tab Title 2" key="2">
            this is tab pane 2...
          </TabPane>
        </Tabs>
      </div>
    </div>
  )
}

export default Detail;
