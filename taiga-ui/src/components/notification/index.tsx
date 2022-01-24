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

import { notification, Modal } from 'antd';
import type { NotificationApi } from 'antd/lib/notification';

let singletonNotificationCursorTime = 0;
/**
 * 校验是否处在单实例的时间段
 */
function checkIsTimeout() {
	const offset = 1000;
	const now = new Date().getTime();
	const old = singletonNotificationCursorTime;

	singletonNotificationCursorTime = new Date().getTime();
	if (now - offset > old) {
		return true;
	}
	return false;
}

/**
 * 包装一下
 */
function dtNotification(title: any, message: any, type: any, config: any) {
	const showType: any = type || 'error';
	const WrapperModal: any = Modal;
	const showMessage =
		message.length > 100 ? (
			<span>
				{message.substring(0, 100)}...{' '}
				<a
					onClick={() => {
						WrapperModal[showType]({
							title,
							content: message,
							width: 520,
							style: { wordBreak: 'break-word' },
						});
					}}
				>
					查看详情
				</a>
			</span>
		) : (
			message
		);
	notification[showType as keyof NotificationApi]({
		...config,
		message: title,
		description: showMessage,
	});
}

/**
 * 全局唯一的notification实例
 * 规则：在固定时间段内，相连并且相同的错误信息只会弹出一个。
 * @param {*} title
 * @param {*} message
 */
export default function singletonNotification(title: any, message?: any, type?: any, style?: any) {
	const notifyMsgs = document.querySelectorAll('.ant-notification-notice-description');

	/**
	 * 1.当前无实例
	 * 2.当前存在实例，但是当前实例的最后一个信息和调用的信息不相等
	 * 3.存在实例，并且相等，但是已经超出了限定的时间
	 */
	if (
		!notifyMsgs.length ||
		notifyMsgs[notifyMsgs.length - 1].innerHTML !== message ||
		checkIsTimeout()
	) {
		dtNotification(title, message, type, {
			style,
		});
	}
}
