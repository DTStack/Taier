import React, { Component } from 'react'

import MrEditor from './mrEditor'
import SQLEditor from './sqlEditor';

import { TASK_TYPE } from '../../../../comm/const';

export default class RealtimeEditor extends Component {

    render() {
        const {
            currentPage
        } = this.props

        const showContent = currentPage.taskType === TASK_TYPE.SQL ?
            (<SQLEditor  {...this.props}/>)
            :
            (<MrEditor {...this.props}/>)

        return (
            <div className="editor-container">
                {showContent}
            </div>
        )
    }
}
