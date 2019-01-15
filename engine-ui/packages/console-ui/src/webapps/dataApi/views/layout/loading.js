import React, { Component } from 'react'

export default class GlobalLoading extends Component {
    render () {
        return (
            <div className="laoding-wrapper" style={{ zIndex: 2005, position: 'fixed' }}>
                <div className="loading-center">
                    <h1 className="loading-title">{window.APP_CONF.prefix + ' ' + window.APP_CONF.loadingTitle}</h1>
                    <div className="bouncywrap">
                        <div className="dotcon dc1">
                            <div className="dot"></div>
                        </div>
                        <div className="dotcon dc2">
                            <div className="dot"></div>
                        </div>
                        <div className="dotcon dc3">
                            <div className="dot"></div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}
