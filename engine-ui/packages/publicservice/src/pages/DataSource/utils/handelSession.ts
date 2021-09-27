export let checks = [];
export const saveCheckStauts = (checkdList) => {
  checks = checkdList;
};

//清除数据
export function remove() {
  sessionStorage.removeItem('current');
  sessionStorage.removeItem('sqlType');
  sessionStorage.removeItem('version');
  saveCheckStauts([]);
}

export function getSaveStatus() {
  let sqlType: any = {
    dataType: '',
  };
  try {
    sqlType = JSON.parse(sessionStorage.getItem('sqlType'));
  } catch (error) {}
  let saveStatus = {
    menuSelected: sessionStorage.getItem('current'),
    sqlType,
    version: sessionStorage.getItem('version') || '',
  };

  return saveStatus;
}
