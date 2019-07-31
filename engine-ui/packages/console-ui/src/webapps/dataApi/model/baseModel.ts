export default class BaseModel {
    constructor (initData = {}) {
        const keyAndValue = Object.entries(initData);
        keyAndValue.forEach(([key, value]) => {
            this[key] = value;
        });
        this.id = this.id || new Date().getTime() + '_' + ~~(Math.random() * 10000);
    }
}
