import * as React from 'react';
import classnames from 'classnames';
import './style.scss';

interface IProps {
    option: {
        label: string;
        value: string|number;
    }[];
    value: string|number;
    onChange: (value: any) => void;
}

export class TagSelect extends React.Component<IProps, {}> {
    onHandleClick = (value) => {
        this.props.onChange(value == this.props.value ? '' : value)
    }
    render () {
        const { option = [], value } = this.props;
        return (<div className="tagSelect">
            {
                option.map((item: any) => <span key={item.value} onClick={() => this.onHandleClick(item.value)} className={classnames('normal', {
                    active: value == item.value
                })}>{item.label}</span>)
            }
        </div>)
    }
}
