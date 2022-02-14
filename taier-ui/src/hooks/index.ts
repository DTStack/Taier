import { useState } from 'react';

interface IPagination {
	current?: number;
	pageSize?: number;
	total?: number;
}

export const usePagination = ({
	current: initialCurrent = 1,
	pageSize: initalPageSize = 20,
	total: initialTotal = 0,
}: IPagination) => {
	const [current, setCurrent] = useState(initialCurrent);
	const [pageSize, setPageSize] = useState(initalPageSize);
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
		if (c) {
			setCurrent(c);
		}
		if (p) {
			setPageSize(p);
		}
		if (t) {
			setTotal(t);
		}
	};

	return { current, pageSize, total, setPagination };
};
