import { operationActions } from '../../consts/operationActions'
import { cloneDeep } from 'lodash'

const defaultState: any = {
    projectList: [],
    personList: []
}

export default function (state = defaultState, action: any) {
    switch (action.type) {
        case operationActions.GET_PROJECT_LIST: {
            const projectList = action.data
            const newState = cloneDeep(state)
            newState.projectList = projectList
            return newState
        }
        case operationActions.GET_PERSON_LIST: {
            const personList = action.data
            const newState = cloneDeep(state)
            newState.personList = personList
            return newState
        }
        default:
            return state
    }
}
