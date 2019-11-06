import * as React from 'react';
import { Icon, Tooltip } from 'antd';
import * as Doc from './docs'

export default class HelpDoc extends React.Component<any, any> {
    render () {
        const { doc, style, overlayClassName } = this.props
        return (
            <Tooltip overlayClassName={overlayClassName} title={(Doc as any)[doc]}>
                <Icon className="help-doc" style={style} type="question-circle-o" />
            </Tooltip>
        )
    }
}
