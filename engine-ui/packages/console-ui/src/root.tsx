import * as React from 'react'
import { Router } from 'react-router'
import { Provider } from 'react-redux'
import { ConfigProvider } from 'antd'
import zhCN from 'antd/es/locale/zh_CN'

// 继承主应用
import 'ant-design-dtinsight-theme/theme/dt-theme/reset.less';
import 'ant-design-dtinsight-theme/theme/dt-theme/index.less';

// Styles
import './styles/main.scss'

import routers from './routers'

export default class Root extends React.Component<any, any> {
    render () {
        const { store, history } = this.props
        return (
            <ConfigProvider locale={zhCN}>
                <Provider store={store} >
                    <Router routes={routers} history={history} key={Math.random()} {...{ onEnter: () => {
                        console.log('enter')
                    } }} />
                </Provider>
            </ConfigProvider>
        )
    }
}
