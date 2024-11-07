package com.bob.wechat;

public class APIUrl {

    private static final String DOMAIN_URL = "https://login.weixin.qq.com";

    public static final String GENERATE_UID_URL = DOMAIN_URL + "/jslogin?appid=wx782c26e4c19acffb";

    public static final String GENERATE_QR_URL = DOMAIN_URL + "/qrcode/%s";

    public static final String CHECK_LOGIN_URL = DOMAIN_URL + "/cgi-bin/mmwebwx-bin/login?tip=0&uuid=%s";

    public static final String LOGIN_URL = "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxnewloginpage?ticket=%s&uuid=%s&lang=zh_CN&fun=new&version=v2&mod=desktop";

    public static final String INIT_URL = DOMAIN_URL + "/cgi-bin/mmwebwx-bin/webwxinit?pass_ticket=%s";

    public static final String STATUS_NOTIFY_URL = DOMAIN_URL + "/webwxstatusnotify?pass_ticket=%s";

    public static final String SYNC_CHECK_URL = "https://webpush.wx.qq.com/cgi-bin/mmwebwx-bin/synccheck";

    public static final String SYNC_MESSAGE_URL = "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxsync?sid=%s&skey=%s&pass_ticket=%s";

    public static final String GET_MSG_IMG = "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxgetmsgimg";

    public static final String GET_MSG_VOICE = "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxgetvoice";

    public static final String GET_MSG_VIDEO = "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxgetvideo";

    public static final String SEND_MSG_URL = "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxsendmsg";

    public static final String GET_CONTACT_URL = "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxgetcontact";

}
