import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';
import { bindActionCreators } from 'redux';

import { Row, Col, Icon, Card, Button } from 'antd';
import NewProject from '../../components/newProject';

import * as baseActions from '../../actions/base'

import Api from '../../api'

@(connect((state: any) => {
    return {
        projectTotal: state.project.projectList.length
    }
}, (dispatch: any) => {
    return {
        ...bindActionCreators(baseActions, dispatch)
    }
}) as any)
class Welcome extends React.Component<any, any> {
    state: any = {
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
    getProjects = async () => {
        let res = await Api.comm.getTopProject();
        if (res && res.code == 1) {
            this.setState({
                projects: res.data
            })
        }
    }
    getStatistic = async () => {
        let res = await Api.comm.getAllJobStatus();
        if (res && res.code == 1) {
            this.setState({
                statistic: res.data
            })
        }
    }
    gotoProjectList = () => {
        this.props.router.push('/science/index/projectList')
    }
    render () {
        const { projects, statistic, visible } = this.state;
        const style: any = {
            width: '20%'
        }
        return (
            <Row className="welcome">
                <Col span={20} className="welcome-page">
                    <div>
                        <Row gutter={20}>
                            <Col span={5} style={style}>
                                <div className="info-box blue">
                                    <div className="info-box-left">
                                        <p><Icon type="appstore-o" /><span>总项目数</span></p>
                                        <p className="number">{this.props.projectTotal}</p>
                                    </div>
                                </div>
                            </Col>
                            <Col span={5} style={style}>
                                <div className="info-box purple">
                                    <div className="info-box-left">
                                        <p><Icon type="usb" /><span>总实验数</span></p>
                                        <p className="number">{statistic.totalLabCount || 0}</p>
                                    </div>
                                </div>
                            </Col>
                            <Col span={5} style={style}>
                                <div className="info-box green">
                                    <div className="info-box-left">
                                        <p><Icon type="book" /><span>总Notebook作业数</span></p>
                                        <p className="number">{statistic.totalNotebookCount || 0}</p>
                                    </div>
                                </div>
                            </Col>
                            <Col span={5} style={style}>
                                <div className="info-box orange">
                                    <div className="info-box-left">
                                        <p><Icon type="usb" /><span>正在运行的实验</span></p>
                                        <p className="number">{statistic.successLabCount || 0}</p>
                                    </div>
                                </div>
                            </Col>
                            <Col span={5} style={style}>
                                <div className="info-box yellow">
                                    <div className="info-box-left">
                                        <p><Icon type="book" /><span>正在运行的Notebook作业</span></p>
                                        <p className="number">{statistic.successNotebookCount || 0}</p>
                                    </div>
                                </div>
                            </Col>
                        </Row>
                        <Card
                            style={{ marginTop: 20 }}
                            bordered={false}
                            noHovering
                            title={<div style={{ fontSize: '16px' }}>欢迎使用DTInsight.Science数据科学平台</div>}
                            extra={<>
                                <Button className='o-button--large-font' ghost type="primary" onClick={this.handleNewProject}>创建项目</Button>
                                <Button className='o-button--large-font' ghost type="primary" onClick={this.gotoProjectList}>项目列表</Button>
                            </>}>
                            <img src='public/science/img/welcome.png' />
                        </Card>
                    </div>
                </Col>
                <Col span={4} className="common-projects">
                    <div style={{ marginBottom: 20 }}>
                        <div className="title">常用项目</div>
                        {
                            projects.map((item: any) => {
                                return <a className="project" key={item.id} onClick={() => {
                                    this.props.router.push('/science/workbench');
                                    this.props.setProject(item);
                                }}>{item.projectAlias}</a>
                            })
                        }
                    </div>
                    {/* <div>
                        <div className="title">新手指南</div>
                        <a className="project" href="javascript:void(0)">创建算法实验</a>
                        <a className="project" href="javascript:void(0)">任务离线调度</a>
                        <a className="project" href="javascript:void(0)">模型自动调参</a>
                    </div> */}
                </Col>
                <NewProject
                    onCancel={this.handleCancel}
                    visible={visible} />
            </Row>
        );
    }
}

export default withRouter(Welcome);
