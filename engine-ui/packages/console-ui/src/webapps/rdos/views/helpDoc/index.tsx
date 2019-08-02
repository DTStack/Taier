import * as React from 'react';
import { Icon, Tooltip } from 'antd';
import * as Doc from './docs'

export const relativeStyle: any = {
    position: 'initial',
    right: 0,
    top: 0
}

export default class HelpDoc extends React.Component<any, any> {
    render () {
        const { doc, style } = this.props
        return doc ? (
            <Tooltip title={(Doc as any)[doc]}>
                <Icon className="help-doc" style={style} type="question-circle-o" />
            </Tooltip>
        ) : ''
    }
}
