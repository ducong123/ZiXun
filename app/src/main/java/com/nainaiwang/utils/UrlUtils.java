package com.nainaiwang.utils;

/**
 * Created by Administrator on 2017/1/3.
 */
public class UrlUtils {
    /**
     * 首页频道接口
     */
    public static final String HOMEPAGE = "http://124.166.246.120:8000/nnzx/app/category/cateList";

    /**
     * 首页推荐页面接口
     */
    public static final String TUIJIAN = "http://124.166.246.120:8000/nnzx/app/article/arclist";

    /**
     * 文章详情接口
     */
    public static final String DETAILS = "http://124.166.246.120:8000/nnzx/app/article/arcInfo/id/";

    /**
     * 广告页接口
     */
    public static final String AD = "http://124.166.246.120:8000/nnzx/app/article/ad";

    /**
     * 注册接口
     */
    public static final String Reg = "http://124.166.246.120:8000/nn2/user/login/doreg";
    /**
     * 短信验证码接口
     */
    public static final String GET_VERIFICATION_CODE ="http://124.166.246.120:8000/nn2/user/login/sendMessage";

    /**
     * 登录接口
     */
    public static final String LOGIN = "http://124.166.246.120:8000/nn2/user/app/dolog";


    /**
     * 判断是否登录接口
     */
    public static final String CHECKLOG = "http://124.166.246.120:8000/nn2/user/app/checklog";

    /**
     * 退出登录接口
     */
    public static final String EXIT = "http://124.166.246.120:8000/nn2/user/app/logout";
    /**
     * 修改密码接口
     */
    public static final String SET_PASSWORD = "http://124.166.246.120:8000/nn2/user/ucenter/chgPass";
    /**
     * 资料修改接口
     */
    public static final String SET_DATA = "http://124.166.246.120:8000/nn2/user/ucenterApp/editInfo";
    /**
     * 上传图片接口
     */
    public static final String UPLOAD_IMG = "http://124.166.246.120:8000/nn2/user/ucenterApp/upload";
    /**
     * 图片验证码
     */
    public static final String Img ="http://124.166.246.120:8000/nn2/user/login/getcaptcha" ;
   /* *//**
     *个人资料 编辑接口
     *//*
    public static final String EDITDATE = "http://124.166.246.120:8000/nn2/user/ucenterApp/getInfo";*/
     /*
     * 文章收藏接口
     * */
     public static  final  String COLLECTION ="http://124.166.246.120:8000/nnzx/app/ucenter/addFavorite";
    /*
     * 文章取消收藏接口
     * */
    public static  final  String NOCOLLECTION ="http://124.166.246.120:8000/nnzx/app/ucenter/cancleFavorite";
    /*
    * 文章收藏列表接口
    * */
    public static  final  String COLLECTIONLIST="http://124.166.246.120:8000/nnzx/app/ucenter/favoriteList";
    /*
    * 文章收藏列表接口
    * */
    public static  final  String COMMENTlIST="http://124.166.246.120:8000/nnzx/app/ucenter/addComment";


}
