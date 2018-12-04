package com.hypericum.hypapp.utils;


import com.ciyashop.library.apicall.URLS;

public class APIS {

//  ===================================================================

    //  TODO:Copy and paste URL and key below from Admin Panel
    public final String APP_URL = "https://hypericumimpex.com/";   //TODO:Application URL
    public final String WOO_MAIN_URL = APP_URL + "wp-json/wc/v2/"; //TODO:WooCommerce API Path
    public final String MAIN_URL = APP_URL + "wp-json/pgs-woo-api/v1/"; //TODO:PGS Woo API Path

    public static final String CONSUMERKEY = "hRSK7ijTW2bd";
    public static final String CONSUMERSECRET = "jU6Un6MTP53NUnFGHiyj7lT43fQ0lLxgET98erCY8n0EE6xS";
    public static final String OAUTH_TOKEN = "QunwmpaTeHq3Ja34FpY8bDWt";
    public static final String OAUTH_TOKEN_SECRET = "oxOEOn8kLxIlJOOnHeDX5dvukLDZPt6axsy6BDMDGmLCCGL7";
    public static final String WOOCONSUMERKEY = "ck_4dbf3da384dbb9b6e55243d3197bac8e73793826";
    public static final String WOOCONSUMERSECRET = "cs_74d4324f5a0f9fd527cf1362a2573ae6e328bc4a";
    public static final String version = "";


//  ===================================================================


    public APIS() {
        URLS.APP_URL = APP_URL;
        URLS.WOO_MAIN_URL = WOO_MAIN_URL;
        URLS.MAIN_URL = MAIN_URL;
        URLS.version = version;
        URLS.CONSUMERKEY = CONSUMERKEY;
        URLS.CONSUMERSECRET = CONSUMERSECRET;
        URLS.OAUTH_TOKEN = OAUTH_TOKEN;
        URLS.OAUTH_TOKEN_SECRET = OAUTH_TOKEN_SECRET;
        URLS.WOOCONSUMERKEY = WOOCONSUMERKEY;
        URLS.WOOCONSUMERSECRET = WOOCONSUMERSECRET;
    }
}
