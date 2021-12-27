package com.dtstack.engine.common.util;

import com.google.common.collect.ArrayListMultimap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * SLOGAN:让未来变成现在
 *
 * @author <a href="mailto:maoba@dtstack.com">猫爸</a>
 * 2018-04-17 21:22.
 */
public class Collections {

    private static final Logger logger = LoggerFactory.getLogger(Collections.class);
    /**
     * 一个不可变更的集合
     */
    public static final List<?> EMPTY_LIST = java.util.Collections.unmodifiableList(new ArrayList<>(0));

    public static final <T> boolean isEmpty(Collection<T> collection) {
        return Objects.isNull(collection) || collection.size() == 0;
    }

    public static final <T> boolean nonEmpty(Collection<T> collection) {
        return !isEmpty(collection);
    }

    public static final <T> boolean isNotEmpty(Collection<T> collection) {
        return !isEmpty(collection);
    }

    /**
     * 返回集合的第一个元素
     *
     * @param collection 待处理的集合
     * @param <T>        元素类型
     * @return 若集合不存在或为空则返回null, 否则返回集合的第一个元素
     */
    public static final <T> T first(Collection<T> collection) {
        return nonEmpty(collection) ? collection.iterator().next() : null;
    }

    public static final <T> boolean onlyOne(Collection<T> collection) {
        return nonEmpty(collection) && collection.size() == 1;
    }

    public static final <T> T one(Collection<T> collection) {
        return one(collection, "集合元素存在{}个元素,获取单条记录异常", Objects.nonNull(collection) ? collection.size() : 0);
    }

    public static final <T> T one(Collection<T> collection, String message, Object args) {
        if (collection.size() > 1) {
            throw new IllegalArgumentException(Strings.format(message, args));
        } else if (isEmpty(collection)) {
            return null;
        } else {
            return collection.iterator().next();
        }
    }

    @SuppressWarnings("unchecked")
    public static final <T> List<T> emptyList() {
        return (List<T>) EMPTY_LIST;
    }


    /**
     * 集合类映射转换
     *
     * @param collection        集合
     * @param function          映射函数
     * @param collectionFactory 转换函数
     * @param <T>               集合元素原始类型T
     * @param <R>               集合元素转换类型R
     * @param <C>               原始集合类型
     * @param <CR>              转换集合类型
     * @return 通过映射转换的集合类型
     */
    public static <T, R, C extends Collection<T>, CR extends Collection<R>>
    CR mapperCollection(Collection<T> collection, Function<T, R> function, Supplier<CR> collectionFactory) {
        return Optional.ofNullable(collection).orElse(emptyList())
                .stream().map(function).collect(Collectors.toCollection(collectionFactory));
    }

    /**
     * List类映射转换
     *
     * @see Collections#mapperCollection(Collection, Function, Supplier)
     */
    public static <C extends Collection, T, R> List<R> mapperList(Collection<T> collection, Function<T, R> function) {
        return mapperCollection(collection, function, ArrayList::new);
    }

    /**
     * Set类映射转换
     *
     * @see Collections#mapperCollection(Collection, Function, Supplier)
     */
    public static <C extends Collection, T, R> Set<R> mapperSet(Collection<T> collection, Function<T, R> function) {
        return mapperCollection(collection, function, HashSet::new);
    }

    /**
     * 指定Filter过滤
     */
    public static <C extends Collection, T> List<T> filter(Collection<T> collection, Predicate<T> predicate) {
        return Optional.ofNullable(collection)
                .map(c -> c.stream().filter(predicate).collect(Collectors.toList()))
                .orElse(emptyList());
    }

    /**
     * 指定非Null过滤
     */
    public static <C extends Collection, T> List<T> filterNonNull(Collection<T> collection) {
        return filter(collection, Objects::nonNull);
    }


    /**
     * 根据自定义keyMapper映射单实体
     *
     * @param models    实体对象集合
     * @param keyMapper 自定义key映射
     * @return Map
     */
    public static <K, T> Map<K, T> ofMap(Collection<T> models, Function<T, K> keyMapper) {
        if (Collections.isEmpty(models)) {
            return java.util.Collections.emptyMap();
        }
        return models
                .stream()
                .filter(Objects::nonNull)
                .filter(f -> keyMapper.apply(f) != null)
                .collect(Collectors.toMap(keyMapper, v -> v, (ol, ne) -> ne));
    }

    /**
     * 根据自定义keyMapper映射单实体
     *
     * @param models    实体对象集合
     * @param keyMapper 自定义key映射 不能为空
     * @param valMapper 自定义val映射 不能为空
     * @return Map
     */
    public static <K, V, T> Map<K, V> ofMap(Collection<T> models, Function<T, K> keyMapper, Function<T, V> valMapper) {
        return ofMap(models, keyMapper, valMapper, HashMap::new);
    }

    /**
     * 根据自定义keyMapper映射单实体
     *
     * @param models      实体对象集合
     * @param keyMapper   自定义key映射 不能为空
     * @param valMapper   自定义val映射 不能为空
     * @param mapSupplier 自定义Map初始化
     * @return Map
     */
    public static <K, V, T, M extends Map<K, V>> Map<K, V> ofMap(Collection<T> models,
                                                                 Function<T, K> keyMapper,
                                                                 Function<T, V> valMapper,
                                                                 Supplier<M> mapSupplier) {
        if (Collections.isEmpty(models)) {
            return new HashMap<>(0);
        }
        return models
                .stream()
                .filter(Objects::nonNull)
                .filter(f -> keyMapper.apply(f) != null)
                .collect(Collectors.toMap(keyMapper, valMapper, (ol, ne) -> ne, mapSupplier));
    }


    /**
     * 根据自定义keyMapper映射多实体
     *
     * @param models    实体对象集合
     * @param <T>       extends BxModel
     * @param keyMapper 自定义key映射
     * @return Multimap
     */
    public static <K, T> ArrayListMultimap<K, T> ofMultimap(Collection<T> models, Function<T, K> keyMapper) {
        ArrayListMultimap<K, T> multimap = ArrayListMultimap.create();
        if (Collections.isEmpty(models)) {
            return multimap;
        }
        models.forEach(x -> multimap.put(keyMapper.apply(x), x));

        return multimap;
    }

    /**
     * 根据自定义keyMapper映射多实体
     *
     * @param models    实体对象集合
     * @param <T>       模型类型
     * @param keyMapper 自定义key映射
     * @return Multimap
     */
    public static <K, V, T> ArrayListMultimap<K, V> ofMultimap(Collection<T> models, Function<T, K> keyMapper, Function<T, V> valMapper) {
        ArrayListMultimap<K, V> multimap = ArrayListMultimap.create();
        if (Collections.isEmpty(models)) {
            return multimap;
        }
        models.forEach(x -> multimap.put(keyMapper.apply(x), valMapper.apply(x)));

        return multimap;
    }


    /**
     * 提取对象集合中的某一元素集合
     *
     * @param list    集合
     * @param lMapper 左值映射函数
     * @param rMapper 右值映射函数
     * @param <T>     初始对象类型
     * @param <L>     左值提取元素类型
     * @param <R>     右值提取元素类型
     * @return List
     */
    public static <T, L, R> List<Pair<L, R>> ofPair(Collection<T> list, Function<T, L> lMapper, Function<T, R> rMapper) {
        if (Collections.isEmpty(list)) {
            return java.util.Collections.emptyList();
        }
        Objects.requireNonNull(lMapper);
        Objects.requireNonNull(rMapper);
        return list
                .stream()
                .filter(Objects::nonNull)
                .map(m -> Pair.of(lMapper.apply(m), rMapper.apply(m)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * 该集合是否含有null元素
     *
     * @param collection 集合
     * @param <T>        泛型类型
     * @return true含有null元素
     */
    public static <T> boolean hasNull(Collection<T> collection) {
        if (Collections.isEmpty(collection)) {
            return false;
        }
        return collection.stream().anyMatch(Objects::isNull);
    }

    /**
     * 该集合是否含有null元素 或空字符串元素
     *
     * @param collection 集合
     * @return true含有null元素
     */
    public static boolean hasNullOrEmpty(Collection<String> collection) {
        if (Collections.isEmpty(collection)) {
            return false;
        }
        return collection.stream().anyMatch(StringUtils::isEmpty);
    }

    /**
     * Constructs a new set containing the elements of the specified collection
     *
     * @return HashSet
     */
    public static <T> HashSet<T> newHashSet(Collection<T> coll) {
        return Optional.ofNullable(coll)
                .map(x -> new HashSet<>(coll))
                .orElse(new HashSet<>(0));
    }

    /**
     * Constructs a new list containing the elements of the specified collection
     *
     * @return ArrayList
     */
    public static <T> ArrayList<T> newArrayList(Collection<T> coll) {
        return Optional.ofNullable(coll)
                .map(ArrayList::new)
                .orElse(new ArrayList<>(0));
    }

    /**
     * Constructs a new list containing the elements of the specified collection
     *
     * @return LinkedList
     */
    public static <T> LinkedList<T> newLinkedList(Collection<T> coll) {
        return Optional.ofNullable(coll)
                .map(LinkedList::new)
                .orElse(new LinkedList<>());
    }

    /**
     * List深度拷贝
     * 元素类必须有可调用的clone方法
     *
     * @param src 源数组
     * @param <T> 数组元素类型
     * @return 深拷贝后的数组
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static <T extends Cloneable> List<T> deepCopy(List<T> src) {
        return deepCopyOf(src, null);
    }

    /**
     * List深度拷贝
     * 元素类必须有可调用的clone方法
     *
     * @param src   源数组
     * @param <T>   数组元素类型
     * @param after 拷贝完成后的工作
     * @return 深拷贝后的数组
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static <T extends Cloneable> List<T> deepCopyOf(List<T> src, Consumer<T> after) {
        if (isEmpty(src)) {
            return emptyList();
        }
        Class<? extends Cloneable> clz = src.get(0).getClass();
        List<T> dest = new ArrayList<>();
        // 反射拿到clone方法
        Method method = null;
        try {
            method = clz.getMethod("clone");
        } catch (NoSuchMethodException e) {
            logger.error("NoSuchMethodException - need [clone] method.", e);
        }
        try {
            for (T s : src) {
                T d = (T) method.invoke(s);
                if (after != null) {
                    after.accept(d);
                }
                dest.add(d);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error("InvokeMethodException - occur error during invoking the [clone] method.", e);
        }
        return dest;
    }

}

