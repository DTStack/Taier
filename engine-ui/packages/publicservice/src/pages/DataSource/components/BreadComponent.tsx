import React from 'react';
import { Breadcrumb } from 'antd';
import { withRouter } from 'react-router';

function BreadComponent(props: { name: string; router?: any }) {
  let { name, router } = props;

  return (
    <div>
      <Breadcrumb>
        <Breadcrumb.Item
          onClick={() => {
            router.push('/data-source/list');
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
export default withRouter(BreadComponent);
