import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Link, hashHistory } from "react-router";

import moment from 'moment'

import { Input, Card, Row, Col, Tooltip, Icon, Button, Pagination, message, Spin, Select } from "antd"

import ProjectForm from '../project/form'
import Api from '../../api'
import * as ProjectAction from "../../store/modules/project";
import NoData from '../../components/no-data';

const Search = Input.Search;
const Option = Select.Option;
class Index extends Component {

    state = {
        visible: false,
        loading: true,
        projectListInfo: [],
        sortTitleStatus: 1,
        totalSize: undefined,
        projectListParams: {
            fuzzyName: undefined,
            page: 1,
            pageSize: 9,
            orderBy: undefined,
        }
    }

    componentDidMount() {
        this.getProjectListInfo();
    }
    componentWillUnmount(){
        this._isUnmounted=true;
        clearTimeout(this._timeClock);
    }
    debounceGetProList(){
        if(this._isUnmounted){
            return ;
        }
       this._timeClock=setTimeout(() => {
            this.getProjectListInfo(null,true);
        }, 3000);
    }
    setCard = (data) => {
        if (data.status == 2 || data.status == 3) {//"删除项目" 
            Api.deleteProject({ projectId: data.id }).then(v => {
                if (v.code == 1) {
                    message.success('删除项目成功！');
                    this.getProjectListInfo();
                }
            })
        } else if (data.stickStatus == 0) {//"置顶"
            Api.setSticky({ appointProjectId: data.id, stickStatus: 1 }).then(v => {
                if (v.code == 1) {
                    message.success('置顶成功！');
                    this.getProjectListInfo();
                }
            })
        } else if (data.stickStatus == 1) {//"取消置顶"
            Api.setSticky({ appointProjectId: data.id, stickStatus: 0 }).then(v => {
                if (v.code == 1) {
                    message.success('取消置顶成功！');
                    this.getProjectListInfo();
                }
            })
        }
    }

    getProjectListInfo = (params,isSilent) => {
        const { projectListParams } = this.state;
        const queryParsms = { ...projectListParams, ...params };
        if(!isSilent){
            this.setState({
                loading: true,
            })
        }
        clearTimeout(this._timeClock);
        Api.getProjectListInfo(queryParsms).then((res) => {
            if (res.code === 1) {
                if(res.data&&res.data.data){
                    for(let project of res.data.data){
                        if(project.status==0){
                            this.debounceGetProList();
                            break;
                        }
                    }
                }
                this.setState({
                    projectListInfo: res.data && res.data.data || [],
                    totalSize: res.data && res.data.totalCount || 0,
                    projectListParams: queryParsms,
                    loading: false,
                })
            } else {
                this.setState({
                    loading: false
                })
            }
        })
    }

    // 选择测试或生产项目搜索
    changeProjectType(value) {
        
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
        const params = { page: page }
        this.getProjectListInfo(params)
    }

    setRouter = (type, v) => {
        let src;
        const { dispatch } = this.props;
        if (type === "operation") {
            src = "/operation"
        } else {
            src = "/offline/task"
        }
        dispatch(ProjectAction.getProject(v.id));
        hashHistory.push(src)
    }

    generalTitle = (data) => {
        const deleteImg = <img className="tooltip-img" src="/public/rdos/img/delete.svg" />;
        const setTopImg = <img className="tooltip-img setTopImg" src="/public/rdos/img/cancel-top.svg" />;
        // 生产项目图标
        const produceImg = <img className="produce-test-img" src="/public/rdos/img/icon/produce.svg" />;
        // 测试项目图标
        const developImg = <img className="produce-test-img" src="/public/rdos/img/icon/develop.svg" />;
        const cancelTop = <span className="cancel-top">取消置顶</span>;
        const tooltipTittle = <div>
            {
                data.status == 2 || data.status == 3 ? "删除项目" : "置顶"
            }
        </div>
        const tooltipImg = <div onClick={() => { this.setCard(data) }}>
            {
                (data.status != 2 || data.status != 3) && data.stickStatus == 1 ? //取消置顶非图标,不需要Tooltip提示,过滤掉
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
                <Col span="16" >
                    {data.status == 1 ? (
                        <Link to={`/offline/task?projectId=${data.id}`}>
                            <span className="company-name" onClick={() => { this.setRouter('operation', data) }}>
                                {data.projectAlias}&nbsp;&nbsp;
                        </span>
                        </Link>
                    ) : (<span className="company-name no-hover">
                        {data.projectAlias}&nbsp;&nbsp;
                </span>)}
                    {this.renderTitleText(data)}
                    {(data.projectType == 1) ? developImg : ((data.projectType == 2) ? produceImg : null)}
                    {/* {produceImg} */}
                </Col>
                <Col span="8">
                    {tooltipImg}
                </Col>
            </Row>
        </div>
        return title;
    }
    renderTitleText(data) {
        switch (data.status) {
            case 0: {
                return (
                    <span>
                        <Icon type="loading" style={{ fontSize: 14, color: "#2491F7", paddingLeft: 16 }} />
                        <span style={{ color: '#999', paddingLeft: "8px" }}>创建中</span>
                    </span>
                )
            }
            case 1: {
                return (
                    <span style={{ color: '#999' }}>
                        {`(${data.projectName})`}
                    </span>
                )
            }
            case 2:
            case 3: {
                return (
                    <span>
                        <Icon type="close-circle" style={{ fontSize: 14, color: "#f00", paddingLeft: 16 }} />
                        <span style={{ color: '#999', paddingLeft: "8px" }}>创建失败</span>
                    </span>
                )
            }
        }
    }
    changeSort = (v) => {
        const status = v === "defaultSort" ? 1 : 2;
        if (status != this.state.sortTitleStatus) {
            if (status === 1) {
                this.setState({
                    sortTitleStatus: status
                }, () => {
                    this.getProjectListInfo({ orderBy: undefined })
                })

            } else {
                this.setState({
                    sortTitleStatus: status
                }, () => {
                    this.getProjectListInfo({ orderBy: "jobSum" })
                })
            }
        }
    }

    searchProject = (v, value) => {
        if (v) {
            this.getProjectListInfo({ fuzzyName: v.target.value || undefined, page: 1 })
        } else {
            this.getProjectListInfo({ fuzzyName: value, page: 1 })
        }
    }

    handleMouseOver = (e) => {
            e.currentTarget.getElementsByTagName('img')[0].src = "/public/rdos/img/icon/offline3.svg"
    }

    handleMouseOut = (e) => {
            e.currentTarget.getElementsByTagName('img')[0].src = "/public/rdos/img/icon/offline2.svg"
    }


    render() {
        const { visible, projectListInfo, sortTitleStatus, totalSize, projectListParams, loading, offlineSrc, realtimeSrc } = this.state;
        return (
            <Spin tip="Loading..." spinning={loading} delay={500} >
                <div className="project-dashboard develop-kit" style={{ padding: "20 35" }}>
                    <Row gutter={10}>
                        <Col span="16" >
                            <Select
                                className="project-select"
                                allowClear={true}
                                placeholder="请选择项目"
                                onChange={this.changeProjectType.bind(this)}
                            >
                                <Option key="1">生产项目</Option>
                                <Option key="2">测试项目</Option>
                            </Select>
                            <div className="project-search" >
                                <Search placeholder="按项目名称、项目显示名称搜索" onSearch={value => this.searchProject(null, value)} onPressEnter={this.searchProject} />
                            </div>
                            <Button
                                style={{ float: "left", margin: "10 0 0 15" }}
                                type="primary"
                                onClick={() => { this.setState({ visible: true }) }}>
                                创建项目
                            </Button>
                        </Col>
                        <Col span="8" >
                            <div className="sortTitle">
                                <span className="faileSort" style={sortTitleStatus == 2 ? { color: "#2491F7" } : {}} onClick={() => { this.changeSort('faileSort') }}>按任务失败数排序</span>
                                <span className="faileSort">|</span>
                                <span className="defaultSort" style={sortTitleStatus == 1 ? { color: "#2491F7" } : {}} onClick={() => { this.changeSort('defaultSort') }}>默认排序</span>
                            </div>
                        </Col>
                    </Row>
                    <Row gutter={10}>
                        <Col span="24" >
                            <Row gutter={10} style={{ margin: 0 }}>
                                {
                                    projectListInfo && projectListInfo.length === 0 && !loading ? <NoData/> : ''
                                }
                                {
                                    projectListInfo && projectListInfo.map(v => {
                                        return <Col span="8" className="card-width" key={v.id} style={{ padding: 0 }}>
                                            <Card className="general-card" title={this.generalTitle(v)} noHovering bordered={false}>
                                                <Row className="card-content" >
                                                    <Col span="16">
                                                        <div className="statistics" >已发布/总任务数： <span className="statistics-info">{`${v.taskCountMap.submitCount}/${v.taskCountMap.allCount}`}</span></div>
                                                        <div className="statistics" >表数量： <span className="statistics-info">{v.tableCount}</span></div>
                                                        <div className="statistics" >项目占用存储： <span className="statistics-info">{v.totalSize}</span></div>
                                                        <div className="statistics" >创建时间： <span className="statistics-info">{moment(v.gmtCreate).format("YYYY-MM-DD HH:mm:ss")}</span></div>
                                                    </Col>
                                                    <Col span="8">
                                                        <div style={{ fontSize: 14 }}>今日任务失败数</div>
                                                        {v.status != 1 ? (
                                                            <div className="number no-hover">
                                                                {
                                                                    v.jobSum ? <span>{v.jobSum}</span> :
                                                                        <span style={{ color: "#999" }}>{v.jobSum || 0}</span>
                                                                }
                                                            </div>
                                                        ) : (
                                                                <div className="number" onClick={() => { this.setRouter('operation', v) }}>
                                                                    {
                                                                        v.jobSum ? <span>{v.jobSum}</span> :
                                                                            <span style={{ color: "#999" }}>{v.jobSum || 0}</span>
                                                                    }
                                                                </div>
                                                            )}
                                                    </Col>
                                                    <Col span="24" className="card-task-padding">
                                                        {
                                                            v.status != 1 ? "" : <Row >
                                                                <Col span="12">
                                                                    <Card className="card-task"
                                                                        onClick={() => { this.setRouter('offline', v) }}
                                                                        onMouseOver={(e) => { this.handleMouseOver(e) }}
                                                                        onMouseOut={(e) => { this.handleMouseOut(e) }}
                                                                        noHovering
                                                                    >
                                                                        <span className="img-container">
                                                                            <img className="task-img" src="/public/rdos/img/icon/offline2.svg" />
                                                                        </span>
                                                                        数据开发
                                                                            </Card>
                                                                </Col>
                                                                <Col span="12">
                                                                    <Card className="card-task" style={{ padding: "1.5 0" }}
                                                                        onClick={() => { this.setRouter('operation', v) }}
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
                                                    </div> : ""
                                                }
                                            </Card>
                                        </Col>
                                    })
                                }
                            </Row>
                            <Row>
                                <Col >
                                    <div style={{ float: "right" }}>
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
