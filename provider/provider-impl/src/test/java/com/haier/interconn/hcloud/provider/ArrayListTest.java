package com.haier.interconn.hcloud.provider;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Author: bandd
 * Mailto:bandd@haier.com
 * On: 2017-08-31  15:34
 */
public class ArrayListTest {

        public static void main(String[] args)  {
            ArrayList<Integer> list = new ArrayList<Integer>();
            list.add(2);
            Iterator<Integer> iterator = list.iterator();
            while(iterator.hasNext()){
                Integer integer = iterator.next();
                if(integer==2)
                    iterator.remove();
            }
        }
    }

