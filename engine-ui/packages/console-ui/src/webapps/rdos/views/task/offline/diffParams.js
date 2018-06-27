import React from 'react';
import { connect } from 'react-redux';
import { Row, Col, Tabs, Tooltip} from 'antd';

const TabPane = Tabs.TabPane;

import DiffCodeEditor from '../../../components/diff-code-editor';

import ajax  from '../../../api/index';

import { workbenchAction } from '../../../store/modules/offlineTask/actionType';

class TaskInfo extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            currentValue: this.props.currentValue || {},
            historyvalue: this.props.historyvalue || {},
            contrastResults: this.props.contrastResults || {},
        }
    }
    
    componentWillReceiveProps(nextProps) {
        if(nextProps != this.props){
            this.getvalue(nextProps)
        }
    }

    getvalue = (nextProps) => {
        this.setState({
            currentValue: nextProps.currentValue || {},
            historyvalue: nextProps.historyvalue || {},
            contrastResults: nextProps.contrastResults || {},
        }) 
    }

     versionInfo = (version,taskInfo,contrast={}) => {
         const { attributes, upstreamTask, crosscycleDependence } = contrast;
         const contrastStyle = {color:"#f00"};
        return (
            <Col span={12}>
                <div className="title">{version}</div>
                <div className="box-padding">
                    <div className="sub-title" style={attributes ? contrastStyle : {}}>调度属性</div>
                    <div className="line"></div>
                    <Row>
                        <Col span="6" className="txt-left">调度状态 : </Col>
                        <Col span="18" className="txt-left">
                            {taskInfo.scheduleStatus}
                        </Col>
                    </Row>
                    <Row>
                        <Col span="6" className="txt-left">生效日期 : </Col>
                        <Col span="18" className="txt-left">
                        {taskInfo.effectiveDate}
                        </Col>
                    </Row>
                    <Row>
                        <Col span="6" className="txt-left">调度周期 : </Col>
                        <Col span="18" className="txt-left">{taskInfo.schedulingCycle}</Col>
                    </Row>
                    <Row>
                        <Col span="6" className="txt-left">具体时间 : </Col>
                        <Col span="18" className="txt-left">
                            {taskInfo.specificTime}
                        </Col>
                    </Row>
                    <div className="sub-title" style={upstreamTask ? contrastStyle : {}}>任务间依赖</div>
                    <div className="line"></div>
                    <Row>
                        <Col span="24" className="txt-left">上游任务 : </Col>
                        <Tooltip title={taskInfo.upstreamTask} overlayStyle={{fontSize:"14px"}}>
                            <Col span="20" className="word-break">
                                {taskInfo.upstreamTask}
                            </Col>
                        </Tooltip>
                    </Row>
                    <div className="sub-title" style={crosscycleDependence ? contrastStyle :{}}>跨周期依赖</div>
                    <div className="line"></div>
                    <Row>
                        <Tooltip title={taskInfo.crosscycleDependence} overlayStyle={{fontSize:"14px"}}>
                            <Col span="20" className="word-break">
                                {taskInfo.crosscycleDependence}
                            </Col>
                        </Tooltip>
                    </Row>
                </div>
            </Col>
        )
    }    
    render(){
        const { currentValue, historyvalue, contrastResults } = this.state;
        return (
            <Row  gutter={16} className="diff-params" style={{padding:"0 10px"}}>
               {this.versionInfo('当前版本',currentValue,contrastResults)}
               {this.versionInfo('历史版本',historyvalue)}
            </Row>
        )
    }
    
}

class DiffParams extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            historyvalue:{},
            historyParse:{},
            contrastResults:{
                attributes: false,
                upstreamTask: false,
                crosscycleDependence: false,
            },
            currentParse:{},
            tabKey:"config",
            tableRefresh: Math.random(),
        }
        this.currentValue = this.props.currentTabData;
        this.versionId = this.props.diffParams&&this.props.diffParams.id;
    }

    componentWillReceiveProps(nextProps) {

        if(nextProps.diffParams.id!=this.props.diffParams.id){
            if(nextProps.diffParams.id){
                this.getData(nextProps.diffParams.id);
                this.currentValue = nextProps.currentTabData;
            }
            this.setState({
                tabKey:"config"
            })
        }
    }

    getData = (id) => {
        ajax.taskVersionScheduleConf({versionId:id}).then(res => {
            if(res.code == 1){
                this.setState({
                    historyvalue: res.data
                },this.contrastData)
            }else{
                this.setState({
                    historyvalue: {}
                },this.contrastData)
            }
        })
    }

    parseScheduleConf = (data)=> {
        const parseScheduleConf = {};
        const scheduleConf = data.scheduleConf&&JSON.parse(data.scheduleConf) || {};

        if(data.scheduleStatus == 1){
            parseScheduleConf.scheduleStatus = "已冻结"
        }else{
            parseScheduleConf.scheduleStatus = "未冻结"
        }

        const effectiveDate = `${scheduleConf.beginDate} ~ ${scheduleConf.endDate}`;
        parseScheduleConf.effectiveDate = effectiveDate;

        let schedulingCycle;
        switch (scheduleConf.periodType) {
            case "0":
                schedulingCycle = "分钟"
                break;
            case "1":
                schedulingCycle = "小时"
                break;
            case "2":
                schedulingCycle = "天"
                break;
            case "3":
                schedulingCycle = "周"
                break;
            case "4":
                schedulingCycle = "月"
                break;
            default:
                schedulingCycle = ""
                break;
        }
        parseScheduleConf.schedulingCycle = schedulingCycle;

        const specificTime = `${scheduleConf.hour}:${scheduleConf.hour}`;
        parseScheduleConf.specificTime = specificTime;

        
        const readWriteLockVO =  data.taskVOS || [];
        let upstreamTask = readWriteLockVO.map(v=>{
            return v.name
        })
        upstreamTask = upstreamTask.join(" 、")
        parseScheduleConf.upstreamTask = upstreamTask;

        let crosscycleDependence;
        switch (scheduleConf.selfReliance) {
            case "0":
            case "false":
                crosscycleDependence = "不依赖上一调度周期"
                break;
            case "1":
            case "true":
                crosscycleDependence = "自依赖，等待上一调度周期成功，才能继续运行"
                break;
            case "3":
                crosscycleDependence = "自依赖，等待上一调度周期结束，才能继续运行"
                break;
            case "2":
                crosscycleDependence = "等待下游任务的上一周期成功，才能继续运行"
                break;
            case "4":
                crosscycleDependence = "等待下游任务的上一周期结束，才能继续运行"
                break;
            default:
                crosscycleDependence = "不依赖上一调度周期"
                break;
        }
        parseScheduleConf.crosscycleDependence = crosscycleDependence;
        return parseScheduleConf;
    }

    contrastData = ()=>{
        const { historyvalue, contrastResults } = this.state;
        contrastResults.attributes = false;
        contrastResults.upstreamTask = false;
        contrastResults.crosscycleDependence = false;
        const historyParse = this.parseScheduleConf(historyvalue);
        const currentParse = this.parseScheduleConf(this.currentValue);
        const currentAttributes = ["scheduleStatus","effectiveDate","schedulingCycle","specificTime"];
        currentAttributes.forEach(v => {
            if(historyParse[v] != currentParse[v]){
                contrastResults.attributes = true;
                return;
            }
        });
        
        if(historyParse.upstreamTask != currentParse.upstreamTask){
            contrastResults.upstreamTask = true;
        };

        if(historyParse.crosscycleDependence != historyParse.crosscycleDependence){
            contrastResults.crosscycleDependence = true;
        };

        this.setState({
            currentParse,
            historyParse,
            contrastResults
        });
    }

    componentDidMount = () => {
        this.getData(this.versionId)
    }

    callback = (key) => {
        this.setState({
            tabKey:key,
            tableRefresh: Math.random()
        })
    }
    
    codeChange = (old, newVal) => {
        this.props.setTaskParams(newVal)
    }

    render() {
        console.log('----this.props:',this.props);
        
        const { contrastResults,historyParse,currentParse,historyvalue,tabKey,tableRefresh } = this.state;
        // const isLocked = this.currentValue.readWriteLockVO && !this.currentValue.readWriteLockVO.getLock
       
        return <div className="m-taksdetail">
            <Tabs onChange={this.callback} type="card"  activeKey={tabKey} >
                <TabPane tab="调度配置" key="config">
                    <TaskInfo historyvalue={historyParse} contrastResults={contrastResults} currentValue={currentParse} />
                </TabPane>
                <TabPane tab="环境参数" key="params">
                    <DiffCodeEditor
                        readOnly={true}
                        // readOnly={isLocked}
                        compareTo={historyvalue.taskParams||" "} 
                        value={this.currentValue.taskParams||" "}
                        tableRefresh={tableRefresh}
                        // onChange={this.codeChange}
                    /> 
                </TabPane>

            </Tabs>
        </div>
    }
}

const mapState = state => {
    const { currentTab, tabs } = state.offlineTask.workbench;

    const currentTabData = tabs.filter(tab => {
        return tab.id === currentTab;
    })[0];

    return {
        currentTabData,
    };
};

export default connect(mapState)(DiffParams);
