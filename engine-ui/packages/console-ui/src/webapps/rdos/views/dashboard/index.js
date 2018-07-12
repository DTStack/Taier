import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Link,hashHistory } from "react-router";

import moment from 'moment'

import { Input, Card, Row, Col, Tooltip, Icon, Button, Pagination, message, Spin } from "antd"

import ProjectForm from '../project/form'
import Api from '../../api'
import * as ProjectAction from "../../store/modules/project";

const Search = Input.Search;

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
        if(data.status == 2||data.status == 3){//"删除项目" 
            Api.deleteProject({projectId: data.id}).then(v=>{
                if(v.code==1){
                    message.success('删除项目成功！');
                    this.getProjectListInfo();
                }
            })
        }else if(data.stickStatus == 0){//"置顶"
            Api.setSticky({appointProjectId: data.id,stickStatus: 1}).then(v=>{
                console.log('置顶',v);
                if(v.code==1){
                    message.success('置顶成功！');
                    this.getProjectListInfo();
                }
            })
        }else if(data.stickStatus == 1){//"取消置顶"
            Api.setSticky({appointProjectId: data.id,stickStatus: 0}).then(v=>{
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
                     data.status == 2||data.status == 3 ? "删除项目" : data.stickStatus == 1 ? "取消置顶" :  "置顶"
                }
            </div>
        const title = <div>
            <Row>
                <Col span="20" >
                    <Link to={`/offline/task?projectId=${data.id}`}>
                        <span className="company-name" onClick={()=>{this.setRouter('operation',data)}}>
                            {data.projectAlias}&nbsp;&nbsp;
                        </span>
                    </Link>
                    {
                       data.status == 2||data.status == 3 ? 
                        <span>
                            <Icon type="close-circle" style={{ fontSize: 14,color:"#f00",paddingLeft: 16 }}/>
                            <span style={{ color: '#999'}}>  创建失败</span>
                        </span>
                        :
                        <span style={{ color: '#999' }}>
                            { `(${data.projectName})` }
                        </span> 
                    }
                </Col>
                <Col span="4">
                    <Tooltip title={tooltipTittle} placement="bottomRight" overlayClassName="tooltip">
                        <Icon type="setting" style={{ fontSize: 16,marginTop: "16px" ,float: "right"}} />
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

    searchProject = (v,value) => {
        if(v){
            this.getProjectListInfo({fuzzyName:v.target.value||undefined,page: 1})
        }else{
            this.getProjectListInfo({fuzzyName:value,page: 1})
        }
    }


    render() {
        const { visible, projectListInfo, sortTitleStatus, totalSize, projectListParams, loading } = this.state;
        return (
            <Spin tip="Loading..." spinning={loading}  delay={500} >
                <div className="project-dashboard" style={{ padding: "20 40" }}>
                    <Row gutter={10}>
                        <Col span="10" >
                            <div className="project-search" >
                                <Search placeholder="按项目名称、项目显示名称搜索"  onSearch={value => this.searchProject(null,value)} onPressEnter={this.searchProject}/>
                            </div>
                            <Button 
                                style={{ float: "left" ,margin: "10 0 0 20" }}
                                type="primary" 
                                onClick={() => { this.setState({ visible: true }) }}>
                                创建项目
                            </Button>
                        </Col>
                        <Col span="14" >
                            <div className="sortTitle">
                                <span  className="faileSort" style={sortTitleStatus == 2 ? {color:"#2491F7"}: {}} onClick={()=>{this.changeSort('faileSort')}}>按任务失败数排序</span>
                                 <span className="faileSort">|</span>
                                <span  className="defaultSort" style={sortTitleStatus == 1 ? {color:"#2491F7"}: {}} onClick={()=>{this.changeSort('defaultSort')}}>默认排序</span>
                            </div>
                        </Col>
                    </Row>
                    <Row gutter={40}>
                        <Col span="24" >
                            <Row gutter={24}>   
                                {
                                    projectListInfo.map(v=>{
                                        return  <Col span="8" className="card-width" key={v.id}>
                                                    <Card  className="general-card" title={this.generalTitle(v)}>
                                                        <Row className="card-content">
                                                            <Col span="18">
                                                                <div className="statistics" >已发布/总任务数： {`${v.taskCountMap.submitCount}/${v.taskCountMap.allCount}`}</div>
                                                                <div className="statistics" >表数量： {v.tableCount}</div>
                                                                <div className="statistics" >项目占用存储： {v.totalSize}</div>
                                                                <div className="statistics" >创建时间： {moment(v.gmtCreate).format("YYYY-MM-DD HH:mm:ss")}</div>
                                                            </Col>
                                                            <Col span="6">
                                                                <div style={{fontSize:14}}>今日任务失败数</div>
                                                                <div className="number" onClick={()=>{this.setRouter('operation',v)}}>
                                                                    {
                                                                        v.jobSum ? <span>{v.jobSum}</span> :
                                                                            <span style={{color: "#999"}}>{v.jobSum||0}</span>
                                                                    }
                                                                </div>
                        
                                                            </Col>
                                                            <Col span="24" className="card-task-padding">
                                                                {
                                                                    v.status == 2 || v.status == 3 ? "" : <Row >
                                                                        <Col span="8">
                                                                            <Card className="card-task" onClick={()=>{this.setRouter('offline',v)}}>
                                                                                <span className="img-container">
                                                                                    <img className="task-img" src="/public/rdos/img/icon/offline.png" />
                                                                                </span>
                                                                                离线任务开发
                                                                            </Card>
                                                                        </Col>
                                                                        <Col span="8">
                                                                            <Card className="card-task" onClick={()=>{this.setRouter('realtime',v)}}>
                                                                                <span className="img-container">
                                                                                    <img className="task-img" src="/public/rdos/img/icon/realtime.png" />
                                                                                </span>
                                                                                实时任务开发
                                                                            </Card>
                                                                        </Col >
                                                                        <Col span="8">
                                                                            <Card className="card-task" onClick={()=>{this.setRouter('operation',v)}}>
                                                                                <span className="img-container">
                                                                                        <span className="task-img" />
                                                                                    </span>
                                                                                运维中心
                                                                            </Card>
                                                                        </Col>
                                                                    </Row>
                                                                }
                                                            </Col>
                                                        </Row>
                                                        {
                                                            v.stickStatus == 1 ? <div className="triangle_border_right">
                                                                    <span></span>
                                                                </div>:""
                                                        }
                                                    </Card>
                                                </Col>
                                    })
                                }
                            </Row>
                            <Row>
                                <Col >
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
