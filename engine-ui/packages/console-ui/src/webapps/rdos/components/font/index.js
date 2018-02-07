import React from 'react'

import './style.scss'

export function Warning(props) {
    return (
        <span className="font-warning">
            { props.children }
        </span>
    )
}

export function Normal(props) {
    return (
        <span className="font-normal">
            { props.children }
        </span>
    )
}
