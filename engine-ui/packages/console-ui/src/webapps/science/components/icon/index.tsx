import * as React from 'react';
import './style.scss';

const imgBase = 'public/science/img/icon';

export default class Icon extends React.Component<any, any> {
    render () {
        const props = this.props;
        return (
            <img
                {...props}
                className={`my-icon ${props.className || ''}`}
                alt={props.alt}
                src={`${imgBase}/${props.type}.svg`}
            />
        );
    }
}
