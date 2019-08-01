import * as React from 'react';
import { Tabs } from 'antd';
import { hashHistory } from 'react-router';
import { connect } from 'react-redux';

import utils from 'utils';
import '../../../styles/pages/package.scss'

import PackageCreate from './create';
import PackagePublish from './publish';
import { PROJECT_TYPE } from '../../../comm/const';

const TabPane = Tabs.TabPane;

@(connect((state: any) => {
    return {
        project: state.project
    }
}) as any)
class PackageContainer extends React.Component<any, any> {
    state={
    }
    /* eslint-disable */
    componentWillReceiveProps(nextProps: any) {
        const { project } = nextProps;
        const { project: old_project } = this.props;
        if (project.id != old_project.id) {
            if (project.projectType != PROJECT_TYPE.TEST) {
                hashHistory.push('/operation');
            }
        }
    }
    /* eslint-enable */
    onChange(key: any) {
        const { location } = this.props;
        hashHistory.push({ pathname: `/package/${key}`, query: location.query })
    }
    render () {
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
                    // onEdit={this.onEdit}
                    tabBarStyle={{ background: 'transparent', borderWidth: '0px' }}
                >
                    <TabPane className="m-panel2" tab="创建发布包" key="create">
                        <PackageCreate
                            changeTab={this.onChange.bind(this)}
                            mode={mode} />
                    </TabPane>
                    <TabPane className="m-panel2" tab="发布包" key="publish">
                        <PackagePublish
                            isShow={params.type == 'publish'}
                            mode={mode} />
                    </TabPane>
                </Tabs>
                {this.props.children}
            </div>
        )
    }
}

export default PackageContainer;
