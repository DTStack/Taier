import * as React from 'react';
import { Icon } from 'antd';
import classnames from 'classnames';
import './style.scss';

interface Props {
    active?: boolean;
    expandIcon?: any;
    title?: string;
    extra?: any;
    children?: any;
    className?: string;
}
interface State {
    active: boolean;
}
export default class Collapse extends React.Component<Props, State> {
    state: State = {
        active: false
    }
    componentDidMount () {
        this.setState({
            active: this.props.active
        })
    }
    componentDidUpdate (preProps) {
        const { active } = this.props;
        if (active != preProps.active) {
            this.setState({
                active
            })
        }
    }
    onHandleActive = () => {
        this.setState({
            active: !this.state.active
        })
    }
    render () {
        const { title, extra, className, children } = this.props;
        const { active } = this.state;
        return (
            <div className={classnames('collapse', className)}>
                <div className="collapse_header">
                    <div><Icon className="icon_c" type={active ? 'minus-square-o' : 'plus-square-o'} onClick={this.onHandleActive} />{title}</div>
                    {
                        extra
                    }
                </div>
                <div className="collapse_content" style={{ display: active ? 'block' : 'none' }}>
                    {
                        children
                    }
                </div>

            </div>)
    }
}
