package com.pwnpub.search.utils;

/**
 * @program BlockExplorer-Web-h5
 * @author: joon.h
 * @create: 2019/01/21 13:31
 */

public class CommonUtils {


    public static boolean isNumeric0(String str){
        for(int i=str.length();--i>=0;){
            int chr=str.charAt(i);
            if(chr<48 || chr>57)
                return false;
        }
        return true;
    }
}
