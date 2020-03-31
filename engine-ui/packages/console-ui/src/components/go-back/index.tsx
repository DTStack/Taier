import * as React from 'react'
import { Icon } from 'antd'
import { browserHistory, hashHistory } from 'react-router'

export default class GoBack extends React.Component<any, any> {
    go = () => {
        const { url, history, autoClose } = this.props

        if (url) {
            if (history) { browserHistory.push(url) } else { hashHistory.push(url) }
        } else {
            if (window.history.length == 1) {
                if (autoClose) {
                    window.close();
                }
            } else {
                hashHistory.go(-1);
            }
        }
    }

    getButtonView () {
        const { style } = this.props;

        let iconStyle: any = {
            cursor: 'pointer',
            fontFamily: 'anticon',
            fontSize: '18px',
            color: 'rgb(148, 168, 198)',
            letterSpacing: '5px',
            position: 'relative',
            top: '2px'
        }

        if (style) {
            Object.assign(iconStyle, style)
        }

        return (
            <span
                style={iconStyle}
                onClick={this.go}
            >
                <Icon
                    type={'left-circle-o'}
                />
                { this.props.children }
            </span>
        )
    }

    render () {
        return this.getButtonView();
    }
}
