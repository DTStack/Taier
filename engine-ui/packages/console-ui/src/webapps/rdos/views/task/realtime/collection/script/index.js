import React from "react";
import utils from "utils";

import CommonEditor from "../../editor/commonEditor"

class CollectionScript extends React.Component {
    formatJson(text) {
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
    render() {
        return (
            <div>
                <CommonEditor
                    mode="json"
                    {...this.props}
                    onFormat={this.formatJson}
                />
            </div>
        )
    }
}

export default CollectionScript;