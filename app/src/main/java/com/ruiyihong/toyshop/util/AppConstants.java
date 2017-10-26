package com.ruiyihong.toyshop.util;

/**
 * Created by 81521 on 2017/7/6.
 * 常量类
 */

public class AppConstants {
    public static final long TIME_OUT = 3000;//网络请求超时时间

    public static final String TENCENT_APP_ID = "101417461";//官方获取的APPID
    public static final String IS_OPENMAIN = "is_openning"; //是否第一次登陆 key
    public static final String LAST_LOCATION="last_location";//上一次定位地址 key
    public static final String WEIXIN_APP_ID = "wx14290e98fe5178c8"; //微信App

    /*************接口*首页************************/
    public static final String SERVE_URL = "http://appadmin.y91edu.com/Index.php/";//服务器前缀
    public static final String LOGIN_URL = SERVE_URL+"index/index/login";//登录接口
    public static final String REGI_URL = SERVE_URL+"index/index/reg";//注册接口
    public static final String BUSER_URL = SERVE_URL+"index/infoedit/index";//修改信息接口


    public static final String HOME_LUNBOTU_URL=SERVE_URL+"index/index/lunbo";//首页轮播图接口
    public static final String HOME_MORE_TOY = SERVE_URL+"/index/index/zxwjlist";//更多玩具
    public static final String HOME_MORE_BOOK = SERVE_URL+"/index/index/zxtslist";//更多图书
    public static final String HOME_MORE_SUIT = SERVE_URL+"/index/index/qltjlist";//强力套餐
    public static final String HOME_MORE_TOY_UPLOAD = SERVE_URL+"/index/index/zxwjloading";//更多玩具_上拉加载
    public static final String HOME_MORE_BOOK_UPLOAD = SERVE_URL+"/index/index/zxtsloading";//更多图书_上拉加载
    public static final String HOME_MORE_SUIT_UPLOAD = SERVE_URL+"/index/index/qltjlistload";//强力套餐_上拉加载
    public static final String HOME_ENTER_OUR = SERVE_URL+"/index/kefu/joinus";//加入我们

    /***********************商城Head******************************/
    public static final String TOYSHOP_URL= "http://appadmin.y91edu.com/Index.php/index/Wan";//玩具城的前缀
    public static final String ALL_TOY_URL = TOYSHOP_URL+"/wlist";//打开玩具城的默认加载玩具列表
    public static final String ALL_BOOK_URL = TOYSHOP_URL+"/tlist";//打开玩具城的默认加载图书列表
    public static final String TOYSHOP_TOY_LOADMORE_URL = AppConstants.TOYSHOP_URL+"/wload"; //玩具城玩具加载更多
    public static final String TOYSHOP_BOOK_LOADMORE_URL = AppConstants.TOYSHOP_URL+"/tload"; //玩具城图书加载更多
    public static final String ALLAGE_URL = TOYSHOP_URL+"/qbage";//全部年龄
    public static final String TOY_GONGNENG = TOYSHOP_URL+"/wjlei";//玩具功能
    public static final String BOOK_JXFL = TOYSHOP_URL+"/tslei";//图书精细分类
    //public static final String TOY_BOOK_SEARCH = TOYSHOP_URL+"/search";//玩具功能

    /***********************商城********************************/

   //获取到的数据使用图片专用请求主机头+后缀
    //String n = "http://appadmin.xingkongxueyuan.cn/gaimage";
    public static final String IMG_BASE_URL="http://appadmin.y91edu.com/gaimage/";

    public static final String TOY_DETAIL = SERVE_URL+"index/Xiang/xiang";
    public static final String BOOK_DETAIL = SERVE_URL+"index/Xiang/tsxiang";
    public static final String TOY_RECOMMEND_DETAIL = SERVE_URL+"index/Xiang/wjtuiye";
    public static final String BOOK_RECOMMEND_DETAIL = SERVE_URL+"index/Xiang/tstuiye";
    public static final String TOY_SUIT_DETAIL = SERVE_URL+"index/Xiang/tcxiang";
    public static final String TOY_RECOMMEND_SUIT_DETAIL = SERVE_URL+"index/Xiang/tctuiye";

    /***********************购物车接口Head******************************/
    public static final String QueryShoppingCar=SERVE_URL+"index/gou/selgou";
    public static final String AddShoppingCar=SERVE_URL+"index/gou/editshu";
    public static final String DelShoppingCar=SERVE_URL+"index/gou/delgou";
    public static final String UpdateShoppingCar=SERVE_URL+"index/gou/editshu";
    public static final String SHOP_BUY_SUCCESS=SERVE_URL+"index/order/retPayStatus";
    /***********************购物车接口*********************************/



    /***********************课程接口******************************/
    public static final String VIDEO_BASE_URL="http://appadmin.y91edu.com/gamedia/";//视频存放文件路径
    public static final String CLASS_MAIN=SERVE_URL+"index/Kc/kcindex";//课程主页
    public static final String CLASS_DETAIL=SERVE_URL+"index/Kc/kcxq";//课程详情
    public static final String CLASS_RECOMMEND=SERVE_URL+"index/Kc/tuijian";//相关推荐
    public static final String CLASS_RECOMMEND_VIDEO=SERVE_URL+"index/kc/sgsp";//相关视频
    public static final String CLASS_PAY=SERVE_URL+"index/kc/kcpay";//支付后更新数据接口
    public static final String CLASS_LOAD_MORE=SERVE_URL+"index/Kc/kcLoad";//上拉加载
  //  public static final String CLASS_REFRESH=SERVE_URL+"index/Kc/kcLoad";//下拉刷新
   public static final String CLASS_COLLECT=SERVE_URL+"index/collect/isshou";//收藏
    public static final String CLASS_BUY_SUCCESS=SERVE_URL+"index/order/retPaykeStatus";


    /***********************课程接口******************************/

    /***********************活动接口******************************/
    public static final String EVENT_MINE_YOUHUI=SERVE_URL+"index/Active/xshd";
    public static final String EVENT_MINE_GONGYI=SERVE_URL+"index/Active/jqgy";
    public static final String EVENT_MINE_OUTDOOR=SERVE_URL+"index/Active/hwhd";
    public static final String EVENT_MINE_QINZI=SERVE_URL+"index/Active/qzhd";
    public static final String EVENT_ENTER_PT=SERVE_URL+"index/Active/bmxq";
    public static final String EVENT_VIDEO_UPLOAD = SERVE_URL+"index/active/spupload";
    public static final String EVENT_MORE_YOUHUI = SERVE_URL+"index/Active/morexs";//更多优惠
    public static final String EVENT_MORE_YOUHUI_UPLOAD = SERVE_URL+"index/Active/xsload";//更多优惠——上拉加载
    public static final String EVENT_MORE_BENEFIT = SERVE_URL+"index/active/actlist";//更多公益
    public static final String EVENT_MORE_BENEFIT_UPLOAD = SERVE_URL+"index/active/actlistload";//更多优惠——上拉加载
    public static final String EVENT_BENEFIT_DETAIL = SERVE_URL+"index/active/actxq";//公益详情
    /***********************活动接口******************************/

    /***********************会员接口******************************/
    public static final String VIP_ALL=SERVE_URL+"index/Vip/vctype";
    public static final String VIP_DETAIL=SERVE_URL+"index/Vip/selvctype";
    public static final String VIP_BUY=SERVE_URL+"index/vip/viporder";//生成购买会员卡的订单
    public static final String VIP_BUY_SUCCESS=SERVE_URL+"index/order/retPayvipStatus";
    public static final String VIP_UPDATE_STATE=SERVE_URL+"";

    /***********************会员接口******************************/


    /*******************发现页面接口************************************/
    public static final String FIND_IMAGE_BASE_URL = "http://appadmin.y91edu.com/findimg/";
    public static final String FIND_HOT_TALKING_IAMGE_BASE = "http://appadmin.y91edu.com/gaimage/";
    public static final String FIND_LUNBO = SERVE_URL+"index/find/flb";//轮播图
    public static final String FIND_HOT_TALKING= SERVE_URL+"index/find/hotht";//热门话题
    public static final String FIND_HOT_TUIJIAN= SERVE_URL+"index/find/hottj";//热门推荐
    public static final String FIND_HOT_WANTUQUAN= SERVE_URL+"index/find/wtq";//热门玩图圈
    public static final String PUBLISH_UPLOAD_VIDEO_OR_PHOTO = SERVE_URL+"index/find/publish";//上传图片或视频
    public static final String PUBLISH_UPLOAD_SHUOSHUO = SERVE_URL+"index/find/pubs";//上传图片或视频
    public static final String FIND_HOTTUIJIAN_PINGLUN  = SERVE_URL+"index/find/hottjpl";//评论
    public static final String FIND_HOTTUIJIAN_PINGLUN_LIST  = SERVE_URL+"index/find/pllist";//评论列表
    public static final String FIND_HOTTUIJIAN_DIANZAN  = SERVE_URL+"index/find/dz";//点赞
    public static final String FIND_HOTTUIJIAN_LOADMORE  = SERVE_URL+"index/find/hottjload";//加载更多
    public static final String FIND_HOTTUIJIAN_DELETE  = SERVE_URL+"index/find/delshuo";//删除说说
    public static final String FIND_HOTTUIJIAN_DELETE_PINLUN  = SERVE_URL+"index/find/delping";//删除评论
    public static final String FIND_HOTTALKING_DETAIL  = SERVE_URL+"index/hot/xqhotht";//热门话题详情页
    /*******************发现页面接口************************************/


    /*******************积分商城接口************************************/
    public static final String JIFEN_DUIHUAN_DAIJINQUAN = SERVE_URL+"index/Quan/seldjq";//兑换代金券
    public static final String JIFEN_DUIHUAN_DAIJINQUAN_UPLOAD = SERVE_URL+"index/quan/djqload";//兑换代金券_上拉加载
    public static final String JIFEN_DUIHUAN_TOY = SERVE_URL+"index/Exchange/wjchange";//兑换玩具
    public static final String JIFEN_DUIHUAN_TOY_UPLOAD = SERVE_URL+"index/Exchange/wjchangeload";//兑换玩具_上拉加载
    public static final String JIFEN_DUIHUAN_BOOK = SERVE_URL+"index/Exchange/tschange";//兑换图书
    public static final String JIFEN_DUIHUAN_BOOK_UPLOAD = SERVE_URL+"index/Exchange/tschangeload";//兑换图书_上拉加载
    public static final String JIFEN_DUIHUAN_HOT_PRODUCT = SERVE_URL+"index/exchange/hotdh";//热门商品兑换
    public static final String JIFEN_DUIHUAN_HOT_PRODUCT_UPLOAD = SERVE_URL+"index/exchange/hotdhload";//热门商品兑换_上拉加载
    public static final String JIFEN_DUIHUAN_HOT_DJQ = SERVE_URL+"index/exchange/hotdhdjq";//热门代金券兑换
    public static final String JIFEN_DUIHUAN_HOT_DJQ_PRODUCT_UPLOAD = SERVE_URL+"index/exchange/djqload";//热门代金券兑换_上拉加载
    public static final String JIFEN_DUIHUAN_EXCHANGE_DJQ = SERVE_URL+"index/Quan/dhdjq";//兑换代金券
    public static final String JIFEN_DUIHUAN_GET_CREDIT = SERVE_URL+"index/Exchange/userjf";//获取用户积分
    /*******************积分商城接口************************************/


    /*******************收藏接口************************************/
    public static final String COLLECT_URL = SERVE_URL+"index/collect/tcollect";
    /*******************收藏接口************************************/

    /*******************分享接口************************************/
    public static final String SHARED_URL = SERVE_URL+"index/share/shareurl";
    /*******************分享接口************************************/

    /***********************我的************************************/
    public static final String MY_DJQ  = SERVE_URL+"index/quan/ismydjz";//我的代金券
    public static final String MY_OVERLINE_DJQ  = SERVE_URL+"index/Quan/selhisdjq";//历史代金券
    public static final String MY_UPLOAD_PJ_PIC  = SERVE_URL+"index/Ping/addplimg";//上传评价图片
    public static final String MY_ORDER_SEL  ="http://appadmin.y91edu.com/index/Order/selOrder";//查询订单详情
    /***********************我的************************************/

    /***********************支付宝支付************************************/
    public static final String ALIPAY_PAY  = "http://api.xingkongxueyuan.cn/alipay/pay/orderInfo.php";//支付宝支付
    public static final String ALIPAY_MAKE_ORDER  = SERVE_URL+"index/kc/korder";//课程订单
    /**********************版本更新***************************/
    public static final String VERSION_UPDATE_HEAD = "http://appadmin.y91edu.com/gazip/";
    /***********************支付宝支付************************************/


    /*************SP*************************/
    public static final String SP_LOGIN="login";
    public static final String SP_ZJZH="zjzh";
    public static final String SP_KEYWORD="keyword";
    public static final int TYPE_SUIT=2;
    /*************SharedPreferences*************************/


    /*************video 压缩路径*************************/
    /**
     * Application root directory. All media files wi'll be stored here.
     */
    public static final String VIDEO_COMPRESSOR_APPLICATION_DIR_NAME = "ruiyihong/video";//VideoCompressor

    /**
     * Application folder for video files
     */
    public static final String VIDEO_COMPRESSOR_COMPRESSED_VIDEOS_DIR = "/CompressedVideos";

    /**
     * Application folder for video files
     */
    /*************video 压缩路径*************************/


}
