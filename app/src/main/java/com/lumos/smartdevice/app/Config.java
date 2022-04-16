package com.lumos.smartdevice.app;

import com.lumos.smartdevice.BuildConfig;
import com.lumos.smartdevice.utils.SHA256Encrypt;
import com.lumos.smartdevice.utils.StringUtil;



public class Config {

    public static final boolean IS_BUILD_DEBUG = BuildConfig.DEBUG;//打包模式
    public static final boolean IS_APP_DEBUG = BuildConfig.ISAPPDEBUG;//调试模式


}
