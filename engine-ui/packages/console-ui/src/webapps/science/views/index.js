import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { connect } from 'react-redux'

import { scienceApp } from 'config/base'

import Header from './layout/header'

import { updateApp } from 'main/actions/app'
import { getProjectList, initCurrentProject } from '../actions/base'
import { getUser } from '../actions/user';
import { getSupportTaskTypes } from '../actions/task';
const propType = {
    children: PropTypes.node
}
const defaultPro = {
    children: []
}

@connect(state => {
    return {
        project: state.project
    }
})
class Main extends Component {
    componentDidMount () {
        const { dispatch } = this.props;
        dispatch(updateApp(scienceApp));
        dispatch(getUser())
        dispatch(getProjectList());
        dispatch(initCurrentProject())
        dispatch(getSupportTaskTypes())
    }

    render () {
        const { children, project } = this.props
        return (
            <div className="app-science main header-fixed">
                <Header/>
                <div key={project.currentProject && project.currentProject.id} className="container">
                    { children }
                </div>
            </div>
        )
    }
}
Main.propTypes = propType
Main.defaultProps = defaultPro

export default Main
