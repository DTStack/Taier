import React, { Component } from 'react'

class APIApproval extends Component {

    componentDidMount() { }

    render() {
        const { children } = this.props
        return (
            <div className="api-approval">
                <div className="container">
                    <h1>APIApproval.</h1>
                </div>
            </div>
        )
    }
}

export default APIApproval
