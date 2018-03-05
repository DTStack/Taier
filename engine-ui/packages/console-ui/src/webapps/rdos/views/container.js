import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { connect } from 'react-redux'

import utils from 'utils'

import Header from './layout/header'
import Dashboard from '../views/dashboard'
import * as ProjectAction from '../store/modules/project'
import * as UserAction from '../store/modules/user'

const propType = {
    children: PropTypes.node,
}
const defaultPro = {
    children: [],
}

class Container extends Component {

    componentDidMount() {
        const { dispatch } = this.props
        dispatch(UserAction.getUser())
        dispatch(ProjectAction.getProjects())
        this.initProject()
    }

    initProject() {
        const { dispatch, router } = this.props
        const pathname = router.location.pathname
        if (pathname !== '/') {
            const pid = parseInt(utils.getCookie('project_id'), 10)
            if (pid) {
                dispatch(ProjectAction.getProject(pid))
            }
        }
    }

    componentWillReceiveProps(nextProps) {
        const nowId = nextProps.params.pid
        if (nowId && nowId !== this.props.params.pid) {
            this.props.dispatch(ProjectAction.getProject(nowId))
        }
    }

    render() {
        const { children } = this.props
        return (
            <div className="dt-dev-tools">
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

function mapStateToProps(state) {
    return {
        user: state.user,
        projects: state.projects,
        project: state.project,
        apps: state.apps,
    }
}
export default connect(mapStateToProps)(Container)
