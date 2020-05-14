
const createType=(keys:any)=>{
  let obj={};
  keys.forEach(item=>{
    obj[item]=item;
  })
  return obj;
}


export const globalType:any= createType(
  [
  'GET_USER_DATA',
  'GET_NAV_DATA'
 ]
);

