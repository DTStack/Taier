import * as React from 'react';
import { Input, message } from 'antd';
interface IProps {
    value?: string | number;
    onChange?: Function;
    max?: number;
    [propName: string]: any;
}

interface IState {
    value: string | number;
}
export default class EditInput extends React.PureComponent<IProps, IState> {
    constructor (props: IProps) {
        super(props);
    }
    input: any
    state: IState = {
        value: ''
    }
    componentDidMount () {
        this.setState({
            value: this.props.value
        })
    }
    componentDidUpdate (preProps: any) {
        const { value } = this.props;
        if (value != preProps.value) {
            this.setState({
                value
            })
        }
    }
    onChangeInput = (e: any) => {
        this.props.onChange(e)
    }
    onChangeValue = (e: any) => {
        let value = e.target.value;
        const { max } = this.props;
        if (value && max && value.length > max) {
            message.warning(`字符长度不可超过${max}!`);
        } else {
            this.setState({
                value
            })
        }
    }
    render () {
        const { value } = this.state;
        return (
            <Input className="input" {...this.props} value={value} onChange={this.onChangeValue} onBlur={this.onChangeInput} />
        )
    }
}
