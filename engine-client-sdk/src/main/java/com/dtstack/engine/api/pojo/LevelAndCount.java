package com.dtstack.engine.api.pojo;/**
 * @author chenfeixiang6@163.com
 * @date 2021/4/15
 */

/**
 *类名称:LevelAndCount
 *类描述:数量和层数
 *创建人:newman
 *创建时间:2021/4/15 3:37 下午
 *Version 1.0
 */
public class LevelAndCount {

    /**数量**/
    private Integer count;

    /**层数**/
    private Integer levelCount;

    /**直接数量**/
    private Integer directCount;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getLevelCount() {
        return levelCount;
    }

    public void setLevelCount(Integer levelCount) {
        this.levelCount = levelCount;
    }

    public Integer getDirectCount() {
        return directCount;
    }

    public void setDirectCount(Integer directCount) {
        this.directCount = directCount;
    }
}


