import { ALARM_TYPE, CHANNEL_MODE_VALUE } from '../../consts';
export function canTestAlarm (alertGateType: number): boolean {
    return ALARM_TYPE.MSG === alertGateType || ALARM_TYPE.EMAIL === alertGateType
}
export function showAlertTemplete (alertGateType: number, alertGateCode: string): boolean {
    return ALARM_TYPE.EMAIL === alertGateType && CHANNEL_MODE_VALUE.MAIL_DT === alertGateCode
}
