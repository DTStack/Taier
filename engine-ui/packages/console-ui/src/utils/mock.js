/**
 * 数据Mock
 * @param {Object} params
 */
export default function mock (params) {
    return function (target, name, descriptor) {
        console.log('target:', target, name, descriptor)
        for (const props in params) {
            if (target[props]) {
                target[props] = params[props]
            }
        }
    }
}
