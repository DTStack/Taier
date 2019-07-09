import React from 'react';
import { Icon, Tooltip } from 'antd';
import * as Doc from './docs'

export default class HelpDoc extends React.Component {
    render () {
        const { doc } = this.props
        return (
            <Tooltip key={doc} title={Doc[doc]}>
                <Icon className="help-doc" {...this.props.style} type="question-circle-o" />
            </Tooltip>
        )
    }
}
