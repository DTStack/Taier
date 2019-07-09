import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { connect } from 'react-redux'

import Header from './layout/header'

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
/* eslint-disable */
class Main extends Component {
    componentWillMount () {
        const { dispatch } = this.props;
    }
    componentDidMount () {
        const { dispatch } = this.props;
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
