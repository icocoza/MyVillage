package com.ccz.myvillage.common;

import android.graphics.Color;

import java.util.Arrays;
import java.util.List;

public class ColorCollection {

    static List<Integer> colors = Arrays.asList(0xff00994d, 0xff7733ff, 0xff3385ff, 0xff00b386, 0xff009900, 0xffcc7a00, 0xffe600e6, 0xff0077b3, 0xffff4d4d, 0xffcc8800);

    public static Integer get(int pos) {
        if(colors.size()<=pos)
            return colors.get(pos % colors.size());
        return colors.get(pos);
    }

}
