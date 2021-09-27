import * as React from 'react'
declare var window: any;
export default class GlobalLoading extends React.Component<any, any> {
    render () {
        return (
            <div className="laoding-wrapper" style={{ zIndex: 2005, position: 'fixed' }}>
                <div className="loading-center">
                    <h1 className="loading-title">{window.APP_CONF.prefix} CONSOLE</h1>
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
