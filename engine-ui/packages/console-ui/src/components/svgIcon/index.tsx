import * as React from 'react'
import './style.scss'

export default class SvgIcon extends React.Component<any, any> {
    render () {
        const props = this.props
        return <svg
            { ...props }
            className={`icon-svg default-icon ${props.className ?? ''}`}
            aria-hidden="true"
        >
            <use xlinkHref={`#${props.linkHref}`}></use>
        </svg>
    }
}
