import React from 'react';
import './style';

interface IPropsContainer {
  children: React.ReactNode;
}

const Container = (props: IPropsContainer) => {
  const { children } = props;
  return <div className="dm-container">{children}</div>;
};

export default Container;
