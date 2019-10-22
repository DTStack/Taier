import * as React from 'react'
import PropTypes from 'prop-types'
import { connect } from 'react-redux'

import utils from 'utils'

import { rdosApp } from 'config/base'
import { updateApp } from 'main/actions/app'

import Header from './layout/header'
import Dashboard from '../views/dashboard'
import * as ProjectAction from '../store/modules/project'
import * as UserAction from '../store/modules/user'
import DataManageAction from '../store/modules/dataManage/actionCreator';
import { getTaskTypes, getScriptTypes } from '../store/modules/offlineTask/comm';
import { getTenantList } from '../store/modules/tenant';
import API from '../api';

const propType: any = {
    children: PropTypes.node
}
const defaultPro: any = {
    children: []
}

class Container extends React.Component<any, any> {
    static propTypes = propType
    static defaultProps = defaultPro
    componentDidMount () {
        const { dispatch } = this.props
        dispatch(UserAction.getUser())
        dispatch(ProjectAction.getProjects())
        dispatch(ProjectAction.getAllProjects())
        dispatch(ProjectAction.getTenantProjects())
        dispatch(ProjectAction.getProjectSupportEngine())
        dispatch(DataManageAction.getCatalogues({ isGetFile: false }))
        dispatch(getTaskTypes());
        dispatch(getScriptTypes())
        dispatch(updateApp(rdosApp))
        dispatch(getTenantList());
        this.initProject();
        this.trackUser();
    }
    initProject () {
        const { dispatch, router } = this.props
        const pathname = router.location.pathname
        if (pathname !== '/') {
            let pid = '';
            const projectIdFromURL = utils.getParameterByName('pid');
            const projectIdFromCookie = utils.getCookie('project_id');
            if (projectIdFromURL) { // 优先从URL截取项目ID, 后从 Cookie 获取
                pid = projectIdFromURL;
            } else if (projectIdFromCookie) {
                pid = projectIdFromCookie;
            }
            if (pid) {
                dispatch(ProjectAction.getProject(parseInt(pid, 10)))
            }
        }
    }

    trackUser () {
        // 跟踪用户
        API.trackUserActions(rdosApp.id, {
            action: 'visit',
            target: rdosApp.id
        })
    }

    // eslint-disable-next-line
    componentWillReceiveProps(nextProps: any) {
        const nowId = nextProps.params.pid;
        const project = nextProps.project
        const oldProj = this.props.project
        if (nowId && nowId !== this.props.params.pid) {
            this.props.dispatch(ProjectAction.getProject(nowId))
        }
        if (oldProj && project && oldProj.id !== project.id) {
            this.props.dispatch(getTaskTypes())
            this.props.dispatch(getScriptTypes())
            this.props.dispatch(ProjectAction.getProjectSupportEngine())
        }
    }

    render () {
        const { children } = this.props
        return (
            <div className="dt-dev-tools" id="JS_APP">
                <Header showMenu {...this.props} />
                <div className="container">
                    { children || <Dashboard /> }
                </div>
            </div>
        )
    }
}

function mapStateToProps (state: any) {
    return {
        user: state.user,
        projects: state.projects,
        project: state.project,
        apps: state.apps,
        app: state.app
    }
}
export default connect(mapStateToProps)(Container)
