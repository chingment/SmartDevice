package com.lumos.smartdevice.utils;

import java.util.ArrayList;
import java.util.List;

public class HAUtil {
    public static <T> List<T> objToList(Object obj, Class<T> cla)
     {
         List<T> list = new ArrayList<T>();
         try {
             if (obj instanceof ArrayList<?>) {
                 for (Object o : (List<?>) obj) {
                     list.add(cla.cast(o));
                 }
                 return list;
             }

             return list;
         }
         catch(Exception ex) {

             return list;
         }
    }
}
