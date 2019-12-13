import * as React from 'react'

import './style.scss'

export function Warning (props: any) {
    return (
        <span className="font-warning">
            { props.children }
        </span>
    )
}

export function Normal (props: any) {
    return (
        <span className="font-normal">
            { props.children }
        </span>
    )
}
