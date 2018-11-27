import React from 'react';
import { Card, Tabs } from 'antd';
import { hashHistory } from 'react-router';
import { connect } from 'react-redux';

import utils from 'utils';
import '../../../styles/pages/package.scss'

import PackageCreate from './create';
import PackagePublish from './publish';
import { PROJECT_TYPE } from '../../../comm/const';

const TabPane = Tabs.TabPane;

@connect(state => {
    return {
        project: state.project
    }
})
class PackageContainer extends React.Component {
    state={
        createKey: 1024,
        publishKey: 2048
    }

    componentWillReceiveProps (nextProps) {
        const { project } = nextProps;
        const { project: old_project } = this.props;
        if (project.id != old_project.id) {
            if (project.projectType != PROJECT_TYPE.TEST) {
                hashHistory.push('/operation');
            } else {
                this.setState({
                    createKey: ~~(Math.random() * 100000),
                    publishKey: ~~(Math.random() * 100000)
                })
            }
        }
    }

    onChange (key) {
        const { location } = this.props;
        if (key == 'create') {
            this.setState({
                createKey: ~~(Math.random() * 100000)
            })
        } else {
            this.setState({
                publishKey: ~~(Math.random() * 100000)
            })
        }
        hashHistory.push({ pathname: `/package/${key}`, query: location.query })
    }
    render () {
        const { createKey, publishKey } = this.state;
        const { params } = this.props;
        const mode = utils.getParameterByName('type')
        const title = '任务发布'
        return (
            <div
                className="m-tabs box-pd-h"
            >
                <h1 style={{ marginBottom: '20px' }} className="box-title-bolder">{title}</h1>
                <Tabs
                    style={{ height: 'calc(100% - 44px)' }}
                    className="nav-border"
                    animated={false}
                    onChange={this.onChange.bind(this)}
                    activeKey={params.type}
                    onEdit={this.onEdit}
                    tabBarStyle={{ background: 'transparent', borderWidth: '0px' }}
                >
                    <TabPane className="m-panel2" tab="创建发布包" key="create">
                        <PackageCreate
                            changeTab={this.onChange.bind(this)}
                            // key={createKey}
                            mode={mode} />
                    </TabPane>
                    <TabPane className="m-panel2" tab="发布包" key="publish">
                        <PackagePublish
                        // key={publishKey}
                            activeKey={params.type}
                            mode={mode} />
                    </TabPane>
                </Tabs>
                {this.props.children}
            </div>
        )
    }
}

export default PackageContainer;
