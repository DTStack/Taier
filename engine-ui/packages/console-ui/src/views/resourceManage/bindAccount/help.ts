export function giveMeAKey (): string {
    return (new Date().getTime() + '' + ~~(Math.random() * 100000))
}
