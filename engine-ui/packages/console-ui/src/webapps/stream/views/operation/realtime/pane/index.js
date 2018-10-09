import React from "react"

import {
    Tabs,Button
} from 'antd'
import SlidePane from 'widgets/slidePane'
import {TaskStatus} from "../../../../components/status"
import AlarmMsg from "./tabs/alarmMsg"
import BaseInfo from "./tabs/BaseInfo"
import CheckPoint from "./tabs/checkPoint"
import DataDelay from "./tabs/dataDelay"
import RunCode from "./tabs/runCode"


import { TASK_TYPE } from "../../../../../stream/comm/const";



const TabPane = Tabs.TabPane;

class TaskDetailPane extends React.Component {
    getTabs() {
        const { data = {} } = this.props;
        const { taskType } = data;
        switch (taskType) {
            case TASK_TYPE.DATA_COLLECTION: {
                return [
                    <TabPane tab="基本指标" key="taskFlow">
                        <BaseInfo data={data}/>
                    </TabPane>,
                    <TabPane tab="运行代码" key="runCode">
                        <RunCode data={data}/>
                    </TabPane>,
                    <TabPane tab="告警信息" key="alarmMsg">
                        <AlarmMsg data={data}/>
                    </TabPane>
                ]
            }
            case TASK_TYPE.SQL:
            case TASK_TYPE.MR: {
                return [
                    <TabPane tab="基本指标" key="taskFlow">
                        <BaseInfo data={data}/>
                    </TabPane>,
                    <TabPane tab="数据延迟" key="dataDelay">
                        <DataDelay data={data}/>
                    </TabPane>,
                    <TabPane tab="checkpoint" key="checkpoint">
                        <CheckPoint data={data}/>
                    </TabPane>,
                    <TabPane tab="运行代码" key="runCode">
                        <RunCode data={data}/>
                    </TabPane>,
                    <TabPane tab="告警信息" key="alarmMsg">
                        <AlarmMsg data={data}/>
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
            visibleSlidePane,data={},extButton,
            closeSlidePane
        } = this.props;
        return (
            <SlidePane
                className="m-tabs bd-top bd-right m-slide-pane"
                onClose={closeSlidePane}
                visible={visibleSlidePane}
                style={{ right: '0px', width: '75%', height: '100%', minHeight: '600px', }}
            >   
                <header className="detailPane-header">
                    <span style={{fontSize:"14px"}}>{data.name}</span>
                    <span style={{marginLeft:"25px"}}><TaskStatus value={data.status} /></span>
                    <span style={{float:"right"}}>{extButton}</span>
                </header>
                <Tabs style={{borderTop:"1px solid #DDDDDD"}} animated={false} onChange={this.onTabChange}>
                    {this.getTabs()}
                </Tabs>
            </SlidePane>
        )
    }
}

export default TaskDetailPane;