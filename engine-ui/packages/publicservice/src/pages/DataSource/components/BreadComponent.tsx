import React from 'react';
import { Breadcrumb } from 'antd';
import { hashHistory } from 'react-router';

export default function BreadComponent(props: { name: string }) {
  let { name } = props;

  return (
    <div>
      <Breadcrumb>
        <Breadcrumb.Item
          onClick={() => {
            hashHistory.push('/data-source/list');
          }}>
          <a>数据源中心</a>
        </Breadcrumb.Item>
        <Breadcrumb.Item>
          <a>{name}数据源</a>
        </Breadcrumb.Item>
      </Breadcrumb>
    </div>
  );
}
