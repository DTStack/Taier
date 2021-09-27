import React from 'react';
import CodeBlock from '@/pages/DataModel/components/CodeBlock';

const overflowContainer = (child) => {
  return (
    <div className="card-container">
      <div className="inner-container">{child}</div>
    </div>
  );
};

const noneOverflowScrollContainer = (child) => {
  return <div>{child}</div>;
};

const SqlPreview = (props) => {
  const { code, overflowEnable = true } = props;
  const container = overflowEnable
    ? overflowContainer
    : noneOverflowScrollContainer;
  return container(<CodeBlock code={code} overflowEnable={overflowEnable} />);
};

export default SqlPreview;
