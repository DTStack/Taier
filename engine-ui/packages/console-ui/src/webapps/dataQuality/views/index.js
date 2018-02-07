import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { connect } from 'react-redux'

import utils from 'utils'

import Header from './layout/header'
import * as UserAction from '../actions/user'

const propType = {
    children: PropTypes.node,
}
const defaultPro = {
    children: [],
}

class Container extends Component {

    componentDidMount() {
        const { dispatch } = this.props
        // dispatch(UserAction.getUser())
    }

    render() {
        const { children } = this.props
        return (
            <div className="main">
                <Header showMenu {...this.props} />
                <div className="container">
                    { children || "i'm container." }
                </div>
            </div>
        )
    }
}
Container.propTypes = propType
Container.defaultProps = defaultPro

function mapStateToProps(state) {
    return {
        user: state.user,
    }
}
export default connect(mapStateToProps)(Container)
