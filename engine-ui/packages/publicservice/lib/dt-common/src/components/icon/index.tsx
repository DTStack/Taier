import * as React from 'react'
import './style.scss'

const imgBase = 'public/img/icon'
const imgDarkBase = 'public/img/theme-dark'

export default class Icon extends React.Component<any, any> {
    render () {
        const props = this.props
        const base = !props.themeDark ? imgBase : imgDarkBase;

        return (<img
            {...props}
            className={`m-icon ${props.className || ''}`}
            alt={props.alt} src={`${base}/${props.type}.svg`} />
        )
    }
}
