import React from "react";

import { Icon } from "antd";

class ExtraPaneTableDetail extends React.Component {

    render() {
        const { tableName } = this.props;
        return (
            <div className="extraPane-table-detail-box">
                <header>
                    <span className="table-name">{tableName}</span>
                    <Icon
                        className="close-icon"
                        type="close"
                        onClick={this.props.close.bind(null,tableName)}
                    />
                </header>
            </div>
        )
    }
}

export default ExtraPaneTableDetail;