import * as React from 'react'
import { connect } from 'react-redux';
import MrEditor from './mrEditor'
import CodeEditor from './codeEditor';
import CommonEditor from './commonEditor';
import CollectionGuide from '../collection/guide'
import CollectionScript from '../collection/script'
import ConvertToScript from '../convertToScript';
import { TASK_TYPE, DATA_SYNC_TYPE } from '../../../../comm/const';

import utils from 'utils';

@(connect((state: any) => {
    const currentPage = state.realtimeTask.currentPage
    return {
        currentPage: currentPage
    }
}) as any)

export default class RealtimeEditor extends React.Component<any, any> {
    formatJson (text: any) {
        if (!text) {
            return text;
        }
        return new Promise(
            (resolve: any, reject: any) => {
                const formatText = utils.jsonFormat(text);
                if (!formatText) {
                    resolve(text)
                } else {
                    resolve(formatText)
                }
            }
        )
    }
    render () {
        const {
            currentPage
        } = this.props
        const isLocked = currentPage.readWriteLockVO && !currentPage.readWriteLockVO.getLock;
        let showContent: any;
        switch (currentPage.taskType) {
            case TASK_TYPE.SQL: {
                if (currentPage.createModel == DATA_SYNC_TYPE.GUIDE || currentPage.createModel == null) {
                    showContent = <CodeEditor {...this.props} key={currentPage.id} toolBarOptions={{
                        leftCustomButton: <ConvertToScript isLocked={isLocked} />
                    }} />
                } else if (currentPage.createModel == DATA_SYNC_TYPE.SCRIPT) {
                    showContent = <CommonEditor
                        mode="json"
                        {...this.props}
                        onFormat={this.formatJson}
                    />
                }
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
