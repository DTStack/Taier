import React, { Component } from 'react'



export default class GlobalLoading extends Component {
    render() {
        return (
            <div className="laoding-wrapper" style={{zIndex:2000,position:"fixed"}}>

                <div className="loading-container">
                    <div className="loading"></div>
                    <div id="loading-text">loading</div>
                    <a href="http://www.dtstack.com" id="link">dtstack.com</a>
                </div>
            </div>
        )
    }
}