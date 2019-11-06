import * as React from 'react'
import './style.scss'

export function Button (props: any) {
    return (
        <div
            {...props}
            className={`rdos-button ${props.className}`}
        >
            {props.children || ''}
        </div>
    )
}
