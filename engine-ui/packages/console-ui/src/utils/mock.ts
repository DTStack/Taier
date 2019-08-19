/**
 * 数据Mock
 * @param {Object} params
 */
export default function mock (params: any) {
    return function (target: any, name: any, descriptor: any) {
        console.log('target:', target, name, descriptor)
        for (const props in params) {
            if (target[props]) {
                target[props] = params[props]
            }
        }
    }
}
