import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Modal, Tabs } from 'antd';

import TagPane from './tagPane';
import { apiMarketActions } from '../../../actions/apiMarket';
import '../../../styles/views/tagConfig.scss';

const TabPane = Tabs.TabPane;

@connect()
class TagConfigIndex extends Component {
    componentDidMount () {
        this.props.dispatch(apiMarketActions.getCatalogue(0));
    }
    /* eslint-disable */
    showInfo = () => {
        Modal.info({
            title: '注册标签与新建标签的区别',
            maskClosable: true,
            width: '40%',
            content: (
                <div>
                    <p>注册标签：您已有计算好的标签结果，不需要配置标签计算逻辑，注册标签后即可提供标签服务</p>
                    <p>使用流程：点击“注册标签”->配置标签服务的信息->发布</p>
                    <p style={{ marginTop: 10 }}>新建标签：您只有原始数据，需要通过加工原始数据来形成标签</p>
                    <p>使用流程：点击“新建标签”->配置标签计算逻辑->配置标签服务信息->发布</p>
                </div>
            )
        });
    }
    /* eslint-enable */
    render () {
        return (
            <div className="box-1 m-card shadow m-tabs">
                <Tabs
                    animated={false}
                    defaultActiveKey={'1'}
                    // onChange={this.onTabChange}
                    style={{ height: 'auto' }}
                >
                    <TabPane tab="规则标签" key="1">
                        <TagPane tagType="rule" />
                    </TabPane>
                    <TabPane tab="注册标签" key="2">
                        <TagPane tagType="register" />
                    </TabPane>
                </Tabs>
                <div className="diff-tooltip">
                    <a onClick={this.showInfo}>注册与新建有什么区别？</a>
                </div>
            </div>
        )
    }
}
export default TagConfigIndex;
