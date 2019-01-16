export default class BaseModel {
    constructor (initData = {}) {
        const keyAndValue = Object.entries(initData);
        keyAndValue.forEach(([key, value]) => {
            this[key] = value;
        })
    }
}
