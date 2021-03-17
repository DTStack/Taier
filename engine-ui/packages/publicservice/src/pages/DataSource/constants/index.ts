// export function getAllStorage() {
//   let saveStatus = {
//     menuSelected: sessionStorage.getItem("current"),
//     sqlType: JSON.parse(sessionStorage.getItem("sqlType")),
//     version: sessionStorage.getItem("version"),
//     checkdList: sessionStorage.getItem("checkdList"),
//   };

//   return saveStatus;
// }

export default{
  menuSelected: sessionStorage.getItem("current"),
    sqlType: JSON.parse(sessionStorage.getItem("sqlType")),
    version: sessionStorage.getItem("version"),
    checkdList: sessionStorage.getItem("checkdList"),
}