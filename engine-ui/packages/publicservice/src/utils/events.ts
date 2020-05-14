export default class EventEmitter {
  /**
   * Creates an instance of EventEmitter.
   * @param {string} symbol 标识符
   * @memberof EventEmitter
   */
  constructor(symbol) {
    //this.eventList = [];
    this._symbol = symbol || Symbol('EventEmitter');
  }
  eventList=[]

  _symbol =Symbol('EventEmitter');
  /**
   * @description 注册事件
   * @param {string} eventName
   * @param {function} cb
   * @param {object} context
   * @memberof EventEmitter
   */
  addEvent(eventName, cb, context) {
    if(this.eventList.map(o => Object.keys(o)[0]).includes(eventName)) {
      this.eventList = this.eventList.map(subscribe => {
        if(Object.keys(subscribe)[0] === eventName) {
          return {
            [eventName]: [...subscribe[eventName], args => cb.call(context, args)]
          }
        }
        else {
          return subscribe;
        }
      });
    }
    else {
      this.eventList.push({
        [eventName]: [args => cb.call(context, args)]
      });
    }
  }

  /**
   * @description 触发事件
   * @param {string} eventName
   * @param {any} args 回调函数入参
   * @memberof EventEmitter
   */
  emitEvent(eventName, args) {
    this.eventList.forEach(event => {
      if(Object.keys(event)[0] === eventName) {
        event[eventName].forEach(cb => cb.call(this, args));
      }
    });
  }

  /**
   * @description 移除事件
   * @param {string} eventName
   * @memberof EventEmitter
   */
  removeEvent(eventName) {
    this.eventList = this.eventList.filter(event => {
      return eventName !== Object.keys(event)[0];
    });
  }

  _debug() {
    console.log(this.eventList);
  }
}
