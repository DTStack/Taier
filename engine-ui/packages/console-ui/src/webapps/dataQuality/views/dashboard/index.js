import React, { Component } from 'react'
import PropTypes from 'prop-types'

class Dashboard extends Component {

    componentDidMount() {
    }

    initProject() {
    }

    render() {
        const { children } = this.props
        return (
            <div className="dt-dcenter">
                <div className="container">
                    { children || "i'm container." }
                </div>
            </div>
        )
    }
}

export default Dashboard
