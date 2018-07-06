import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Link,hashHistory } from "react-router";

import moment from 'moment'

import { Input, Card, Row, Col, Tooltip, Icon, Button, Pagination, message, Spin } from "antd"

import ProjectForm from '../project/form'
import Api from '../../api'
import * as ProjectAction from "../../store/modules/project";

class Index extends Component {

    state = {
        visible: false,
        loading: true,
        projectListInfo: [],
        sortTitleStatus: 1,
        totalSize: undefined,
        projectListParams:{
            fuzzyName: undefined,
            page: 1,
            pageSize: 9,
            orderBy: undefined,
        }
    }

    componentDidMount() {
        this.getProjectListInfo();
    }

    setCard = (data) => {
        if(data.projectStatus == 2||data.projectStatus == 3){//"删除项目" 
            message.success('删除项目成功！');
            this.getProjectListInfo();
        }else if(data.status == 0){//"置顶"
            Api.setSticky({appointProjectId: data.id,stick: 1}).then(v=>{
                console.log('置顶',v);
                if(v.code==1){
                    message.success('置顶成功！');
                    this.getProjectListInfo();
                }
            })
        }else if(data.status == 1){//"取消置顶"
            Api.setSticky({appointProjectId: data.id,stick: 0}).then(v=>{
                console.log('取消置顶',v);
                if(v.code==1){
                    message.success('取消置顶成功！');
                    this.getProjectListInfo();
                }
            })
        }
    }

    getProjectListInfo = (params) => {
        const { projectListParams } = this.state;
        const queryParsms = {...projectListParams,...params};
        this.setState({
            loading: true,
        })
        Api.getProjectListInfo(queryParsms).then((res) => {
            if (res.code === 1) {
                this.setState({
                    projectListInfo: res.data&&res.data.data||[],
                    totalSize: res.data&&res.data.totalCount||0,
                    projectListParams:queryParsms,
                    loading: false,
                })
            }else{
                this.setState({
                    loading: false
                })
            }
        })
    }
    
    createProject = (project) => {
        const { dispatch } = this.props;
        Api.createProject(project).then((res) => {
            if (res.code === 1) {
                this.setState({ visible: false });
                this.getProjectListInfo();
                dispatch(ProjectAction.getProjects());
                message.success('创建项目成功！');
            }
        })
    }

    changePage = (page) => {
        const params = {page:page}
        this.getProjectListInfo(params)
    }

    setRouter = (type,v) => {
        let src;
        const { dispatch } = this.props;
        if(type==="operation"){
            src = "/operation"
        }else if(type==="offline"){
            src = "/offline/task"
        }else{//realtime
            src = "/realtime"
        }
        dispatch(ProjectAction.getProject(v.id));
        hashHistory.push(src)
    }

    generalTitle = (data)=>{
        const tooltipTittle = <div className="tooltip-tittle-text" onClick={()=>{this.setCard(data)}}>
                {
                     data.projectStatus == 2||data.projectStatus == 3 ? "删除项目" : data.status == 0 ? "置顶" :  "取消置顶"
                }
            </div>
        const title = <div>
            <Row>
                <Col span="12" >
                    <Link to={`/offline/task?projectId=${data.id}`}>
                        <span className="company-name" onClick={()=>{this.setRouter('operation',data)}}>
                            {data.projectName}&nbsp;&nbsp;
                        </span>
                    </Link>
                    {
                       data.projectStatus == 2||data.projectStatus == 3 ? 
                        <span>
                            <Icon type="close-circle" style={{ fontSize: 14,color:"#f00",paddingLeft: 16 }}/>
                            <span style={{ color: '#999'}}>  创建失败</span>
                        </span>
                        :
                        <span style={{ color: '#999' }}>
                            { `(${data.projectAlias})` }
                        </span> 
                    }
                </Col>
                <Col span="8" className="tooltipSet">
                    <span className="fail-task" >
                        今日任务失败数
                    </span>
                    <Tooltip title={tooltipTittle} >
                        <Icon type="setting" style={{ fontSize: 14}} />
                    </Tooltip>
                </Col>
            </Row>
        </div>
        return title;
    }

    changeSort = (v) => {
        const status = v === "defaultSort" ? 1 : 2;
        if(status != this.state.sortTitleStatus){
            if(status === 1){
                this.setState({
                    sortTitleStatus: status
                },()=>{
                    this.getProjectListInfo({orderBy: undefined})
                })
                
            }else{
                this.setState({
                    sortTitleStatus: status
                },()=>{
                    this.getProjectListInfo({orderBy: "jobSum"})
                })
            }
        }
    }

    searchProject = (v) => {
        this.getProjectListInfo({fuzzyName:v.target.value||undefined})
    }

    render() {
        const { visible, projectListInfo, sortTitleStatus, totalSize, projectListParams, loading } = this.state;
        const mockData = [
            {id:142412,status:1,projectName:"wujing",projectAlias:"wujing_1",projectDesc:"",createUserId:"",tenantId:235252,jobSum:522,tableCount:4241,totalSize:"11241.g4Mb",taskCountMap:{allCount:2635252,submitCount:12511}},
            {id:142411,status:2,projectName:"bajie",projectAlias:"bajie_1",projectDesc:"",createUserId:"",tenantId:235252,jobSum:522,tableCount:4741,totalSize:"71241.g4Mb",taskCountMap:{allCount:735252,submitCount:12611}},
            {id:142413,status:0,projectName:"yuren",projectAlias:"yuren_1",projectDesc:"",createUserId:"",tenantId:235252,jobSum:522,tableCount:4841,totalSize:"61241.g4Mb",taskCountMap:{allCount:885252,submitCount:12311}},
            {id:142414,status:0,projectName:"feizi",projectAlias:"feizi_1",projectDesc:"",createUserId:"",tenantId:235252,jobSum:522,tableCount:3241,totalSize:"41241.g4Mb",taskCountMap:{allCount:8535252,submitCount:12711}},
            {id:142415,status:2,projectName:"dema",projectAlias:"dema_1",projectDesc:"",createUserId:"",tenantId:235252,jobSum:522,tableCount:7241,totalSize:"71241.g4Mb",taskCountMap:{allCount:635252,submitCount:12911}},
            {id:142416,status:1,projectName:"浙大图书馆",projectAlias:"浙大图书馆_1",projectDesc:"",createUserId:"",tenantId:235252,jobSum:112,tableCount:4241,totalSize:"81241.g4Mb",taskCountMap:{allCount:4235252,submitCount:12011}},
            {id:142417,status:0,projectName:"华夏银行",projectAlias:"华夏银行_1",projectDesc:"",createUserId:"",tenantId:235252,jobSum:322,tableCount:4241,totalSize:"1241.g4Mb",taskCountMap:{allCount:5235252,submitCount:12211}},
            {id:142418,status:2,projectName:"招商银行",projectAlias:"招商银行_1",projectDesc:"",createUserId:"",tenantId:235252,jobSum:122,tableCount:4241,totalSize:"19241.g4Mb",taskCountMap:{allCount:1235252,submitCount:12111}},
            {id:142419,status:2,projectName:"阿凡提",projectAlias:"afanty",projectDesc:"",createUserId:"",tenantId:2352,jobSum:521,tableCount:4241,totalSize:"41248.g4Mb",taskCountMap:{allCount:2735252,submitCount:62311}},
        ]
        return (
            <Spin tip="Loading..." spinning={loading}  delay={500} >
                <div className="project-dashboard" style={{ padding: 40 }}>
                    <Row gutter={40}>
                        <Col span="10" >
                            <h1 className="box-title" style={{ padding: 0}}>
                                我的项目: 
                                <div className="project-search" >
                                    <Input placeholder="按项目名称、项目显示名称搜索" size="large" onPressEnter={this.searchProject}/>
                                </div>
                            </h1>
                            
                        </Col>
                        <Col span="10" style={{ marginBottom: -10,paddingTop: 10 }}>
                            <div className="sortTitle">
                                <span  className="faileSort" style={sortTitleStatus == 2 ? {color:"#2491F7"}: {}} onClick={()=>{this.changeSort('faileSort')}}>按任务失败数排序</span>
                                <span  className="defaultSort" style={sortTitleStatus == 1 ? {color:"#2491F7"}: {}} onClick={()=>{this.changeSort('defaultSort')}}>默认顺序</span>
                            </div>
                        </Col>
                    </Row>
                    <Row gutter={40}>
                        <Col span="20" >
                            <Row gutter={24}>   
                                {
                                    //mockData projectListInfo
                                    projectListInfo.map(v=>{
                                        return  <Col span="8" className="card-width" key={v.id}>
                                                    <Card  className="general-card" title={this.generalTitle(v)}>
                                                        <Row className="card-content">
                                                            <Col span="14">
                                                                <div className="statistics" >已发布/总任务数： {`${v.taskCountMap.allCount}/${v.taskCountMap.submitCount}`}</div>
                                                                <div className="statistics" >表数量： {v.tableCount}</div>
                                                                <div className="statistics" >项目占用存储： {v.totalSize}</div>
                                                                <div className="statistics" >创建时间： {moment(v.gmtCreate).format("YYYY-MM-DD hh:mm:ss")}</div>
                                                            </Col>
                                                            <Col span="10">
                                                                <div className="number" onClick={()=>{this.setRouter('operation',v)}}>
                                                                    {
                                                                        v.jobSum ? <span>{v.jobSum}</span> :
                                                                            <span style={{color: "#999"}}>{v.jobSum||0}</span>
                                                                    }
                                                                </div>
                                                            
                                                                {
                                                                    v.status == 0 ? "" : <div>
                                                                        <Card className="card-task" onClick={()=>{this.setRouter('offline',v)}}>
                                                                            <span className="img-container">
                                                                                <img className="task-img" src="/public/rdos/img/icon/offline.png" />
                                                                            </span>
                                                                            离线任务开发
                                                                        </Card>
                                                                        <Card className="card-task" onClick={()=>{this.setRouter('realtime',v)}}>
                                                                            <span className="img-container">
                                                                                <img className="task-img" src="/public/rdos/img/icon/realtime.png" />
                                                                            </span>
                                                                            实时任务开发
                                                                        </Card>
                                                                        <Card className="card-task" onClick={()=>{this.setRouter('operation',v)}}>运维中心</Card>
                                                                    </div>
                                                                }
                                                            </Col>
                                                        
                                                        </Row>
                                                    {
                                                            v.status == 1 ? <div className="triangle_border_right">
                                                                    <span></span>
                                                                </div>:""
                                                        }
                                                    </Card>
                                                </Col>
                                    })
                                }
                            </Row>
                            <Row>
                            <Col span="12">
                                <Button 
                                    style={{ marginTop: 10 }}
                                    type="primary" 
                                    onClick={() => { this.setState({ visible: true }) }}>
                                    创建项目
                                </Button>
                            </Col>
                            <Col span="12">
                                <div style={{float: "right"}}>
                                    <Pagination 
                                        current={projectListParams.page} 
                                        total={totalSize} 
                                        onChange={this.changePage}
                                        pageSize={projectListParams.pageSize}
                                    />
                                </div>
                            </Col>
                        </Row>
                        </Col>
                        <Col span="4">
                            <Card  style={{ minHeight: 400 ,padding:"0 20" }}>
                                <h3 className="title-help">使用帮助</h3>
                                <p className="help-doc-rdos"><a>开发套件产品介绍</a></p>
                                <p className="help-doc-rdos"><a>支持对哪些数据源做同步</a></p>
                                <p className="help-doc-rdos"><a>如何配置数据源</a></p>
                                <p className="help-doc-rdos"><a>开发套件产品介绍</a></p>
                                <p className="help-doc-rdos"><a>开发套件产品介绍</a></p>
                                <p className="help-doc-rdos"><a>开发套件产品介绍</a></p>
                                <p className="help-doc-rdos"><a>开发套件产品介绍</a></p>
                                <p className="help-doc-rdos"><a>开发套件产品介绍</a></p>
                                <p className="help-doc-rdos"><a>开发套件产品介绍</a></p>
                                <p className="help-doc-rdos"><a>开发套件产品介绍</a></p>
                                <p className="help-doc-rdos"><a>开发套件产品介绍</a></p>
                                <p className="help-doc-rdos"><a>开发套件产品介绍</a></p>
                            </Card>
                        </Col>
                    </Row>
                    <ProjectForm
                        title="创建项目"
                        onOk={this.createProject}
                        visible={visible}
                        onCancel={() => this.setState({ visible: false })}
                    />
                </div>
            </Spin>
        )
    }
}
export default connect((state) => {
    return {
        user: state.user,
        projects: state.projects,
    }
})(Index)
