import * as React from 'react';
import { Link, hashHistory, withRouter } from 'react-router';
import { connect } from 'react-redux';
import { Card, Row, Col, Icon, Spin, Button } from 'antd';
import moment from 'moment';
import SummaryPanel from './summaryPanel';
import NoData from '../../components/no-data';
import NewProjectModal from '../../components/newProject';
import Api from '../../api/project';
import * as projectActions from '../../actions/project';
import { PROJECT_STATUS } from '../../consts';

interface ProjectState {
    loading: boolean;
    projectListInfo: any[];
    visible: boolean;
}

class ProjectPanel extends React.Component<any, ProjectState> {
    constructor (props: any) {
        super(props);
        this.state = {
            loading: false,
            visible: false,
            projectListInfo: [
                {
                    createUserId: 29,
                    gmtCreate: 1556158149000,
                    gmtModified: 1556158153000,
                    id: 32,
                    isAllowDownload: 0,
                    isDeleted: 0,
                    jobSum: 0,
                    produceProjectId: 287,
                    projectAlias: 'mq_test',
                    projectDesc: null,
                    projectIdentifier: 'mq_test',
                    projectName: 'mq_test',
                    projectType: 1,
                    scheduleStatus: 1,
                    status: 1,
                    stick: 1562822506000,
                    stickStatus: 1,
                    supportEngineType: [1, 2],
                    tableCount: 114,
                    taskCountMap: { submitCount: 72, allCount: 146 },
                    tenantId: 27,
                    totalSize: '438.77KB'
                },
                {
                    createUserId: 29,
                    gmtCreate: 1556158149000,
                    gmtModified: 1556158153000,
                    id: 34,
                    isAllowDownload: 0,
                    isDeleted: 0,
                    jobSum: 0,
                    produceProjectId: 287,
                    projectAlias: 'mq_test',
                    projectDesc: null,
                    projectIdentifier: 'mq_test',
                    projectName: 'mq_test',
                    projectType: 1,
                    scheduleStatus: 1,
                    status: 1,
                    stick: 1562822506000,
                    stickStatus: 1,
                    supportEngineType: [1, 2],
                    tableCount: 114,
                    taskCountMap: { submitCount: 72, allCount: 146 },
                    tenantId: 27,
                    totalSize: '438.77KB'
                },
                {
                    createUserId: 29,
                    gmtCreate: 1556158149000,
                    gmtModified: 1556158153000,
                    id: 35,
                    isAllowDownload: 0,
                    isDeleted: 0,
                    jobSum: 0,
                    produceProjectId: 287,
                    projectAlias: 'mq_test',
                    projectDesc: null,
                    projectIdentifier: 'mq_test',
                    projectName: 'mq_test',
                    projectType: 1,
                    scheduleStatus: 1,
                    status: 1,
                    stick: 1562822506000,
                    stickStatus: 1,
                    supportEngineType: [1, 2],
                    tableCount: 114,
                    taskCountMap: { submitCount: 72, allCount: 146 },
                    tenantId: 27,
                    totalSize: '438.77KB'
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

    handleNewProject = () => {
        this.setState({
            visible: true
        });
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

    getCardTitle = (data: any) => {
        const title = <div>
            <Row>
                <Col span={18} className='c_offten_project_card_title_info' >
                    <p className='c_offten_project_card_title_name'>公司内部测试</p>
                    <span className='c_offten_project_card_title_name_alias'>1231</span>
                </Col>
                <Col span={6} >
                    <div className='c_offten_project_card_title_icon'>
                        <img src='public/dataApi/img/project1.png' />
                    </div>
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
    gotoProjectList = () => {
        this.props.router.push('/api/projectList')
    }
    renderProjectCard = (project: any) => {
        return (
            <Col span={8} className="c_offten_project_col">
                <Card className="c_offten_project_card" noHovering bordered={false} title={this.getCardTitle(project)}>
                    <Row className='c_offten_project_card_content'>
                        <Col span={13}>
                            API创建数： <span className='c_project_num'>12312</span>
                        </Col>
                        <Col span={11}>
                            API发布数： <span className='c_project_num'>2132</span>
                        </Col>
                        <Col span={24}>
                            <Row>
                                <Col>创建时间： <span className='c_project_num'>2019-10-22 12:00:09</span></Col>
                            </Row>
                        </Col>
                        <Col span={24} className="c_opera">
                            <Row gutter={16}>
                                <Col span={12}>
                                    <div className="c_api_opera" >API申请</div>
                                </Col>
                                <Col span={12}>
                                    <div className="c_api_opera" >API管理</div>
                                </Col>
                            </Row>
                        </Col>
                    </Row>
                </Card>
            </Col>
        )
    }
    render () {
        const { loading, projectListInfo = [], visible } = this.state;
        const { licenseApps } = this.props;
        const fixArrChildrenApps = this.fixApiChildrenApps(licenseApps[4] && licenseApps[4].children) || [];
        const apiMarket = fixArrChildrenApps[1];
        const apiManage = fixArrChildrenApps[3];
        return (
            <div className='c_project_wrapper'>
                <main>
                    <section className='c_left_section_wrapper'>
                        <div className='c_offten_project_list'>
                            <Row className='c_offten_project_title'>
                                <Col span={20}>
                                    <img className='c_offten_project_title_img' src='public/dataApi/img/often_project.png' />
                                </Col>
                                <Col span={4}>
                                    <span className='c_offten_project_title_opera'>
                                        <a>创建项目</a>
                                        <a>项目列表</a>
                                    </span>
                                </Col>
                            </Row>
                            {
                                projectListInfo && projectListInfo.length > 0 ? (
                                    <Row gutter={16}>
                                        {
                                            projectListInfo.map(project => {
                                                return this.renderProjectCard(project)
                                            })
                                        }
                                    </Row>
                                ) : (
                                    <Row className='c_no_project'>
                                        <Col span={24}>暂无常用项目</Col>
                                    </Row>
                                )
                            }
                        </div>
                        <div className='c_api_process_pic'>
                            <img src='public/dataApi/img/process_api.png' />
                        </div>
                    </section>

                    <section className='c_right_section_wrapper'>
                        {/* 常用项目 */}
                        <div style={{ position: 'absolute', right: 0, top: 0, width: '-webkit-fill-available' }}>
                            <Row className='c_summary_project'>
                                <img src='public/dataApi/img/summary_project.png' />
                            </Row>
                            <Row>
                                <Card className='c_summary_project_card'>
                                    <Row gutter={16}>
                                        <Col span={8}>
                                            <div className='c_summary_sub'>
                                                <img src ='public/dataApi/img/all_project.png' className='c_summary_sub_pic' />
                                                <span className='c_summary_sub_name'>总项目数</span>
                                                <span className='c_summary_sub_num'>152</span>
                                            </div>
                                        </Col>
                                        <Col span={8}>
                                            <div className='c_summary_sub'>
                                                <img src ='public/dataApi/img/api_create.png' className='c_summary_sub_pic' />
                                                <span className='c_summary_sub_name'>API创建数</span>
                                                <span className='c_summary_sub_num'>12</span>
                                            </div>
                                        </Col>
                                        <Col span={8}>
                                            <div className='c_summary_sub'>
                                                <img src ='public/dataApi/img/api_publish.png' className='c_summary_sub_pic' />
                                                <span className='c_summary_sub_name'>API发布数</span>
                                                <span className='c_summary_sub_num'>2</span>
                                            </div>
                                        </Col>
                                    </Row>
                                    <Row gutter={16}>
                                        <Col span={12}>
                                            <Card className='c_latest_day_card' noHovering bordered={false}>
                                                <Row>
                                                    <div className='c_latest_day_title'>最近24h累计调用次数</div>
                                                    <div className='c_latest_day_num'>3560</div>
                                                    <div className='c_latest_day_img'><img src='public/dataApi/img/call_number.png' /></div>
                                                </Row>
                                            </Card>
                                        </Col>
                                        <Col span={12}>
                                            <Card className='c_latest_day_card' noHovering bordered={false}>
                                                <Row>
                                                    <div className='c_latest_day_title'>最近24h调用失败率</div>
                                                    <div className='c_latest_day_num'>22%</div>
                                                    <div className='c_latest_day_img'><img src='public/dataApi/img/fail.png' /></div>
                                                </Row>
                                            </Card>
                                        </Col>
                                    </Row>
                                </Card>
                            </Row>
                        </div>
                        {/* 快速入门 */}
                        <div style={{ position: 'absolute', right: 0, top: 370, bottom: 0, width: '-webkit-fill-available' }}>
                            <Row className='c_summary_project'>
                                <img src='public/dataApi/img/quick_start.png' />
                            </Row>
                            <div>
                                <Row>
                                    <Card className='c_use_tutorial_card'>
                                        <Row gutter={16}>
                                            <Col span={8}>
                                                <div className='c_help_target'>API生成</div>
                                            </Col>
                                            <Col span={8}>
                                                <div className='c_help_target'>API发布</div>
                                            </Col>
                                            <Col span={8}>
                                                <div className='c_help_target'>API申请</div>
                                            </Col>
                                        </Row>
                                        <Row gutter={16}>
                                            <Col span={8}>
                                                <div className='c_help_target'>API测试</div>
                                            </Col>
                                            <Col span={8}>
                                                <div className='c_help_target'>API调用</div>
                                            </Col>
                                        </Row>
                                    </Card>
                                </Row>
                                <Row>
                                    <Card className='c_use_tutorial_card c_video_width'>
                                        <Row>
                                            <Col span={14}>1</Col>
                                            <Col span={10}>2</Col>
                                        </Row>
                                    </Card>
                                </Row>
                                {/* <div>
                                    <Card className='c_use_tutorial_card c_video_width'>
                                    </Card>
                                </div> */}
                            </div>
                        </div>
                    </section>
                </main>
            </div>
        )
    }
}
export default connect((state: any) => {
    return {
        user: state.user,
        projects: state.projects,
        licenseApps: state.licenseApps
    }
})(withRouter(ProjectPanel))
