import * as React from 'react'

import MrEditor from './mrEditor'
import CodeEditor from './codeEditor';
import CollectionGuide from '../collection/guide'
import CollectionScript from '../collection/script'

import { TASK_TYPE, DATA_SYNC_TYPE } from '../../../../comm/const';

export default class RealtimeEditor extends React.Component<any, any> {
    render () {
        const {
            currentPage
        } = this.props

        let showContent: any;
        switch (currentPage.taskType) {
            case TASK_TYPE.SQL: {
                showContent = <CodeEditor {...this.props} />
                break;
            }
            case TASK_TYPE.DATA_COLLECTION: {
                if (currentPage.createModel == DATA_SYNC_TYPE.GUIDE) {
                    showContent = <CollectionGuide key={currentPage.id} {...this.props} />
                } else if (currentPage.createModel == DATA_SYNC_TYPE.SCRIPT) {
                    showContent = <CollectionScript {...this.props} />
                }
                break;
            }
            case TASK_TYPE.MR:
            default: {
                showContent = <MrEditor {...this.props} />
                break;
            }
        }
        return (
            <div className="editor-container">
                {showContent}
            </div>
        )
    }
}
