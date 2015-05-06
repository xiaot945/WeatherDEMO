package pku.ss.xiaot.util;

import java.util.List;

/**
 * Created by xiaot on 2015/3/31.
 */
public class StaticFunction {

    public static String[] list2StringArray(List<String> list) {
        String[] stringArray = new String[list.size()];
        for (int i = 0; i< list.size(); i++) {
            stringArray[i] = list.get(i);
        }
        return stringArray;
    }

}
