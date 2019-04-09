import React, { Component } from 'react';
import { Row, Col, Icon, Card, Button } from 'antd';
import NewProject from '../../components/newProject';
class Welcome extends Component {
    state = {
        projects: [],
        statistic: {},
        visible: false
    }
    componentDidMount () {
        this.getProjects();
        this.getStatistic();
    }
    handleNewProject = () => {
        this.setState({
            visible: true
        });
    }
    handleCancel = () => {
        this.setState({
            visible: false
        });
    }
    getProjects = () => {
        this.setState({
            projects: [{
                projectId: 1,
                projectName: '中金易云项目'
            }, {
                projectId: 2,
                projectName: 'SG航空项目'
            }, {
                projectId: 3,
                projectName: '中原银行项目'
            }, {
                projectId: 4,
                projectName: '北京国网项目'
            }, {
                projectId: 5,
                projectName: '京东方项目'
            }]
        })
    }
    getStatistic = () => {
        this.setState({
            statistic: {
                projects: 1,
                experiments: 2,
                notebook: 3,
                experimenting: 4,
                notebooking: 5
            }
        })
    }
    render () {
        const { projects, statistic, visible } = this.state;
        const style = {
            width: '20%'
        }
        return (
            <Row className="inner-container">
                <Col span={20} className="welcome-page">
                    <div>
                        <Row gutter={20}>
                            <Col span={5} style={style}>
                                <div className="info-box blue">
                                    <div className="info-box-left">
                                        <p><Icon type="appstore-o" /><span>总项目数</span></p>
                                        <p className="number">{statistic.projects || 0}</p>
                                    </div>
                                </div>
                            </Col>
                            <Col span={5} style={style}>
                                <div className="info-box purple">
                                    <div className="info-box-left">
                                        <p><Icon type="usb" /><span>总实验数</span></p>
                                        <p className="number">{statistic.experiments || 0}</p>
                                    </div>
                                </div>
                            </Col>
                            <Col span={5} style={style}>
                                <div className="info-box green">
                                    <div className="info-box-left">
                                        <p><Icon type="book" /><span>总Notebook作业数</span></p>
                                        <p className="number">{statistic.notebook || 0}</p>
                                    </div>
                                </div>
                            </Col>
                            <Col span={5} style={style}>
                                <div className="info-box orange">
                                    <div className="info-box-left">
                                        <p><Icon type="usb" /><span>正在运行的实验</span></p>
                                        <p className="number">{statistic.experimenting || 0}</p>
                                    </div>
                                </div>
                            </Col>
                            <Col span={5} style={style}>
                                <div className="info-box yellow">
                                    <div className="info-box-left">
                                        <p><Icon type="book" /><span>正在运行的Notebook作业</span></p>
                                        <p className="number">{statistic.notebooking || 0}</p>
                                    </div>
                                </div>
                            </Col>
                        </Row>
                        <Card
                            style={{ marginTop: 20 }}
                            bordered={false}
                            noHovering
                            title={<div>欢迎使用DTInsight.Science数据科学平台</div>}
                            extra={<>
                                <Button ghost type="primary" onClick={this.handleNewProject}>创建项目</Button>
                                <Button ghost type="primary" onClick={() => this.props.toggle()}>项目列表</Button>
                            </>}>
                            <img src='public/science/img/welcome.png' />,
                        </Card>
                    </div>
                </Col>
                <Col span={4} className="common-projects">
                    <div style={{ marginBottom: 20 }}>
                        <div className="title">常用项目</div>
                        {
                            projects.map((item) => {
                                return <a className="project" key={item.projectId} href="javascript:void(0)">{item.projectName}</a>
                            })
                        }
                    </div>
                    <div>
                        <div className="title">新手指南</div>
                        <a className="project" href="javascript:void(0)">创建算法实验</a>
                        <a className="project" href="javascript:void(0)">任务离线调度</a>
                        <a className="project" href="javascript:void(0)">模型自动调参</a>
                    </div>
                </Col>
                <NewProject
                    onCancel={this.handleCancel}
                    visible={visible} />
            </Row>
        );
    }
}

export default Welcome;
