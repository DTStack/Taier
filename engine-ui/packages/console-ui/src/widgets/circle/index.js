import React from 'react'
import './style.scss'

export function Circle (props) {
    const defaultClass = 'circle_default';
    const { className, ...other } = props;
    return (
        <div
            className={`${defaultClass} ${className} `}
            {...other}
        >
            {props.children || ''}
        </div>
    )
}
