import React, { Component } from 'react';

class BenchContent extends Component {
    renderContent () {
        return 11;
    }

    render () {
        return (
            <div className="m-content">
                {this.renderContent()}
            </div>
        )
    }
}

export default BenchContent
