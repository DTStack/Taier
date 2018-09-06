import React, { Component } from 'react';

export default class LockPanel extends Component {
    render() {
        const { lockTarget } = this.props;
        const isLocked = lockTarget && lockTarget.readWriteLockVO && !lockTarget.readWriteLockVO.getLock;
        return isLocked ? <div className="cover-mask"></div>: null  
    }
}