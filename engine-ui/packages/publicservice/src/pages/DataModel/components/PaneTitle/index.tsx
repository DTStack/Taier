/*
 * @Author: lenlen
 * @Date: 2021-04-19 10:53:46
 * @LastEditors: lenlen
 * @LastEditTime: 2021-04-19 19:22:45
 * @FilePath: \dt-public-service-front\src\pages\DataModel\components\PaneTitle\index.tsx
 */
import React from 'react';
import './style';

interface IPropsPaneTitle {
  title: string;
}

const PaneTitle = (props: IPropsPaneTitle) => {
  const { title } = props;
  return (
    <div className="pane-title" data-testid="pane-title">
      {/* <div className="block" /> */}
      <span className="title">{title}</span>
    </div>
  );
};

export default PaneTitle;
