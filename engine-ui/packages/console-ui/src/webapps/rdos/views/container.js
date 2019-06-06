import React, { Component } from 'react'
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
import { getTaskTypes } from '../store/modules/offlineTask/comm';
import { getTenantList } from '../store/modules/tenant';
import API from '../api';

const propType = {
    children: PropTypes.node
}
const defaultPro = {
    children: []
}

class Container extends Component {
    componentDidMount () {
        const { dispatch } = this.props
        dispatch(UserAction.getUser())
        dispatch(ProjectAction.getProjects())
        dispatch(ProjectAction.getAllProjects())
        dispatch(DataManageAction.getCatalogues({ isGetFile: false }))
        dispatch(getTaskTypes());
        dispatch(updateApp(rdosApp))
        dispatch(getTenantList());
        this.initProject();
        this.trackUser();
    }
    initProject () {
        const { dispatch, router } = this.props
        const pathname = router.location.pathname
        if (pathname !== '/') {
            const pid = parseInt(utils.getCookie('project_id'), 10)
            if (pid) {
                dispatch(ProjectAction.getProject(pid))
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
    componentWillReceiveProps (nextProps) {
        const nowId = nextProps.params.pid
        if (nowId && nowId !== this.props.params.pid) {
            this.props.dispatch(ProjectAction.getProject(nowId))
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
Container.propTypes = propType
Container.defaultProps = defaultPro

function mapStateToProps (state) {
    return {
        user: state.user,
        projects: state.projects,
        project: state.project,
        apps: state.apps,
        app: state.app
    }
}
export default connect(mapStateToProps)(Container)
