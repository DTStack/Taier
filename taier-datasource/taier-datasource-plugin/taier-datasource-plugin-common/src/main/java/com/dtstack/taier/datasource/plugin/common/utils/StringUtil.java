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

package com.dtstack.taier.datasource.plugin.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringUtil {
    private static final char[] DIGITS_LOWER = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String encodeHex(byte[] data) {
        return encodeHex(data, DIGITS_LOWER);
    }

    public static String encodeHex(byte[] data, char[] toDigits) {
        int length = data.length;
        char[] out = new char[length * 3];
        int i = 0;
        for (int var = 0; i < length; ++i) {
            out[var++] = toDigits[(240 & data[i]) >>> 4];
            out[var++] = toDigits[15 & data[i]];
            out[var++] = ' ';
        }

        return String.valueOf(out);
    }

    /**
     * 解析出对应符号对内的内容
     *
     * @param text     需要解析的字符串
     * @param signPair 左右符号
     * @return 解析结果
     */
    public static String splitWithPair(String text, Pair<Character, Character> signPair) {
        if (StringUtils.isEmpty(text) || text.length() < 2) {
            return text;
        }
        if (text.startsWith(String.valueOf(signPair.getLeft())) && text.endsWith(String.valueOf(signPair.getRight()))) {
            return text.substring(1, text.length() -1);
        }
        return text;
    }

    /**
     * 按指定切割符号进行切割字符串并忽略指定括号内的切割符号, 返回值不带指定括号
     *
     * @param str       字符串
     * @param delimiter 切割符号
     * @param pair      左右括号
     * @return 切割后的字符串集合
     */
    public static List<String> splitWithOutQuota(String str, char delimiter, Pair<Character, Character> pair) {
        return splitIgnoreQuota(str, delimiter, pair).stream().map(sp -> splitWithPair(sp, pair)).collect(Collectors.toList());
    }

    /**
     * 按指定切割符号进行切割字符串并忽略指定括号内的切割符号
     *
     * @param str       字符串
     * @param delimiter 切割符号
     * @param pair      左右括号
     * @return 切割后的字符串集合
     */
    public static List<String> splitIgnoreQuota(String str, char delimiter, Pair<Character, Character> pair) {
        List<String> resultList = new ArrayList<>();
        char[] chars = str.toCharArray();
        StringBuilder b = new StringBuilder();

        if (pair.getLeft().equals(pair.getRight())) {
            boolean inEqualQuotes = false;
            for (char c : chars) {
                if (c == delimiter) {
                    if (inEqualQuotes) {
                        b.append(c);
                    } else {
                        resultList.add(b.toString());
                        b = new StringBuilder();
                    }
                } else if (c == pair.getLeft()) {
                    inEqualQuotes = !inEqualQuotes;
                    b.append(c);
                } else {
                    b.append(c);
                }
            }
        } else {
            int bracketLeftNum = 0;
            for (char c : chars) {
                if (c == delimiter) {
                    if (bracketLeftNum > 0) {
                        b.append(c);
                    } else {
                        resultList.add(b.toString());
                        b = new StringBuilder();
                    }
                } else if (c == pair.getLeft()) {
                    bracketLeftNum++;
                    b.append(c);
                } else if (c == pair.getRight()) {
                    bracketLeftNum--;
                    b.append(c);
                } else {
                    b.append(c);
                }
            }
        }
        resultList.add(b.toString());
        return resultList;
    }

    public static void main(String[] args) {
        System.out.println(splitWithOutQuota("[ab].[b.c]", '.', Pair.of('[', ']')));
        System.out.println("------------------------------------");
        System.out.println(splitWithOutQuota("ab.[b.c]", '.', Pair.of('[', ']')));
        System.out.println("------------------------------------");
        System.out.println(splitWithOutQuota("[ab].c", '.', Pair.of('[', ']')));
        System.out.println("------------------------------------");
        System.out.println(splitWithOutQuota("b.c", '.', Pair.of('[', ']')));

        System.out.println("++++++++++++++++++++++++++++++++++++");

        System.out.println(splitWithOutQuota("\"ab\".\"b.c\"", '.', Pair.of('\"', '\"')));
        System.out.println("------------------------------------");
        System.out.println(splitWithOutQuota("ab.\"b.c\"", '.', Pair.of('\"', '\"')));
        System.out.println("------------------------------------");
        System.out.println(splitWithOutQuota("\"ab\".c", '.', Pair.of('\"', '\"')));
        System.out.println("------------------------------------");
        System.out.println(splitWithOutQuota("b.c", '.', Pair.of('\"', '\"')));
        System.out.println("------------------------------------");
        System.out.println(splitWithOutQuota("\"b.c\"", '.', Pair.of('\"', '\"')));
    }
}
