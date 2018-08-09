package com.ccz.myvillage.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.ccz.myvillage.IConst;

public class Preferences {

    public static String TOKENID = "tokenid";
    public static String TOEKN = "token";
    public static String UUID = "uuid";

    public static String USERNAME = "username";
    public static String VIEWTYPE = "viewtype";


    public static String get(Context ctx, String fieldName) {
        SharedPreferences pref = ctx.getApplicationContext().getSharedPreferences(IConst.PreferenceID, 0); // 0 - for private mode
        if(pref!=null)
            return pref.getString(fieldName, null);
        return null;
    }

    public static void set(Context ctx, String key, String value) {
        SharedPreferences pref = ctx.getApplicationContext().getSharedPreferences(IConst.PreferenceID, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static boolean getBool(Context ctx, String fieldName) {
        SharedPreferences pref = ctx.getApplicationContext().getSharedPreferences(IConst.PreferenceID, 0); // 0 - for private mode
        if(pref!=null)
            return pref.getBoolean(fieldName, false);
        return false;
    }

    public static void setBool(Context ctx, String key, boolean value) {
        SharedPreferences pref = ctx.getApplicationContext().getSharedPreferences(IConst.PreferenceID, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

}
