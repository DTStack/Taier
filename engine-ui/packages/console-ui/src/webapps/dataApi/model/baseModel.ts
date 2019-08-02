export default class BaseModel {
    public id: any;
    constructor (initData = {}) {
        const keyAndValue = Object.entries(initData);
        keyAndValue.forEach(([key, value]) => {
            (this as any)[key] = value;
        });
        this.id = this.id || new Date().getTime() + '_' + ~~(Math.random() * 10000);
    }
}
