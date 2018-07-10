import React, { Component } from 'react'
import { connect } from 'react-redux'
import moment from 'moment'
import { Checkbox } from "antd" 
import { setProject } from '../../../store/modules/project'

import Overview from './overview'

class Index extends Component {

    state = {
        isAdmin: false
    }

    componentDidMount() {
<<<<<<< HEAD
        // this.props.dispatch(setProject({ id: 0 }))
=======
        this.props.dispatch(setProject({ id: 0 }))
>>>>>>> 9a1c481802d969752135b5c9294087be804e03a1
    }

    onChange = (e) => {
        this.setState({total: e.target.checked})
      }

    render() {
        return (
            <div className="project-dashboard">
                <h1 className="box-title">
                    数据资产概况&nbsp;&nbsp;&nbsp;&nbsp;
                    <span style={{ fontSize: '12px', color: '#999999' }}>
                        (当前租户内的数据,截至{moment().format('YYYY-MM-DD')})
                    </span>&nbsp;&nbsp;&nbsp;&nbsp;
                    <Checkbox onChange={this.onChange}>只看我参与的项</Checkbox>
                </h1>
                <Overview {...this.props} total={!this.state.total}/>
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
