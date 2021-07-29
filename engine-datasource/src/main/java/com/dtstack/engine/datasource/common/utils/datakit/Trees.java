package com.dtstack.engine.datasource.common.utils.datakit;


import com.dtstack.engine.datasource.common.utils.datakit.struct.tree.BaseLevelTree;
import com.dtstack.engine.datasource.common.utils.datakit.struct.tree.BaseTree;
import com.google.common.base.Predicates;
import dt.insight.plat.lang.base.Numbers;
import dt.insight.plat.lang.base.Strings;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * SLOGAN:让未来变成现在
 *
 * @author <a href="mailto:maoba@dtstack.com">猫爸</a>
 * 2018-04-17 20:35.
 */
public class Trees {
    /**
     * 构建树状结构,内置支持id为Integer/Long/String,其中数值型为空或者null则为顶级节点,字符型为空白字符串或null则为顶级节点
     *
     * @param nodeList 树状节点列表
     * @param <T>      树状节点类型
     * @return 组装完毕的树状结构
     */
    public static final <T extends BaseTree> List<T> buildTree(List<T> nodeList) {
        if (Collections.isEmpty(nodeList)) {
            return buildTree(nodeList, Predicates.alwaysFalse());
        } else {
            T node = nodeList.get(0);
            //若父id为null,则为root节点
            Predicate<T> predicate = x -> Objects.isNull(x.getParentId());

            Object parentId = node.getParentId();
            if (parentId instanceof Long) {
                //Long类型为null或0则为root
                predicate = predicate.or(x -> Numbers.zero((Long) x.getParentId()));
            } else if (parentId instanceof Integer) {
                //Integer类型为null或0则为root节点
                predicate = predicate.or(x -> Numbers.zero((Integer) x.getParentId()));
            } else if (parentId instanceof String) {
                //字符串类型为null或未空白字符串则为root节点
                predicate = predicate.or(x -> Strings.isBlank((String) x.getParentId()));
            } else {
                throw new IllegalArgumentException("树主键ID非Integer/Long/String,请指定根节点判断规则");
            }

            return buildTree(nodeList, predicate);
        }
    }

    /**
     * 构建树状结构
     *
     * @param nodeList  树状节点列表
     * @param predicate 是否是父节点
     * @param <T>       树状节点类型
     * @return 组装完毕的树状结构
     */
    public static final <T extends BaseTree> List<T> buildTree(List<T> nodeList, Predicate<T> predicate) {
        Objects.requireNonNull(predicate);

        List<T> topNodeList = new ArrayList<>();

        if (Collections.isNotEmpty(nodeList)) {
            /**
             * 父节点id为key,节点本身为同父节点的节点列表
             */
            Map<Object, List<T>> nodeMap = new HashMap<>(nodeList.size());

            /**
             * 压栈实现尾递归
             */
            Stack<T> stack = new Stack<>();

            for (T node : nodeList) {
                if (predicate.test(node)) {
                    topNodeList.add(node);
                    if (node instanceof BaseLevelTree) {
                        ((BaseLevelTree) node).setLevel(Strings.format("{}", node.getId()));
                    }

                    stack.push(node);
                } else {
                    List<T> familyList = nodeMap.get(node.getParentId());
                    if (familyList != null) {
                        familyList.add(node);
                    } else {
                        List<T> list = new ArrayList<>();

                        list.add(node);
                        nodeMap.put(node.getParentId(), list);
                    }
                }
            }
            while (!stack.empty()) {
                T parent = stack.pop();

                List<T> list = nodeMap.get(parent.getId());
                if (list != null) {
                    parent.setChildren(list);
                    for (int k = 0, size = list.size(); k < size; k++) {
                        T child = list.get(k);

                        if (child instanceof BaseLevelTree) {
                            ((BaseLevelTree) child).setLevel(Strings.format("{}_{}",
                                    ((BaseLevelTree) parent).getLevel(),
                                    child.getId()));
                        }

                        stack.push(child);
                    }
                } else {
                    parent.setChildren(new ArrayList());
                }
            }
        }

        return topNodeList;
    }



    /**
     * 构建树状结构
     *
     * @param nodeList  树状节点列表
     * @param predicate 是否是父节点
     * @param <T>       树状节点类型
     * @return 组装完毕的树状结构
     */
    public static final <T, V extends BaseTree> List<V> buildTree(List<T> nodeList, Predicate<V> predicate, Function<T, V> mapper) {
        Objects.requireNonNull(predicate);

        List<V> topNodeList = new ArrayList<>();

        if (Collections.isNotEmpty(nodeList)) {
            /**
             * 父节点id为key,节点本身为同父节点的节点列表
             */
            Map<Object, List<V>> nodeMap = new HashMap<>(nodeList.size());

            /**
             * 压栈实现尾递归
             */
            Stack<V> stack = new Stack<>();

            for (T node : nodeList) {
                V vNode = mapper.apply(node);
                if (predicate.test(vNode)) {
                    topNodeList.add(vNode);
                    if (vNode instanceof BaseLevelTree) {
                        ((BaseLevelTree) vNode).setLevel(Strings.format("{}", vNode.getId()));
                    }

                    stack.push(vNode);
                } else {
                    List<V> familyList = nodeMap.get(vNode.getParentId());
                    if (familyList != null) {
                        familyList.add(vNode);
                    } else {
                        List<V> list = new ArrayList<>();

                        list.add(vNode);
                        nodeMap.put(vNode.getParentId(), list);
                    }
                }
            }
            while (!stack.empty()) {
                V parent = stack.pop();

                List<V> list = nodeMap.get(parent.getId());
                if (list != null) {
                    parent.setChildren(list);
                    for (int k = 0, size = list.size(); k < size; k++) {
                        V child = list.get(k);

                        if (child instanceof BaseLevelTree) {
                            ((BaseLevelTree) child).setLevel(Strings.format("{}_{}",
                                    ((BaseLevelTree) parent).getLevel(),
                                    child.getId()));
                        }

                        stack.push(child);
                    }
                } else {
                    parent.setChildren(new ArrayList());
                }
            }
        }

        return topNodeList;
    }
}