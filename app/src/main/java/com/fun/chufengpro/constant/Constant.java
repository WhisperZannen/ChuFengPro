package com.fun.chufengpro.constant;

public class Constant {
    //  接口类
//外网
    public static final String URL = "http://galaxydream.cn/";
//内网
//    public static final String URL = "http://172.17.5.192:8005/";
    public static final String URL_USER = URL + "user/";
    public static final String USER_LOGIN = URL_USER + "loginbyusername/";
    public static final String USER_SIGNUP = URL_USER + "signup/";
    public static final String URL_UPLOAD = URL + "upload/";
    public static final String UPLOAD_IMG = URL_UPLOAD + "img/";
    public static final String UPLOAD_CONTEXT = URL_UPLOAD + "post/";

    public static final String URL_POST = URL + "post/";
    public static final String POST_GETPAGE = URL_POST + "getall/";
    //  资源类
    public static final String SRC_IMGS = URL + "/images/";

    /*
     * 其他配置类
     * */

    //    朋友圈分页配置
    public static final String PAGE_LIMIT = "3";
}
