import { Router } from 'react-router'
import Layout from '../layout/layout'
import routers from '../routers'
import 'ant-design-dtinsight-theme/theme/dt-theme/reset.less'
import 'ant-design-dtinsight-theme/theme/dt-theme/index.less'

import './registerMicroApps'
import '@/styles/App.css'
import 'ant-design-dtinsight-theme/theme/dt-theme/default/index.less'

function App (props: any) {
    const { history } = props

    return (
        <Layout history={history}>
            <Router routes={routers} history={history} />
        </Layout>
    )
}

export default App
