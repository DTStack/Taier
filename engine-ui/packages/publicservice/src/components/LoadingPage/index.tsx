import React, { CSSProperties } from 'react';
import { Spin } from 'antd';
import './style';

interface IPropsLoadingPage {
  style?: CSSProperties;
  className?: string;
}

const LoadingPage = (props: IPropsLoadingPage) => {
  const { style, className = '' } = props;
  const _className = 'loading-page ' + className;
  return (
    <div className={_className} style={{ ...style }}>
      <Spin className="loading-page-spin" />
    </div>
  );
};

export default LoadingPage;
