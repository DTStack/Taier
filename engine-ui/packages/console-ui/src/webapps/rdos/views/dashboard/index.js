import React, { Component } from 'react'
import { connect } from 'react-redux'
import moment from 'moment'

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
                <h1 className="box-title">
                    概况&nbsp;&nbsp;
                    <span style={{ fontSize: '12px', color: '#999999' }}>
                        截至{moment().format('YYYY-MM-DD')}
                    </span>
                </h1>
                <Overview {...this.props} />
                <h1 className="box-title">项目列表</h1>
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
