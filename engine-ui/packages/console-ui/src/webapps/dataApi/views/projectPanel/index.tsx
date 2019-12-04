import * as React from 'react';
import { hashHistory, withRouter } from 'react-router';
import { connect } from 'react-redux';
import { Card, Row, Col, Icon, Spin } from 'antd';
import { cloneDeep } from 'lodash';
import moment from 'moment';
import utils from 'utils'
import NewProjectModal from '../../components/newProject';
import Api from '../../api/project';
import * as projectActions from '../../actions/project';
import { PROJECT_STATUS, OPERA_ROW_ONE_DATA, OPERA_ROW_TWO_DATA } from '../../consts';

interface ProjectState {
    visible: boolean;
    projectSummary: {
        apiCount: number;
        projectCount: number;
        apiIssueCount: number;
        total24InvokeCount: number;
        total24FailProbability: number;
    };
}

class ProjectPanel extends React.Component<any, ProjectState> {
    constructor (props: any) {
        super(props);
        this.state = {
            visible: false,
            projectSummary: {
                apiCount: undefined,
                projectCount: undefined,
                apiIssueCount: undefined,
                total24InvokeCount: undefined,
                total24FailProbability: undefined
            }
        }
    }
    componentDidMount () {
        const { dispatch } = this.props;
        dispatch(projectActions.getProjectList());
        this.getProjectSummary();
    }

    componentDidUpdate (prevProps: any, prevState: any) {
        const { projectListInfo } = this.props;
        const oldProjectListInfo = prevProps.projectListInfo;
        if (projectListInfo && projectListInfo != oldProjectListInfo) {
            this.getProjectSummary();
        }
    }

    getProjectSummary = () => {
        Api.getProjectSummary().then(res => {
            if (res.code === 1) {
                this.setState({
                    projectSummary: res.data || {}
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
    enterProject () {
        hashHistory.push(`/api/overview`);
    }
    getCardTitle = (project: any, index: number) => {
        if (!project) return;
        const title = <div>
            <Row>
                <Col span={18} className='c_offten_project_card_title_info' >
                    {
                        project.status == PROJECT_STATUS.NORMAL ? (
                            <>
                                <span className='c_offten_project_card_title_name' onClick={
                                    () => {
                                        this.props.dispatch(projectActions.getProject(project.id, this.enterProject))
                                    }}
                                title={project.projectAlias}>
                                    {utils.textOverflowExchange(project.projectAlias, 19)}
                                </span><br />
                            </>
                        ) : <span className='c_offten_project_card_title_name' title={project.projectAlias}>
                            {utils.textOverflowExchange(project.projectAlias, 19)}
                        </span>
                    }
                    <span className='c_offten_project_card_title_name_alias'>
                        {this.renderTitleText(project)}
                    </span>
                </Col>
                <Col span={6} >
                    <div className='c_offten_project_card_title_icon'>
                        <img src={`public/dataApi/img/project${index + 1}.png`} />
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
                    <span title={data.projectName}>
                        {utils.textOverflowExchange(data.projectName, 19)}
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
    setRouter = (type: any, project: any) => {
        let src: any;
        const { dispatch } = this.props;
        if (type === 'apiMarket') {
            src = '/api/market'
        } else {
            src = '/api/manage'
        }
        dispatch(projectActions.getProject(project.id));
        hashHistory.push(src)
    }
    gotoProjectList = () => {
        this.props.router.push('/api/projectList')
    }
    renderProjectCard = (project: any, index: number) => {
        const { licenseApps } = this.props;
        const fixArrChildrenApps = this.fixApiChildrenApps(licenseApps[4] && licenseApps[4].children) || [];
        const apiMarket = fixArrChildrenApps[1];
        const apiManage = fixArrChildrenApps[3];
        const getCardContent = () => {
            if (project) {
                return (
                    <Card className="c_offten_project_card" noHovering bordered={false} title={this.getCardTitle(project, index)}>
                        <Row className='c_offten_project_card_content'>
                            <Col span={12}>
                                API创建数： <span className='c_project_num'>{project.apiCreateCount}</span>
                            </Col>
                            <Col span={12}>
                                API发布数： <span className='c_project_num'>{project.apiIssueCount}</span>
                            </Col>
                            <Col span={24}>
                                <Row>
                                    <Col>创建时间： <span className='c_project_num'>{moment(project.gmtCreate).format('YYYY-MM-DD HH:mm:ss')}</span></Col>
                                </Row>
                            </Col>
                            <Col span={24} className="c_opera">
                                <Row gutter={16}>
                                    {project.status != PROJECT_STATUS.NORMAL || (apiMarket && !apiMarket.isShow) ? null : (
                                        <Col span={12}>
                                            <div className="c_opera_link" {...{ onClick: () => { this.setRouter('apiMarket', project) } }} >API市场</div>
                                        </Col>
                                    )}
                                    {
                                        project.status != PROJECT_STATUS.NORMAL || (apiManage && !apiManage.isShow) ? null : (
                                            <Col span={12}>
                                                <div className="c_opera_link" {...{ onClick: () => { this.setRouter('apiManage', project) } }}>API管理</div>
                                            </Col>
                                        )
                                    }
                                </Row>
                            </Col>
                        </Row>
                    </Card>
                )
            } else {
                return (
                    <Card className="c_no_project_card" noHovering bordered={false}>
                        <Row className='c_no_project_row'>
                            <Col className='c_no_project_content' span={24}>
                                <p>暂无项目</p>
                            </Col>
                            <Col span={24}>
                                <div className="c_opera_link" {...{ onClick: () => { this.handleNewProject() } }}>
                                    <img src='public/dataApi/img/plus.svg' />
                                    创建项目
                                </div>
                            </Col>
                        </Row>
                    </Card>
                )
            }
        }
        return (
            <Col span={8} className="c_offten_project_col">
                {getCardContent()}
            </Col>
        )
    }
    renderTotalData = (data: any[] = [], type: string) => {
        const colSpan = 24 / data.length;
        return data.map((item, index) => {
            const { imgSrc, dataName, data } = item;
            if (type == 'all') {
                return (
                    <Col span={colSpan} key={index}>
                        <div className='c_summary_sub'>
                            <img src ={imgSrc} className='c_summary_sub_pic' />
                            <span className='c_summary_sub_name'>{dataName}</span>
                            <span className='c_summary_sub_num'>{data}</span>
                        </div>
                    </Col>
                )
            } else {
                return (
                    <Col span={colSpan} key={index} style={{ marginTop: '16px' }}>
                        <Card className='c_latest_day_card' noHovering bordered={false}>
                            <Row>
                                <div className='c_latest_day_title'>{dataName}</div>
                                <div className='c_latest_day_num'>{data}</div>
                                <div className='c_latest_day_img'><img src={imgSrc} /></div>
                            </Row>
                        </Card>
                    </Col>
                )
            }
        })
    }

    loopOperaLink = (data: any[] = []) => {
        return data.map((item, index) => {
            const { title, link } = item;
            // const push = index == 1 ? 1 : 2;
            return (
                <Col span={8} key={index}>
                    <div className='c_help_target'>
                        <img src='public/dataApi/img/help.png' />
                        <a rel="noopener noreferrer" target="_blank" href={link}>{title}</a>
                    </div>
                </Col>
            )
        })
    }
    exChangeShowProject = (projects: any[] = []) => {
        // 填充数组, 渲染无项目 card
        const projectMap = cloneDeep(projects);
        if (projects.length === 0 || projects.length === 3) {
            return projectMap
        } else if (projects.length === 1) {
            return projectMap.concat([null, null])
        } else if (projects.length === 2) {
            return projectMap.concat(null)
        };
    }
    render () {
        const { visible, projectSummary } = this.state;
        const { apiCount, projectCount, apiIssueCount, total24InvokeCount, total24FailProbability } = projectSummary;
        const { projectListInfo = [], panelLoading } = this.props;
        const showProjects = this.exChangeShowProject(projectListInfo.slice(0, 3));
        const percent = total24FailProbability == 0 ? '' : '%';
        const totalData = [{
            dataName: '总项目数',
            data: projectCount,
            imgSrc: 'public/dataApi/img/all_project.png'
        }, {
            dataName: 'API创建数',
            data: apiCount,
            imgSrc: 'public/dataApi/img/api_create.png'
        }, {
            dataName: 'API发布数',
            data: apiIssueCount,
            imgSrc: 'public/dataApi/img/api_publish.png'
        }];
        const recentData = [{
            dataName: '最近24h累计调用次数',
            data: total24InvokeCount,
            imgSrc: 'public/dataApi/img/call_number.png'
        }, {
            dataName: '最近24h调用失败率',
            data: `${total24FailProbability}${percent}`,
            imgSrc: 'public/dataApi/img/fail.png'
        }];
        return (
            <div className='c_project_wrapper'>
                <main>
                    {/* 左侧项目 */}
                    <section className='c_left_section_wrapper'>
                        <div className='c_offten_project_list'>
                            <Row className='c_offten_project_title'>
                                <Col span={20}>
                                    <img className='c_offten_project_title_img' src='public/dataApi/img/often_project.png' />
                                </Col>
                                <Col span={4}>
                                    <span className='c_offten_project_title_opera'>
                                        <a onClick={this.handleNewProject}>创建项目</a>
                                        <a onClick={this.gotoProjectList}>项目列表</a>
                                    </span>
                                </Col>
                            </Row>
                            <div className='c_spin_loading'>
                                <Spin spinning={panelLoading} delay={500}>
                                    {
                                        showProjects && showProjects.length > 0 ? (
                                            <Row gutter={16}>
                                                {
                                                    showProjects.map((project: any, index: any) => {
                                                        return this.renderProjectCard(project, index)
                                                    })
                                                }
                                            </Row>
                                        ) : (
                                            <Row className='c_no_project'>
                                                <Col span={24}>
                                                    暂无项目，来创建您的第一个项目吧！
                                                </Col>
                                                <Col span={24}>
                                                    <div className="c_opera_link" {...{ onClick: () => { this.handleNewProject() } }}>
                                                        <img src='public/dataApi/img/plus.svg' />
                                                        创建项目
                                                    </div>
                                                </Col>
                                            </Row>
                                        )
                                    }
                                </Spin>
                            </div>
                        </div>
                        <div className='c_process_pic'>
                            <img src='public/dataApi/img/process.png' />
                        </div>
                    </section>
                    {/* 右侧项目总信息 */}
                    <section className='c_right_section_wrapper'>
                        {/* 项目汇总 */}
                        <div className='c_top'>
                            <Row className='c_summary_project'>
                                <img src='public/dataApi/img/summary_project.png' />
                            </Row>
                            <Row>
                                <Card className='c_summary_project_card'>
                                    <Row gutter={16}>
                                        {this.renderTotalData(totalData, 'all')}
                                    </Row>
                                    <Row gutter={16}>
                                        {this.renderTotalData(recentData, 'recent')}
                                    </Row>
                                </Card>
                            </Row>
                        </div>
                        {/* 快速入门 */}
                        <div className='c_bottom'>
                            <Row className='c_summary_project'>
                                <img src='public/dataApi/img/quick_start.png' />
                            </Row>
                            <div>
                                <Row>
                                    <Card className='c_use_tutorial_card'>
                                        <Row gutter={24}>
                                            {this.loopOperaLink(OPERA_ROW_ONE_DATA)}
                                        </Row>
                                        <Row gutter={24}>
                                            {this.loopOperaLink(OPERA_ROW_TWO_DATA)}
                                        </Row>
                                    </Card>
                                </Row>
                                <Row>
                                    <Card className='c_use_tutorial_card c_video_width' style={{ position: 'relative' }} {...{ onClick: () => {
                                        const devEle = document.getElementById('c_developing');
                                        devEle.style.display = 'block';
                                        setTimeout(() => {
                                            devEle.style.display = 'none';
                                        }, 2000)
                                    } }}>
                                        <Row>
                                            <Col span={24} style={{ padding: 0 }}>
                                                {/* 暂时无视频，先用图片替代 */}
                                                <img src='public/dataApi/img/opera_guide.png' style={{ width: '380px', height: '240px' }} />
                                            </Col>
                                            <div className='c_developing' id='c_developing' style={{ display: 'none' }}>
                                                开发中～敬请期待！
                                            </div>
                                        </Row>
                                    </Card>
                                </Row>
                            </div>
                        </div>
                    </section>
                </main>
                <NewProjectModal
                    visible={visible}
                    onCancel={() => { this.setState({ visible: false }) }}
                />
            </div>
        )
    }
}
export default connect((state: any) => {
    return {
        user: state.user,
        projects: state.projects,
        projectListInfo: state.projectList,
        panelLoading: state.panelLoading,
        licenseApps: state.licenseApps
    }
})(withRouter(ProjectPanel))
