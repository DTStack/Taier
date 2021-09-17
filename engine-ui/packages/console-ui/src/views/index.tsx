import * as React from 'react';

import Layout from './layout';
class Main extends React.Component<any, any> {
    render () {
        let { children } = this.props
        return (
            <div className="main">
                <Layout>
                    <div className="container overflow-hidden" id='JS_console_container'>
                        { children || '加载中....' }
                    </div>
                </Layout>
            </div>
        )
    }
}

export default Main
