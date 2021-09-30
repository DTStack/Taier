/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { ALARM_TYPE, CHANNEL_MODE_VALUE } from '../../consts';
export function showAlertGateCode(alertGateType: number): boolean {
	return ALARM_TYPE.CUSTOM !== alertGateType;
}
export function showIsDefault(alertGateType: number): boolean {
	return ALARM_TYPE.CUSTOM !== alertGateType;
}
export function showConfigFile(alertGateType: number): boolean {
	return ALARM_TYPE.CUSTOM === alertGateType;
}
export function showAlertGateJson(alertGateCode: string, alertGateType?: number): boolean {
	return CHANNEL_MODE_VALUE.DING_DT !== alertGateCode || ALARM_TYPE.CUSTOM === alertGateType;
}
export function showAlertTemplete(alertGateType: number, alertGateCode: string): boolean {
	return (
		(ALARM_TYPE.EMAIL === alertGateType && CHANNEL_MODE_VALUE.MAIL_DT === alertGateCode) ||
		ALARM_TYPE.CUSTOM === alertGateType
	);
}
export function canTestAlarm(alertGateType: number): boolean {
	return ALARM_TYPE.DING !== alertGateType;
}
export function textAlertKey(type: number): string {
	return type === ALARM_TYPE.EMAIL ? 'emails' : 'phones';
}
