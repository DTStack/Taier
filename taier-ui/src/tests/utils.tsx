export const $ = <T extends Element>(selector: string) => {
    return document.querySelector<T>(selector);
};

export const $$ = <T extends Element>(selector: string) => {
    return document.querySelectorAll<T>(selector);
};

export async function sleep(delay = 300) {
    return new Promise<void>((resolve) => {
        setTimeout(() => {
            resolve();
        }, delay);
    });
}
