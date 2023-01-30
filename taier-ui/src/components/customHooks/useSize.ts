import { useEffect, useState } from 'react';

/**
 * 监听 dom 元素的尺寸变化
 */
export default function useSize(dom: HTMLElement | string) {
    const [size, setSize] = useState({ width: 0, height: 0 });

    useEffect(() => {
        const resizeObserver = new ResizeObserver((entries) => {
            const current = entries[0];
            setSize({
                width: current.contentRect.width,
                height: current.contentRect.height,
            });
        });
        const observeDom = typeof dom === 'string' ? document.querySelector(`.${dom}`) : dom;
        if (observeDom) {
            resizeObserver.observe(observeDom);

            return () => {
                resizeObserver.unobserve(observeDom);
            };
        }
    }, []);

    return size;
}
