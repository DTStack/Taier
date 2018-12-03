import React, { Component } from 'react'
import OfflinePane from './offlineConfigPane'

export default class AlarmList extends Component {
    render () {
        return (
            <div className="box-1 m-tabs">
                <OfflinePane />
            </div>
        )
    }
}
