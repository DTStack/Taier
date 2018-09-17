import React, { Component } from 'react';

export default class LockPanel extends Component {
    render() {
        const { lockTarget, couldEdit } = this.props;
        const isLocked = lockTarget && lockTarget.readWriteLockVO && !lockTarget.readWriteLockVO.getLock;
        return isLocked||(!couldEdit&&typeof couldEdit=="boolean") ? <div className="cover-mask"></div>: null  
    }
}