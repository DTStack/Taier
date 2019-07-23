import React from 'react';
import utils from 'utils';

import CommonEditor from '../../editor/commonEditor'
import Toolbar from './toolBar'

class CollectionScript extends React.Component {
    formatJson (text) {
        if (!text) {
            return text;
        }
        return new Promise(
            (resolve, reject) => {
                const formatText = utils.jsonFormat(text);
                if (!formatText) {
                    resolve(text)
                } else {
                    resolve(formatText)
                }
            }
        )
    }
    getLeftButton () {
        return <Toolbar {...this.props} />
    }
    render () {
        return (
            <CommonEditor
                mode="json"
                {...this.props}
                onFormat={this.formatJson}
                toolBarOptions={{
                    leftCustomButton: this.getLeftButton()
                }}
            />
        )
    }
}

export default CollectionScript;
