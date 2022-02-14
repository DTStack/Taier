import API from '@/api/operation';
import { operationActions } from './actionType';

export function getProjectList(params: any) {
  return (dispatch: any) => {
    API.getProjectList(params).then((res: any) => {
      if (res.code === 1) {
        dispatch({
          type: operationActions.GET_PROJECT_LIST,
          data: res?.data ?? [],
        });
      }
    });
  };
}

export function getPersonList() {
  return (dispatch: any) => {
    API.getPersonInCharge().then((res: any) => {
      if (res.code === 1) {
        dispatch({
          type: operationActions.GET_PERSON_LIST,
          data: res?.data ?? [],
        });
      }
    });
  };
}
