import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { connect } from 'react-redux'

import utils from 'utils'
import NotFund from 'widgets/notFund'

const propType = {
    children: PropTypes.node,
}
const defaultPro = {
    children: [],
}

class Main extends Component {
    render() {
        return this.props.children || <NotFund />
    }
}
Main.propTypes = propType
Main.defaultProps = defaultPro

export default Main
