import * as React from 'react'
import { connect } from 'react-redux'
import { Spin } from 'antd'

import {
    workbenchActions
} from '../../../store/modules/offlineTask/offlineAction'

@(connect(null, (dispatch: any) as any) => {
    const actions = workbenchActions(dispatch)
    return {
        goToTaskDev: (id: any) => {
            actions.openTaskInDev(id)
        }
    }
})
class OpenOfflineTask extends React.Component<any, any> {
    componentDidMount () {
        const { params, goToTaskDev } = this.props
        console.log('OpenOfflineTask:', this.props)
        const taskId = params.taskId;
        if (taskId) goToTaskDev(taskId)
    }

    render () {
        return (
            <div className="absolute-middle txt-center" style={{ width: 50, height: 50 }}>
                <Spin
                    tip="Loading..."
                    size="large"
                    spinning={'loading'}
                />
            </div>
        )
    }
}

export default OpenOfflineTask
