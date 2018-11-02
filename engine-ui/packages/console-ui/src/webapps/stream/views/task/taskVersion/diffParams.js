import React from 'react';
import { connect } from 'react-redux';
import { Row, Col, Tabs, Tooltip} from 'antd';

const TabPane = Tabs.TabPane;

import DiffCodeEditor from 'widgets/editor/diff';

import ajax  from '../../../api/index';
import { TASK_TYPE } from '../../../comm/const';

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
                    <div className="schedulingCycle">
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
                            <Col span="18" className="txt-left">{taskInfo.schedulingCycle&&taskInfo.schedulingCycle.period}</Col>
                        </Row>
                        { 
                            taskInfo.schedulingCycle && taskInfo.schedulingCycle.beginTime ?
                                <Row>
                                    <Col span="6" className="txt-left">开始时间 : </Col>
                                    <Col span="18" className="txt-left">
                                        {taskInfo.schedulingCycle.beginTime}
                                    </Col>
                                </Row> : ""
                        }
                        { 
                            taskInfo.schedulingCycle && taskInfo.schedulingCycle.gapTime ?
                                <Row>
                                    <Col span="6" className="txt-left">间隔时间 : </Col>
                                    <Col span="18" className="txt-left">
                                        {taskInfo.schedulingCycle.gapTime}
                                    </Col>
                                </Row> : ""
                        }
                        { 
                            taskInfo.schedulingCycle && taskInfo.schedulingCycle.endTime ?
                                <Row>
                                    <Col span="6" className="txt-left">结束时间 : </Col>
                                    <Col span="18" className="txt-left">
                                        {taskInfo.schedulingCycle.endTime}
                                    </Col>
                                </Row> : ""
                        }
                        
                        { 
                            taskInfo.schedulingCycle && taskInfo.schedulingCycle.selectTime ?
                                <Row>
                                    <Col span="6" className="txt-left">选择时间 : </Col>
                                    <Col span="18" className="txt-left">
                                        {taskInfo.schedulingCycle.selectTime}
                                    </Col>
                                </Row> : ""
                        }
                        { 
                            taskInfo.schedulingCycle && taskInfo.schedulingCycle.specificTime ?
                                <Row>
                                    <Col span="6" className="txt-left">具体时间 : </Col>
                                    <Col span="18" className="txt-left">
                                        {taskInfo.schedulingCycle.specificTime}
                                    </Col>
                                </Row> : ""
                        }
                    </div>
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
            currentValue:this.props.taskType==="realTimeTask" ? this.props.currentRealTabData : this.props.currentTabData,
            currentParse:{},
            tabKey: this.props.taskType === "realTimeTask"?"params":"config",
        }
        this.versionId = this.props.diffParams&&this.props.diffParams.id;
    }

    componentWillReceiveProps(nextProps) {

        if(!this.props.taskType&&nextProps.diffParams.id!=this.props.diffParams.id){
            if(nextProps.diffParams.id){
                this.getData(nextProps.diffParams.id);
                this.setState({
                    currentValue:nextProps.currentTabData
                })
            }
        
            this.setState({
                tabKey:"config"
            }) 
        }else if(this.props.taskType === "realTimeTask"){
            this.getRealData()
            this.setState({
                currentValue:nextProps.currentRealTabData
            })
        }
    }

    getData = (id) => {
        this.setState({
            historyvalue: {}
        })
        ajax.taskVersionScheduleConf({versionId:id}).then(res => {
            if(res.code == 1){
                this.setState({
                    historyvalue: res.data||{}
                },this.contrastData)
            }else{
                this.setState({
                    historyvalue: {}
                },this.contrastData)
            }
        })
    }

    getRealData= () => {
        this.setState({
            historyvalue: this.props.currentRealTabData&&this.props.currentRealTabData.taskVersions[0]||{}
        },this.contrastData)
    }

    checkTime = (time) => {
        let modTime = "00";
        if(time){
            modTime = time.toString().length > 1 ? time : `0${time}`
        }
        return modTime;
    }

    dealWeekDay = (day) => {
        let weekDay;
        console.log('dealWeekDay',day);
        
        switch (day.toString()) {
            case "1":
                weekDay = "一"
                break;
            case "2":
                weekDay = "二"
                break;
            case "3":
                weekDay = "三"
                break;
            case "4":
                weekDay = "四"
                break;
            case "5":
                weekDay = "五"
                break;
            case "6":
                weekDay = "六"
                break;
            case "7":
                weekDay = "七"
                break;
            default:
                break;
        }
        return weekDay ? `星期${weekDay}` : " ";
    }

    parseScheduleConf = (data,type)=> {
        const parseScheduleConf = {};
        const scheduleConf = data.scheduleConf&&JSON.parse(data.scheduleConf) || {};

        if(data.scheduleStatus == 2){
            parseScheduleConf.scheduleStatus = "已冻结"
        }else if(data.scheduleStatus == 1){
            parseScheduleConf.scheduleStatus = "未冻结"
        }else{
            parseScheduleConf.scheduleStatus = ""
        }

        const effectiveDate = scheduleConf.beginDate&&scheduleConf.endDate ? `${scheduleConf.beginDate} ~ ${scheduleConf.endDate}` : "";
        parseScheduleConf.effectiveDate = effectiveDate;

        let schedulingCycle = {};
        switch (scheduleConf.periodType) {
            case "0":
                schedulingCycle.period = "分钟"
                schedulingCycle.gapTime = scheduleConf.gapMin ? `${scheduleConf.gapMin}分钟` : " ";
                schedulingCycle.beginTime =  `${this.checkTime(scheduleConf.beginHour)}:${this.checkTime(scheduleConf.beginMin)}`;
                schedulingCycle.endTime =  `${this.checkTime(scheduleConf.endHour)}:${this.checkTime(scheduleConf.endMin)}`;
                break;
            case "1":
                schedulingCycle.period = "小时";
                schedulingCycle.gapTime = scheduleConf.gapHour ? `${scheduleConf.gapHour}小时`:" ";
                schedulingCycle.beginTime =  `${this.checkTime(scheduleConf.beginHour)}:${this.checkTime(scheduleConf.beginMin)}`;
                schedulingCycle.endTime =  `${this.checkTime(scheduleConf.endHour)}:${this.checkTime(scheduleConf.endMin)}`;
                break;
            case "2":
                schedulingCycle.period = "天";
                schedulingCycle.specificTime =  `${this.checkTime(scheduleConf.hour)}:${this.checkTime(scheduleConf.min)}`;
                break;
            case "3":
                schedulingCycle.period = "周"
                let weekDay = scheduleConf.weekDay ? scheduleConf.weekDay.split(',').map(v=>{
                    return this.dealWeekDay(v);
                }): []
                schedulingCycle.selectTime = weekDay ? weekDay.join(',') : " ";
                schedulingCycle.specificTime =  `${this.checkTime(scheduleConf.hour)}:${this.checkTime(scheduleConf.min)}`;
                break;
            case "4":
                schedulingCycle.period = "月"
                schedulingCycle.selectTime = scheduleConf.day  ? `每月${scheduleConf.day}号`: " ";
                schedulingCycle.specificTime =  `${this.checkTime(scheduleConf.hour)}:${this.checkTime(scheduleConf.min)}`;
                break;
            default:
                schedulingCycle.period = ""
                break;
        }
        parseScheduleConf.schedulingCycle = schedulingCycle;

        if(type === 1){
            const upstreamTask = data.dependencyTaskNames&&data.dependencyTaskNames.join(" 、");
            parseScheduleConf.upstreamTask = upstreamTask;
        }else{
            const readWriteLockVO =  data.taskVOS || [];
            let upstreamTask = readWriteLockVO.map(v=>{
                return v.name
            })
            upstreamTask = upstreamTask.join(" 、")
            parseScheduleConf.upstreamTask = upstreamTask;
        }
       

        let crosscycleDependence;
        switch (scheduleConf.selfReliance) {
            case 0:
            case false:
                crosscycleDependence = "不依赖上一调度周期"
                break;
            case 1:
            case true:
                crosscycleDependence = "自依赖，等待上一调度周期成功，才能继续运行"
                break;
            case 3:
                crosscycleDependence = "自依赖，等待上一调度周期结束，才能继续运行"
                break;
            case 2:
                crosscycleDependence = "等待下游任务的上一周期成功，才能继续运行"
                break;
            case 4:
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
        const historyParse = this.parseScheduleConf(historyvalue,1);
        const currentParse = this.parseScheduleConf(this.state.currentValue,2);
        console.log('historyParse',historyParse);
        console.log('currentParse',currentParse);
        
        const currentAttributes = ["scheduleStatus","effectiveDate","schedulingCycle"];
        
        currentAttributes.forEach(v => {
            if(JSON.stringify(historyParse[v]) != JSON.stringify(currentParse[v])){
                
                contrastResults.attributes = true;
                return;
            }
        });
        
        if(Boolean(historyParse.upstreamTask) != Boolean(currentParse.upstreamTask)){
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
        if(this.props.taskType != "realTimeTask"){
            this.getData(this.versionId)
        }else{
            this.getRealData()
        }
    }

    callback = (key) => {
        this.setState({
            tabKey:key
        })
    }
    
    codeChange = (old, newVal) => {
        this.props.setTaskParams(newVal)
    }

    render() {
        const { taskType } = this.props;
        const { 
            contrastResults, historyParse, currentParse, 
            historyvalue, tabKey, 
        } = this.state;

        return <div className="m-taksdetail" style={{marginTop: '5px'}}>
            <Tabs onChange={this.callback} type="card" activeKey={tabKey}>
                {
                    taskType === "realTimeTask" ?  "" : 
                        <TabPane tab="调度配置" key="config">
                            <TaskInfo 
                                historyvalue={historyParse} 
                                contrastResults={contrastResults} 
                                currentValue={currentParse} 
                            />
                        </TabPane>
                }
                {
                    this.state.currentValue.taskType !== TASK_TYPE.SYNC && 
                    this.state.currentValue.taskType !== TASK_TYPE.WORKFLOW && 
                    <TabPane tab="环境参数" key="params">
                        <DiffCodeEditor
                            language="ini"
                            className="merge-text"
                            style={{height:"500px"}}
                            options={{readOnly:true}}
                            sync={true}
                            modified={{value:historyvalue&&historyvalue.taskParams||" "}}
                            original={{value:this.state.currentValue&&this.state.currentValue.taskParams||" "}}
                            value={this.state.currentValue&&this.state.currentValue.taskParams||" "}
                        /> 
                    </TabPane>
                }
            </Tabs>
        </div>
    }
}

const mapState = state => {
    const { currentPage } = state.realtimeTask;
    return {
        currentRealTabData: currentPage,
    };
};

export default connect(mapState)(DiffParams);
