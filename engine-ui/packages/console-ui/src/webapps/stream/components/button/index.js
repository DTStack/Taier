import React from 'react'
import './style.scss'

export function Button (props) {
    return (
        <div
            {...props}
            className={`rdos-button ${props.className}`}
        >
            {props.children || ''}
        </div>
    )
}
