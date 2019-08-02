import * as React from 'react';
import { Icon, Tooltip } from 'antd';
import * as Doc from './docs'

export default class HelpDoc extends React.Component<any, any> {
    render () {
        const { doc } = this.props
        return (
            <Tooltip key={doc} title={(Doc as any)[doc]}>
                <Icon className="help-doc" {...this.props.style} type="question-circle-o" />
            </Tooltip>
        )
    }
}
