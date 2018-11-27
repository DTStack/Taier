import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { connect } from 'react-redux'

import utils from 'utils'

import Header from './layout/header'

import * as UserAction from '../actions/user'

import { updateApp } from 'main/actions/app'
import { consoleApp } from 'config/base'

const propType = {
    children: PropTypes.node
}
const defaultPro = {
    children: []
}
const mapStateToProps = state => {
    const { common } = state;
    return { common }
};
@connect(
    mapStateToProps
)
class Main extends Component {
    componentWillMount () {
        const { dispatch } = this.props;
    }
    componentDidMount () {
        const { dispatch } = this.props;
        // dispatch(UserAction.getUser())
        dispatch(updateApp(consoleApp));
    }

    render () {
        let { children } = this.props
        let header = <Header />
        return (
            <div className="main">

                {header}
                <div className="container overflow-x-hidden">
                    { children || '加载中....' }
                </div>
            </div>
        )
    }
}
Main.propTypes = propType
Main.defaultProps = defaultPro

export default Main
