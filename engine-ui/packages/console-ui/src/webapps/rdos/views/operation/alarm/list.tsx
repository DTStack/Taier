import * as React from 'react'
import OfflinePane from './offLineListPane'

export default class AlarmList extends React.Component<any, any> {
    render () {
        return (
            <div className="box-1 m-tabs">
                <OfflinePane />
            </div>
        )
    }
}
