import React, { Component } from 'react';
import { Router } from 'react-router';
import { Provider } from 'react-redux';
import { getRoutes } from './routers';
import zhCN from 'antd/es/locale/zh_CN';
import { ConfigProvider } from 'antd';

import 'ant-design-dtinsight-theme/theme/dt-theme/reset.less';
import 'ant-design-dtinsight-theme/theme/dt-theme/index.less';

interface Props {
  store: any;
  history?: any;
}
export default class Root extends Component<Props> {
  render() {
    const { store, history } = this.props;
    return (
      <ConfigProvider locale={zhCN}>
        <Provider store={store}>
          <Router routes={getRoutes()} history={history} />
        </Provider>
      </ConfigProvider>
    );
  }
}
