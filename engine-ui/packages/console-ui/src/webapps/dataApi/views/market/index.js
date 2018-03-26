import React, { Component } from 'react'

class APIMarket extends Component {

    componentDidMount() { }

    render() {
        const { children } = this.props
        return (
            <div className="api-market">
                <div className="container">
                    <h1>APIMarket.</h1>
                </div>
            </div>
        )
    }
}

export default APIMarket
