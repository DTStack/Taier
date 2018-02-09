import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { connect } from 'react-redux'

import utils from 'utils'

import * as UserAction from '../actions/user'

const propType = {
    children: PropTypes.node,
}
const defaultPro = {
    children: [],
}

class Main extends Component {

    componentDidMount() {
        const { dispatch } = this.props
        // dispatch(UserAction.getUser())
    }

    render() {
        const { children } = this.props
        return  <div> { children } </div>
    }
}
Main.propTypes = propType
Main.defaultProps = defaultPro

export default Main
