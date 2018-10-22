import React from "react"

import {
    Tabs, Button
} from 'antd'
import SlidePane from 'widgets/slidePane'
import { TaskStatus } from "../../../../components/status"
import AlarmMsg from "./tabs/alarmMsg"
import BaseInfo from "./tabs/baseInfo"
import CheckPoint from "./tabs/checkPoint"
import DataDelay from "./tabs/dataDelay"
import RunCode from "./tabs/runCode"
import HelpDoc from "../../../helpDoc"


import { TASK_TYPE } from "../../../../../stream/comm/const";



const TabPane = Tabs.TabPane;

class TaskDetailPane extends React.Component {
    state = {
        tabKey: 'taskFlow'
    }

    componentWillReceiveProps(nextProps) {
        const {data={}} = this.props;
        const {data:nextData={}} = nextProps;
        if (data.id != nextData.id) {
            this.setState({
                tabKey: "taskFlow"
            })
        }
    }
    onTabChange(activeKey) {
        this.setState({
            tabKey: activeKey
        })
    }
    getTabs() {
        const { tabKey } = this.state;
        const { data = {} } = this.props;
        const { taskType } = data;
        const scrollStyle = {
            position: "absolute",
            top: "36px",
            bottom: "50px",
            overflow: "auto",
            paddingBottom: "1px",
            paddingTop: "20px"
        }
        const scrollStyleNoPt = {
            position: "absolute",
            top: "36px",
            bottom: "50px",
            overflow: "auto",
            paddingBottom: "1px"
        }
        switch (taskType) {
            case TASK_TYPE.DATA_COLLECTION: {
                return [
                    <TabPane style={scrollStyleNoPt} tab="基本指标" key="taskFlow">
                        <BaseInfo isShow={tabKey == "taskFlow"} data={data} />
                    </TabPane>,
                    <TabPane style={scrollStyle} tab="运行代码" key="runCode">
                        <RunCode data={data} />
                    </TabPane>,
                    <TabPane style={scrollStyleNoPt} tab="告警信息" key="alarmMsg">
                        <AlarmMsg data={data} />
                    </TabPane>
                ]
            }
            case TASK_TYPE.SQL:
            case TASK_TYPE.MR: {
                return [
                    <TabPane style={scrollStyleNoPt} tab="基本指标" key="taskFlow">
                        <BaseInfo isShow={tabKey == "taskFlow"} data={data} />
                    </TabPane>,
                    <TabPane 
                    style={scrollStyleNoPt} 
                    tab={(
                        <span>数据延迟<HelpDoc style={{
                            position:"relative",
                            marginLeft:"5px",
                            right:"initial",
                            top:"initial",
                            marginRight:"0px"
                        }}  doc="delayTabWarning" /></span>
                    )
                    } 
                    key="dataDelay">
                        <DataDelay data={data} />
                    </TabPane>,
                    <TabPane style={scrollStyleNoPt} tab="checkpoint" key="checkpoint">
                        <CheckPoint data={data} />
                    </TabPane>,
                    <TabPane style={scrollStyle} tab="运行代码" key="runCode">
                        <RunCode data={data} />
                    </TabPane>,
                    <TabPane style={scrollStyleNoPt} tab="告警信息" key="alarmMsg">
                        <AlarmMsg data={data} />
                    </TabPane>
                ]
            }
            default: {
                return []
            }
        }
    }
    render() {
        const {
            visibleSlidePane, data = {}, extButton,
            closeSlidePane
        } = this.props;
        const { tabKey } = this.state;
        const extButtonStyle = {
            position: "absolute",
            right: "30px",
            top: "11px"
        }
        return (
            <div>
                <SlidePane
                    className="m-tabs bd-top bd-right m-slide-pane"
                    onClose={closeSlidePane}
                    visible={visibleSlidePane}
                    style={{ right: '0px', width: '75%', height: '100%', minHeight: '600px', }}
                >
                    <header className="detailPane-header">
                        <span style={{ fontSize: "14px" }}>{data.name}</span>
                        <span style={{ marginLeft: "25px" }}><TaskStatus value={data.status} /></span>
                        <span style={extButtonStyle}>{extButton}</span>
                    </header>
                    <Tabs
                        style={{ borderTop: "1px solid #DDDDDD", position: "relative" }}
                        animated={false}
                        onChange={this.onTabChange.bind(this)}
                        activeKey={tabKey}
                    >
                        {this.getTabs()}
                    </Tabs>
                </SlidePane>
            </div>

        )
    }
}

export default TaskDetailPane;