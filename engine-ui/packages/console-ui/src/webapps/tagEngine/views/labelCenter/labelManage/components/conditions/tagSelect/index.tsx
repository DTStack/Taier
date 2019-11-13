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
    onHandleClick = (e: any) => {
        const target = e.target;
        if (target.getAttribute('class') == 'normal') {
            const data = target.getAttribute('ref');
            const value = JSON.parse(data);
            this.props.onChange(value)
        }
    }
    render () {
        const { option = [], value } = this.props;
        return (<div className="tagSelect" onClick={this.onHandleClick}>
            {
                option.map((item: any) => <span key={item.value} ref={ JSON.stringify(item) } className={classnames('normal', {
                    active: value == item.value
                })}>{item.label}</span>)
            }
        </div>)
    }
}
