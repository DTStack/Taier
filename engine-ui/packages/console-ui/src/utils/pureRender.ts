function isEqual (a: any, b: any) {
    for (const key in a) {
        if ({}.hasOwnProperty.call(a, key) &&
      (!{}.hasOwnProperty.call(b, key) || a[key] !== b[key])) {
            return false;
        }
    }
    for (const key in b) {
        if ({}.hasOwnProperty.call(b, key) && !{}.hasOwnProperty.call(a, key)) {
            return false;
        }
    }
    return true;
}

export default function shouldRender (targetComponent: any) {
    targetComponent.prototype.shouldComponentUpdate = function (props: any, state: any) {
        return !isEqual(this.state, state) || !isEqual(this.props, props)
    }
}
