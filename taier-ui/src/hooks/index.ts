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

import { useEffect, useRef, useState } from 'react';

interface IPagination {
    current?: number;
    pageSize?: number;
    total?: number;
}

export const usePagination = ({
    current: initialCurrent = 1,
    pageSize: initialPageSize = 20,
    total: initialTotal = 0,
}: IPagination) => {
    const [current, setCurrent] = useState(initialCurrent);
    const [pageSize, setPageSize] = useState(initialPageSize);
    const [total, setTotal] = useState(initialTotal);

    const setPagination = ({
        current: c,
        pageSize: p,
        total: t,
    }: {
        current?: number;
        pageSize?: number;
        total?: number;
    }) => {
        if (c !== undefined) {
            setCurrent(c);
        }
        if (p !== undefined) {
            setPageSize(p);
        }
        if (t !== undefined) {
            setTotal(t);
        }
    };

    return {
        current,
        pageSize,
        total,
        setPagination,
    };
};

export function useConstant<T>(val: T) {
    const ref = useRef(val);

    useEffect(() => {
        ref.current = val;
    });

    return ref;
}
