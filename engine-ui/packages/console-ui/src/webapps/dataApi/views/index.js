import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { connect } from 'react-redux'

import utils from 'utils'

import Header from './layout/header'
import * as UserAction from '../actions/user'

import { updateApp } from 'main/actions/app'
import { daApp } from 'config/base'

const propType = {
    children: PropTypes.node,
}
const defaultPro = {
    children: [],
}

@connect()
class Main extends Component {

    componentDidMount() {
        const { dispatch } = this.props;
        dispatch(UserAction.getUser())
        dispatch(updateApp(daApp));
        
    }

    render() {
        const { children } = this.props
        return (
            <div className="main">
                <Header />
                <div className="container">
                    { children || "i'm container." }
                </div>
            </div>
        )
    }
}
Main.propTypes = propType
Main.defaultProps = defaultPro

export default Main
