import msgActions from 'main/consts/msgActions'

export function updateMsg (fields) {
    return {
        type: msgActions.UPDATE_MSG,
        data: fields
    }
}
