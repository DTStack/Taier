import React from 'react';
import { Icon, Tooltip } from 'antd';
import * as Doc from './docs'

export const relativeStyle = {
    position: 'initial',
    right: 0,
    top: 0,
}

export default class HelpDoc extends React.Component {
    render() {
        const { doc, style } = this.props
        return (
            <Tooltip title={Doc[doc]}>
                <Icon className="help-doc" style={style} type="question-circle-o" />
            </Tooltip>
        )
    }
}