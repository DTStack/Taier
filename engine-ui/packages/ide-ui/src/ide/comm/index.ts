import debounce from "lodash/debounce";

// 请求防抖动
export function debounceEventHander(func: any, wait?: number, options?: any) {
  const debounced = debounce(func, wait, options);
  return function (e: any) {
    e.persist();
    return debounced(e);
  };
}
