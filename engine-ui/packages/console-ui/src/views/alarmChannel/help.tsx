import { ALARM_TYPE, CHANNEL_MODE_VALUE } from '../../consts';
export function canTestAlarm (alarmType: number): boolean {
    return ALARM_TYPE.MSG === alarmType || ALARM_TYPE.EMAIL === alarmType
}
export function showAlertTemplete (alarmType: number, alertGateCode: string): boolean {
    return ALARM_TYPE.EMAIL === alarmType && CHANNEL_MODE_VALUE.MAIL_DT === alertGateCode
}
