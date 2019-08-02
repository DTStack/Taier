import * as React from 'react'
import './style.scss'

const imgBase = 'public/main/img/icon'

export default class Icon extends React.Component<any, any> {
    render () {
        const props = this.props

        return (<img
            {...props}
            className={`m-icon ${props.className || ''}`}
            alt={props.alt} src={`${imgBase}/${props.type}.svg`} />
        )
    }
}
