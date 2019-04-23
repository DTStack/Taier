import React from 'react';
import { Icon, Tooltip } from 'antd';
import * as Doc from './docs';

import './style.scss';

export const relativeStyle = {
    position: 'initial',
    right: 0,
    top: 0
}

export default class HelpDoc extends React.Component {
    render () {
        const { doc, ...others } = this.props
        return (
            <Tooltip key={doc} title={Doc[doc]}>
                <Icon className="help-doc" {...others} type="question-circle-o" />
            </Tooltip>
        )
    }
}
