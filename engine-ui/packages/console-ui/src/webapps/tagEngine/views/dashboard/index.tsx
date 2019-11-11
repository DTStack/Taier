import * as React from 'react'
import { connect } from 'react-redux'
import { Link, hashHistory } from 'react-router';

import moment from 'moment'

import { Input, Card, Row, Col, Tooltip, Icon, Button, Pagination, message, Spin } from 'antd'

import ProjectForm from '../project/form'
import { Circle } from 'widgets/circle'
import Api from '../../api'
import * as ProjectAction from '../../reducers/modules/project';
import NoData from '../../components/no-data';
import { PROJECT_STATUS, TASK_STATUS } from '../../comm/const';
import './style.scss';

const Search = Input.Search;

class Index extends React.Component<any, any> {
    _isUnmounted: any;
    _timeClock: any;
    state: any = {
        visible: false,
        loading: true,
        projectListInfo: [],
        sortTitleStatus: 1,
        totalSize: undefined,
        projectListParams: {
            fuzzyName: undefined,
            page: 1,
            pageSize: 9,
            orderBy: undefined
        }
    }

    componentDidMount () {
        this.getProjectListInfo();
    }
    componentWillUnmount () {
        this._isUnmounted = true;
        clearTimeout(this._timeClock);
    }
    debounceGetProList () {
        if (this._isUnmounted) {
            return;
        }
        this._timeClock = setTimeout(() => {
            this.getProjectListInfo(null, true);
        }, 3000);
    }
    setCard = (data: any) => {
        if (data.status == PROJECT_STATUS.DISABLE || data.status == PROJECT_STATUS.FAIL) { // "删除项目"
            Api.deleteProject({ projectId: data.id }).then((v: any) => {
                if (v.code == 1) {
                    message.success('删除项目成功！');
                    this.getProjectListInfo();
                }
            })
        } else if (data.stickStatus == 0) { // "置顶"
            Api.setSticky({ appointProjectId: data.id, stickStatus: 1 }).then((v: any) => {
                if (v.code == 1) {
                    message.success('置顶成功！');
                    this.getProjectListInfo();
                }
            })
        } else if (data.stickStatus == 1) { // "取消置顶"
            Api.setSticky({ appointProjectId: data.id, stickStatus: 0 }).then((v: any) => {
                if (v.code == 1) {
                    message.success('取消置顶成功！');
                    this.getProjectListInfo();
                }
            })
        }
    }

    getProjectListInfo = (params?: any, isSilent?: any) => {
        const { projectListParams } = this.state;
        const queryParsms: any = { ...projectListParams, ...params };
        if (!isSilent) {
            this.setState({
                loading: true
            })
        }
        clearTimeout(this._timeClock);
        Api.getProjectListInfo(queryParsms).then((res: any) => {
            if (res.code === 1) {
                if (res.data && res.data.data) {
                    for (let project of res.data.data) {
                        if (project.status == 0) {
                            this.debounceGetProList();
                            break;
                        }
                    }
                }
                this.setState({
                    projectListInfo: (res.data && res.data.data) || [],
                    totalSize: (res.data && res.data.totalCount) || 0,
                    projectListParams: queryParsms,
                    loading: false
                })
            } else {
                this.setState({
                    loading: false
                })
            }
        })
    }

    createProject = (project: any) => {
        const { dispatch } = this.props;
        Api.createProject(project).then((res: any) => {
            if (res.code === 1) {
                this.setState({ visible: false });
                this.getProjectListInfo();
                dispatch(ProjectAction.getProjects());
                message.success('创建项目成功！');
            }
        })
    }

    changePage = (page: any) => {
        const params: any = { page: page }
        this.getProjectListInfo(params)
    }

    setRouter = (type: any, v: any, filterError?: any) => {
        let src: any;
        const { dispatch } = this.props;
        if (type === 'operation') {
            src = '/operation'
        } else { // realtime
            src = '/realtime'
        }
        dispatch(ProjectAction.getProject(v.id));
        hashHistory.push({
            pathname: src,
            state: {
                statusList: filterError ? ['' + TASK_STATUS.RUN_FAILED, '' + TASK_STATUS.SUBMIT_FAILED] : undefined
            }
        })
    }

    fixArrayIndex = (arr: any) => {
        let fixArrChildrenApps: any = [];
        if (arr && arr.length > 1) {
            arr.map((item: any) => {
                switch (item.name) {
                    case '数据源':
                        fixArrChildrenApps[0] = item;
                        break;
                    case '数据开发':
                        fixArrChildrenApps[1] = item;
                        break;
                    case '任务运维':
                        fixArrChildrenApps[2] = item;
                        break;
                    case '项目管理':
                        fixArrChildrenApps[3] = item;
                        break;
                }
            })
            return fixArrChildrenApps
        } else {
            return []
        }
    }

    generalTitle = (data: any) => {
        const deleteImg = <img className="tooltip-img" src="/public/stream/img/delete.svg" />;
        const setTopImg = <img className="tooltip-img setTopImg" src="/public/stream/img/cancel-top.svg" />;
        const cancelTop = <span className="cancel-top">取消置顶</span>;
        const tooltipTittle = <div>
            {
                data.status == PROJECT_STATUS.DISABLE || data.status == PROJECT_STATUS.FAIL ? '删除项目' : '置顶'
            }
        </div>
        const tooltipImg = <div onClick={() => { this.setCard(data) }}>
            {
                (data.status != PROJECT_STATUS.DISABLE || data.status != PROJECT_STATUS.FAIL) && data.stickStatus == 1 // 取消置顶非图标,不需要Tooltip提示,过滤掉
                    ? cancelTop
                    : <Tooltip title={tooltipTittle} mouseEnterDelay={0.5}>
                        {
                            data.status == PROJECT_STATUS.DISABLE || data.status == PROJECT_STATUS.FAIL ? deleteImg : setTopImg
                        }
                    </Tooltip>
            }
        </div>
        const title = <div>
            <Row>
                <Col span={20} >
                    {data.status == PROJECT_STATUS.NORMAL ? (
                        <Link to={`/entityManage?projectId=${data.id}`}>
                            <span className="company-name" onClick={() => { this.setRouter('operation', data) }}>
                                {data.projectAlias}&nbsp;&nbsp;
                            </span>
                        </Link>
                    ) : (<span className="company-name no-hover">
                        {data.projectAlias}&nbsp;&nbsp;
                    </span>)}
                    {this.renderTitleText(data)}
                </Col>
                <Col span={4}>
                    {tooltipImg}
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
    changeSort = (v: any) => {
        const status = v === 'defaultSort' ? 1 : 2;
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
                    this.getProjectListInfo({ orderBy: 'jobSum' })
                })
            }
        }
    }

    searchProject = (v: any, value: any) => {
        if (v) {
            this.getProjectListInfo({ fuzzyName: v.target.value || undefined, page: 1 })
        } else {
            this.getProjectListInfo({ fuzzyName: value, page: 1 })
        }
    }

    handleMouseOver = (type: any, e: any) => {
        if (type == 'operation') {
            e.currentTarget.getElementsByTagName('img')[0].src = '/public/stream/img/icon/operation2.svg'
        } else {
            e.currentTarget.getElementsByTagName('img')[0].src = '/public/stream/img/icon/realtime2.svg'
        }
    }

    handleMouseOut = (type: any, e: any) => {
        if (type == 'operation') {
            e.currentTarget.getElementsByTagName('img')[0].src = '/public/stream/img/icon/operation.svg'
        } else {
            e.currentTarget.getElementsByTagName('img')[0].src = '/public/stream/img/icon/realtime.svg'
        }
    }

    render () {
        const { visible, projectListInfo, sortTitleStatus, totalSize, projectListParams, loading } = this.state;
        const { licenseApps } = this.props;
        const fixArrChildrenApps = this.fixArrayIndex(licenseApps[1] && licenseApps[1].children);
        const taskNav = fixArrChildrenApps[1];
        const operaNav = fixArrChildrenApps[2];
        return (
            <Spin tip="Loading..." spinning={loading} delay={500} >
                <div className="project-dashboard develop-kit" style={{ padding: '20px 35px' }}>
                    <div className="project-header" >
                        <div className="project-search-box">
                            <div className="project-search" >
                                <Search placeholder="按项目名称、项目显示名称搜索" onSearch={(value: any) => this.searchProject(null, value)} onPressEnter={this.searchProject as any} />
                            </div>
                            <Button
                                type="primary"
                                onClick={() => { this.setState({ visible: true }) }}>
                                创建项目
                            </Button>
                        </div>
                        <div className="sortTitle">
                            <span className="sort-item" style={sortTitleStatus == 1 ? { color: '#2491F7' } : {}} onClick={() => { this.changeSort('defaultSort') }}>默认排序</span>
                            <span className="sort-item">|</span>
                            <span className="sort-item" style={sortTitleStatus == 2 ? { color: '#2491F7' } : {}} onClick={() => { this.changeSort('faileSort') }}>按任务失败数排序</span>
                        </div>
                    </div>
                    <Row >
                        <Col span={24} >
                            <Row>
                                {
                                    projectListInfo && projectListInfo.length === 0 && !loading ? <NoData /> : ''
                                }
                                {
                                    projectListInfo && projectListInfo.map((v: any) => {
                                        const { taskCountMap } = v;
                                        const cardFix = {
                                            onClick: () => { this.setRouter('realtime', v) },
                                            onMouseOver: (e: any) => { this.handleMouseOver('realtime', e) },
                                            onMouseOut: (e: any) => { this.handleMouseOut('realtime', e) }
                                        }
                                        const cardFix1 = {
                                            onClick: () => { this.setRouter('operation', v) },
                                            onMouseOver: (e: any) => { this.handleMouseOver('operation', e) },
                                            onMouseOut: (e: any) => { this.handleMouseOut('operation', e) }
                                        }
                                        return <Col span={8} className="card-width" key={v.id} style={{ padding: 0 }}>
                                            <Card className="general-card" title={this.generalTitle(v)} noHovering bordered={false}>
                                                <Row className="card-content" >
                                                    <Col span={17}>
                                                        <div className="statistics" >已提交/总任务数： <span className="statistics-info">{`${taskCountMap.submitCount} / ${taskCountMap.allCount}`}</span></div>
                                                        <div className="statistics" >项目创建时间： <span className="statistics-info">{moment(v.gmtCreate).format('YYYY-MM-DD HH:mm:ss')}</span></div>
                                                        <div className="statistics" >
                                                            <Circle style={{ background: '#00A755', marginRight: '5px' }} />运行中任务：
                                                            <span className="statistics-info">{taskCountMap.runningCount}</span>
                                                        </div>
                                                        <div className="statistics" >
                                                            <Circle style={{ background: '#F5A623', marginRight: '5px' }} />停止/取消：
                                                            <span className="statistics-info">{taskCountMap.cancelCount}</span>
                                                        </div>
                                                    </Col>
                                                    <Col span={7}>
                                                        <div style={{ fontSize: 12, lineHeight: '26px' }}>任务失败数</div>
                                                        {v.status != PROJECT_STATUS.NORMAL ? (
                                                            <div className="number no-hover">
                                                                {
                                                                    taskCountMap.failCount ? <span>{taskCountMap.failCount}</span>
                                                                        : <span style={{ color: '#999' }}>{taskCountMap.failCount || 0}</span>
                                                                }
                                                            </div>
                                                        ) : (<div className="number" onClick={() => { this.setRouter('operation', v, true) }}>
                                                            {
                                                                taskCountMap.failCount ? <span>{taskCountMap.failCount}</span>
                                                                    : <span style={{ color: '#999' }}>{taskCountMap.failCount || 0}</span>
                                                            }
                                                        </div>)
                                                        }
                                                    </Col>
                                                    <Col span={24} className="card-task-padding">
                                                        <Row>
                                                            {
                                                                v.status != PROJECT_STATUS.NORMAL || (taskNav && !taskNav.isShow) ? '' : (
                                                                    <Col span={12}>
                                                                        <Card className="card-task"
                                                                            style={{ marginRight: '6px' }}
                                                                            // onClick={() => { this.setRouter('realtime', v) }}
                                                                            // onMouseOver={(e: any) => { this.handleMouseOver('realtime', e) }}
                                                                            // onMouseOut={(e: any) => { this.handleMouseOut('realtime', e) }}
                                                                            noHovering
                                                                            {...cardFix}
                                                                        >
                                                                            <span className="img-container">
                                                                                <img className="task-img" src="/public/stream/img/icon/realtime.svg" />
                                                                            </span>
                                                                            数据开发
                                                                        </Card>
                                                                    </Col >
                                                                )
                                                            }
                                                            {
                                                                v.status != PROJECT_STATUS.NORMAL || (operaNav && !operaNav.isShow) ? '' : (
                                                                    <Col span={12}>
                                                                        <Card className="card-task"
                                                                            style={{ marginLeft: '6px' }}
                                                                            // onClick={() => { this.setRouter('operation', v) }}
                                                                            // onMouseOver={(e: any) => { this.handleMouseOver('operation', e) }}
                                                                            // onMouseOut={(e: any) => { this.handleMouseOut('operation', e) }}
                                                                            noHovering
                                                                            {...cardFix1}
                                                                        >
                                                                            <span className="img-container">
                                                                                <img className="task-img" src="/public/stream/img/icon/operation.svg" />
                                                                            </span>
                                                                            运维中心
                                                                        </Card>
                                                                    </Col>
                                                                )
                                                            }
                                                        </Row>
                                                    </Col>
                                                </Row>
                                                {
                                                    v.stickStatus == 1 ? <div className="triangle_border_right">
                                                        <span></span>
                                                    </div> : ''
                                                }
                                            </Card>
                                        </Col>
                                    })
                                }
                            </Row>
                            <Row>
                                <Col >
                                    <div style={{ float: 'right' }}>
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
export default connect((state: any) => {
    return {
        user: state.user,
        projects: state.projects,
        licenseApps: state.licenseApps
    }
})(Index)
