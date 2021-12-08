/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from 'react'
import PropTypes from 'prop-types'
import { Layout } from 'antd'
import Sidebar from './sidebar'
import { connect } from 'react-redux'

import { getPersonList } from '../../actions/operation'

const { Sider, Content } = Layout;
const propType: any = {
    children: PropTypes.node
}
const defaultPro: any = {
    children: []
}

const mapStateToProps = (state: any) => {
    const { common } = state;
    return { common }
};
@(connect(
    mapStateToProps
) as any)
class Container extends React.Component<any, any> {
    state: any = {
        collapsed: false
    };
    static propTypes = propType
    static defaultProps = defaultPro

    componentDidMount () {
        const { dispatch } = this.props;
        dispatch(getPersonList());
    }

    toggleCollapsed = (data) => {
        this.setState({
            collapsed: data
        });
    }

    render () {
        const { children } = this.props
        const { collapsed } = this.state

        return (
            <div className="main">
                <div className="container overflow-x-hidden" id='JS_console_container'>
                    <Layout className="dt-operation">
                        <Sider className="bg-w ant-slider-pos"
                            collapsed={collapsed}
                        >
                            <Sidebar {...this.props} changeCollapsed={this.toggleCollapsed} />
                        </Sider>
                        <Content>
                            { children || "i'm container." }
                        </Content>
                    </Layout>
                </div>
            </div>
        )
    }
}

export default Container
