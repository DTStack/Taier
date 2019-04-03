import React, { Component } from 'react';
import './style.scss';

const imgBase = 'public/analyticsEngine/img/icon';

export default class Icon extends Component {
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
