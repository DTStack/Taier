import { ALARM_TYPE } from '../../consts';
export function canTestAlarm (alarmType: number): boolean {
    return ALARM_TYPE.MSG === alarmType || ALARM_TYPE.EMAIL === alarmType
}
