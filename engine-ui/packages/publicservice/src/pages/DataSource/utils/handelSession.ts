export function remove() {
  sessionStorage.removeItem('current');
  sessionStorage.removeItem('sqlType');
  sessionStorage.removeItem('version');
  sessionStorage.removeItem('checkdList');
}

export function getSaveStatus() {
  let saveStatus = {
    menuSelected: sessionStorage.getItem('current'),
    sqlType: JSON.parse(sessionStorage.getItem('sqlType')),
    version: sessionStorage.getItem('version') || '',
    checkdList: sessionStorage.getItem('checkdList'),
  };

  return saveStatus;
}
