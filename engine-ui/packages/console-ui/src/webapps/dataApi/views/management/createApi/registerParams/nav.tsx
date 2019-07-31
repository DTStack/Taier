import * as React from 'react';

export const NAV_KEYS: any = {
    PARAMS: 'params',
    RESULT: 'result',
    ERRORCODE: 'errorCode'
}
const items: any = [{
    key: NAV_KEYS.PARAMS,
    name: '请求参数配置'
}, {
    key: NAV_KEYS.RESULT,
    name: '返回示例'
}, {
    key: NAV_KEYS.ERRORCODE,
    name: '错误码'
}]

class RegisterParamsNav extends React.Component<any, any> {
    state: any = {
        value: items[0].key
    }
    getSelectKey () {
        if ('value' in this.props) {
            return this.props.value || ''
        }
        return this.state.value
    }
    onSelect (value: any) {
        this.setState({
            value: value
        }, this.emit.bind(this, value))
    }
    emit (value: any) {
        this.props.onChange && this.props.onChange(value);
    }
    renderLinks () {
        const value = this.getSelectKey();
        return items.map((item: any) => {
            return (
                <li key={item.key} className={item.key == value ? 'c-register-params__nav__link--select' : ''}>
                    <span className = 'c-register-params__nav__link__text' onClick={this.onSelect.bind(this, item.key)}>{item.name}</span>
                </li>
            )
        })
    }
    render () {
        return (
            <React.Fragment>
                <div className='c-register-params__nav__title'>参数配置</div>
                <ul className='c-register-params__nav__links'>
                    {this.renderLinks()}
                </ul>
            </React.Fragment>
        )
    }
}
export default RegisterParamsNav;
