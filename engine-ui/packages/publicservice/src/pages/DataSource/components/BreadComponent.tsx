import React from 'react';
import { Breadcrumb } from 'antd';
import { useHistory } from 'react-router';

export default function BreadComponent(props: { name: string }) {
  const history = new useHistory();

  let { name } = props;

  return (
    <div>
      <Breadcrumb>
        <Breadcrumb.Item
          onClick={() => {
            history.push('/data-source');
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
