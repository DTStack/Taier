import React from 'react';
import CodeBlock from '@/pages/DataModel/components/CodeBlock';

const SqlPreview = (props) => {
  const { code } = props;
  return (
    <div className="card-container">
      <div className="inner-container">
        <CodeBlock code={code} />
      </div>
    </div>
  );
};

export default SqlPreview;
