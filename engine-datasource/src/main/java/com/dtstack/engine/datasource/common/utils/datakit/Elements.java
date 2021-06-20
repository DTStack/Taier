package com.dtstack.engine.datasource.common.utils.datakit;

import com.google.common.collect.Lists;

import java.util.*;
import java.util.function.Function;

/**
 * @author <a href="mailto:linfeng@dtstack.com">林丰</a> 2019/10/25.
 * @desc 集合元素相关工具类
 */
public class Elements {

    private Elements() {
    }

    /**
     * list\list是否有交集（是否有重复）
     *
     * @param lefts  左边的数组
     * @param rights 右边的数据
     * @param <T>
     * @return true:有交集(有重复)
     */
    public static <T> boolean isRepeatWith(List<T> lefts, List<T> rights) {
        if (Collections.isEmpty(lefts) || Collections.isEmpty(rights)) {
            return false;
        }
        List<T> bigList = lefts;
        List<T> smallList = rights;
        if (rights.size() > lefts.size()) {
            bigList = rights;
            smallList = lefts;
        }
        Set<T> tempSet = new HashSet<>(bigList.size());
        bigList.stream().filter(Objects::nonNull).forEach(tempSet::add);

        return smallList.stream().filter(Objects::nonNull).anyMatch(tempSet::contains);
    }


    public static final <T> List<T> getRepeats(List<T> lefts, List<T> rights) {
        if (Collections.isEmpty(lefts) || Collections.isEmpty(rights)) {
            return Lists.newArrayListWithCapacity(0);
        }
        List<T> bigList = lefts;
        List<T> smallList = rights;
        if (rights.size() > lefts.size()) {
            bigList = rights;
            smallList = lefts;
        }
        Set<T> set = new HashSet<>(bigList.size());
        bigList.stream().filter(Objects::nonNull).forEach(set::add);
        List<T> repeats = new ArrayList<>();
        for (T t : smallList) {
            if (t != null && set.contains(t)) {
                repeats.add(t);
            }
        }
        return repeats;
    }


    /**
     * 两个集合数组比对, 筛选出`左集合有的`而`右集合没有的`元素集合
     *
     * @param lefts  左集合
     * @param rights 右集合
     * @return lefts有rights没有
     */
    public static <T> List<T> differentFromLefts(Collection<T> lefts, Collection<T> rights) {
        return comparison(lefts, rights).get(0);
    }

    /**
     * 两个集合数组比对
     *
     * @param lefts  左集合
     * @param rights 右集合
     * @return 第0个:lefts有rights没有 第1个:rights有lefts没有 第2个:互相重复的
     */
    public static <T> List<List<T>> comparison(Collection<T> lefts, Collection<T> rights) {
        if (Collections.isEmpty(lefts)) {
            return Lists.newArrayList(
                    Collections.emptyList(),
                    rights == null ? Collections.emptyList() : new ArrayList<>(rights),
                    Collections.emptyList()
            );
        }
        if (Collections.isEmpty(rights)) {
            return Lists.newArrayList(lefts == null ? Collections.emptyList() : new ArrayList<>(lefts),
                    Collections.emptyList(),
                    Collections.emptyList()
            );
        }
        Collection<T> bigList = lefts;
        Collection<T> smallList = rights;
        boolean normalSequence = true; // 正常顺序:true 反响顺序:false
        if (rights.size() > lefts.size()) {
            bigList = rights;
            smallList = lefts;
            normalSequence = false;
        }
        Map<T, Integer> tempMap = new HashMap<>(bigList.size());
        // -1:rights有lefts没有 | 0:lefts有rights没有 | 1:重复
        if (normalSequence) {
            bigList.stream().filter(Objects::nonNull).forEach(x -> tempMap.put(x, 0));
        } else {
            bigList.stream().filter(Objects::nonNull).forEach(x -> tempMap.put(x, -1));
        }
        List<T> existOnLefts = new ArrayList<>();
        List<T> existOnRights = new ArrayList<>();
        List<T> repeats = new ArrayList<>();
        if (normalSequence) {
            for (T t : smallList) {
                if (t != null) {
                    tempMap.put(t, tempMap.containsKey(t) ? 1 : -1);
                }
            }
        } else {
            for (T t : smallList) {
                if (t != null) {
                    tempMap.put(t, tempMap.containsKey(t) ? 1 : 0);
                }
            }
        }
        tempMap.forEach((k, v) -> {
            if (v == 0) { // 0:lefts有rights没有
                existOnLefts.add(k);
            } else if (v == -1) { // -1:rights有lefts没有
                existOnRights.add(k);
            } else if (v == 1) { // 1:重复
                repeats.add(k);
            }
        });
        return Lists.newArrayList(existOnLefts, existOnRights, repeats);
    }


    /**
     * 判断两个集合的元素是否完全相同
     *
     * @param lefts  集合1
     * @param rights 集合2
     * @param <T>    泛型
     * @return true相同 false不相同
     */
    public static final <T> boolean allTheSame(Collection<T> lefts, Collection<T> rights) {
        if (Collections.isEmpty(lefts) || Collections.isEmpty(rights)) {
            return false;
        }
        if (lefts.size() != rights.size()) {
            return false;
        }
        List<List<T>> detectResult = comparison(lefts, rights);
        return Collections.isEmpty(detectResult.get(0)) && Collections.isEmpty(detectResult.get(1));
    }

    /**
     * 该集合是否含有重复的元素
     */
    public static final <T> boolean hasRepeat(Collection<T> collection) {
        return hasRepeat(collection, v -> v);
    }

    /**
     * 该集合是否含有重复的元素
     *
     * @param collection 集合
     * @param <T>        泛型
     * @return true含有重复 false不含有
     */
    public static final <T, V> boolean hasRepeat(Collection<T> collection, Function<T, V> mapper) {
        if (Collections.isEmpty(collection)) {
            return false; // 为空默认false
        }
        return collection
                .stream()
                .filter(Objects::nonNull)
                .map(mapper)
                .distinct()
                .count() != collection.size();
    }

    /**
     * 该集合是否含不同的元素
     */
    public static final <T, V> boolean hasDifferent(Collection<T> collection) {
        return hasDifferent(collection, v -> v);
    }

    /**
     * 该集合是否含不同的元素
     *
     * @param collection 集合
     * @param <T>        泛型
     * @return true含有不同的元素 false不含有
     */
    public static final <T, V> boolean hasDifferent(Collection<T> collection, Function<T, V> mapper) {
        if (Collections.isEmpty(collection)) {
            return false; // 为空默认false
        }
        return collection
                .stream()
                .filter(Objects::nonNull)
                .map(mapper)
                .distinct()
                .count() > 1;
    }


}
