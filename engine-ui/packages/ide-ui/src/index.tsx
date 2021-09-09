import React from 'react';
import ReactDOM from 'react-dom';
import { hashHistory } from 'react-router';
import { ConfigProvider } from 'antd';
import 'dt-react-component/lib/style/index.css';

import App from './views/App';
import '@/styles/task/task.scss';

const packageName = require('../package.json').name;

ReactDOM.render(
    <React.StrictMode>
        <ConfigProvider prefixCls={packageName}>
            <App history={hashHistory} />
        </ConfigProvider>
    </React.StrictMode>,
    document.getElementById('root')
);
