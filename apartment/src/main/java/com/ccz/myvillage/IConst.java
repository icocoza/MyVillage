package com.ccz.myvillage;

import android.util.Size;

public class IConst {
    //remote
    public static String host = "ws://45.76.220.83:8081/wss"; //vultr
    public static String apptoken = "965PFFqrhmdGZs86NyHjhujntqOeOtcipHsKoGCw9z1CFTfIFlyEA6xcDxEyfgCbhPGw65MT73BSynSZM2Yk85gieie";

    //local
    //public static String host = "ws://192.168.43.224:8081/wss";//home
    //public static String host = "ws://172.23.252.200:8081/wss";  //
    //public static String apptoken = "XM2ezCyVelxmm99qg99ZzrZRVJ4A6fLJ9vgLKKVxripXYFMn6hVw7d8Ax1g6LaV3mCBr3kG0zCxJcgwwWvRxbLAieie";

    public static String PreferenceID = "MyVillagePref";

    public static String ServerAction = "appserver";
    public static String ServiceCode = "apartment";

    public static int MAX_BOARDITEM_COUNT = 7;

    public static Size ScreenPixels;
    public static Size ScreenDp;

    public static int IMG_MAX_WIDTH = 1024;
    public static int IMG_MAX_HEIGHT = 576;

    public static int MAX_UPLOAD_SIZE = 1024*1024*10;

    public static int MAX_VOTE_COUNT = 10;
}
