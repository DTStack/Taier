/*
 * @Author: 云乐
 * @Date: 2021-03-16 14:02:25
 * @LastEditTime: 2021-03-16 17:29:48
 * @LastEditors: 云乐
 * @Description:移除存储数据
 */

export function remove() {
  sessionStorage.removeItem("current");
  sessionStorage.removeItem("sqlType");
  sessionStorage.removeItem("version");
  sessionStorage.removeItem("checkdList");
}
