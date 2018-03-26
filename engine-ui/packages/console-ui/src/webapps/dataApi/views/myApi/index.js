import React, { Component } from 'react'

class MyAPI extends Component {

    componentDidMount() { }

    render() {
        const { children } = this.props
        return (
            <div className="api-mine">
                <div className="container">
                    <h1>MyAPI.</h1>
                </div>
            </div>
        )
    }
}

export default MyAPI
