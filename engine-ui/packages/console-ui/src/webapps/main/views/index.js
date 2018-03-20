import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { connect } from 'react-redux'

import utils from 'utils'
import NotFund from 'widgets/notFund'

import { getInitUser } from '../actions/user'

const propType = {
    children: PropTypes.node,
}
const defaultPro = {
    children: [],
}

@connect()
class Main extends Component {
    componentDidMount() {
        this.props.dispatch(getInitUser())
    }
    render() {
        return this.props.children || <NotFund />
    }
}
Main.propTypes = propType
Main.defaultProps = defaultPro

export default Main
