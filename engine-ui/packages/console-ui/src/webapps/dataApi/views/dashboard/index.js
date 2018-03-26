import React, { Component } from 'react'

class Dashboard extends Component {

    componentDidMount() { }

    render() {
        const { children } = this.props
        return (
            <div className="dashboard">
                <div className="container">
                    <h1>Dashboard</h1>
                </div>
            </div>
        )
    }
}

export default Dashboard
