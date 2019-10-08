import * as React from 'react'
// import { get } from 'lodash';

import {
    Tabs
} from 'antd'
import SlidePane from 'widgets/slidePane'
import { TaskStatus } from '../../../../components/status'
import AlarmMsg from './tabs/alarmMsg'
import RunLog from './tabs/runLog'
import Failover from './tabs/failover';
import CheckPoint from './tabs/checkPoint'
import DataDelay from './tabs/dataDelay'
import RunCode from './tabs/runCode'
import HelpDoc from '../../../helpDoc'

import { TASK_TYPE, TASK_STATUS } from '../../../../../stream/comm/const';
import TaskGraph from './tabs/taskGraph';

const TabPane = Tabs.TabPane;

class TaskDetailPane extends React.Component<any, any> {
    constructor(props: any) {
        super(props);
        this.state = {
            tabKey: this.getInitTabKey(props.data)
        }
    }

    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps (nextProps: any) {
        const { data = {} } = this.props;
        const { data: nextData = {} } = nextProps;
        if (data.id != nextData.id) {
            this.setState({
                tabKey: this.getInitTabKey(nextData)
            });
        }
    }
    getInitTabKey (data = {}) {
        return 'taskGraph';
        // if (this.showGraph(get(data, 'status'))) {
        //     return 'taskGraph';
        // } else {
        //     return 'runLog';
        // }
    }
    showGraph (status: any) {
        return status == TASK_STATUS.RUNNING || status == TASK_STATUS.WAIT_RUN;
    }
    isFail (status: any) {
        return status == TASK_STATUS.RUN_FAILED || status == TASK_STATUS.SUBMIT_FAILED;
    }
    onTabChange (activeKey: any) {
        this.setState({
            tabKey: activeKey
        })
    }
    getTabs () {
        const { tabKey } = this.state;
        const { data = {} } = this.props;
        const { taskType, status } = data;
        const isFail = this.isFail(status);
        let tabs: any = [];
        const scrollStyle: any = {
            position: 'absolute',
            top: '36px',
            bottom: '1px',
            overflow: 'auto',
            paddingBottom: '1px',
            paddingTop: '20px'
        }
        const scrollStyleNoPt: any = {
            position: 'absolute',
            top: '36px',
            bottom: '1px',
            overflow: 'auto',
            paddingBottom: '1px'
        }
        const runCodeView = (
            <TabPane style={scrollStyle} tab="属性参数" key="runCode">
                <RunCode isShow={tabKey == 'runCode'} data={data} />
            </TabPane>
        )
        const runInfoView = (
            <TabPane style={scrollStyleNoPt} tab={'运行日志'} key="runLog">
                <RunLog key={data.id + '~' + data.status} isShow={tabKey == 'runLog'} data={data} />
            </TabPane>
        )
        const alarmMsgView = (
            <TabPane style={scrollStyleNoPt} tab="告警" key="alarmMsg">
                <AlarmMsg data={data} />
            </TabPane>
        )
        const taskGraph = (
            <TabPane style={scrollStyleNoPt} tab="数据曲线" key="taskGraph">
                <TaskGraph isShow={tabKey == 'taskGraph'} data={data} />
            </TabPane>
        )
        const checkpointView = (
            <TabPane style={scrollStyleNoPt} tab="checkpoint" key="checkpoint">
                <CheckPoint data={data} />
            </TabPane>)
        const failover = (
            isFail && <TabPane style={scrollStyleNoPt} tab="failover" key="failover">
                <Failover key={data.id} isShow={tabKey == 'failover'} data={data} />
            </TabPane>
        )
        switch (taskType) {
            case TASK_TYPE.DATA_COLLECTION: {
                tabs = [
                    runInfoView,
                    checkpointView,
                    runCodeView,
                    failover,
                    alarmMsgView
                ]
                break;
            }
            case TASK_TYPE.SQL:
            case TASK_TYPE.MR: {
                tabs = [
                    runInfoView,
                    <TabPane
                        style={scrollStyleNoPt}
                        tab={(
                            <span>数据延迟<HelpDoc style={{
                                position: 'relative',
                                marginLeft: '5px',
                                right: 'initial',
                                top: 'initial',
                                marginRight: '0px'
                            }} doc="delayTabWarning" /></span>
                        )
                        }
                        key="dataDelay">
                        <DataDelay data={data} />
                    </TabPane>,
                    checkpointView,
                    failover,
                    runCodeView,
                    alarmMsgView
                ];
                break;
            }
            default: {
                tabs = [];
            }
        }
        tabs.unshift(taskGraph);
        return tabs.filter(Boolean);
    }
    render () {
        const {
            visibleSlidePane, data = {}, extButton,
            closeSlidePane
        } = this.props;
        const { tabKey } = this.state;
        const extButtonStyle: any = {
            position: 'absolute',
            right: '30px',
            top: '11px'
        }
        return (
            <SlidePane
                className="m-tabs bd-top bd-right m-slide-pane"
                onClose={closeSlidePane}
                visible={visibleSlidePane}
                style={{ right: '0px', width: '75%', height: '100%', minHeight: '600px' }}
            >
                <div className="pane-height100-box">
                    <header className="detailPane-header">
                        <span style={{ fontSize: '14px' }}>{data.name}</span>
                        <span style={{ marginLeft: '25px' }}><TaskStatus value={data.status} /></span>
                        <span style={extButtonStyle}>{extButton}</span>
                    </header>
                    <Tabs
                        className="pane-tabs"
                        style={{ borderTop: '1px solid #DDDDDD', position: 'relative' }}
                        animated={false}
                        onChange={this.onTabChange.bind(this)}
                        activeKey={tabKey}
                    >
                        {this.getTabs()}
                    </Tabs>
                </div>
            </SlidePane>
        )
    }
}

export default TaskDetailPane;
