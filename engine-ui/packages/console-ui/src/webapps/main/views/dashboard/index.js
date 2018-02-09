import React, { Component } from 'react'
import { connect } from 'react-redux'

import Header from '../layout/header'

class Dashboard extends Component {

    componentDidMount() {
    }

    render() {
        const { children } = this.props
        return (
            <div className="dashboard">
                <Header />
                <div className="container">
                    { children || "dashboard" }
                </div>
            </div>
        )
    }
}

export default Dashboard
