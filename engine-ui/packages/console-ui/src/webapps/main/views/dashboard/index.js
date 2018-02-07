import React, { Component } from 'react'
import PropTypes from 'prop-types'

class Dashboard extends Component {

    componentDidMount() {
    }

    render() {
        const { children } = this.props
        return (
            <div className="dashboard">
                { children || "dashboard" }
            </div>
        )
    }
}

export default Dashboard
