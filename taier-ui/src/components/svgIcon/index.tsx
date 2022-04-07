import * as React from 'react'
import './style.scss'

interface IProps {
    className?: string;
    linkHref?: string;
    [propName: string]: any;
}

export default class SvgIcon extends React.Component<IProps, any> {
    render () {
        const props = this.props
        return <svg
            { ...this.props }
            className={`icon-svg default-icon ${props.className ?? ''}`}
            aria-hidden="true"
        >
            <use xlinkHref={`#${props.linkHref}`}></use>
        </svg>
    }
}
