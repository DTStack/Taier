import React, { Component } from 'react'
import { connect } from 'react-redux'
import moment from 'moment'
import { Checkbox } from 'antd'

import Api from '../../../api'
import Overview from './overview'

export default class Index extends Component {
    state = {
        isAdmin: false,
        projects: []
    }

    componentDidMount () {
        this.getAllProjects()
    }

    onChange = (e) => {
        this.setState({ total: !e.target.checked }, this.getAllProjects)
    }

    getAllProjects () {
        const { total } = this.state;
        Api.getAllProjects({ total }).then((res) => {
            if (res.code == 1) {
                this.setState({
                    projects: res.data
                })
            }
        })
    }

    render () {
        const { projects, total } = this.state;
        return (
            <div className="project-dashboard">
                <h1 className="box-title">
                    数据资产概况&nbsp;&nbsp;&nbsp;&nbsp;
                    <span style={{ fontSize: '12px', color: '#999999' }}>
                        (当前租户内的数据,截至{moment().format('YYYY-MM-DD')})
                    </span>&nbsp;&nbsp;&nbsp;&nbsp;
                    <Checkbox onChange={this.onChange}>只看我参与的项目</Checkbox>
                </h1>
                <Overview {...this.props} projects = {projects} total={total}/>
            </div>
        )
    }
}
