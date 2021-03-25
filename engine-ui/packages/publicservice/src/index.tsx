import React from 'react';
import { render } from 'react-dom';
import { Provider } from 'react-redux';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/es/locale/zh_CN';
import Routers from './router';
import 'assets/styles/index.less';
import store from './store';
import 'ant-design-dtinsight-theme/theme/dt-theme/reset.less';
import 'ant-design-dtinsight-theme/theme/dt-theme/index.less';

const App = () => (
  <ConfigProvider locale={zhCN}>
    <Provider store={store}>
      <Routers />
    </Provider>
  </ConfigProvider>
);

render(<App />, document.getElementById('app'));
