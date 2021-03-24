import React from 'react';
import './style';

interface IPropsPaneTitle {
  title: string;
}

const PaneTitle = (props: IPropsPaneTitle) => {
  const { title } = props;
  return (
    <div className="pane-title">
      <div className="block" />
      <span className="title">{title}</span>
    </div>
  );
};

export default PaneTitle;
