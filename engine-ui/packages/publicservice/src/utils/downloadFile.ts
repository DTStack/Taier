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
export default function downloadFile(
  response: Response,
  optionSaveName?: string
) {
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
