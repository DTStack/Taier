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
                if(v.code==1){
                    message.success('置顶成功！');
                    this.getProjectListInfo();
                }
            })
        }else if(data.stickStatus == 1){//"取消置顶"
            Api.setSticky({appointProjectId: data.id,stickStatus: 0}).then(v=>{
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
        const deleteImg = <img className="tooltip-img" src="/public/rdos/img/delete.svg" />;
        const setTopImg = <img className="tooltip-img setTopImg" src="/public/rdos/img/cancel-top.svg" />;
        const cancelTop = <span className="cancel-top">取消置顶</span>;
        const tooltipTittle = <div>
            {
                    data.status == 2 || data.status == 3 ? "删除项目" : "置顶"
            }
        </div>
        const tooltipImg = <div onClick={()=>{this.setCard(data)}}>
           { 
               ( data.status != 2 || data.status != 3 )&&data.stickStatus == 1 ? //取消置顶非图标,不需要Tooltip提示,过滤掉
                    cancelTop : 
                    <Tooltip title={tooltipTittle} mouseEnterDelay={0.5}>
                        {
                            data.status == 2 || data.status == 3 ? deleteImg : setTopImg
                        }
                    </Tooltip>
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
                    {tooltipImg}
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

    handleMouseOver = (type,e) => { 
        if(type === "realtime"){
            e.currentTarget.getElementsByTagName('img')[0].src = "/public/rdos/img/icon/realtime3.svg"
        }else{
            e.currentTarget.getElementsByTagName('img')[0].src = "/public/rdos/img/icon/offline3.svg"
        }
    }

    handleMouseOut = (type,e) => { 
        if(type === "realtime"){
            e.currentTarget.getElementsByTagName('img')[0].src = "/public/rdos/img/icon/realtime2.svg"
        }else{
            e.currentTarget.getElementsByTagName('img')[0].src = "/public/rdos/img/icon/offline2.svg"
        }
    }


    render() {
        const { visible, projectListInfo, sortTitleStatus, totalSize, projectListParams, loading, offlineSrc, realtimeSrc } = this.state;
        return (
            <Spin tip="Loading..." spinning={loading}  delay={500} >
                <div className="project-dashboard develop-kit" style={{ padding: "20 35" }}>
                    <Row gutter={10}>
                        <Col span="10" >
                            <div className="project-search" >
                                <Search placeholder="按项目名称、项目显示名称搜索"  onSearch={value => this.searchProject(null,value)} onPressEnter={this.searchProject}/>
                            </div>
                            <Button 
                                style={{ float: "left" ,margin: "10 0 0 15" }}
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
                    <Row gutter={10}>
                        <Col span="24" >
                            <Row gutter={10} style={{margin: 0}}>   
                                {
                                    projectListInfo.map(v=>{
                                        return  <Col span="8" className="card-width" key={v.id} style={{padding: 0}}>
                                                    <Card  className="general-card" title={this.generalTitle(v)} noHovering bordered={false}>
                                                        <Row className="card-content" >
                                                            <Col span="16">
                                                                <div className="statistics" >已发布/总任务数： <span className="statistics-info">{`${v.taskCountMap.submitCount}/${v.taskCountMap.allCount}`}</span></div>
                                                                <div className="statistics" >表数量： <span className="statistics-info">{v.tableCount}</span></div>
                                                                <div className="statistics" >项目占用存储： <span className="statistics-info">{v.totalSize}</span></div>
                                                                <div className="statistics" >创建时间： <span className="statistics-info">{moment(v.gmtCreate).format("YYYY-MM-DD HH:mm:ss")}</span></div>
                                                            </Col>
                                                            <Col span="8">
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
                                                                            <Card className="card-task" 
                                                                                onClick={()=>{this.setRouter('offline',v)}} 
                                                                                onMouseOver={(e)=>{this.handleMouseOver('offline',e)}} 
                                                                                onMouseOut={(e)=>{this.handleMouseOut('offline',e)}}
                                                                                noHovering
                                                                            >
                                                                                <span className="img-container">
                                                                                    <img className="task-img" src="/public/rdos/img/icon/offline2.svg" />
                                                                                </span>
                                                                                离线任务开发
                                                                            </Card>
                                                                        </Col>
                                                                        <Col span="8">
                                                                            <Card className="card-task" 
                                                                                onClick={()=>{this.setRouter('realtime',v)}} 
                                                                                onMouseOver={(e)=>{this.handleMouseOver('realtime',e)}} 
                                                                                onMouseOut={(e)=>{this.handleMouseOut('realtime',e)}}
                                                                                noHovering
                                                                            >
                                                                                <span className="img-container">
                                                                                    <img className="task-img" src="/public/rdos/img/icon/realtime2.svg" />
                                                                                </span>
                                                                                实时任务开发
                                                                            </Card>
                                                                        </Col >
                                                                        <Col span="8">
                                                                            <Card className="card-task" style={{padding:"1.5 0"}} 
                                                                                onClick={()=>{this.setRouter('operation',v)}}
                                                                                noHovering
                                                                            >
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
