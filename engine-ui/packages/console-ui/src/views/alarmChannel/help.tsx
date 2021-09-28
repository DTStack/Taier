import { ALARM_TYPE, CHANNEL_MODE_VALUE } from '../../consts';
export function showAlertGateCode (alertGateType: number): boolean {
    return ALARM_TYPE.CUSTOM !== alertGateType
}
export function showIsDefault (alertGateType: number): boolean {
    return ALARM_TYPE.CUSTOM !== alertGateType
}
export function showConfigFile (alertGateType: number): boolean {
    return ALARM_TYPE.CUSTOM === alertGateType
}
export function showAlertGateJson (alertGateCode: string, alertGateType?: number): boolean {
    return CHANNEL_MODE_VALUE.DING_DT !== alertGateCode || ALARM_TYPE.CUSTOM === alertGateType
}
export function showAlertTemplete (alertGateType: number, alertGateCode: string): boolean {
    return (ALARM_TYPE.EMAIL === alertGateType && CHANNEL_MODE_VALUE.MAIL_DT === alertGateCode) ||
        ALARM_TYPE.CUSTOM === alertGateType
}
export function canTestAlarm (alertGateType: number): boolean {
    return ALARM_TYPE.DING !== alertGateType
}
export function textAlertKey (type: number): string {
    return type === ALARM_TYPE.EMAIL ? 'emails' : 'phones'
}
