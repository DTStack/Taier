import React, { Component } from 'react'
import { connect } from 'react-redux'
import moment from 'moment'

import { Input, Card, Row, Col, Tooltip, Icon, Button, Pagination } from "antd"

import { setProject } from '../../store/modules/project'
import ProjectForm from '../project/form'
import Api from '../../api'


class Index extends Component {

    state = {
        visible: false,
    }

    pageSize = 10

    componentDidMount() {
        this.props.dispatch(setProject({ id: 0 }))
    }

    setCard = (value) => {
        console.log(111);
    }

    queryProjectList = (params) => {
        const ctx = this
        this.setState({ loading: 'loading' })
        const reqParams = Object.assign({
            projectName: '',
            isAdmin: false,
            currentPage: 1,
            pageSize: 10,
        }, params)
        Api.queryProjects(reqParams).then((res) => {
            if (res.code === 1) {
                ctx.setState({ projectList: res.data, loading: 'success' })
            }
        })
    }

    
    createProject = (project) => {
        const ctx = this
        const { dispatch } = this.props
        Api.createProject(project).then((res) => {
            if (res.code === 1) {
                ctx.setState({ visible: false })
                ctx.queryProjectList()
                dispatch(ProjectAction.getProjects())
                message.success('创建项目成功！')
            }
        })
    }

    changePage = (page,pageSize)=>{
        console.log(page,pageSize);
    }

    render() {
        const { visible } = this.state;
        const tooltipTittle = <div className="tooltip-tittle-text" onClick={this.setCard}>置顶</div>
        const title = <div>
            <Row>
                <Col span="12" >
                    <span className="company-name">
                        公司测试项目&nbsp;&nbsp;
                    </span>
                    <span style={{ color: '#999' }}>
                        (company_test)
                    </span>
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
 
        return (
            <div className="project-dashboard" style={{ padding: 40 }}>
                <h1 className="box-title" style={{ padding: 0}}>
                    我的项目: 
                    <div className="project-search" >
                        <Input placeholder="按项目名称、项目显示名称搜索" size="large" />
                    </div>
                </h1>
                
                <Row gutter="40">
                    <Col span="20" gutter="24" >
                        <Row gutter="24">   
                            <div className="sortTitle">
                                <span  className="faileSort" >按任务失败数排序</span>
                                <span  className="defaultSort" >默认顺序</span>
                            </div>
                            {
                                [1,2,3,4,55,6,8,4,1].map(v=>{
                                    return  <Col span="8" className="card-width">
                                                <Card  className="general-card" title={title}>
                                                    <Row className="card-content">
                                                        <Col span="12">
                                                            <div className="statistics" >已发布/总任务数： 23/2344</div>
                                                            <div className="statistics" >表数量： 123</div>
                                                            <div className="statistics" >项目占用存储： 234.34GB</div>
                                                            <div className="statistics" >创建时间： 2016-03-31 14:05:27</div>
                                                        </Col>
                                                        <Col span="12">
                                                            <div className="number" >435</div>
                                                            <Card className="card-task">
                                                                <span className="img-container">
                                                                    <img className="task-img" src="/public/rdos/img/icon/offline.png" />
                                                                </span>
                                                                离线任务开发
                                                            </Card>
                                                            <Card className="card-task">
                                                                <span className="img-container">
                                                                    <img className="task-img" src="/public/rdos/img/icon/realtime.png" />
                                                                </span>
                                                                实时任务开发
                                                            </Card>
                                                            <Card className="card-task">运维中心</Card>
                                                        </Col>
                                                        <div className="triangle_border_right">
                                                            <span></span>
                                                        </div>
                                                    </Row>
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
                                    defaultCurrent={1} 
                                    total={500} 
                                    onChange={this.changePage}
                                    pageSize={this.pageSize}

                                />
                            </div>
                        </Col>
                    </Row>
                    </Col>
                    <Col span="4">
                        <Card  style={{ height: 743,marginTop:20,padding:20 }}>
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
        )
    }
}
export default connect((state) => {
    return {
        user: state.user,
        projects: state.projects,
    }
})(Index)
