import React, { Component } from 'react';
import { Tabs, Button } from 'antd';

// import { ENGINE_TYPE } from '../../../comm/const';

const TabPane = Tabs.TabPane;

class EngineConfig extends Component {
    state = {
        data: []
    }

    componentDidMount () {
        this.fetchData();
    }

    fetchData = () => {
    }

    renderTabPanes = () => {
        const { data } = this.state;
        const tabPanes = data.map(paneItem => {
            return (
                <TabPane tab={paneItem.name} key={paneItem.id}>
                </TabPane>
            )
        })
        return tabPanes;
    }
    render () {
        return (
            <div>
                <h1 className="box-title">
                    计算引擎配置
                    <Button
                        type="primary"
                        style={{ marginTop: 10 }}
                        onClick={this.initAddMember}
                    >
                            添加成员
                    </Button>
                </h1>
                <div className="box-2 m-tabs m-card">
                    <Tabs
                        animated={false}
                        style={{ height: 'auto', minHeight: 'calc(100% - 40px)' }}
                    >
                        { this.renderTabPanes() }
                    </Tabs>
                </div>
            </div>
        )
    }
}

export default EngineConfig
