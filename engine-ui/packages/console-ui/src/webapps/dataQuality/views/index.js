import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { connect } from 'react-redux'

import utils from 'utils'
import { dqApp } from 'config/base'

import Header from './layout/header'
import Dashboard from '../views/dashboard'

import { currentApp } from '../consts'
import * as UserAction from '../actions/user'
import { dataSourceActions } from '../actions/dataSource'
import { commonActions } from '../actions/common'
import { updateApp } from 'main/actions/app'

const propType = {
    children: PropTypes.node,
}
const defaultPro = {
    children: [],
}

@connect()
class Main extends Component {

    componentDidMount() {
        const { dispatch } = this.props
        dispatch(UserAction.getUser());
        dispatch(updateApp(dqApp));
        dispatch(commonActions.getUserList());
        dispatch(commonActions.getAllDict());
        dispatch(dataSourceActions.getDataSourcesType());
    }

    render() {
        const { children } = this.props
        return (
            <div className="main header-fixed container-relative">
                <Header/>
                <div className="container">
                    { children || <Dashboard /> }
                </div>
            </div>
        )
    }
}
Main.propTypes = propType
Main.defaultProps = defaultPro

export default Main
