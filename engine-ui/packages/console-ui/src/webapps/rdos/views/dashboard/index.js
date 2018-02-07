import React, { Component } from 'react'
import { connect } from 'react-redux'

import { setProject } from '../../store/modules/project'

import ProjectList from './projectList'
import Overview from './overview'

class Index extends Component {

    componentDidMount() {
        this.props.dispatch(setProject({ id: -1 }))
    }

    render() {
        return (
            <div className="project-dashboard">
                <Overview {...this.props} />
                <ProjectList {...this.props} />
            </div>
        )
    }
}
export default connect((state) => {
    return {
        user: state.user,
        projects: state.projects,
    }
})(Index)
