import * as React from 'react';
import './style.scss';

interface IProps {
    title: string;
    extra?: any;
    style?: any;
}

export default class ModuleTitle extends React.Component<IProps, any> {
    state: any = {

    }

    componentDidMount () {

    }

    render () {
        const { title, extra, style = {} } = this.props;
        return (
            <div className="module-title" style={style}>
                <div className="title">{title}</div>
                <div className="extra">{extra}</div>
            </div>
        )
    }
}
