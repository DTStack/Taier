import React, { Component } from 'react'
import './style.scss'

const imgBase = 'public/rdos/img/icon';
const imgDarkBase = 'public/rdos/img/theme-dark'

export default class Icon extends Component {
    render () {
        const props = this.props;
        const base = !props.themeDark ? imgBase : imgDarkBase;
        return (<img
            {...props}
            className={`rdos-icon ${props.className || ''}`}
            alt={props.alt} src={`${base}/${props.type}.svg`} />
        )
    }
}
