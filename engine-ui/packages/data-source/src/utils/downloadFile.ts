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

/*
 * @Author: 云乐
 * @Date: 2021-03-15 16:20:05
 * @LastEditTime: 2021-03-16 16:58:27
 * @LastEditors: 云乐
 * @Description: 文件下载
 */
function getFileName(str: string): string {
	const strList = str.split(';');
	let ret = '';
	strList.forEach((item) => {
		if (item.indexOf('filename') >= 0) {
			const itemStr = item.split('=');
			ret = itemStr[1];
		}
	});
	if (!ret) {
		return Math.random().toString(36).slice(2);
	}
	return decodeURIComponent(ret);
}

// 下载方法实现
export default function downloadFile(response: Response, optionSaveName?: string) {
	const responseHeaders = response.headers;
	const contenType = responseHeaders.get('content-type');
	const contentDisposition = responseHeaders.get('content-disposition');
	const fileName = optionSaveName || getFileName(contentDisposition);
	response.blob().then((blobStream) => {
		const blob = new Blob([blobStream], {
			type: contenType,
		});

		if (window.navigator.msSaveBlob) {
			try {
				window.navigator.msSaveBlob(blob, fileName);
			} catch (e) {
				console.error(e);
			}
		} else {
			const link = document.createElement('a');
			link.href = window.URL.createObjectURL(blob);
			link.download = fileName;
			document.body.appendChild(link);
			link.click();
		}
	});
}
