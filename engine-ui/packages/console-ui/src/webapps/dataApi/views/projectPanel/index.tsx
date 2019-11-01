import * as React from 'react';
import { Link, hashHistory } from 'react-router';
import { connect } from 'react-redux';
import { Input, Card, Row, Col, Tooltip, Icon, Button, Pagination, message, Spin } from 'antd';
import moment from 'moment';
import SummaryPanel from './summaryPanel';
import NoData from '../../components/no-data';
import Api from '../../api/project';
import * as projectActions from '../../actions/project';
import { PROJECT_STATUS } from '../../consts';

interface ProjectState {
    loading: boolean;
    projectListInfo: any[]
}


class ProjectPanel extends React.Component<any, ProjectState> {
    constructor (props: any) {
        super(props);
        this.state = {
            loading: false,
            projectListInfo: [
                {
                    createUserId: 29,
                    gmtCreate: 1556158149000,
                    gmtModified: 1556158153000,
                    id: 33,
                    isAllowDownload: 0,
                    isDeleted: 0,
                    jobSum: 0,
                    produceProjectId: 287,
                    projectAlias: "mq_test",
                    projectDesc: null,
                    projectIdentifier: "mq_test",
                    projectName: "mq_test",
                    projectType: 1,
                    scheduleStatus: 1,
                    status: 1,
                    stick: 1562822506000,
                    stickStatus: 1,
                    supportEngineType: [1, 2],
                    tableCount: 114,
                    taskCountMap: {submitCount: 72, allCount: 146},
                    tenantId: 27,
                    totalSize: "438.77KB",
                },
                {
                    createUserId: 29,
                    gmtCreate: 1556158149000,
                    gmtModified: 1556158153000,
                    id: 33,
                    isAllowDownload: 0,
                    isDeleted: 0,
                    jobSum: 0,
                    produceProjectId: 287,
                    projectAlias: "mq_test",
                    projectDesc: null,
                    projectIdentifier: "mq_test",
                    projectName: "mq_test",
                    projectType: 1,
                    scheduleStatus: 1,
                    status: 1,
                    stick: 1562822506000,
                    stickStatus: 1,
                    supportEngineType: [1, 2],
                    tableCount: 114,
                    taskCountMap: {submitCount: 72, allCount: 146},
                    tenantId: 27,
                    totalSize: "438.77KB",
                },
                {
                    createUserId: 29,
                    gmtCreate: 1556158149000,
                    gmtModified: 1556158153000,
                    id: 33,
                    isAllowDownload: 0,
                    isDeleted: 0,
                    jobSum: 0,
                    produceProjectId: 287,
                    projectAlias: "mq_test",
                    projectDesc: null,
                    projectIdentifier: "mq_test",
                    projectName: "mq_test",
                    projectType: 1,
                    scheduleStatus: 1,
                    status: 1,
                    stick: 1562822506000,
                    stickStatus: 1,
                    supportEngineType: [1, 2],
                    tableCount: 114,
                    taskCountMap: {submitCount: 72, allCount: 146},
                    tenantId: 27,
                    totalSize: "438.77KB",
                }
            ]
        }
    }
    componentDidMount () {
        // this.getProjectListInfo();
    }
    getProjectListInfo = () => {
        this.setState({
            loading: true
        })
        Api.getProjectListInfo().then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    projectListInfo: (res.data && res.data.data) || [],
                    loading: false
                })
            } else {
                this.setState({
                    loading: false
                })
            }
        })
    }
     // Api
     fixApiChildrenApps = (arr: any) => {
        let fixApiChildrenApps: any = [];
        if (arr && arr.length > 1) {
            arr.map((item: any) => {
                switch (item.name) {
                    case '概览':
                        fixApiChildrenApps[0] = item;
                        break;
                    case 'API市场':
                        fixApiChildrenApps[1] = item;
                        break;
                    case '我的API':
                        fixApiChildrenApps[2] = item;
                        break;
                    case 'API管理':
                        fixApiChildrenApps[3] = item;
                        break;
                    case '授权与安全':
                        fixApiChildrenApps[4] = item;
                        break;
                    case '数据源管理':
                        fixApiChildrenApps[5] = item;
                        break;
                }
            })
            return fixApiChildrenApps
        } else {
            return arr;
        }
    }

    generalTitle = (data: any) => {
        const title = <div>
            <Row>
                <Col span={20} >
                    {data.status == PROJECT_STATUS.NORMAL ? (
                        <Link to={`/realtime/task?projectId=${data.id}`}>
                            <span className="company-name" onClick={() => { this.setRouter('operation', data) }}>
                                {data.projectAlias}&nbsp;&nbsp;
                            </span>
                        </Link>
                    ) : (<span className="company-name no-hover">
                        {data.projectAlias}&nbsp;&nbsp;
                    </span>)}
                    {this.renderTitleText(data)}
                </Col>
            </Row>
        </div>
        return title;
    }
    renderTitleText (data: any) {
        switch (data.status) {
            case PROJECT_STATUS.INITIALIZE: {
                return (
                    <span>
                        <Icon type="loading" style={{ fontSize: 14, color: '#2491F7', paddingLeft: 16 }} />
                        <span style={{ color: '#999', paddingLeft: '8px' }}>创建中</span>
                    </span>
                )
            }
            case PROJECT_STATUS.NORMAL: {
                return (
                    <span style={{ color: '#999' }}>
                        {`(${data.projectName})`}
                    </span>
                )
            }
            case PROJECT_STATUS.DISABLE:
            case PROJECT_STATUS.FAIL: {
                return (
                    <span>
                        <Icon type="close-circle" style={{ fontSize: 14, color: '#f00', paddingLeft: 16 }} />
                        <span style={{ color: '#999', paddingLeft: '8px' }}>创建失败</span>
                    </span>
                )
            }
        }
    }
    handleMouseOver = (type?: any, e?: any) => {
        if (type == 'apiMarket') {
            e.currentTarget.getElementsByTagName('img')[0].src = '/public/stream/img/icon/operation2.svg'
        } else {
            e.currentTarget.getElementsByTagName('img')[0].src = '/public/stream/img/icon/realtime2.svg'
        }
    }

    handleMouseOut = (type?: any, e?: any) => {
        if (type == 'apiMarket') {
            e.currentTarget.getElementsByTagName('img')[0].src = '/public/stream/img/icon/operation.svg'
        } else {
            e.currentTarget.getElementsByTagName('img')[0].src = '/public/stream/img/icon/realtime.svg'
        }
    }
    setRouter = (type: any, v: any) => {
        let src: any;
        const { dispatch } = this.props;
        if (type === 'apiMarket') {
            src = '/api/market'
        } else {
            src = '/api/manage'
        }
        dispatch(projectActions.getProjects());
        dispatch(projectActions.getProject(v.id));
        hashHistory.push(src)
    }
    render () {
        const { loading, projectListInfo } = this.state;
        const { licenseApps } = this.props;
        const fixArrChildrenApps = this.fixApiChildrenApps(licenseApps[4] && licenseApps[4].children) || [];
        const apiMarket = fixArrChildrenApps[1];
        const apiManage = fixArrChildrenApps[3];
        return(
            <Spin tip="Loading..." spinning={loading} delay={500} >
                <div className="project-dashboard develop-kit" style={{ padding: '20px 35px', overflow: 'hidden' }}>
                    <div className="project-left">
                        <Row style={{ marginTop: '10px' }} gutter={10}>
                            <Col span={24} >
                                <Row gutter={10} style={{ margin: 0 }}>
                                    {
                                        projectListInfo && projectListInfo.length === 0 && !loading ? <NoData/> : ''
                                    }
                                    {
                                        projectListInfo && projectListInfo.map((v: any) => {
                                            return <Col span={8} className="card-width" key={v.id} style={{ padding: 0 }}>
                                                <Card className="general-card" title={this.generalTitle(v)} noHovering bordered={false}>
                                                    <Row className="card-content" >
                                                        <Col span={16}>
                                                            <div className="statistics" >API创建数： <span className="statistics-info">{`${v.taskCountMap.submitCount}/${v.taskCountMap.allCount}`}</span></div>
                                                            <div className="statistics" >API发布数： <span className="statistics-info">{v.tableCount}</span></div>
                                                            <div className="statistics" >创建时间： <span className="statistics-info">{moment(v.gmtCreate).format('YYYY-MM-DD HH:mm:ss')}</span></div>
                                                        </Col>
                                                        <Col span={24} className="card-task-padding">
                                                            <Row>
                                                                {
                                                                    v.status != 1 || (apiMarket && !apiMarket.isShow) ? '' : (
                                                                        <Col span={12}>
                                                                            <Card className="card-task"
                                                                                {...{ onClick: () => { this.setRouter('apiMarket', v) } }}
                                                                                noHovering
                                                                            >
                                                                                {/* <span className="img-container">
                                                                                    <img className="task-img" src="/public/rdos/img/icon/offline2.svg" />
                                                                                </span> */}
                                                                                API市场
                                                                            </Card>
                                                                        </Col>
                                                                    )
                                                                }
                                                                {
                                                                    v.status != 1 || (apiManage && !apiManage.isShow) ? '' : (
                                                                        <Col span={12}>
                                                                            <Card className="card-task" style={{ padding: '1.5 0', marginLeft: '6px' }}
                                                                                {...{ onClick: () => { this.setRouter('apiManage', v) } }}
                                                                                noHovering
                                                                            >
                                                                                {/* <span className="img-container">
                                                                                    <img className="task-img" src="/public/rdos/img/icon/offline2.svg" />
                                                                                </span> */}
                                                                                API管理
                                                                            </Card>
                                                                        </Col>
                                                                    )
                                                                }
                                                            </Row>
                                                        </Col>
                                                    </Row>
                                                </Card>
                                            </Col>
                                        })
                                    }
                                </Row>
                            </Col>
                        </Row>
                    </div>
                    <div className="project-right">
                        <SummaryPanel />
                    </div>
                </div>
            </Spin>
        )
    }
}
export default connect((state: any) => {
    return {
        user: state.user,
        projects: state.projects,
        licenseApps: state.licenseApps
    }
})(ProjectPanel)
