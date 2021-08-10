import { notification, Modal } from 'antd'
import { NotificationApi } from 'antd/lib/notification'
/**
 * 去除空串
 */
export function trim (str: string) {
    return typeof str === 'string'
        ? str.replace(/^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g, '')
        : str
}

let _singletonNotificationCursorTime = 0
/**
 * 校验是否处在单实例的时间段
 */
function checkIsTimeout () {
    const offset = 1000
    const now = new Date().getTime()
    const old = _singletonNotificationCursorTime

    _singletonNotificationCursorTime = new Date().getTime()
    if (now - offset > old) {
        return true
    }
    return false
}

/**
 * 不区分大小写的过滤 value Option
 */
export const filterValueOption = (input: any, option: any) => {
    return option.props.value.toLowerCase().indexOf(input.toLowerCase()) >= 0
}

/**
 * 包装一下
 */
export function dtNotification (title: any, message: any, type: any, config: any) {
    const showType: any = type || 'error'
    const WrapperModal: any = Modal
    const showMessage = message.length > 100
        ? (<span>
            {message.substring(0, 100)}... <a onClick={() => {
                WrapperModal[showType]({
                    title: title,
                    content: message,
                    width: 520,
                    style: { wordBreak: 'break-word' }
                })
            }}>查看详情</a>
        </span>)
        : message
    notification[showType as keyof NotificationApi]({
        ...config,
        message: title,
        description: showMessage
    })
}

/**
 * 全局唯一的notification实例
 * 规则：在固定时间段内，相连并且相同的错误信息只会弹出一个。
 * @param {*} title
 * @param {*} message
 */
export function singletonNotification (title: any, message?: any, type?: any, style?: any) {
    const notifyMsgs = document.querySelectorAll('.ant-notification-notice-description')

    /**
    * 1.当前无实例
    * 2.当前存在实例，但是当前实例的最后一个信息和调用的信息不相等
    * 3.存在实例，并且相等，但是已经超出了限定的时间
    */
    if (!notifyMsgs.length ||
        notifyMsgs[notifyMsgs.length - 1].innerHTML !== message ||
        checkIsTimeout()
    ) {
        dtNotification(title, message, type, {
            style
        })
    }
}

export function formJsonValidator (rule: any, value: any, callback: any) {
    let msg: any
    try {
        if (value) {
            const t = JSON.parse(value)
            if (typeof t !== 'object') {
                msg = '请填写正确的JSON'
            }
        }
    } catch (e) {
        msg = '请检查JSON格式，确认无中英文符号混用！'
    } finally {
        callback(msg)
    }
}
