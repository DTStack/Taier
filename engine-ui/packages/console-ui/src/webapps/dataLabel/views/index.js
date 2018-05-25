import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { connect } from 'react-redux'

import utils from 'utils'
import { dlApp } from 'config/base'

import Header from './layout/header'
import TagMarket from '../views/market'

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
        dispatch(updateApp(dlApp));
        dispatch(commonActions.getUserList());
        dispatch(commonActions.getPeriodType());
        dispatch(commonActions.getNotifyType());
        dispatch(dataSourceActions.getDataSourcesType());
    }

    render() {
        const { children } = this.props
        return (
            <div className="main">
                <Header />
                <div className="container">
                    { children || <TagMarket /> }
                </div>
            </div>
        )
    }
}
Main.propTypes = propType
Main.defaultProps = defaultPro

export default Main
