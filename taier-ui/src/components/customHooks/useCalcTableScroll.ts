/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { useCallback,useEffect, useState } from 'react';
import type { TableProps } from 'antd';

interface IProps {
    className: string;
}

type IScroll<T> = Pick<Partial<TableProps<T>>, 'scroll'>['scroll'];

function useCalcTableScroll<T>(props: IProps) {
    const { className } = props;
    const [scroll, setScroll] = useState<IScroll<T>>({});

    const calcTableScroll = useCallback((targetTableEle: HTMLElement) => {
        return () => {
            const tableContentHeight = targetTableEle.offsetHeight;
            const tableContentWidth = targetTableEle.offsetWidth;
            const tableHeader = targetTableEle.querySelector<HTMLElement>('.ant-table-thead');
            const tableFooter = targetTableEle.querySelector<HTMLElement>('.ant-table-footer');
            setScroll({
                y: tableContentHeight - (tableHeader?.offsetHeight || 0) - (tableFooter?.offsetHeight || 0),
                x: tableContentWidth,
            });
        };
    }, []);

    useEffect(() => {
        const targetTableEle = document.querySelector<HTMLElement>(`.${className}`);
        if (!targetTableEle) return;
        const resizeObserver = new ResizeObserver(calcTableScroll(targetTableEle));
        resizeObserver.observe(targetTableEle);

        return () => {
            resizeObserver.unobserve(targetTableEle);
        };
    }, []);

    return { scroll };
}

export default useCalcTableScroll;
