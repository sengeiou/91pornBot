package com.example.demo.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则工具
 */
public class DealStrSub {
    /**
     * 正则表达式匹配两个指定字符串中间的内容
     *
     * @param soap
     * @return
     */
    public static List<String> getSubUtil(String soap, String rgex) {
        List<String> list = new ArrayList<String>();
        Pattern pattern = Pattern.compile(rgex);// 匹配的模式
        Matcher m = pattern.matcher(soap);
        while (m.find()) {
            int i = 1;
            list.add(m.group(i));
            i++;
        }
        return list;
    }

    /**
     * 返回单个字符串，若匹配到多个的话就返回第一个，方法与getSubUtil一样
     *
     * @param soap
     * @param rgex
     * @return
     */
    public static String getSubUtilSimple(String soap, String rgex) {
        Pattern pattern = Pattern.compile(rgex);// 匹配的模式
        Matcher m = pattern.matcher(soap);
        while (m.find()) {
            return m.group(1);
        }
        return "";
    }

    /**
     * 测试
     *  String str = "abc3443abcfgjhgabcgfjabc";
     *  String rgex = "abc(.*?)abc"
     * @param args
     */
//    public static void main(String[] args) {
//        String str = "<source src='https://ccn.killcovid2021.com//m3u8/495042/495042.m3u8?st=502wTAG3kY5jMZYXP56aOA&e=1626406675' type='application/x-mpegURL'>";
//        String rgex = "source src='(.*?)' type=";
//        System.out.println(getSubUtilSimple(str, rgex));
//    }
}
