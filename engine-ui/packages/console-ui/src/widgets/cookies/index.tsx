
import * as React from 'react';

/**
 * Cookies 组件
 * 用法：
 * <Cookies onChanged={callback}></Cookies>
 */
const defaultIntervalTime = 200;
class Cookies extends React.Component<any, any> {
    _currentCookies;
    _timerId;

    componentDidMount () {
        this.initEvent();
    }
    componentWillUnmount () {
        clearInterval(this._timerId)
    }

    compareValue = () => {
        const { onChanged } = this.props;
        const old = '' + this._currentCookies;
        const newCookies = document.cookie;
        if (old !== newCookies) {
            if (onChanged) onChanged(old, newCookies);
            this._currentCookies = newCookies;
            this.onFieldsChange(old, newCookies);
        }
    }

    onFieldsChange = (old: any, newCookies: any) => {
        const { watchFields, onFieldsChanged } = this.props;
        if (watchFields) {
            const changedFields: any = [];
            for (let i = 0; i < watchFields.length; i++) {
                const key = watchFields[i];
                const originValue = this.getCookieValue(old, key);
                const newValue = this.getCookieValue(newCookies, key);
                if (originValue !== null && originValue !== newValue) {
                    console.log('fieldChanged:', key, originValue, newValue);
                    changedFields.push({ key, value: newValue });
                }
            }
            if (onFieldsChanged) {
                onFieldsChanged(changedFields);
            }
        }
    }

    // 根据 Cookies获取 name
    getCookieValue = (cookies: any, name: any) => {
        if (cookies) {
            const arr = cookies.match(
                new RegExp('(^| )' + name + '=([^;]*)(;|$)')
            );
            if (arr != null) return unescape(decodeURI(arr[2]));
        }
        return null;
    }

    initEvent = () => {
        this._timerId = setInterval(() => {
            this.compareValue();
        }, defaultIntervalTime);
    }

    render () {
        return (
            <React.Fragment>
                {
                    this.props.children
                }
            </React.Fragment>
        )
    }
}

export default Cookies
