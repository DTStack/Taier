import React, { Component } from 'react'

class APIMana extends Component {

    componentDidMount() { }

    render() {
        const { children } = this.props
        return (
            <div className="api-management">
                <div className="container">
                    <h1>APIMana.</h1>
                </div>
            </div>
        )
    }
}

export default APIMana
